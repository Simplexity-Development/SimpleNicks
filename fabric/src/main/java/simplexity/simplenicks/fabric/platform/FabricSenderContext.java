package simplexity.simplenicks.fabric.platform;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.platform.SenderContext;

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
        return me.lucko.fabric.api.permissions.v0.Permissions.check(source, permission, 2);
    }

    @Override
    public @NotNull Optional<UUID> getUuid() {
        ServerPlayer player = source.getPlayer();
        if (player == null) return Optional.empty();
        return Optional.of(player.getUUID());
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        String plain = PlainTextComponentSerializer.plainText().serialize(message);
        source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(plain), false);
    }

    @Override
    public boolean isPlayer() {
        return source.getPlayer() != null;
    }

    @Override
    public @NotNull String getDisplayName() {
        ServerPlayer player = source.getPlayer();
        if (player == null) return "[Console]";
        return player.getGameProfile().getName();
    }
}
