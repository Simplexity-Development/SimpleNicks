package simplexity.simplenicks.config;

import org.slf4j.Logger;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.platform.ConfigProvider;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConfigHandler {

    private static ConfigHandler instance;

    private Pattern regex;
    private boolean mySql, tablistNick, usernameProtection, onlineNickProtection, offlineNickProtection, debugMode,
            nickRequiresPermission, colorRequiresPermission, formatRequiresPermission, whoRequiresPermission;
    private int maxLength, maxSaves;
    private static final int MILLI_PER_DAY = 86_400_000;
    private String regexString, nickPrefix, mySqlIp, mySqlName, mySqlUsername, mySqlPassword;
    private long usernameProtectionTime, offlineNickProtectionTime = 0;

    private ConfigHandler() {
    }

    public static ConfigHandler getInstance() {
        if (instance != null) return instance;
        instance = new ConfigHandler();
        return instance;
    }

    private static Logger logger() {
        return SimpleNicksCore.get().platform().getLogger();
    }

    public void reloadConfig() {
        ConfigProvider config = SimpleNicksCore.get().platform().getConfigProvider();
        config.reload();
        LocaleHandler.getInstance().reloadLocale();
        try {
            String regexSetting = config.getString("nickname-regex", "[A-Za-z0-9_]+");
            regexString = regexSetting;
            regex = Pattern.compile(regexSetting);
        } catch (PatternSyntaxException e) {
            logger().error(LocaleMessage.ERROR_INVALID_CONFIG_REGEX.getMessage(), e);
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
        usernameProtection = config.getBoolean("nickname-protection.username.enabled", true);
        usernameProtectionTime = config.getLong("nickname-protection.username.expires", 30) * MILLI_PER_DAY;
        nickPrefix = config.getString("nickname-prefix", "");
        onlineNickProtection = config.getBoolean("nickname-protection.online.enabled", false);
        offlineNickProtection = config.getBoolean("nickname-protection.offline.enabled", false);
        offlineNickProtectionTime = config.getLong("nickname-protection.offline.expires", 30) * MILLI_PER_DAY;
    }

    public Pattern getRegex() {
        return regex;
    }

    public String getRegexString() {
        return regexString;
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

    public boolean isUsernameProtection() {
        return usernameProtection;
    }

    public String getNickPrefix() {
        return nickPrefix;
    }
}
