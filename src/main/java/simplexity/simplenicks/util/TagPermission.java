package simplexity.simplenicks.util;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum TagPermission {
    //Nickname Perms
    HEX_COLOR(new Permission("simplenick.nick.color", PermissionDefault.OP), StandardTags.color()),
    GRADIENT(new Permission("simplenick.nick.gradient", PermissionDefault.OP), StandardTags.gradient()),
    RAINBOW(new Permission("simplenick.nick.rainbow", PermissionDefault.OP), StandardTags.rainbow()),
    RESET(new Permission("simplenick.nick.format.reset", PermissionDefault.OP), StandardTags.reset()),
    UNDERLINE(new Permission("simplenick.nick.format.underline", PermissionDefault.OP), StandardTags.decorations(TextDecoration.UNDERLINED)),
    ITALIC(new Permission("simplenick.nick.format.italic", PermissionDefault.OP), StandardTags.decorations(TextDecoration.ITALIC)),
    STRIKETHROUGH(new Permission("simplenick.nick.format.strikethrough", PermissionDefault.OP), StandardTags.decorations(TextDecoration.STRIKETHROUGH)),
    BOLD(new Permission("simplenick.nick.format.bold", PermissionDefault.OP), StandardTags.decorations(TextDecoration.BOLD)),
    OBFUSCATED(new Permission("simplenick.nick.format.obfuscated", PermissionDefault.OP), StandardTags.decorations(TextDecoration.OBFUSCATED));


    private final Permission permission;
    private final TagResolver resolver;

    TagPermission(Permission permission, TagResolver resolver) {
        this.permission = permission;
        this.resolver = resolver;
    }

    public Permission getPermission() {
        return permission;
    }

    public TagResolver getTagResolver() {
        return resolver;
    }
}
