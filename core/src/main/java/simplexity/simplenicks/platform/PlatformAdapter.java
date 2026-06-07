package simplexity.simplenicks.platform;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Abstracts all platform-specific operations (scheduling, player access, display names, permissions).
 * <p>
 * Each platform (Paper, Fabric) provides its own implementation. Core logic accesses the
 * platform exclusively through this interface via {@link simplexity.simplenicks.SimpleNicksCore}.
 * </p>
 */
public interface PlatformAdapter {

    /**
     * Runs a task on a background thread.
     *
     * @param task the task to run asynchronously
     */
    void runAsync(@NotNull Runnable task);

    /**
     * Runs a task on the main server thread.
     *
     * @param task the task to run synchronously
     */
    void runSync(@NotNull Runnable task);

    /**
     * Returns whether a player is currently online.
     *
     * @param uuid the player's UUID
     * @return {@code true} if the player is online
     */
    boolean isPlayerOnline(@NotNull UUID uuid);

    /**
     * Returns the username of a player if they are currently online.
     *
     * @param uuid the player's UUID
     * @return the username, or empty if offline
     */
    @NotNull
    Optional<String> getPlayerUsername(@NotNull UUID uuid);

    /**
     * Returns the UUIDs of all currently online players.
     *
     * @return collection of online player UUIDs
     */
    @NotNull
    Collection<UUID> getOnlinePlayers();

    /**
     * Sets the display name shown in chat and above the player's head.
     *
     * @param uuid        the player's UUID
     * @param displayName the Adventure component to display
     */
    void setDisplayName(@NotNull UUID uuid, @NotNull Component displayName);

    /**
     * Sets the name shown in the tab list.
     *
     * @param uuid         the player's UUID
     * @param tablistName  the Adventure component to display
     */
    void setTablistName(@NotNull UUID uuid, @NotNull Component tablistName);

    /**
     * Clears the player's display name, reverting to their username.
     *
     * @param uuid the player's UUID
     */
    void clearDisplayName(@NotNull UUID uuid);

    /**
     * Clears the player's tab list name, reverting to their username.
     *
     * @param uuid the player's UUID
     */
    void clearTablistName(@NotNull UUID uuid);

    /**
     * Sends an Adventure component message to an online player.
     * Does nothing if the player is not online.
     *
     * @param uuid    the player's UUID
     * @param message the component to send
     */
    void sendMessageToPlayer(@NotNull UUID uuid, @NotNull Component message);

    /**
     * Checks whether the given player has a permission node.
     *
     * @param uuid       the player's UUID
     * @param permission the permission node string
     * @return {@code true} if the player has the permission
     */
    boolean hasPermission(@NotNull UUID uuid, @NotNull String permission);

    /**
     * Returns the plugin's data directory (where config and database files are stored).
     *
     * @return the data directory path
     */
    @NotNull
    Path getDataDirectory();

    /**
     * Returns the plugin logger.
     *
     * @return SLF4J logger instance
     */
    @NotNull
    Logger getLogger();

    /**
     * Returns the configured {@link MiniMessage} instance with all permitted color and format
     * tag resolvers registered.
     *
     * @return the MiniMessage instance
     */
    @NotNull
    MiniMessage getMiniMessage();

    /**
     * Returns the {@link ConfigProvider} for {@code config.yml}.
     *
     * @return config provider
     */
    @NotNull
    ConfigProvider getConfigProvider();

    /**
     * Returns the {@link ConfigProvider} for {@code locale.yml}.
     *
     * @return locale provider
     */
    @NotNull
    ConfigProvider getLocaleProvider();
}
