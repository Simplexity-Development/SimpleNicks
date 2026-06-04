package simplexity.simplenicks.fabric.platform;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplexity.simplenicks.platform.ConfigProvider;
import simplexity.simplenicks.platform.PlatformAdapter;
import simplexity.simplenicks.util.ColorTag;
import simplexity.simplenicks.util.FormatTag;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * {@link PlatformAdapter} implementation for the Fabric platform.
 */
public class FabricPlatformAdapter implements PlatformAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger("SimpleNicks");

    private final MinecraftServer server;
    private final MiniMessage miniMessage;
    private final FabricConfigProvider configProvider;
    private final FabricConfigProvider localeProvider;
    private final Path dataDirectory;

    public FabricPlatformAdapter(@NotNull MinecraftServer server, @NotNull Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.miniMessage = buildMiniMessage();
        this.configProvider = new FabricConfigProvider(dataDirectory.resolve("config.yml"));
        this.localeProvider = new FabricConfigProvider(dataDirectory.resolve("locale.yml"));
    }

    @Override
    public void runAsync(@NotNull Runnable task) {
        Thread.ofVirtual().start(task);
    }

    @Override
    public void runSync(@NotNull Runnable task) {
        server.execute(task);
    }

    @Override
    public boolean isPlayerOnline(@NotNull UUID uuid) {
        return server.getPlayerList().getPlayer(uuid) != null;
    }

    @Override
    public @NotNull Optional<String> getPlayerUsername(@NotNull UUID uuid) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return Optional.empty();
        return Optional.of(player.getGameProfile().getName());
    }

    @Override
    public @NotNull Collection<UUID> getOnlinePlayers() {
        return server.getPlayerList().getPlayers().stream()
                .map(ServerPlayer::getUUID)
                .collect(Collectors.toList());
    }

    @Override
    public void setDisplayName(@NotNull UUID uuid, @NotNull Component displayName) {
        // TODO: Fabric display name support requires a mixin or packet-level approach.
        // Adventure's FabricServerAudiences does not expose a direct display name setter.
    }

    @Override
    public void setTablistName(@NotNull UUID uuid, @NotNull Component tablistName) {
        // TODO: Implement via ClientboundPlayerInfoUpdatePacket with DISPLAY_NAME action.
    }

    @Override
    public void clearDisplayName(@NotNull UUID uuid) {
        // TODO: See setDisplayName.
    }

    @Override
    public void clearTablistName(@NotNull UUID uuid) {
        // TODO: See setTablistName.
    }

    @Override
    public boolean hasPermission(@NotNull UUID uuid, @NotNull String permission) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return false;
        return me.lucko.fabric.api.permissions.v0.Permissions.check(player, permission, 2);
    }

    @Override
    public @NotNull Path getDataDirectory() {
        return dataDirectory;
    }

    @Override
    public @NotNull Logger getLogger() {
        return LOGGER;
    }

    @Override
    public @NotNull MiniMessage getMiniMessage() {
        return miniMessage;
    }

    @Override
    public @NotNull ConfigProvider getConfigProvider() {
        return configProvider;
    }

    @Override
    public @NotNull ConfigProvider getLocaleProvider() {
        return localeProvider;
    }

    /**
     * Returns the underlying {@link MinecraftServer}. Used by Fabric-specific event handlers.
     *
     * @return the server instance
     */
    @NotNull
    public MinecraftServer getServer() {
        return server;
    }

    @NotNull
    private static MiniMessage buildMiniMessage() {
        TagResolver.Builder tagResolver = TagResolver.builder();
        for (ColorTag colorTag : ColorTag.values()) {
            tagResolver.resolver(colorTag.getTagResolver());
        }
        for (FormatTag formatTag : FormatTag.values()) {
            tagResolver.resolver(formatTag.getTagResolver());
        }
        return MiniMessage.builder()
                .strict(false)
                .tags(tagResolver.build())
                .build();
    }
}
