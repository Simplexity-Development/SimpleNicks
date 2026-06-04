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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.PaperPlatformAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for migrating nickname save data from older storage formats into the current system.
 */
public class SaveMigrator {

    public static final NamespacedKey nickNameSave = new NamespacedKey(plugin(), "nickname");

    private static final List<NicknameRecord> records = new ArrayList<>();
    private static final AtomicInteger processed = new AtomicInteger();
    private static final AtomicInteger failed = new AtomicInteger();
    private static int taskId;

    private static Logger logger() {
        return SimpleNicksCore.get().platform().getLogger();
    }

    private static JavaPlugin plugin() {
        return ((PaperPlatformAdapter) SimpleNicksCore.get().platform()).getPlugin();
    }

    /**
     * Migrates all nickname data from the legacy YAML file ("nickname_data.yml") into the current database format.
     */
    public static void migrateFromYml() {
        File dataFile = SimpleNicksCore.get().platform().getDataDirectory().resolve("nickname_data.yml").toFile();
        if (!dataFile.exists()) return;
        FileConfiguration nicknameData = new YamlConfiguration();
        try {
            nicknameData.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger().warn("Unable to migrate nicknames from YML: {}", e.getMessage(), e);
            return;
        }
        logger().info("Starting Save Migration");

        Set<String> savedUuids = nicknameData.getKeys(false);
        int totalUuids = savedUuids.size();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin(), () -> consoleNotifier(totalUuids), 0L, 100L);
        SimpleNicksCore.get().platform().runAsync(() -> {
            for (String uuidKey : savedUuids) {
                saveChecks(uuidKey, nicknameData);
            }
            boolean success = SqlHandler.getInstance().batchInsertNicknames(records);
            if (success) {
                logger().info("Save data migrated successfully! {} users' data migrated", processed.get());
            } else {
                logger().error("Save data was not migrated properly! Please report this to the developers.");
            }
            if (failed.get() > 0) logger().warn("{} users' data was not successfully migrated", failed.get());
            File backupFile = SimpleNicksCore.get().platform().getDataDirectory().resolve("MIGRATED_nickname_data.yml").toFile();
            boolean renamed = dataFile.renameTo(backupFile);
            if (!renamed) {
                logger().warn("Unable to rename 'nickname_data.yml' - if migration was successful, please remove or rename this file.");
            } else {
                logger().info("Successfully renamed 'nickname_data.yml' - this migration process will no longer be attempted.");
            }
            Bukkit.getScheduler().cancelTask(taskId);
        });
    }

    /**
     * Migrates the nickname stored in a player's PersistentDataContainer (PDC) into the current {@link Cache}.
     *
     * @param player the player whose PDC nickname should be migrated
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

    private static void consoleNotifier(int total) {
        int done = processed.get();
        double percent = (done / (double) total) * 100.0;
        logger().info("[MIGRATION] {}% complete ({} / {})", String.format("%.1f", percent), done, total);
        logger().info("[MIGRATION] ⚠ Do NOT restart the server until migration completes!");
    }

    private static void debug(@NotNull String message, @NotNull Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            logger().info("[MIGRATION DEBUG] " + String.format(message, args));
        }
    }
}
