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


    private static ConfigHandler instance;

    private final Logger logger = SimpleNicks.getSimpleNicksLogger();
    private Pattern regex;
    private boolean mySql, tablistNick, onlineNickProtection, offlineNickProtection, debugMode, nickRequiresPermission,
            colorRequiresPermission, formatRequiresPermission, whoRequiresPermission;
    private int maxLength, maxSaves;
    private String regexString, nickPrefix, mySqlIp, mySqlName, mySqlUsername, mySqlPassword;
    private long usernameProtectionTime, offlineNickProtectionTime = 0;

    private ConfigHandler() {
    }

    public static ConfigHandler getInstance() {
        if (instance != null) return instance;
        instance = new ConfigHandler();
        return instance;
    }

    public void reloadConfig() {
        SimpleNicks.getInstance().reloadConfig();
        LocaleHandler.getInstance().reloadLocale();
        FileConfiguration config = SimpleNicks.getInstance().getConfig();
        // Check the validity of the regex.
        try {
            String regexSetting = config.getString("nickname-regex", "[A-Za-z0-9_]+");
            regexString = regexSetting;
            regex = Pattern.compile(regexSetting);
        } catch (PatternSyntaxException e) {
            logger.severe(LocaleMessage.ERROR_INVALID_CONFIG_REGEX.getMessage());
        }
        debugMode = config.getBoolean("debug-mode", false);
        mySql = config.getBoolean("mysql.enabled", false);
        nickRequiresPermission = config.getBoolean("require-permission.nick", true);
        colorRequiresPermission = config.getBoolean("require-permission.color", true);
        formatRequiresPermission = config.getBoolean("require-permission.format", true);
        whoRequiresPermission = config.getBoolean("require-permission.who", false);
        mySqlIp = config.getString("mysql.ip", "localhost:3306");
        mySqlName = config.getString("mysql.name", "simplenicks");
        mySqlUsername = config.getString("mysql.username", "username1");
        mySqlPassword = config.getString("mysql.password", "badpassword!");
        maxLength = config.getInt("max-nickname-length", 25);
        maxSaves = config.getInt("max-saves", 5);
        tablistNick = config.getBoolean("tablist-nick", false);
        usernameProtectionTime = config.getLong("username-protection", 30) * 86400000;
        nickPrefix = config.getString("nickname-prefix", "");
        onlineNickProtection = config.getBoolean("nickname-protection.online.enabled", false);
        offlineNickProtection = config.getBoolean("nickname-protection.offline.enabled", false);
        offlineNickProtectionTime = config.getLong("nickname-protection.offline.expires", 30) * 86400000;
    }


    public Pattern getRegex() {
        return regex;
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

    public boolean isMySql() {
        return mySql;
    }

    public String getMySqlIp() {
        return mySqlIp;
    }

    public String getMySqlName() {
        return mySqlName;
    }

    public String getMySqlUsername() {
        return mySqlUsername;
    }

    public String getMySqlPassword() {
        return mySqlPassword;
    }

    public long getOfflineNickProtectionTime() {
        return offlineNickProtectionTime;
    }

    public boolean shouldOnlineNicksBeProtected() {
        return onlineNickProtection;
    }

    public boolean shouldOfflineNicksBeProtected() {
        return offlineNickProtection;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isNickRequiresPermission() {
        return nickRequiresPermission;
    }

    public boolean isColorRequiresPermission() {
        return colorRequiresPermission;
    }

    public boolean isFormatRequiresPermission() {
        return formatRequiresPermission;
    }

    public boolean isWhoRequiresPermission() {
        return whoRequiresPermission;
    }
}
