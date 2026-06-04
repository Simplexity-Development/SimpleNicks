package simplexity.simplenicks.commands;

import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.saving.SqlHandler;

import org.jetbrains.annotations.Nullable;
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

    private NicknameProcessor() {
    }

    public static NicknameProcessor getInstance() {
        if (instance == null) instance = new NicknameProcessor();
        return instance;
    }

    /**
     * Sets a player's active nickname.
     *
     * @param uuid     the player's UUID
     * @param username the player's last known username
     * @param nickname the nickname string to assign
     * @return {@code true} if the nickname was set successfully,
     * {@code false} if it failed to persist or cache
     */
    public boolean setNickname(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname) {
        return Cache.getInstance().setActiveNickname(uuid, username, nickname);
    }

    /**
     * Resets a player's nickname back to their original username.
     *
     * @param uuid the player's UUID
     * @return {@code true} if the nickname was cleared successfully,
     * {@code false} if the database update failed
     */
    public boolean resetNickname(@NotNull UUID uuid) {
        return Cache.getInstance().clearCurrentNickname(uuid);
    }

    /**
     * Saves a nickname to the player's list of saved nicknames.
     *
     * @param uuid     the player's UUID
     * @param username the player's last known username
     * @param nickname the nickname string to save
     * @return {@code true} if the nickname was saved successfully,
     * {@code false} if it failed to persist or cache
     */
    public boolean saveNickname(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname) {
        return Cache.getInstance().saveNickname(uuid, username, nickname);
    }

    /**
     * Deletes a previously saved nickname for a player.
     *
     * @param uuid     the player's UUID
     * @param nickname the nickname string to delete
     * @return {@code true} if the nickname was deleted successfully,
     * {@code false} if no such nickname was found or persistence failed
     */
    public boolean deleteNickname(@NotNull UUID uuid, @NotNull String nickname) {
        return Cache.getInstance().deleteSavedNickname(uuid, nickname);
    }

    /**
     * Gets all saved nicknames for a player.
     * <p>
     * Uses the in-memory cache if the player is online,
     * otherwise queries SQL directly.
     * </p>
     *
     * @param uuid     the player's UUID
     * @param isOnline whether the player is currently online
     * @return a non-null list of {@link Nickname}; empty if none exist
     */
    @NotNull
    public List<Nickname> getSavedNicknames(@NotNull UUID uuid, boolean isOnline) {
        if (isOnline) return Cache.getInstance().getSavedNicknames(uuid);
        List<Nickname> nicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(uuid);
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
     * @param uuid     the player's UUID
     * @param isOnline whether the player is currently online
     * @return the current {@link Nickname}, or {@code null} if none is set
     */
    @Nullable
    public Nickname getCurrentNickname(@NotNull UUID uuid, boolean isOnline) {
        if (isOnline) return Cache.getInstance().getActiveNickname(uuid);
        return SqlHandler.getInstance().getCurrentNicknameForPlayer(uuid);
    }

    /**
     * Gets the number of saved nicknames for a player.
     * <p>
     * Uses the in-memory cache if the player is online,
     * otherwise queries SQL directly.
     * </p>
     *
     * @param uuid     the player's UUID
     * @param isOnline whether the player is currently online
     * @return the number of saved nicknames
     */
    public int getCurrentSavedNickCount(@NotNull UUID uuid, boolean isOnline) {
        if (isOnline) return Cache.getInstance().getSavedNickCount(uuid);
        List<Nickname> savedNicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(uuid);
        if (savedNicks == null || savedNicks.isEmpty()) return 0;
        return savedNicks.size();
    }

    /**
     * Checks if a player has already saved the given nickname.
     *
     * @param uuid     the player's UUID
     * @param nickname the nickname string to search for
     * @return {@code true} if the player already saved this nickname,
     * {@code false} otherwise
     */
    public boolean playerAlreadySavedThis(@NotNull UUID uuid, @NotNull String nickname) {
        return SqlHandler.getInstance().userAlreadySavedThisName(uuid, nickname);
    }
}
