package simplexity.simplenicks.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

public class SNExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "simplenick";
    }

    @Override
    public @NotNull String getAuthor() {
        return "simplexity";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        Nickname nick = Cache.getInstance().getActiveNickname(player.getUniqueId());
        String nickname;
        String prefix = ConfigHandler.getInstance().getNickPrefix();
        if (nick == null && player.getName() == null) {
            return null;
        } else if (nick == null) {
            nickname = player.getName();
        } else {
            nickname = nick.getNickname();
        }
        if (params.equalsIgnoreCase("mininick")) {
            return nickname;
        }
        if (params.equalsIgnoreCase("prefixed-mininick")) {
            return prefix + nickname;
        }
        String parsedNickname = LegacyComponentSerializer.legacySection()
                .serialize(SimpleNicks.getMiniMessage().deserialize(nickname));
        if (params.equalsIgnoreCase("nickname")) {
            return parsedNickname;
        }
        if (params.equalsIgnoreCase("prefixed-nickname")) {
            return prefix + parsedNickname;
        }
        if (params.equalsIgnoreCase("normalized")) {
            if (nick == null) return null;
            return nick.getNormalizedNickname();
        }
        return null;
    }
}
