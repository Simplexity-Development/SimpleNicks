package simplexity.simplenicks.util;

import org.bukkit.permissions.Permission;

public class Constants {
    public static Permission NICK_ADMIN = new Permission("simplenick.admin");
    public static Permission NICK_ADMIN_SET = new Permission("simplenick.admin.set");
    public static Permission NICK_ADMIN_RESET = new Permission("simplenick.admin.reset");
    public static Permission NICK_ADMIN_DELETE = new Permission("simplenick.admin.delete");
    public static Permission NICK_ADMIN_LOOKUP = new Permission("simplenick.admin.lookup");
    public static Permission NICK_COMMAND = new Permission("simplenick.nick");
    public static Permission NICK_SET = new Permission("simplenick.nick.set");
    public static Permission NICK_SAVE = new Permission("simplenick.nick.save");
    public static Permission NICK_WHO = new Permission("simplenick.nick.who");
    public static Permission NICK_BYPASS_USERNAME = new Permission("simplenick.bypass.username");
    public static Permission NICK_BYPASS_LENGTH = new Permission("simplenick.bypass.length");
    public static Permission NICK_BYPASS_REGEX = new Permission("simplenick.bypass.regex");
    public static Permission NICK_BYPASS_NICK_PROTECTION = new Permission("simplenick.bypass.nick-protection");
    public static Permission NICK_RELOAD = new Permission("simplenick.reload");
}
