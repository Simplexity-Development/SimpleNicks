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

    private static final HashMap<String, TagResolver> nickFormatPerms = new HashMap<>();
    private static final HashMap<Permission, String> allPerms = new HashMap<>();
    public static void localeDefaults(){
        FileConfiguration locale = SimpleNicks.getLocale().getLocaleConfig();
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
