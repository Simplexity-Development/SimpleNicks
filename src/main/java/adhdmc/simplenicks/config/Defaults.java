package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import org.bukkit.configuration.file.FileConfiguration;

public class Defaults {
    public static void setLocaleDefaults() {
        FileConfiguration locale = Locale.getInstance().getLocaleConfig();
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

    public static void setConfigDefaults() {
        FileConfiguration config = SimpleNicks.getInstance().getConfig();
        config.addDefault("max-nickname-length", 30);
        config.addDefault("nickname-regex","[A-Za-z0-9_]+");
    }
}
