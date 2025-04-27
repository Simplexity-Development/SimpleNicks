package simplexity.simplenicks.config;

import org.bukkit.configuration.file.FileConfiguration;
import simplexity.simplenicks.SimpleNicks;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConfigHandler {

    public String getRegexString() {
        return regexString;
    }

    public String getNickPrefix() {
        return nickPrefix;
    }

    public enum SAVING_TYPE {PDC, FILE}

    private static ConfigHandler instance;

    private final Logger logger = SimpleNicks.getSimpleNicksLogger();
    private Pattern regex = Pattern.compile("[A-Za-z0-9_]+");
    private SAVING_TYPE savingType = SAVING_TYPE.FILE;
    private int maxLength = 25;
    private int maxSaves = 5;
    private boolean tablistNick = false;
    private String regexString = "[A-Za-z0-9_]+";
    private String nickPrefix;
    private long usernameProtectionTime = 0;

    private ConfigHandler() {
    }

    public static ConfigHandler getInstance() {
        if (instance != null) return instance;
        instance = new ConfigHandler();
        return instance;
    }

    public void reloadConfig() {
        SimpleNicks.getInstance().reloadConfig();
        LocaleHandler.getInstance().loadLocale();
        FileConfiguration config = SimpleNicks.getInstance().getConfig();
        // Check the validity of the regex.
        try {
            String regexSetting = config.getString("nickname-regex", "[A-Za-z0-9_]+");
            regexString = regexSetting;
            assert !regexSetting.isBlank();
            regex = Pattern.compile(regexSetting);
        } catch (PatternSyntaxException e) {
            logger.severe(LocaleHandler.getInstance().getInvalidConfigRegex());
        }
        // Check validity of saving-type.
        try {
            String savingTypeSetting = config.getString("saving-type", "file");
            savingType = SAVING_TYPE.valueOf(savingTypeSetting.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.severe("INVALID SAVING TYPE");
        }
        maxLength = config.getInt("max-nickname-length", 25);
        maxSaves = config.getInt("max-saves", 5);
        tablistNick = config.getBoolean("tablist-nick", false);
        usernameProtectionTime = config.getLong("username-protection", 30) * 86400000;
        nickPrefix = config.getString("nickname-prefix", "");
    }


    public Pattern getRegex() {
        return regex;
    }

    public SAVING_TYPE getSavingType() {
        return savingType;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMaxSaves() {
        return maxSaves;
    }

    public boolean shouldNickTablist() {
        return tablistNick;
    }

    public long getUsernameProtectionTime() {
        return usernameProtectionTime;
    }
}
