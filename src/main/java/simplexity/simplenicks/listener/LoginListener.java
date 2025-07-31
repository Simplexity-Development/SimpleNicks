package simplexity.simplenicks.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.util.NickHandler;

public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(PlayerJoinEvent joinEvent) {
        Bukkit.getScheduler().runTaskLater(SimpleNicks.getInstance(), () -> NickHandler.getInstance().refreshNickname(joinEvent.getPlayer()), 20L);
    }
}
