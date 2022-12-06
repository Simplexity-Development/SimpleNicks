package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtils {
    private static int MAX_NICKNAME_LENGTH = 30;
    private static String NICKNAME_REGEX = "[A-Za-z0-9_]+";

    public static void reloadConfigValues(){
        MAX_NICKNAME_LENGTH = 30;
        NICKNAME_REGEX = "[A-Za-z0-9_]+";
        int configuredLength = SimpleNicks.getInstance().getConfig().getInt("max-nickname-length");
        String configuredREGEX = SimpleNicks.getInstance().getConfig().getString("nickname-regex");
        try{
            MAX_NICKNAME_LENGTH = configuredLength;
        }catch (IllegalArgumentException | NullPointerException e){
            SimpleNicks.getSimpleNicksLogger().warning("Configured max-nickname-length is invalid. Please check your config.");
            MAX_NICKNAME_LENGTH = 30;
        }
        NICKNAME_REGEX = configuredREGEX;
    }

    public static void loadConfigValues(){
        SimpleNicks.getInstance().getConfig();
        Defaults.setConfigDefaults();
        SimpleNicks.getInstance().saveDefaultConfig();
        reloadConfigValues();
    }

    public static String getNicknameRegex() {
        return NICKNAME_REGEX;
    }

    public static int getMaxNicknameLength() {
        return MAX_NICKNAME_LENGTH;
    }
}
