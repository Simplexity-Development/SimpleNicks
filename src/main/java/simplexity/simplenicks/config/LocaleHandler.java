package simplexity.simplenicks.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import simplexity.simplenicks.SimpleNicks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"CallToPrintStackTrace", "CollectionAddAllCanBeReplacedWithConstructor", "ResultOfMethodCallIgnored"})
public class LocaleHandler {
    private static LocaleHandler instance;
    private final String fileName = "locale.yml";
    private final File dataFile = new File(SimpleNicks.getInstance().getDataFolder(), fileName);
    private FileConfiguration locale = new YamlConfiguration();

    private LocaleHandler() {
        try {
            dataFile.getParentFile().mkdirs();
            dataFile.createNewFile();
            reloadLocale();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LocaleHandler getInstance() {
        if (instance == null) {
            instance = new LocaleHandler();
        }
        return instance;
    }

    public void reloadLocale() {
        try {
            locale.load(dataFile);
            populateLocale();
            sortLocale();
            saveLocale();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    private void populateLocale() {
        Set<LocaleMessage> missing = new HashSet<>(Arrays.asList(LocaleMessage.values()));
        for (LocaleMessage localeMessage : LocaleMessage.values()) {
            if (locale.contains(localeMessage.getPath())) {
                localeMessage.setMessage(locale.getString(localeMessage.getPath()));
                missing.remove(localeMessage);
            }
        }

        for (LocaleMessage localeMessage : missing) {
            locale.set(localeMessage.getPath(), localeMessage.getMessage());
        }


    }

    private void sortLocale() {
        FileConfiguration newLocale = new YamlConfiguration();
        List<String> keys = new ArrayList<>();
        keys.addAll(locale.getKeys(true));
        Collections.sort(keys);
        for (String key : keys) {
            newLocale.set(key, locale.getString(key));
        }
        locale = newLocale;
    }

    private void saveLocale() {
        try {
            locale.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
