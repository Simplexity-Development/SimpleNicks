package simplexity.simplenicks.util.saving;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import simplexity.simplenicks.SimpleNicks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YMLFile extends AbstractSaving {
    private final File dataFile = new File(SimpleNicks.getInstance().getDataFolder(), "nickname_data.yml");
    private final FileConfiguration nicknameData = new YamlConfiguration();

    @Override
    public void init() {
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                nicknameData.save(dataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            nicknameData.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNickname(OfflinePlayer offlinePlayer) {
        ConfigurationSection section = nicknameData.getConfigurationSection(offlinePlayer.getUniqueId().toString());
        if (section == null) return null;
        return section.getString("current", null);
    }

    @Override
    public boolean setNickname(OfflinePlayer offlinePlayer, String nickname) {
        ConfigurationSection section = nicknameData.getConfigurationSection(offlinePlayer.getUniqueId().toString());
        if (section == null) section = nicknameData.createSection(offlinePlayer.getUniqueId().toString());
        section.set("current", nickname);
        saveData();
        return true;
    }

    @Override
    public boolean saveNickname(OfflinePlayer offlinePlayer, String nickname) {
        ConfigurationSection section = nicknameData.getConfigurationSection(offlinePlayer.getUniqueId().toString());
        if (section == null) section = nicknameData.createSection(offlinePlayer.getUniqueId().toString());
        List<String> savedNicknames = section.getStringList("saved");
        if (savedNicknames.contains(nickname)) return true;
        savedNicknames.add(nickname);
        section.set("saved", savedNicknames);
        saveData();
        return true;
    }

    @Override
    public boolean deleteNickname(OfflinePlayer offlinePlayer, String nickname) {
        List<String> nicknames = getSavedNicknames(offlinePlayer);
        ConfigurationSection section = nicknameData.getConfigurationSection(offlinePlayer.getUniqueId().toString());
        if (section == null) return false;
        if (!nicknames.contains(nickname)) return false;
        nicknames.remove(nickname);
        section.set("saved", nicknames);
        saveData();
        return true;
    }

    @Override
    public boolean resetNickname(OfflinePlayer offlinePlayer) {
        ConfigurationSection section = nicknameData.getConfigurationSection(offlinePlayer.getUniqueId().toString());
        if (section == null) section = nicknameData.createSection(offlinePlayer.getUniqueId().toString());
        section.set("current", null);
        saveData();
        return true;
    }

    @Override
    public List<String> getSavedNicknames(OfflinePlayer offlinePlayer) {
        ConfigurationSection section = nicknameData.getConfigurationSection(offlinePlayer.getUniqueId().toString());
        if (section == null) return new ArrayList<>();
        return section.getStringList("saved");
    }

    private boolean saveData() {
        try {
            nicknameData.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
