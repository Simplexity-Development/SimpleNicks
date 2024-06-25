package simplexity.simplenicks.util;

import org.bukkit.permissions.Permission;

public class Constants {
    public static Permission NICK_COMMAND = new Permission("simplenick.nick.set");
    public static Permission NICK_OTHERS_RESTRICTIVE = new Permission("simplenick.admin.restrictive");
    public static Permission NICK_OTHERS_BASIC = new Permission("simplenick.admin.basic");
    public static Permission NICK_OTHERS_FULL = new Permission("simplenick.admin.full");
    public static Permission NICK_RESET_OTHERS = new Permission("simplenick.admin.reset");
    public static Permission NICK_RESET = new Permission("simplenick.nick.reset");
    public static Permission NICK_SAVE = new Permission("simplenick.save");
    public static Permission NICK_DELETE = new Permission("simplenick.delete");
    public static Permission NICK_RELOAD = new Permission("simplenick.reload");
    public static Permission NICK_USERNAME_BYPASS = new Permission("simplenick.usernamebypass");
}
