package simplexity.simplenicks.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.SaveMigrator;
import simplexity.simplenicks.saving.SqlHandler;

import java.util.UUID;

public class LoginListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        UUID playerUuid = joinEvent.getPlayer().getUniqueId();
        String username = joinEvent.getPlayer().getName();
        SimpleNicksCore.get().platform().runAsync(() -> {
            SqlHandler.getInstance().updatePlayerTable(playerUuid, username);
            Cache.getInstance().loadCurrentNickname(playerUuid);
            Cache.getInstance().loadSavedNicknames(playerUuid);
            SimpleNicksCore.get().platform().runSync(() -> {
                SaveMigrator.migratePdcNickname(joinEvent.getPlayer());
                NickUtils.refreshDisplayName(playerUuid);
            });
        });
    }
}
