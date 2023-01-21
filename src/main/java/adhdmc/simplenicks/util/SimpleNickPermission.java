package adhdmc.simplenicks.util;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public enum SimpleNickPermission {
    //Nickname Perms
    NICK_COLOR("simplenick.nick.color", StandardTags.color()),
    NICK_GRADIENT("simplenick.nick.gradient", StandardTags.gradient()),
    NICK_RAINBOW("simplenick.nick.rainbow", StandardTags.rainbow()),
    NICK_FORMAT_RESET("simplenick.nick.format.reset", StandardTags.reset()),
    NICK_UNDERLINE("simplenick.nick.format.underline", StandardTags.decorations(TextDecoration.UNDERLINED)),
    NICK_ITALIC("simplenick.nick.format.italic", StandardTags.decorations(TextDecoration.ITALIC)),
    NICK_STRIKETHROUGH("simplenick.nick.format.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH)),
    NICK_BOLD("simplenick.nick.format.bold", StandardTags.decorations(TextDecoration.BOLD)),
    NICK_OBFUSCATED("simplenick.nick.format.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED)),
    //Command Perms
    NICK_COMMAND("simplenick.nick.set", null),
    NICK_OTHERS_RESTRICTIVE("simplenick.admin.restrictive", null),
    NICK_OTHERS_BASIC("simplenick.admin.basic", null),
    NICK_OTHERS_FULL("simplenick.admin.full", null),
    NICK_RESET_OTHERS("simplenick.admin.reset", null),
    NICK_RESET("simplenick.nick.reset", null),
    NICK_SAVE("simplenick.save", null),
    NICK_DELETE("simplenick.delete", null),
    NICK_RELOAD("simplenick.reload", null),
    NICK_USERNAME_BYPASS("simplenick.usernamebypass", null);

    private final String permission;
    private final TagResolver resolver;

    SimpleNickPermission(String permission, TagResolver resolver) {
        this.permission = permission;
        this.resolver = resolver;
    }

    SimpleNickPermission(String permission) {
        this.permission = permission;
        this.resolver = null;
    }

    public String getPermission() {
        return permission;
    }

    public TagResolver getTagResolver() {
        return resolver;
    }
}
