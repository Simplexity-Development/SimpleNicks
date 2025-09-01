package simplexity.simplenicks.commands;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.saving.SqlHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles the high-level logic for nickname management.
 * <p>
 * This class acts as the main entry point for commands or other
 * external systems that want to interact with nicknames.
 * It delegates persistence and caching to {@link Cache} and {@link SqlHandler}.
 * </p>
 */
@SuppressWarnings("UnusedReturnValue")
public class NicknameProcessor {
    private static NicknameProcessor instance;

    public NicknameProcessor() {
    }

    public static NicknameProcessor getInstance() {
        if (instance == null) instance = new NicknameProcessor();
        return instance;
    }

    /**
     * Sets a player's active nickname.
     *
     * @param player   The player whose nickname is being set.
     * @param nickname The nickname string to assign.
     * @return {@code true} if the nickname was set successfully,
     * {@code false} if it failed to persist or cache.
     */
    public boolean setNickname(@NotNull OfflinePlayer player, @NotNull String nickname) {
        UUID playerUuid = player.getUniqueId();
        String username = player.getName();
        if (username == null) return false;
        return Cache.getInstance().setActiveNickname(playerUuid, username, nickname);
    }

    /**
     * Resets a player's nickname back to their original username.
     *
     * @param player The player whose nickname should be reset.
     * @return {@code true} if the nickname was cleared successfully,
     * {@code false} if the database update failed.
     */
    public boolean resetNickname(@NotNull OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        return Cache.getInstance().clearCurrentNickname(playerUuid);
    }

    /**
     * Saves a nickname to the player's list of saved nicknames.
     *
     * @param player   The player saving the nickname.
     * @param nickname The nickname string to save.
     * @return {@code true} if the nickname was saved successfully,
     * {@code false} if it failed to persist or cache.
     */
    public boolean saveNickname(@NotNull OfflinePlayer player, @NotNull String nickname) {
        UUID playerUuid = player.getUniqueId();
        String username = player.getName();
        if (username == null) return false;
        return Cache.getInstance().saveNickname(playerUuid, username, nickname);
    }

    /**
     * Deletes a previously saved nickname for a player.
     *
     * @param player   The player who owns the nickname.
     * @param nickname The nickname string to delete.
     * @return {@code true} if the nickname was deleted successfully,
     * {@code false} if no such nickname was found or persistence failed.
     */
    public boolean deleteNickname(@NotNull OfflinePlayer player, @NotNull String nickname) {
        UUID playerUuid = player.getUniqueId();
        return Cache.getInstance().deleteSavedNickname(playerUuid, nickname);
    }


    /**
     * Gets all saved nicknames for a player.
     * <p>
     * Uses the in-memory cache if the player is online,
     * otherwise queries SQL directly.
     * </p>
     *
     * @param player The player whose saved nicknames to retrieve.
     * @return A non-null list of {@link Nickname}. Empty if none exist.
     */
    @NotNull
    public List<Nickname> getSavedNicknames(@NotNull OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getSavedNicknames(playerUuid);
        List<Nickname> nicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(playerUuid);
        if (nicks == null) return new ArrayList<>();
        return nicks;
    }

    /**
     * Gets the currently active nickname for a player.
     * <p>
     * Uses the in-memory cache if the player is online,
     * otherwise queries SQL directly.
     * </p>
     *
     * @param player The player whose nickname to get.
     * @return The current {@link Nickname}, or {@code null} if none is set.
     */
    @Nullable
    public Nickname getCurrentNickname(@NotNull OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getActiveNickname(playerUuid);
        return SqlHandler.getInstance().getCurrentNicknameForPlayer(playerUuid);
    }

    /**
     * Gets the number of saved nicknames for a player.
     * <p>
     * Uses the in-memory cache if the player is online,
     * otherwise queries SQL directly.
     * </p>
     *
     * @param player The player whose saved nicknames to count.
     * @return The number of saved nicknames.
     */
    public int getCurrentSavedNickCount(@NotNull OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getSavedNickCount(playerUuid);
        List<Nickname> savedNicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(playerUuid);
        if (savedNicks == null || savedNicks.isEmpty()) return 0;
        return savedNicks.size();
    }


    /**
     * Checks if a player has already saved the given nickname.
     *
     * @param player   The player to check.
     * @param nickname The nickname string to search for.
     * @return {@code true} if the player already saved this nickname,
     * {@code false} otherwise.
     */
    public boolean playerAlreadySavedThis(@NotNull OfflinePlayer player, @NotNull String nickname) {
        UUID playerUuid = player.getUniqueId();
        return SqlHandler.getInstance().userAlreadySavedThisName(playerUuid, nickname);
    }

}
