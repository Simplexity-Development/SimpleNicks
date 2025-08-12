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

public class SaveMigrator {

    /*
    I know this is a mess of a class but it is only supposed to be used once, and hopefully never again a;lkdsjf
     */

    private static final Logger logger = SimpleNicks.getInstance().getSLF4JLogger();
    public static final NamespacedKey nickNameSave = new NamespacedKey(SimpleNicks.getInstance(), "nickname");
    private static final List<NicknameRecord> records = new ArrayList<>();
    private static final AtomicInteger processed = new AtomicInteger();
    private static final AtomicInteger failed = new AtomicInteger();
    private static int taskId;

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

    public static void migratePdcNickname(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(nickNameSave)) return;
        String currentNick = pdc.get(nickNameSave, PersistentDataType.STRING);
        UUID uuid = player.getUniqueId();
        Cache.getInstance().setActiveNickname(uuid, player.getName(), currentNick);
        debug("Migrated PDC data from user: {}, uuid: {}, nickname: {}", player.getName(), player.getUniqueId(), currentNick);
        pdc.remove(nickNameSave);
    }


    private static void saveChecks(String uuidKey, FileConfiguration config) {
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

        String nickname = section.getString("current", null);
        List<String> savedNicks = section.getStringList("saved");
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String username = player.getName();
        if (username == null) username = "unknown";
        debug("Migrating nicknames for user: %s - (UUID:%s)", username, uuidKey);

        if (nickname != null && !nickname.isEmpty()) {
            records.add(new NicknameRecord(uuid, username, nickname, NickUtils.normalizeNickname(nickname), true, player.getLastLogin()));
        }

        if (!savedNicks.isEmpty()) {
            for (String savedNickname : savedNicks) {
                records.add(new NicknameRecord(uuid, username, savedNickname, NickUtils.normalizeNickname(nickname), false, player.getLastLogin()));
            }
        }

        processed.incrementAndGet();
    }

    private static void consoleNotifier(int total) {
        int done = processed.get();
        double percent = (done / (double) total) * 100.0;
        logger.info("[MIGRATION] {}% complete ({} / {})", String.format("%.1f", percent), done, total);
        logger.info("[MIGRATION] ⚠ Do NOT restart the server until migration completes!");
    }



    private static void debug(String message, Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            message = message.formatted(args);
            logger.info("[MIGRATION DEBUG] {}", message);
        }
    }
}
