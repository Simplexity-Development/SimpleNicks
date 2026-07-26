package simplexity.simplenicks.platform;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Platform-agnostic representation of a resolved offline or online player target.
 *
 * @param id   the player's UUID
 * @param name the player's last-known username
 */
public record PlayerTarget(@NotNull UUID id, @NotNull String name) {}
