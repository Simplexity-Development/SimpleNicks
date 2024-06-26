package simplexity.simplenicks.util;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public enum TagPermission {
    //Nickname Perms
    HEX_COLOR("simplenick.nick.color", StandardTags.color()),
    GRADIENT("simplenick.nick.gradient", StandardTags.gradient()),
    RAINBOW("simplenick.nick.rainbow", StandardTags.rainbow()),
    RESET("simplenick.nick.format.reset", StandardTags.reset()),
    UNDERLINE("simplenick.nick.format.underline", StandardTags.decorations(TextDecoration.UNDERLINED)),
    ITALIC("simplenick.nick.format.italic", StandardTags.decorations(TextDecoration.ITALIC)),
    STRIKETHROUGH("simplenick.nick.format.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH)),
    BOLD("simplenick.nick.format.bold", StandardTags.decorations(TextDecoration.BOLD)),
    OBFUSCATED("simplenick.nick.format.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED));


    private final String permission;
    private final TagResolver resolver;

    TagPermission(String permission, TagResolver resolver) {
        this.permission = permission;
        this.resolver = resolver;
    }

    public String getPermission() {
        return permission;
    }

    public TagResolver getTagResolver() {
        return resolver;
    }
}
