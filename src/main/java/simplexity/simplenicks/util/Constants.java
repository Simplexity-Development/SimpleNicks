package simplexity.simplenicks.util;

import org.bukkit.permissions.Permission;

public class Constants {
    public static Permission NICK_OTHERS_COMMAND = new Permission("simplenick.admin");
    public static Permission NICK_SET_OTHERS = new Permission("simplenick.admin.set");
    public static Permission NICK_RESET_OTHERS = new Permission("simplenick.admin.reset");
    public static Permission NICK_GET_OTHERS = new Permission("simplenick.admin.get");
    public static Permission NICK_COMMAND = new Permission("simplenick.nick");
    public static Permission NICK_SET = new Permission("simplenick.nick.set");
    public static Permission NICK_SAVE = new Permission("simplenick.nick.save");
    public static Permission NICK_USERNAME_BYPASS = new Permission("simplenick.bypass.username");
    public static Permission NICK_LENGTH_BYPASS = new Permission("simplenick.bypass.length");
    public static Permission NICK_REGEX_BYPASS = new Permission("simplenick.bypass.regex");
    public static Permission NICK_PROTECTION_BYPASS = new Permission("simplenick.bypass.nick-protection");
    public static Permission NICK_RELOAD = new Permission("simplenick.reload");
}
