package simplexity.simplenicks.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;

public class SNExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "simplenicks";
    }

    @Override
    public @NotNull String getAuthor() {
        return "simplexity";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("mininick")) {
            String nickname = NickHandler.getInstance().getNickname(player);
            if (nickname != null) {
                return nickname;
            }
            return player.getName();
        }
        if (params.equalsIgnoreCase("nickname")) {
            String nickname = NickHandler.getInstance().getNickname(player);
            if (nickname == null) {
                return player.getName();
            }
            Component parsedNick = SimpleNicks.getMiniMessage().deserialize(nickname);
            return LegacyComponentSerializer.legacySection().serialize(parsedNick);
        }
        return null;
    }
}
