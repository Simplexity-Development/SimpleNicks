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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class SaveMigrator {

    private static final Logger logger = SimpleNicks.getInstance().getSLF4JLogger();
    public static final NamespacedKey nickNameSave = new NamespacedKey(SimpleNicks.getInstance(), "nickname");


    public static void migrateFromYml() {
        File dataFile = new File(SimpleNicks.getInstance().getDataFolder(), "nickname_data.yml");
        if (!dataFile.exists()) return;
        FileConfiguration nicknameData = new YamlConfiguration();
        try {
            nicknameData.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.warn("Unable to migrate nicknames from YML: {}", e.getMessage(), e);
        }
        int migratedUsers = 0;
        for (String uuidString : nicknameData.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                logger.warn("Skipping invalid UUID key: {}", uuidString);
                continue;
            }

            ConfigurationSection section = nicknameData.getConfigurationSection(uuidString);
            if (section == null) continue;

            String currentNick = section.getString("current", null);
            List<String> savedNicks = section.getStringList("saved");

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String username = player.getName();
            if (username == null) username = "unknown";
            logger.info("Migrating nicknames for user: {} - (UUID:{})", username, uuidString);
            if (currentNick != null && !currentNick.isEmpty()) {
                boolean saved = Cache.getInstance().setActiveNickname(uuid, username, currentNick);
                if (saved) {
                    logger.info("Active nickname: {}", currentNick);
                } else {
                    logger.warn("Failed to save current nickname: {}", currentNick);
                }
            }
            if (!savedNicks.isEmpty()) {
                for (String nickname : savedNicks) {
                    if (nickname == null || nickname.isEmpty()) continue;
                    boolean saved = Cache.getInstance().saveNickname(uuid, username, nickname);
                    if (saved) {
                        logger.info("Saved nickname: {}", nickname);
                    } else {
                        logger.warn("Failed to save nickname: {}", nickname);
                    }
                }
            }
            migratedUsers++;
        }
        logger.info("Save data migrated successfully! {} users' data migrated", migratedUsers);
        File backupFile = new File(SimpleNicks.getInstance().getDataFolder(), "MIGRATED_nickname_data.yml");
        boolean renamed = dataFile.renameTo(backupFile);
        if (!renamed) {
            logger.warn("Unable to rename 'nickname_data.yml' - if migration was successful, please remove or rename this file so as to not overwrite new save data.");
        } else {
            logger.info("Successfully renamed 'nickname_data.yml' - this migration process will no longer be attempted (unless you rename it back, for some reason, I wouldn't recommend that)");
        }
    }

    public static void migratePdcNickname(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(nickNameSave)) return;
        String currentNick = pdc.get(nickNameSave, PersistentDataType.STRING);
        UUID uuid = player.getUniqueId();
        Cache.getInstance().setActiveNickname(uuid, player.getName(), currentNick);
        pdc.remove(nickNameSave);
    }
}
