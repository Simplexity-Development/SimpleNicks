package simplexity.simplenicks.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

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
        return "1.1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        Nickname nickname = Cache.getInstance().getActiveNickname(player.getUniqueId());
        if (params.equalsIgnoreCase("nick-no-prefix")) {
            if (nickname == null) {
                return player.getName();
            }
            return nickname.getNickname();
        }
        if (params.equalsIgnoreCase("mininick") || params.equalsIgnoreCase("nick")) {
            if (nickname == null) {
                return player.getName();
            }
            String prefix = ConfigHandler.getInstance().getNickPrefix();
            if (prefix == null || prefix.isEmpty()) return nickname.getNickname();
            return prefix + nickname.getNickname();
        }
        return null;
    }
}
