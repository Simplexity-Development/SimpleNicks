package simplexity.simplenicks.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import simplexity.simplenicks.util.NickHandler;

public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(PlayerJoinEvent joinEvent) {
        NickHandler.getInstance().refreshNickname(joinEvent.getPlayer().getUniqueId());
    }
}
