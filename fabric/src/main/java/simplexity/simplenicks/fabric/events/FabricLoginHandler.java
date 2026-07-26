package simplexity.simplenicks.fabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.fabric.platform.FabricPlatformAdapter;
import simplexity.simplenicks.fabric.storage.FabricNicknameStorage;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
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
            String username = listener.player.getGameProfile().name();
            // Mark online immediately (main thread, before async task) so Cache.playerIsOffline()
            // returns false when called from the virtual thread.
            ((FabricPlatformAdapter) SimpleNicksCore.get().platform()).markOnline(playerUuid);
            SimpleNicksCore.get().platform().runAsync(() -> {
                SqlHandler.getInstance().updatePlayerTable(playerUuid, username);
                Cache.getInstance().loadCurrentNickname(playerUuid);
                Cache.getInstance().loadSavedNicknames(playerUuid);
                // Write to FabricNicknameStorage immediately on this thread.
                // ConcurrentHashMap writes and pure Adventure→NMS conversion are both
                // thread-safe, so any chat message that arrives before the runSync below
                // executes will already see the correct display name via the mixin.
                preloadNicknameStorage(playerUuid);
                // Full refresh on the main thread: safe player-list access + tab-list packet.
                SimpleNicksCore.get().platform().runSync(() -> NickUtils.refreshDisplayName(playerUuid));
            });
        });
    }

    /**
     * Writes the player's active nickname directly into {@link FabricNicknameStorage} without
     * going through {@code NickUtils.refreshDisplayName}, which requires the main thread for its
     * {@code isPlayerOnline} check. Safe to call from any thread.
     */
    private static void preloadNicknameStorage(@NotNull UUID playerUuid) {
        Nickname nick = Cache.getInstance().getActiveNickname(playerUuid);
        if (nick == null) return;
        FabricPlatformAdapter adapter = (FabricPlatformAdapter) SimpleNicksCore.get().platform();
        MiniMessage mm = SimpleNicksCore.get().miniMessage();
        Component displayName = mm.deserialize(ConfigHandler.getInstance().getNickPrefix())
                .append(mm.deserialize(nick.getNickname()));
        FabricNicknameStorage.setDisplayName(playerUuid, adapter.getAudiences().asNative(displayName));
        if (ConfigHandler.getInstance().shouldNickTablist()) {
            FabricNicknameStorage.setTabName(playerUuid,
                    adapter.getAudiences().asNative(mm.deserialize(nick.getNickname())));
        }
    }
}
