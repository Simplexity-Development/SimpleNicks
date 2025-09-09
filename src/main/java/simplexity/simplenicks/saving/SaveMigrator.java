package simplexity.simplenicks.saving;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.logic.NickUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for migrating nickname save data from older storage formats into the current system.
 * <p>
 * This class is intended to be used only once, during the upgrade of the SimpleNicks plugin.
 * It reads legacy data files or player PersistentDataContainers (PDC) and converts them into the
 * current {@link Cache} and {@link SqlHandler} format. After migration, it renames the old data file
 * to prevent duplicate migrations, while saving the old data if the migration didn't go as planned.
 * </p>
 * <p>
 * While designed for internal use, this class can be adapted by other plugins to import custom
 * nickname data, or for other types of one-time migrations. It should be called during plugin startup,
 * and only if there is not already saved data from these users, to prevent overwriting current data.
 * </p>
 */
public class SaveMigrator {

    /**
     * Namespaced key used for storing nicknames in a player's PersistentDataContainer (PDC).
     * There was no implementation of 'saved' nicknames with PDC, so only the current nickname is migrated.
     */
    public static final NamespacedKey nickNameSave = new NamespacedKey(SimpleNicks.getInstance(), "nickname");

    private static final Logger logger = SimpleNicks.getInstance().getSLF4JLogger();
    private static final List<NicknameRecord> records = new ArrayList<>();
    private static final AtomicInteger processed = new AtomicInteger();
    private static final AtomicInteger failed = new AtomicInteger();
    private static int taskId;

    /**
     * Migrates all nickname data from the legacy YAML file ("nickname_data.yml") into the current database format.
     * <p>
     * This method performs the migration asynchronously to avoid blocking the server. It provides
     * periodic console logs with progress updates. After migration, the legacy YAML file is renamed
     * to "MIGRATED_nickname_data.yml" to prevent repeated attempts.
     * </p>
     * <p>
     * If any UUID keys are invalid or cannot be processed, the migration continues with a warning.
     * The method logs the number of successfully migrated and failed records.
     * </p>
     */
    public static void migrateFromYml() {
        File dataFile = new File(SimpleNicks.getInstance().getDataFolder(), "nickname_data.yml");
        if (!dataFile.exists()) return;
        FileConfiguration nicknameData = new YamlConfiguration();
        try {
            nicknameData.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.warn("Unable to migrate nicknames from YML: {}", e.getMessage(), e);
            return;
        }
        logger.info("Starting Save Migration");

        Set<String> savedUuids = nicknameData.getKeys(false);
        int totalUuids = savedUuids.size();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(SimpleNicks.getInstance(), () -> consoleNotifier(totalUuids), 0L, 100L);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            for (String uuidKey : savedUuids) {
                saveChecks(uuidKey, nicknameData);
            }
            boolean success = SqlHandler.getInstance().batchInsertNicknames(records);
            if (success) {
                logger.info("Save data migrated successfully! {} users' data migrated", processed.get());
            } else {
                logger.error("Save data was not migrated properly! Please report this to the developers at https://github.com/Simplexity-Development/SimpleNicks!\nPlease be sure to attach any error logs that were created, and your config when you make a report!");
            }
            if (failed.get() > 0) logger.warn("{} users' data was not successfully migrated", failed.get());
            File backupFile = new File(SimpleNicks.getInstance().getDataFolder(), "MIGRATED_nickname_data.yml");
            boolean renamed = dataFile.renameTo(backupFile);
            if (!renamed) {
                logger.warn("Unable to rename 'nickname_data.yml' - if migration was successful, please remove or rename this file so as to not overwrite new save data.");
            } else {
                logger.info("Successfully renamed 'nickname_data.yml' - this migration process will no longer be attempted (unless you rename it back, for some reason, I wouldn't recommend that)");
            }
            Bukkit.getScheduler().cancelTask(taskId);
        });
    }

    /**
     * Migrates the nickname stored in a player's PersistentDataContainer (PDC) into the current {@link Cache}.
     * <p>
     * After migration, the PDC entry is removed to prevent duplicate storage. This is safe to call
     * on individual players, typically during player login events.
     * </p>
     *
     * @param player The player whose PDC nickname should be migrated
     */
    public static void migratePdcNickname(@NotNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(nickNameSave)) return;
        String currentNick = pdc.get(nickNameSave, PersistentDataType.STRING);
        if (currentNick == null) {
            pdc.remove(nickNameSave);
            return;
        }
        UUID uuid = player.getUniqueId();
        Cache.getInstance().setActiveNickname(uuid, player.getName(), currentNick);
        debug("Migrated PDC data from user: {}, uuid: {}, nickname: {}", player.getName(), player.getUniqueId(), currentNick);
        pdc.remove(nickNameSave);
    }


    /**
     * Performs sanity checks on a UUID key from the YAML configuration and adds it to the migration
     * batch if valid. Processes both the player's current nickname and saved nicknames.
     *
     * @param uuidKey UUID string of the player
     * @param config  YAML configuration containing the legacy nickname data
     */
    private static void saveChecks(@NotNull String uuidKey, @NotNull FileConfiguration config) {
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidKey);
        } catch (IllegalArgumentException e) {
            debug("Skipping invalid UUID key: %s", uuidKey);
            processed.incrementAndGet();
            failed.incrementAndGet();
            return;
        }

        ConfigurationSection section = config.getConfigurationSection(uuidKey);
        if (section == null) {
            debug("Configuration section null for UUID %s", uuidKey);
            processed.incrementAndGet();
            failed.incrementAndGet();
            return;
        }

        String nicknameString = section.getString("current", null);
        List<String> savedNicks = section.getStringList("saved");
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String username = player.getName();
        if (username == null) username = "unknown";
        debug("Migrating nicknames for user: %s - (UUID:%s)", username, uuidKey);

        if (nicknameString == null || nicknameString.isEmpty()) return;
        records.add(new NicknameRecord(uuid, username, nicknameString, NickUtils.normalizeNickname(nicknameString), true, player.getLastLogin()));

        if (!savedNicks.isEmpty()) {
            for (String savedNickname : savedNicks) {
                records.add(new NicknameRecord(uuid, username, savedNickname, NickUtils.normalizeNickname(nicknameString), false, player.getLastLogin()));
            }
        }

        processed.incrementAndGet();
    }

    /**
     * Periodically logs the migration progress to the console.
     * <p>
     * Shows the percentage complete and reminds server admins not to restart the server during migration.
     * </p>
     *
     * @param total Total number of UUIDs being migrated
     */
    private static void consoleNotifier(int total) {
        int done = processed.get();
        double percent = (done / (double) total) * 100.0;
        logger.info("[MIGRATION] {}% complete ({} / {})", String.format("%.1f", percent), done, total);
        logger.info("[MIGRATION] ⚠ Do NOT restart the server until migration completes!");
    }


    /**
     * Debug logging method that only logs messages if {@link ConfigHandler#isDebugMode()} is enabled.
     * <p>
     * Accepts printf-style formatting with arguments.
     * </p>
     *
     * @param message The message format string
     * @param args    Arguments for the format string
     */
    private static void debug(@NotNull String message, @NotNull Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            message = String.format(message, args);
            logger.info("[MIGRATION DEBUG] {}", message);
        }
    }
}
