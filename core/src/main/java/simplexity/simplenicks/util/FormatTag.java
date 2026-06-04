package simplexity.simplenicks.util;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

public enum FormatTag {
    UNDERLINE("simplenick.format.underline", "op", StandardTags.decorations(TextDecoration.UNDERLINED)),
    ITALIC("simplenick.format.italic", "op", StandardTags.decorations(TextDecoration.ITALIC)),
    STRIKETHROUGH("simplenick.format.strikethrough", "op", StandardTags.decorations(TextDecoration.STRIKETHROUGH)),
    BOLD("simplenick.format.bold", "op", StandardTags.decorations(TextDecoration.BOLD)),
    OBFUSCATED("simplenick.format.obfuscated", "op", StandardTags.decorations(TextDecoration.OBFUSCATED)),
    HOVER("simplenick.format.hover", "false", StandardTags.hoverEvent()),
    FONT("simplenick.format.font", "false", StandardTags.font());

    private final String permissionKey;
    private final String permissionDefault;
    private final TagResolver resolver;

    FormatTag(@NotNull String permissionKey, @NotNull String permissionDefault, @NotNull TagResolver resolver) {
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
