package simplexity.simplenicks.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import simplexity.simplenicks.saving.Cache;

import java.util.UUID;

public class LeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent quitEvent) {
        UUID playerUuid = quitEvent.getPlayer().getUniqueId();
        Cache.getInstance().removePlayerFromCache(playerUuid);
    }
}
