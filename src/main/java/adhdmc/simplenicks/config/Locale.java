package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.subcommands.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Locale {
    private static Locale instance;
    private final SimpleNicks pluginInstance;
    private final String localeName = "locale.yml";
    private YamlConfiguration localeConfig = null;
    private File localeFile = null;

    public enum Message {
        //errors
        INVALID_COMMAND("<red>Invalid Command."),
        NO_ARGUMENTS("<red>No arguments provided."),
        TOO_MANY_ARGUMENTS("<red>Too many arguments provided."),
        NO_PERMISSION("<red>You do not have permission to run this command"),
        CONSOLE_CANNOT_RUN("<red>This command cannot be run on the Console."),
        INVALID_PLAYER("<red>Invalid player specified"),
        INVALID_NICK_REGEX("<red>Not a valid nickname, must follow regex: " + Set.NICKNAME_REGEX),
        INVALID_NICK_TOO_LONG("<red>Nickname is too long, must be <=" + Set.MAX_NICKNAME_LENGTH),
        //plugin messages
        PREFIX("<aqua>SimpleNicks <white>» "),
        NICK_CHANGED_SELF("<green>Changed your own nickname to <nickname>!"),
        NICK_CHANGED_OTHER("<green>Changed <username>'s nickname to <nickname>"),
        NICK_RESET_SELF("<green>Reset your own nickname!"),
        NICK_RESET_OTHER("<green>Reset <username>'s nickname."),
        //other
        VERSION("yes");

        String message;

        Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        private void setMessage(String message) {
            this.message = message;
        }
    }

    private Locale() {
        this.pluginInstance = (SimpleNicks) SimpleNicks.getInstance();
    }

    public static Locale getInstance() {
        if (instance == null) {
            instance = new Locale();
            instance.getLocaleConfig();
            instance.setLocaleDefaults();
            instance.saveConfig();
            instance.loadLocaleMessages();
        }
        return instance;
    }

    public void reloadConfig() {
        if (this.localeFile == null) {
            this.localeFile = new File(this.pluginInstance.getDataFolder(), localeName);
        }
        this.localeConfig = YamlConfiguration.loadConfiguration(this.localeFile);
        this.localeConfig.options().copyDefaults(true);
        InputStream defaultStream = this.pluginInstance.getResource(localeName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.localeConfig.setDefaults(defaultConfig);
        }
    }

    public YamlConfiguration getLocaleConfig() {
        if (this.localeConfig == null) {
            reloadConfig();
        }
        return this.localeConfig;
    }

    public void saveConfig() {
        getLocaleConfig();
        if (this.localeConfig == null || this.localeFile == null) {
            return;
        }
        try {
            this.getLocaleConfig().save(this.localeFile);
        } catch (IOException e) {
            pluginInstance.getLogger().severe("[saveConfig()] Could not save config to " + this.localeFile);
            e.printStackTrace();
        }
        if (!this.localeFile.exists()) {
            this.pluginInstance.saveResource(localeName, false);
        }
    }

    public void loadLocaleMessages() {
        FileConfiguration locale = getLocaleConfig();
        for (Message m : Message.values()) {
            if (m == Message.VERSION) {
                continue;
            }
            String localeOption = m.toString().toLowerCase(java.util.Locale.ENGLISH).replace('_', '-');
            String message = locale.getString(localeOption, null);
            if (message == null) {
                continue;
            }
            m.setMessage(message);
        }
    }

    public void setLocaleDefaults() {
        FileConfiguration locale = getLocaleConfig();
        locale.addDefault("invalid-command", "<red>Invalid command.");
        locale.addDefault("no-arguments", "<red>No arguments provided.");
        locale.addDefault("too-many-arguments", "<red>Too many arguments provided.");
        locale.addDefault("no-permission", "<red>You do not have permission to run this command");
        locale.addDefault("console-cannot-run", "<red>This command cannot be run on the Console.");
        locale.addDefault("invalid-player", "<red>Invalid player specified");
        locale.addDefault("invalid-nick-regex", "<red>Not a valid nickname, must follow regex: ");
        locale.addDefault("invalid-nick-too-long", "<red>Nickname is too long, must be <=");
        locale.addDefault("prefix", "<aqua>SimpleNicks <white>» ");
        locale.addDefault("nick-changed-self", "<green>Changed your own nickname to <nickname>!");
        locale.addDefault("nick-changed-other", "<green>Changed <username>'s nickname to <nickname>");
        locale.addDefault("nick-reset-self", "<green>Reset your own nickname!");
        locale.addDefault("nick-reset-other", "<green>Reset <username>'s nickname.");
    }
}

