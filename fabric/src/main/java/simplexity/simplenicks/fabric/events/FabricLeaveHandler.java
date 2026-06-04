package simplexity.simplenicks.fabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import simplexity.simplenicks.saving.Cache;

/**
 * Handles player disconnect events on Fabric, mirroring {@code LeaveListener} on Paper.
 */
public class FabricLeaveHandler {

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                Cache.getInstance().removePlayerFromCache(handler.player.getUUID())
        );
    }
}
