package simplexity.simplenicks.hooks;

import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

public class SNMiniExpansion {

    /**
     * Builds and registers the MiniPlaceholders expansion for SimpleNicks.
     * <p>
     * Registers the following audience-scoped tags (online players only):
     * <ul>
     *   <li>{@code <simplenick_nick>} — rendered nickname component (MiniMessage-parsed)</li>
     *   <li>{@code <simplenick_prefixed_nick>} — config prefix + rendered nickname</li>
     *   <li>{@code <simplenick_stripped>} — nickname with all MiniMessage tags removed</li>
     *   <li>{@code <simplenick_prefixed_stripped>} — prefix + stripped nickname</li>
     *   <li>{@code <simplenick_normalized>} — normalized (lowercase, stripped) nickname</li>
     * </ul>
     * Falls back to the player's username when no nickname is set.
     * </p>
     */
    public static void register() {
        Expansion.builder("simplenick")
            .audiencePlaceholder(Player.class, "nick", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                return Tag.inserting(SimpleNicks.getMiniMessage().deserialize(rawNick));
            })
            .audiencePlaceholder(Player.class, "prefixed_nick", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                String prefix = ConfigHandler.getInstance().getNickPrefix();
                return Tag.inserting(
                    Component.text(prefix).append(SimpleNicks.getMiniMessage().deserialize(rawNick))
                );
            })
            .audiencePlaceholder(Player.class, "stripped", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                return Tag.inserting(Component.text(SimpleNicks.getMiniMessage().stripTags(rawNick)));
            })
            .audiencePlaceholder(Player.class, "prefixed_stripped", (player, queue, ctx) -> {
                Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
                String rawNick = nick != null ? nick.getNickname() : player.getName();
                String prefix = ConfigHandler.getInstance().getNickPrefix();
                return Tag.inserting(
                    Component.text(prefix + SimpleNicks.getMiniMessage().stripTags(rawNick))
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
