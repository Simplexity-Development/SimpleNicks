package simplexity.simplenicks.fabric.platform;

import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplexity.simplenicks.fabric.storage.FabricNicknameStorage;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import simplexity.simplenicks.platform.ConfigProvider;
import simplexity.simplenicks.platform.PlatformAdapter;
import simplexity.simplenicks.util.ColorTag;
import simplexity.simplenicks.util.FormatTag;

import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
    private final MinecraftServerAudiences audiences;
    // Thread-safe mirror of online players so isPlayerOnline() is safe to call from async tasks.
    private final Set<UUID> onlinePlayerUuids = ConcurrentHashMap.newKeySet();

    public FabricPlatformAdapter(@NotNull MinecraftServer server, @NotNull Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.miniMessage = buildMiniMessage();
        this.configProvider = new FabricConfigProvider(dataDirectory.resolve("config.yml"));
        this.localeProvider = new FabricConfigProvider(dataDirectory.resolve("locale.yml"));
        this.audiences = MinecraftServerAudiences.of(server);
    }

    @Override
    public void runAsync(@NotNull Runnable task) {
        Thread.ofVirtual()
                .uncaughtExceptionHandler((_, e) -> LOGGER.warn("Uncaught exception in async task", e))
                .start(task);
    }

    @Override
    public void runSync(@NotNull Runnable task) {
        server.execute(task);
    }

    @Override
    public boolean isPlayerOnline(@NotNull UUID uuid) {
        return onlinePlayerUuids.contains(uuid);
    }

    /**
     * Marks a player as online. Must be called on join before any async task reads
     * {@link #isPlayerOnline(UUID)}.
     */
    public void markOnline(@NotNull UUID uuid) {
        onlinePlayerUuids.add(uuid);
    }

    /**
     * Marks a player as offline. Called on disconnect.
     */
    public void markOffline(@NotNull UUID uuid) {
        onlinePlayerUuids.remove(uuid);
    }

    @Override
    public @NotNull Optional<String> getPlayerUsername(@NotNull UUID uuid) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return Optional.empty();
        return Optional.of(player.getGameProfile().name());
    }

    @Override
    public @NotNull Collection<UUID> getOnlinePlayers() {
        return server.getPlayerList().getPlayers().stream()
                .map(ServerPlayer::getUUID)
                .collect(Collectors.toList());
    }

    @Override
    public void setDisplayName(@NotNull UUID uuid, @NotNull Component displayName) {
        FabricNicknameStorage.setDisplayName(uuid, audiences.asNative(displayName));
    }

    @Override
    public void setTablistName(@NotNull UUID uuid, @NotNull Component tablistName) {
        FabricNicknameStorage.setTabName(uuid, audiences.asNative(tablistName));
        server.execute(() -> broadcastTabListUpdate(uuid));
    }

    @Override
    public void clearDisplayName(@NotNull UUID uuid) {
        FabricNicknameStorage.clearDisplayName(uuid);
    }

    @Override
    public void clearTablistName(@NotNull UUID uuid) {
        FabricNicknameStorage.clearTabName(uuid);
        server.execute(() -> broadcastTabListUpdate(uuid));
    }

    private void broadcastTabListUpdate(@NotNull UUID uuid) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return;
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME),
                List.of(player)
        );
        server.getPlayerList().broadcastAll(packet);
    }

    @Override
    public void sendMessageToPlayer(@NotNull UUID uuid, @NotNull Component message) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return;
        player.sendSystemMessage(audiences.asNative(message));
    }

    @Override
    public boolean hasPermission(@NotNull UUID uuid, @NotNull String permission) {
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return false;
        return FabricPermissions.check(player, permission);
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

    /**
     * Returns the {@link MinecraftServerAudiences} instance used for Adventure ↔ NMS component
     * conversion and message delivery.
     *
     * @return the audiences instance
     */
    @NotNull
    public MinecraftServerAudiences getAudiences() {
        return audiences;
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
