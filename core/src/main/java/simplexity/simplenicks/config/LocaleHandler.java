package simplexity.simplenicks.config;

import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.platform.ConfigProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("CallToPrintStackTrace")
public class LocaleHandler {

    private static LocaleHandler instance;

    private LocaleHandler() {
    }

    public static LocaleHandler getInstance() {
        if (instance == null) {
            instance = new LocaleHandler();
        }
        return instance;
    }

    public void reloadLocale() {
        ConfigProvider locale = SimpleNicksCore.get().platform().getLocaleProvider();
        locale.reload();
        populateLocale(locale);
        locale.save();
    }

    private void populateLocale(ConfigProvider locale) {
        Set<LocaleMessage> missing = new HashSet<>(Arrays.asList(LocaleMessage.values()));
        for (LocaleMessage localeMessage : LocaleMessage.values()) {
            if (locale.contains(localeMessage.getPath())) {
                localeMessage.setMessage(locale.getString(localeMessage.getPath(), localeMessage.getDefaultMessage()));
                missing.remove(localeMessage);
            }
        }
        for (LocaleMessage localeMessage : missing) {
            locale.set(localeMessage.getPath(), localeMessage.getDefaultMessage());
            localeMessage.setMessage(localeMessage.getDefaultMessage());
        }
    }
}
