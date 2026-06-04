package simplexity.simplenicks.fabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.SqlHandler;

import java.util.UUID;

/**
 * Handles player join events on Fabric, mirroring {@code LoginListener} on Paper.
 */
public class FabricLoginHandler {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerGamePacketListenerImpl listener = handler;
            UUID playerUuid = listener.player.getUUID();
            String username = listener.player.getGameProfile().getName();
            SimpleNicksCore.get().platform().runAsync(() -> {
                SqlHandler.getInstance().updatePlayerTable(playerUuid, username);
                Cache.getInstance().loadCurrentNickname(playerUuid);
                Cache.getInstance().loadSavedNicknames(playerUuid);
                SimpleNicksCore.get().platform().runSync(() ->
                        NickUtils.refreshDisplayName(playerUuid)
                );
            });
        });
    }
}
