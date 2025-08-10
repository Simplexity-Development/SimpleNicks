package simplexity.simplenicks.listener;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.util.NickHandler;

import java.util.EnumSet;
import java.util.UUID;

public class NamePacketListener extends PacketListenerAbstract {

    @Override
    public void onPacketSend(@NotNull PacketSendEvent sendEvent) {
        if (sendEvent.getPacketType() != PacketType.Play.Server.PLAYER_INFO_UPDATE) return;
        WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(sendEvent);
        EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = packet.getActions();

        if (!actions.contains(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER)
            && !actions.contains(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME)) return;

        packet.getEntries().forEach(entry -> {
            UUID uuid = entry.getGameProfile().getUUID();
            Player target = Bukkit.getPlayer(uuid);
            String nickname = NickHandler.getInstance().getNickname(target);
            if (target != null && nickname != null && !nickname.isEmpty()) {
                entry.setDisplayName(SimpleNicks.getMiniMessage().deserialize(nickname));
            }
        });

    }

}
