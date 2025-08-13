package simplexity.simplenicks.util;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum FormatTag {

    UNDERLINE(new Permission("simplenick.format.underline", PermissionDefault.OP), StandardTags.decorations(TextDecoration.UNDERLINED)),
    ITALIC(new Permission("simplenick.format.italic", PermissionDefault.OP), StandardTags.decorations(TextDecoration.ITALIC)),
    STRIKETHROUGH(new Permission("simplenick.format.strikethrough", PermissionDefault.OP), StandardTags.decorations(TextDecoration.STRIKETHROUGH)),
    BOLD(new Permission("simplenick.format.bold", PermissionDefault.OP), StandardTags.decorations(TextDecoration.BOLD)),
    OBFUSCATED(new Permission("simplenick.format.obfuscated", PermissionDefault.OP), StandardTags.decorations(TextDecoration.OBFUSCATED));


    private final Permission permission;
    private final TagResolver resolver;


    FormatTag(Permission permission, TagResolver resolver) {
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
