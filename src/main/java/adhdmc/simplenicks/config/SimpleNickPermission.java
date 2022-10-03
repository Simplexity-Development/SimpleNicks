package adhdmc.simplenicks.config;

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
    NICK_COMMAND_OTHERS("simplenick.nickothers"),
    NICK_COMMAND_RESET("simplenick.nick.reset"),
    NICK_COMMAND_RESET_OTHERS("simplenick.nickothers.reset"),
    NICK_RELOAD("simplenick.reload");

    private String permission;
    SimpleNickPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}

