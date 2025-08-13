package simplexity.simplenicks.util;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum ColorTag {
    //Nickname Perms
    HEX_COLOR(new Permission("simplenick.color.basic", PermissionDefault.OP), StandardTags.color()),
    GRADIENT(new Permission("simplenick.color.gradient", PermissionDefault.OP), StandardTags.gradient()),
    RAINBOW(new Permission("simplenick.color.rainbow", PermissionDefault.OP), StandardTags.rainbow()),
    RESET(new Permission("simplenick.color.reset", PermissionDefault.OP), StandardTags.reset());


    private final Permission permission;
    private final TagResolver resolver;


    ColorTag(Permission permission, TagResolver resolver) {
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
