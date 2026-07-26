package simplexity.simplenicks.hooks;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

public class SNMiniExpansion {

    /**
     * Builds and registers the MiniPlaceholders expansion for SimpleNicks.
     */
    public static void register() {
        Expansion.builder("simplenick")
            .audiencePlaceholder(Player.class, "nick", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                return Tag.inserting(SimpleNicksCore.get().miniMessage().deserialize(rawNick));
            })
            .audiencePlaceholder(Player.class, "prefixed_nick", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                String prefix = ConfigHandler.getInstance().getNickPrefix();
                return Tag.inserting(
                    Component.text(prefix).append(SimpleNicksCore.get().miniMessage().deserialize(rawNick))
                );
            })
            .audiencePlaceholder(Player.class, "stripped", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                return Tag.inserting(Component.text(SimpleNicksCore.get().miniMessage().stripTags(rawNick)));
            })
            .audiencePlaceholder(Player.class, "prefixed_stripped", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                String prefix = ConfigHandler.getInstance().getNickPrefix();
                return Tag.inserting(
                    Component.text(prefix + SimpleNicksCore.get().miniMessage().stripTags(rawNick))
                );
            })
            .audiencePlaceholder(Player.class, "normalized", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                if (nick == null) return Tag.inserting(Component.empty());
                return Tag.inserting(Component.text(nick.getNormalizedNickname()));
            })
            .build()
            .register();
    }
}
