package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Locale {
    private final SimpleNicks instance;
    private static final String localeName = "locale.yml";
    private YamlConfiguration localeConfig = null;
    private File localeFile = null;

    public Locale(SimpleNicks instance) {
        this.instance = instance;
    }

    public void reloadConfig() {
        if (this.localeFile == null) {
            this.localeFile = new File(this.instance.getDataFolder(), localeName);
        }
        this.localeConfig = YamlConfiguration.loadConfiguration(this.localeFile);
        this.localeConfig.options().copyDefaults(true);
        InputStream defaultStream = this.instance.getResource(localeName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.localeConfig.setDefaults(defaultConfig);
        }
    }

    public YamlConfiguration getlocaleConfig() {
        if (this.localeConfig == null) {
            reloadConfig();
        }
        return this.localeConfig;
    }

    public void saveConfig() {
        getlocaleConfig();
        if (this.localeConfig == null || this.localeFile == null) {
            return;
        }
        try {
            this.getlocaleConfig().save(this.localeFile);
        } catch (IOException e) {
            instance.getLogger().severe("[saveConfig()] Could not save config to " + this.localeFile);
            e.printStackTrace();
        }
        if (!this.localeFile.exists()) {
            this.instance.saveResource(localeName, false);
        }
    }
}

