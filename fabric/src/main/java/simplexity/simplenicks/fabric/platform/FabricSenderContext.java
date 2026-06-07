package simplexity.simplenicks.fabric.platform;

import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

import java.util.Optional;
import java.util.UUID;

/**
 * {@link SenderContext} backed by a Minecraft {@link CommandSourceStack}.
 * Permission checks delegate to fabric-permissions-api with an op-level fallback.
 * <p>
 * Message delivery uses plain-text serialization into Minecraft's native text component.
 * Full MiniMessage rendering can be added once a suitable Adventure bridge is available.
 * </p>
 */
public class FabricSenderContext implements SenderContext {

    private final CommandSourceStack source;

    public FabricSenderContext(@NotNull CommandSourceStack source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return FabricPermissions.check(source, permission);
    }

    @Override
    public @NotNull Optional<UUID> getUuid() {
        ServerPlayer player = source.getPlayer();
        if (player == null) return Optional.empty();
        return Optional.of(player.getUUID());
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        FabricPlatformAdapter adapter = (FabricPlatformAdapter) SimpleNicksCore.get().platform();
        net.minecraft.network.chat.Component nms = adapter.getAudiences().asNative(message);
        source.sendSuccess(() -> nms, false);
    }

    @Override
    public boolean isPlayer() {
        return source.getPlayer() != null;
    }

    @Override
    public @NotNull String getDisplayName() {
        ServerPlayer player = source.getPlayer();
        if (player == null) return "[Console]";
        Nickname nick = Cache.getInstance().getActiveNickname(player.getUUID());
        return nick != null ? nick.getNickname() : player.getGameProfile().name();
    }
}
