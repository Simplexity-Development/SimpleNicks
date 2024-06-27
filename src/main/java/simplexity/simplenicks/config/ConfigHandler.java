package simplexity.simplenicks.config;

import org.bukkit.configuration.file.FileConfiguration;
import simplexity.simplenicks.SimpleNicks;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConfigHandler {

    public String getRegexString() {
        return regexString;
    }

    public enum SAVING_TYPE {PDC, FILE}

    private static ConfigHandler instance;

    private Pattern regex = Pattern.compile("[A-Za-z0-9_]+");
    private SAVING_TYPE savingType = SAVING_TYPE.FILE;
    private int maxLength = 25;
    private int maxSaves = 5;
    private boolean tablistNick = false;
    private String regexString = "[A-Za-z0-9_]+";
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
        // Check the validity of the regex.
        try {
            String regexSetting = SimpleNicks.getInstance().getConfig().getString("nickname-regex");
            regexString = regexSetting;
            assert regexSetting != null;
            assert !regexSetting.isBlank();
            regex = Pattern.compile(regexSetting);
        } catch (AssertionError | PatternSyntaxException e) {
            SimpleNicks.getSimpleNicksLogger().severe(LocaleHandler.getInstance().getInvalidConfigRegex());
        }
        // Check validity of saving-type.
        try {
            String savingTypeSetting = SimpleNicks.getInstance().getConfig().getString("saving-type");
            assert savingTypeSetting != null;
            savingType = SAVING_TYPE.valueOf(savingTypeSetting.toUpperCase());
        } catch (AssertionError | IllegalArgumentException e) {
            SimpleNicks.getInstance().getLogger().severe("INVALID SAVING TYPE");
        }
        maxLength = SimpleNicks.getInstance().getConfig().getInt("max-nickname-length");
        maxSaves = SimpleNicks.getInstance().getConfig().getInt("max-saves");
        tablistNick = SimpleNicks.getInstance().getConfig().getBoolean("tablist-nick");
        usernameProtectionTime = SimpleNicks.getInstance().getConfig().getLong("username-protection-time") * 86400000;
    }

    public void setConfigDefaults() {
        FileConfiguration config = SimpleNicks.getInstance().getConfig();
        config.addDefault("saving-type", "file");
        config.addDefault("max-nickname-length", 25);
        config.addDefault("max-saves", 5);
        config.addDefault("nickname-regex", "[A-Za-z0-9_]+");
        config.addDefault("tablist-nick", false);
        config.addDefault("username-protection-time", 30);
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
