package simplexity.simplenicks.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.SaveMigrator;
import simplexity.simplenicks.saving.SqlHandler;

import java.util.UUID;

public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        UUID playerUuid = joinEvent.getPlayer().getUniqueId();
        SaveMigrator.migratePdcNickname(joinEvent.getPlayer());
        Cache.getInstance().loadCurrentNickname(playerUuid);
        Cache.getInstance().loadSavedNicknames(playerUuid);
        NickUtils.getInstance().refreshNickname(playerUuid);
        String username = joinEvent.getPlayer().getName();
        SqlHandler.getInstance().savePlayerToPlayers(playerUuid, username);
    }
}
