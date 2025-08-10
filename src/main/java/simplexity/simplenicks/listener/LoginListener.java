package simplexity.simplenicks.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.SaveMigrator;
import simplexity.simplenicks.saving.SqlHandler;

import java.util.UUID;

public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        UUID playerUuid = joinEvent.getPlayer().getUniqueId();
        String username = joinEvent.getPlayer().getName();
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            SqlHandler.getInstance().updatePlayerTableSqlite(playerUuid, username);
            Cache.getInstance().loadCurrentNickname(playerUuid);
            Cache.getInstance().loadSavedNicknames(playerUuid);
            Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> {
                SaveMigrator.migratePdcNickname(joinEvent.getPlayer());
                NickUtils.getInstance().refreshNickname(playerUuid);
            });
        });
    }
}
