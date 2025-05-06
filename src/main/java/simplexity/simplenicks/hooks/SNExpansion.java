package simplexity.simplenicks.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.NickHandler;
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
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("mininick")) {
            Nickname nickname = Cache.getInstance().getActiveNickname(player.getUniqueId());
            if (nickname != null) {
                return nickname.nickname();
            }
            return player.getName();
        }
        return null;
    }
}
