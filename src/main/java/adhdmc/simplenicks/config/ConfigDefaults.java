package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.subcommands.Set;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfigDefaults {

    private static final HashMap<String, TagResolver> nickFormatPerms = new HashMap<>();
    private static final HashMap<Permission, String> allPerms = new HashMap<>();
    public enum SimpleNickPermission {
        //Nickname Perms
        NICK_COLOR("simplenick.nick.color"),
        NICK_GRADIENT("simplenick.nick.gradient"),
        NICK_RAINBOW("simplenick.nick.rainbow"),
        NICK_FORMAT("simplenick.nick.format"),
        NICK_UNDERLINE("simplenick.nick.format.underline"),
        NICK_ITALIC("simplenick.nick.format.italic"),
        NICK_STRIKETHROUGH("simplenick.nick.format.strikethrough"),
        NICK_BOLD("simplenick.nick.format.bold"),
        NICK_OBFUSCATED("simplenick.nick.format.obfuscated"),
        //Command Perms
        NICK_COMMAND("simplenick.nick"),
        NICK_ADMIN("simplenick.admin"),
        NICK_RESET("simplenick.nick.reset"),
        NICK_RELOAD("simplenick.reload");

        private String permission;
        SimpleNickPermission(String permission) {
            this.permission = permission;
        }

        public String getPermission() {
            return permission;
        }
    }

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

        public String getMessage() { return this.message; }
        private void setMessage(String message) { this.message = message; }
    }

    public static void loadLocaleMessages(){
        FileConfiguration locale = SimpleNicks.getLocale().getLocaleConfig();
        for (Message m : Message.values()) {
            if (m == Message.VERSION) { continue; }
            String localeOption = m.toString().toLowerCase(Locale.ENGLISH).replace('_','-');
            String message = locale.getString(localeOption, null);
            if (message == null) { continue; }
            m.setMessage(message);
        }
    }

    public static void localeDefaults(){
        FileConfiguration locale = SimpleNicks.getLocale().getLocaleConfig();
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

    //TODO See if there's a way to put the Permission -> tag map in the enum declaration -RhythmicSys
    public static void setFormatPerms(){
        nickFormatPerms.put(SimpleNickPermission.NICK_COLOR.getPermission(), StandardTags.color());
        nickFormatPerms.put(SimpleNickPermission.NICK_GRADIENT.getPermission(), StandardTags.gradient());
        nickFormatPerms.put(SimpleNickPermission.NICK_RAINBOW.getPermission(), StandardTags.rainbow());
        nickFormatPerms.put(SimpleNickPermission.NICK_FORMAT.getPermission(), StandardTags.decorations());
        nickFormatPerms.put(SimpleNickPermission.NICK_UNDERLINE.getPermission(), StandardTags.decorations(TextDecoration.UNDERLINED));
        nickFormatPerms.put(SimpleNickPermission.NICK_ITALIC.getPermission(), StandardTags.decorations(TextDecoration.ITALIC));
        nickFormatPerms.put(SimpleNickPermission.NICK_STRIKETHROUGH.getPermission(), StandardTags.decorations(TextDecoration.STRIKETHROUGH));
        nickFormatPerms.put(SimpleNickPermission.NICK_BOLD.getPermission(), StandardTags.decorations(TextDecoration.BOLD));
        nickFormatPerms.put(SimpleNickPermission.NICK_OBFUSCATED.getPermission(), StandardTags.decorations(TextDecoration.OBFUSCATED));
    }

    public static Map<String, TagResolver> getNickPerms(){
        return Collections.unmodifiableMap(nickFormatPerms);
    }

}
