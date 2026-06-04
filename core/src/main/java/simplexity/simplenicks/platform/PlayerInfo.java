package simplexity.simplenicks.platform;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Immutable snapshot of a player's identity and last login time.
 * <p>
 * Replaces {@code OfflinePlayer} in core logic so that player data can be
 * resolved purely from the database without platform API calls.
 * </p>
 *
 * @param uuid             the player's UUID
 * @param username         the player's last known username
 * @param lastLoginMillis  epoch milliseconds of the player's last login, or {@code -1} if unknown
 */
public record PlayerInfo(@NotNull UUID uuid, @NotNull String username, long lastLoginMillis) {}
