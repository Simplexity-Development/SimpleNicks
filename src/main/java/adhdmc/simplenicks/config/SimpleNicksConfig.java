package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.util.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SimpleNicksConfig {
    private static SimpleNicksConfig instance;
    private final SimpleNicks pluginInstance;
    private final String configName = "config.yml";
    private YamlConfiguration simpleNickConfig = null;
    private File configFile = null;
    private static int MAX_NICKNAME_LENGTH;
    private static String NICKNAME_REGEX;

    private SimpleNicksConfig() {
        this.pluginInstance = (SimpleNicks) SimpleNicks.getInstance();
    }

    public static SimpleNicksConfig getInstance() {
        if (instance == null) {
            instance = new SimpleNicksConfig();
            instance.getSimpleNickConfig();
            Defaults.setConfigDefaults();
            instance.reloadConfigValues();
            instance.saveConfig();
        }
        return instance;
    }

    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.pluginInstance.getDataFolder(), configName);
        }
        this.simpleNickConfig = YamlConfiguration.loadConfiguration(this.configFile);
        this.simpleNickConfig.options().copyDefaults(true);
        InputStream defaultStream = this.pluginInstance.getResource(configName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.simpleNickConfig.setDefaults(defaultConfig);
        }
    }

    public YamlConfiguration getSimpleNickConfig() {
        if (this.simpleNickConfig == null) {
            reloadConfig();
        }
        return this.simpleNickConfig;
    }

    public void saveConfig() {
        getSimpleNickConfig();
        if (this.simpleNickConfig == null || this.configFile == null) {
            return;
        }
        try {
            this.getSimpleNickConfig().save(this.configFile);
        } catch (IOException e) {
            pluginInstance.getLogger().severe("[saveConfig()] Could not save config to " + this.configFile);
            e.printStackTrace();
        }
        if (!this.configFile.exists()) {
            this.pluginInstance.saveResource(configName, false);
        }
    }

    public void reloadConfigValues(){
        setMaxNicknameLength(0);
        setNicknameRegex("");
        int configuredLength = SimpleNicks.getInstance().getConfig().getInt("max-nickname-length");
        String configuredREGEX = SimpleNicks.getInstance().getConfig().getString("nickname-regex");
        try{
            setMaxNicknameLength(configuredLength);
        }catch (IllegalArgumentException | NullPointerException e){
            SimpleNicks.getSimpleNicksLogger().warning("Configured max-nickname-length is invalid. Please check your config. Setting max length to 30");
            setMaxNicknameLength(30);
        }
        if (configuredREGEX == null || configuredREGEX.isEmpty()) {
            SimpleNicks.getSimpleNicksLogger().warning("You must provide a REGEX for valid nicknames, setting to default of [A-Za-z0-9_]+");
            setNicknameRegex("[A-Za-z0-9_]+");
        } else {
            setNicknameRegex(configuredREGEX);
        }
    }

    public static void setMaxNicknameLength(int maxNicknameLength) {
        MAX_NICKNAME_LENGTH = maxNicknameLength;
    }

    public static void setNicknameRegex(String nicknameRegex) {
        NICKNAME_REGEX = nicknameRegex;
    }

    public static String getNicknameRegex() {
        return NICKNAME_REGEX;
    }

    public static int getMaxNicknameLength() {
        return MAX_NICKNAME_LENGTH;
    }
}

