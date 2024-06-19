package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Config {

    public enum SAVING_TYPE { PDC, FILE }
    private static Config instance;

    private Pattern regex = Pattern.compile("[A-Za-z0-9_]+");
    private SAVING_TYPE savingType = SAVING_TYPE.FILE;
    private int maxLength = 25;
    private int maxSaves = 5;

    private Config() {}

    public static Config getInstance() {
        if (instance != null) return instance;
        instance = new Config();
        return instance;
    }

    public void reloadConfig() {
        SimpleNicks.getInstance().reloadConfig();
        LocaleHandler.getInstance().loadLocale();
        // Check the validity of the regex.
        try {
            String regexSetting = SimpleNicks.getInstance().getConfig().getString("nickname-regex");
            assert regexSetting != null;
            assert !regexSetting.isBlank();
            regex = Pattern.compile(regexSetting);
        }
        catch (AssertionError | PatternSyntaxException e) {
            SimpleNicks.getSimpleNicksLogger().severe(LocaleHandler.getInstance().getNoRegex());
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
    }

    public void setConfigDefaults() {
        FileConfiguration config = SimpleNicks.getInstance().getConfig();
        config.addDefault("saving-type","file");
        config.addDefault("max-nickname-length", 25);
        config.addDefault("max-saves", 5);
        config.addDefault("nickname-regex","[A-Za-z0-9_]+");
    }

    public Pattern getRegex() { return regex; }
    public SAVING_TYPE getSavingType() { return savingType; }
    public int getMaxLength() { return maxLength; }
    public int getMaxSaves() { return maxSaves; }
}
