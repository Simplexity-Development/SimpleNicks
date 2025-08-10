package simplexity.simplenicks.saving;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SaveMigrator {

    private static final Logger logger = SimpleNicks.getInstance().getSLF4JLogger();
    public static final NamespacedKey nickNameSave = new NamespacedKey(SimpleNicks.getInstance(), "nickname");
    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

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
        int migratedUsers = 0;
        Set<String> savedUuids = nicknameData.getKeys(false);
        savedUuids.stream().parallel().forEach(uuidKey -> {
            saveChecks(uuidKey, nicknameData);
        });
        logger.info("Save data migrated successfully! {} users' data migrated", migratedUsers);
        File backupFile = new File(SimpleNicks.getInstance().getDataFolder(), "MIGRATED_nickname_data.yml");
        boolean renamed = dataFile.renameTo(backupFile);
        if (!renamed) {
            debug("Unable to rename 'nickname_data.yml' - if migration was successful, please remove or rename this file so as to not overwrite new save data.");
        } else {
            debug("Successfully renamed 'nickname_data.yml' - this migration process will no longer be attempted (unless you rename it back, for some reason, I wouldn't recommend that)");
        }
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
            debug("Skipping invalid UUID key: {}", uuidKey);
            return;
        }

        ConfigurationSection section = config.getConfigurationSection(uuidKey);
        if (section == null) return;

        String nickname = section.getString("current", null);
        List<String> savedNicks = section.getStringList("saved");

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String username = player.getName();
        if (username == null) username = "unknown";
        debug("Migrating nicknames for user: {} - (UUID:{})", username, uuidKey);
        if (nickname != null && !nickname.isEmpty()) {
            if (SqlHandler.getInstance().getCurrentNicknameForPlayer(player.getUniqueId()) == null) {
                setCurrentNickname(player, nickname);
            } else {
                saveNickname(player, nickname);
            }
        }
        if (!savedNicks.isEmpty()) {
            for (String savedNickname : savedNicks) {
                saveNickname(player, savedNickname);
            }
        }
    }

    private static void setCurrentNickname(OfflinePlayer player, String nickname) {
        UUID uuid = player.getUniqueId();
        String username = player.getName();
        String normalized = miniMessage.stripTags(nickname).toLowerCase();
        if (username == null || username.isEmpty()) username = "unknown";
        boolean success = SqlHandler.getInstance().setActiveNickname(uuid, username, nickname, normalized);
        if (success) {
            debug("Active nickname: {}", nickname);
        } else {
            debug("Failed to save current nickname: {}", nickname);
        }
    }

    private static void saveNickname(OfflinePlayer player, String nickname) {
        if (nickname == null || nickname.isEmpty()) return;
        UUID uuid = player.getUniqueId();
        String username = player.getName();
        String normalized = miniMessage.stripTags(nickname).toLowerCase();
        if (username == null || username.isEmpty()) username = "unknown";
        boolean saved = SqlHandler.getInstance().saveNickname(uuid, username, nickname, normalized);
        if (saved) {
            debug("Saved nickname: {}", nickname);
        } else {
            debug("Failed to save nickname: {}", nickname);
        }
    }


    private static void debug(String message, Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            logger.info("[MIGRATION DEBUG] {}, {}", message, args);
        }
    }
}
