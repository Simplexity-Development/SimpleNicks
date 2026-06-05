package simplexity.simplenicks.fabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.fabric.platform.FabricPlatformAdapter;
import simplexity.simplenicks.fabric.storage.FabricNicknameStorage;
import simplexity.simplenicks.saving.Cache;

import java.util.UUID;

/**
 * Handles player disconnect events on Fabric, mirroring {@code LeaveListener} on Paper.
 */
public class FabricLeaveHandler {

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID uuid = handler.player.getUUID();
            Cache.getInstance().removePlayerFromCache(uuid);
            FabricNicknameStorage.clearDisplayName(uuid);
            FabricNicknameStorage.clearTabName(uuid);
            ((FabricPlatformAdapter) SimpleNicksCore.get().platform()).markOffline(uuid);
        });
    }
}
