package simplexity.simplenicks.platform;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Platform-agnostic abstraction for a command sender (player or console).
 * <p>
 * Paper wraps {@code CommandSender}; Fabric wraps {@code CommandSourceStack}.
 * Core logic uses this interface instead of platform types for permission checks
 * and message delivery.
 * </p>
 */
public interface SenderContext {

    /**
     * Checks whether this sender has the given permission node.
     *
     * @param permission the permission node string
     * @return {@code true} if the sender has the permission
     */
    boolean hasPermission(@NotNull String permission);

    /**
     * Returns the UUID of this sender if they are a player, otherwise empty.
     *
     * @return the player's UUID, or empty for the console
     */
    @NotNull
    Optional<UUID> getUuid();

    /**
     * Sends an Adventure component message to this sender.
     *
     * @param message the component to send
     */
    void sendMessage(@NotNull Component message);

    /**
     * Returns whether this sender is a player (as opposed to the console or a command block).
     *
     * @return {@code true} if the sender is a player
     */
    boolean isPlayer();

    /**
     * Returns a display-friendly name for this sender (player name or "[Console]").
     *
     * @return the sender's display name
     */
    @NotNull
    String getDisplayName();
}
