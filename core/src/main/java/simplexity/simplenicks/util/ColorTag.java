package simplexity.simplenicks.util;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

public enum ColorTag {
    HEX_COLOR("simplenick.color.basic", "op", StandardTags.color()),
    GRADIENT("simplenick.color.gradient", "op", StandardTags.gradient()),
    RAINBOW("simplenick.color.rainbow", "op", StandardTags.rainbow()),
    RESET("simplenick.color.reset", "op", StandardTags.reset());

    private final String permissionKey;
    private final String permissionDefault;
    private final TagResolver resolver;

    ColorTag(@NotNull String permissionKey, @NotNull String permissionDefault, @NotNull TagResolver resolver) {
        this.permissionKey = permissionKey;
        this.permissionDefault = permissionDefault;
        this.resolver = resolver;
    }

    @NotNull
    public String getPermissionKey() {
        return permissionKey;
    }

    @NotNull
    public String getPermissionDefault() {
        return permissionDefault;
    }

    @NotNull
    public TagResolver getTagResolver() {
        return resolver;
    }
}
