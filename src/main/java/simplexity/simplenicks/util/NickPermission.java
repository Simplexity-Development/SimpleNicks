package simplexity.simplenicks.util;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum NickPermission {
    // name, description, default, children
    NICK_ADMIN(new Permission("simplenick.admin", "Base permission for all admin commands", PermissionDefault.OP)),
    NICK_ADMIN_SET(new Permission("simplenick.admin.set", "Allows an admin to set another user's nickname", PermissionDefault.OP)),
    NICK_ADMIN_RESET(new Permission("simplenick.admin.reset", "Allows an admin to reset another user's nickname", PermissionDefault.OP)),
    NICK_ADMIN_DELETE(new Permission("simplenick.admin.delete", "Allows an admin to delete another user's saved nickname", PermissionDefault.OP)),
    NICK_ADMIN_LOOKUP(new Permission("simplenick.admin.lookup", "Allows an admin to look up someone's nickname and saved nicknames based off their username", PermissionDefault.OP)),
    NICK_COMMAND(new Permission("simplenick.nick", "Base permission for all nickname commands", PermissionDefault.TRUE)),
    NICK_SET(new Permission("simplenick.nick.set", "Allows someone to set their own nickname", PermissionDefault.OP)),
    NICK_SAVE(new Permission("simplenick.nick.save", "Allows someone to save nicknames", PermissionDefault.OP)),
    NICK_WHO(new Permission("simplenick.nick.who", "Allows someone to see the actual username of someone based on their nickname", PermissionDefault.TRUE)),
    NICK_BYPASS_USERNAME(new Permission("simplenick.bypass.username", "Allows a user to nickname themselves the same as someone else's username on this server", PermissionDefault.FALSE)),
    NICK_BYPASS_LENGTH(new Permission("simplenick.bypass.length", "Allows a user to bypass the configured max length of a nickname", PermissionDefault.FALSE)),
    NICK_BYPASS_REGEX(new Permission("simplenick.bypass.regex", "Allows a user to bypass the configured regex", PermissionDefault.FALSE)),
    NICK_BYPASS_NICK_PROTECTION(new Permission("simplenick.bypass.nick-protection", "Allows a user to nickname themselves the same nickname as another user", PermissionDefault.FALSE)),
    NICK_RELOAD(new Permission("simplenick.reload", "Allows a user to reload the config", PermissionDefault.OP));

    private final Permission permission;

    NickPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }

}
