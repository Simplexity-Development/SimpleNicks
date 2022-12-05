package adhdmc.simplenicks.util;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public enum SimpleNickPermission {
    //Nickname Perms
    NICK_COLOR("simplenick.nick.color", StandardTags.color()),
    NICK_GRADIENT("simplenick.nick.gradient", StandardTags.gradient()),
    NICK_RAINBOW("simplenick.nick.rainbow", StandardTags.rainbow()),
    NICK_UNDERLINE("simplenick.nick.format.underline", StandardTags.decorations(TextDecoration.UNDERLINED)),
    NICK_ITALIC("simplenick.nick.format.italic", StandardTags.decorations(TextDecoration.ITALIC)),
    NICK_STRIKETHROUGH("simplenick.nick.format.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH)),
    NICK_BOLD("simplenick.nick.format.bold", StandardTags.decorations(TextDecoration.BOLD)),
    NICK_OBFUSCATED("simplenick.nick.format.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED)),
    //Command Perms
    NICK_COMMAND("simplenick.nick.set"),
    NICK_ADMIN("simplenick.admin"),
    NICK_RESET("simplenick.nick.reset"),
    NICK_RELOAD("simplenick.reload"),
    NICK_USERNAME_BYPASS("simplenick.usernamebypass");

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
