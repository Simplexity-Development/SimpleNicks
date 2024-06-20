package simplexity.simplenicks.util;

import simplexity.simplenicks.SimpleNicks;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class SNExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "simplenicks";
    }

    @Override
    public @NotNull String getAuthor() {
        return SimpleNicks.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return SimpleNicks.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("mininick")) {
            return NickHandler.getInstance().getNickname(player);
        }
        return null;
    }
}
