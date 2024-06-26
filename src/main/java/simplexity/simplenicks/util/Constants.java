package simplexity.simplenicks.util;

import org.bukkit.permissions.Permission;

public class Constants {
    public static Permission NICK_OTHERS_COMMAND = new Permission("simplenick.admin");
    public static Permission NICK_OTHERS_RESTRICTIVE = new Permission("simplenick.admin.restrictive");
    public static Permission NICK_OTHERS_BASIC = new Permission("simplenick.admin.basic");
    public static Permission NICK_OTHERS_FULL = new Permission("simplenick.admin.full");
    public static Permission NICK_RESET_OTHERS = new Permission("simplenick.admin.reset");
    public static Permission NICK_OTHERS_SAVE = new Permission("simplenick.admin.save");
    public static Permission NICK_OTHERS_DELETE = new Permission("simplenick.admin.delete");
    public static Permission NICK_COMMAND = new Permission("simplenick.nick");
    public static Permission NICK_RESET = new Permission("simplenick.nick.reset");
    public static Permission NICK_SAVE = new Permission("simplenick.nick.save");
    public static Permission NICK_DELETE = new Permission("simplenick.nick.delete");
    public static Permission NICK_USERNAME_BYPASS = new Permission("simplenick.bypass.username");
    public static Permission NICK_LENGTH_BYPASS = new Permission("simplenick.bypass.length");
    public static Permission NICK_REGEX_BYPASS = new Permission("simplenick.bypass.regex");
}
