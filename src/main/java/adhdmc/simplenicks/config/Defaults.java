package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Defaults {

    private static final HashMap<Permission, TagResolver> nickFormatPerms = new HashMap<>();
    private static final HashMap<Permission, String> allPerms = new HashMap<>();
    public static void localeDefaults(){
        FileConfiguration locale = SimpleNicks.getLocale().getlocaleConfig();
        locale.addDefault("invalid-command", "<red>Invalid command, please check that you have spelled everything correctly");
        locale.addDefault("no-permission", "<red>You do not have permission to run this command");
        locale.addDefault("console-cannot-run", "Error: This command can only be run by a player");
        locale.addDefault("invalid-player", "<red>Invalid player specified");
        locale.addDefault("invalid-nick", "<red>That nickname is invalid");
        locale.addDefault("prefix", "<dark_aqua>[SimpleNicks] ");
        locale.addDefault("nick-changed-self", "<green>Name changed to <nickname>");
        locale.addDefault("nick-changed-other", "<green><username>'s nickname changed to <nickname>");
        locale.addDefault("nick-reset-self", "<white><u>Nickname reset");
        locale.addDefault("nick-reset-other", "<white><u><username>'s nickname reset");
    }

    public enum Permission {
        //Nickname Perms
        NICK_COLOR, NICK_GRADIENT, NICK_RAINBOW,
        NICK_FORMAT, NICK_UNDERLINE, NICK_ITALIC, NICK_STRIKETHROUGH, NICK_BOLD, NICK_OBFUSCATED,
        //Command Perms
        NICK_COMMAND, NICK_COMMAND_OTHERS, NICK_COMMAND_RESET, NICK_COMMAND_RESET_OTHERS, NICK_RELOAD
    }

    public static void setPluginPerms(){
        allPerms.put(Permission.NICK_COLOR, "justnick.nick.color");
        allPerms.put(Permission.NICK_GRADIENT, "justnick.nick.gradient");
        allPerms.put(Permission.NICK_RAINBOW, "justnick.nick.rainbow");
        allPerms.put(Permission.NICK_FORMAT, "justnick.nick.format");
        allPerms.put(Permission.NICK_UNDERLINE, "justnick.nick.format.underline");
        allPerms.put(Permission.NICK_ITALIC, "justnick.nick.format.italic");
        allPerms.put(Permission.NICK_STRIKETHROUGH, "justnick.nick.format.strikethrough");
        allPerms.put(Permission.NICK_BOLD, "justnick.nick.format.bold");
        allPerms.put(Permission.NICK_OBFUSCATED, "justnick.nick.format.obfuscated");
        allPerms.put(Permission.NICK_COMMAND, "justnick.nick");
        allPerms.put(Permission.NICK_COMMAND_OTHERS, "justnick.nickothers");
        allPerms.put(Permission.NICK_COMMAND_RESET, "justnick.nick.reset");
        allPerms.put(Permission.NICK_COMMAND_RESET_OTHERS, "justnick.nickothers.reset");
        allPerms.put(Permission.NICK_RELOAD, "justnick.reload");
    }

    public static void setFormatPerms(){
        nickFormatPerms.put(Permission.NICK_COLOR, StandardTags.color());
        nickFormatPerms.put(Permission.NICK_GRADIENT, StandardTags.gradient());
        nickFormatPerms.put(Permission.NICK_RAINBOW, StandardTags.rainbow());
        nickFormatPerms.put(Permission.NICK_FORMAT, StandardTags.decorations());
        nickFormatPerms.put(Permission.NICK_UNDERLINE, StandardTags.decorations(TextDecoration.UNDERLINED));
        nickFormatPerms.put(Permission.NICK_ITALIC, StandardTags.decorations(TextDecoration.ITALIC));
        nickFormatPerms.put(Permission.NICK_STRIKETHROUGH, StandardTags.decorations(TextDecoration.STRIKETHROUGH));
        nickFormatPerms.put(Permission.NICK_BOLD, StandardTags.decorations(TextDecoration.BOLD));
        nickFormatPerms.put(Permission.NICK_OBFUSCATED, StandardTags.decorations(TextDecoration.OBFUSCATED));
    }

    public static Map<Permission, TagResolver> getNickPerms(){
        return Collections.unmodifiableMap(nickFormatPerms);
    }

    public static Map<Permission, String> getAllPerms(){
        return Collections.unmodifiableMap(allPerms);
    }
}
