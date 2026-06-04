package simplexity.simplenicks.platform;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import simplexity.simplenicks.util.ColorTag;
import simplexity.simplenicks.util.FormatTag;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * {@link PlatformAdapter} implementation for the Paper platform.
 */
public class PaperPlatformAdapter implements PlatformAdapter {

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage;
    private final BukkitConfigProvider configProvider;
    private final BukkitConfigProvider localeProvider;

    public PaperPlatformAdapter(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = buildMiniMessage();
        this.configProvider = BukkitConfigProvider.forMainConfig(plugin);
        this.localeProvider = BukkitConfigProvider.forFile(
                new File(plugin.getDataFolder(), "locale.yml")
        );
    }

    @Override
    public void runAsync(@NotNull Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runSync(@NotNull Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public boolean isPlayerOnline(@NotNull UUID uuid) {
        return Bukkit.getPlayer(uuid) != null;
    }

    @Override
    public @NotNull Optional<String> getPlayerUsername(@NotNull UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid)).map(Player::getName);
    }

    @Override
    public @NotNull Collection<UUID> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toList());
    }

    @Override
    public void setDisplayName(@NotNull UUID uuid, @NotNull Component displayName) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.displayName(displayName);
    }

    @Override
    public void setTablistName(@NotNull UUID uuid, @NotNull Component tablistName) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.playerListName(tablistName);
    }

    @Override
    public void clearDisplayName(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.displayName(null);
    }

    @Override
    public void clearTablistName(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.playerListName(null);
    }

    @Override
    public boolean hasPermission(@NotNull UUID uuid, @NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return player.hasPermission(permission);
    }

    @Override
    public @NotNull Path getDataDirectory() {
        return plugin.getDataFolder().toPath();
    }

    @Override
    public @NotNull Logger getLogger() {
        return plugin.getSLF4JLogger();
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
     * Returns the underlying Paper plugin instance. Used by Paper-specific classes
     * such as {@link simplexity.simplenicks.saving.SaveMigrator} that need direct
     * plugin API access.
     *
     * @return the plugin instance
     */
    @NotNull
    public JavaPlugin getPlugin() {
        return plugin;
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
