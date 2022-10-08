package adhdmc.simplenicks;

import adhdmc.simplenicks.commands.subcommands.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class LoginListener implements Listener {
    //Basic save handling
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();
        PersistentDataContainer playerPDC = player.getPersistentDataContainer();
        String pdcName = playerPDC.get(Set.nickNameSave, PersistentDataType.STRING);
        if (pdcName != null) {
            Component savedName = SimpleNicks.getMiniMessage().deserialize(pdcName);
            player.displayName(savedName);
        }
    }
}
