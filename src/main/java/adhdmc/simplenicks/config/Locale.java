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
        INVALID_COMMAND("<prefix><red>Invalid Command."),
        NO_ARGUMENTS("<prefix><red>No arguments provided."),
        TOO_MANY_ARGUMENTS("<prefix><red>Too many arguments provided."),
        CANNOT_NICK_USERNAME("<prefix><red>You cannot name yourself <name>, as that is the username of another player on this server. Pick another name"),
        NO_PERMISSION("<prefix><red>You do not have permission to run this command"),
        CONSOLE_CANNOT_RUN("<prefix><red>This command cannot be run on the Console."),
        INVALID_PLAYER("<prefix><red>Invalid player specified"),
        INVALID_NICK_REGEX("<prefix><red>Not a valid nickname, must follow regex: " + Set.NICKNAME_REGEX),
        INVALID_NICK_TOO_LONG("<prefix><red>Nickname is too long, must be <=" + Set.MAX_NICKNAME_LENGTH),
        INVALID_TAGS("<prefix><red>You have used a color or formatting tag you do not have permission to use. Please try again"),
        //plugin messages
        PREFIX("<aqua>SimpleNicks <white>» "),
        NICK_CHANGED_SELF("<prefix><green>Changed your own nickname to <nickname>!"),
        NICK_CHANGED_OTHER("<prefix><green>Changed <username>'s nickname to <nickname>"),
        NICK_RESET_SELF("<prefix><green>Reset your own nickname!"),
        NICK_RESET_OTHER("<prefix><green>Reset <username>'s nickname."),
        //other
        VERSION("<prefix>yes"),
        HELP_BASE("<prefix><green>--------"),
        HELP_SET("<aqua>· <yellow>Setting a nickname: \n   <gray>/nick set <nickname>"),
        HELP_RESET("<aqua>· <yellow>removing a nickname: \n   <gray>/nick reset"),
        HELP_MINIMESSAGE("<aqua>· <yellow>Formatting: \n   <gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>"),
        CONFIG_RELOADED("<prefix><gold>SimpleNicks config and locale reloaded");

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
        locale.addDefault("invalid-command", "<prefix><red>Invalid command.");
        locale.addDefault("no-arguments", "<prefix><red>No arguments provided.");
        locale.addDefault("too-many-arguments", "<prefix><red>Too many arguments provided.");
        locale.addDefault("cant-nick-username", "<prefix><red>You cannot name yourself <name>, as that is the username of another player on this server. Pick another name");
        locale.addDefault("no-permission", "<prefix><red>You do not have permission to run this command");
        locale.addDefault("console-cannot-run", "<prefix><red>This command cannot be run on the Console.");
        locale.addDefault("invalid-player", "<prefix><red>Invalid player specified");
        locale.addDefault("invalid-nick-regex", "<prefix><red>Not a valid nickname, must follow regex: ");
        locale.addDefault("invalid-nick-too-long", "<prefix><red>Nickname is too long, must be <=");
        locale.addDefault("invalid-tags", "<prefix><red>You have used a color or formatting tag you do not have permission to use. Please try again");
        locale.addDefault("prefix", "<aqua>SimpleNicks <white>» ");
        locale.addDefault("help-base", "<prefix><green>--------");
        locale.addDefault("help-set","<aqua>· <yellow>Setting a nickname: \n   <gray>/nick set <nickname>");
        locale.addDefault("help-reset","<aqua>· <yellow>removing a nickname: \n   <gray>/nick reset");
        locale.addDefault("help-minimessage", "<aqua>· <yellow>Formatting: \n   <gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>");
        locale.addDefault("config-reload", "<prefix><gold>SimpleNicks config and locale reloaded");
        locale.addDefault("nick-changed-self", "<prefix><green>Changed your own nickname to <nickname>!");
        locale.addDefault("nick-changed-other", "<prefix><green>Changed <username>'s nickname to <nickname>");
        locale.addDefault("nick-reset-self", "<prefix><green>Reset your own nickname!");
        locale.addDefault("nick-reset-other", "<prefix><green>Reset <username>'s nickname.");
    }
}

