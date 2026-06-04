package simplexity.simplenicks.util;

import org.jetbrains.annotations.NotNull;

public enum NickPermission {
    NICK_ADMIN("simplenick.admin", "op"),
    NICK_ADMIN_SET("simplenick.admin.set", "op"),
    NICK_ADMIN_RESET("simplenick.admin.reset", "op"),
    NICK_ADMIN_DELETE("simplenick.admin.delete", "op"),
    NICK_ADMIN_LOOKUP("simplenick.admin.lookup", "op"),
    NICK_COMMAND("simplenick.nick", "true"),
    NICK_SET("simplenick.nick.set", "op"),
    NICK_SAVE("simplenick.nick.save", "op"),
    NICK_WHO("simplenick.nick.who", "true"),
    NICK_HELP("simplenick.nick.help", "true"),
    NICK_BYPASS_USERNAME("simplenick.bypass.username", "false"),
    NICK_BYPASS_LENGTH("simplenick.bypass.length", "false"),
    NICK_BYPASS_REGEX("simplenick.bypass.regex", "false"),
    NICK_BYPASS_NICK_PROTECTION("simplenick.bypass.nick-protection", "false"),
    NICK_RELOAD("simplenick.reload", "op");

    private final String permissionKey;
    private final String permissionDefault;

    NickPermission(@NotNull String permissionKey, @NotNull String permissionDefault) {
        this.permissionKey = permissionKey;
        this.permissionDefault = permissionDefault;
    }

    @NotNull
    public String getPermissionKey() {
        return permissionKey;
    }

    /**
     * Returns the default grant level: {@code "op"}, {@code "true"}, or {@code "false"}.
     *
     * @return the permission default string
     */
    @NotNull
    public String getPermissionDefault() {
        return permissionDefault;
    }
}
