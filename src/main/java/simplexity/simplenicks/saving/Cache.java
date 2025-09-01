package simplexity.simplenicks.saving;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory cache for player nicknames.
 * <p>
 * This class provides fast access to players' active and saved nicknames without
 * hitting the database for every request. It keeps nicknames synchronized with
 * the underlying SQL database (via {@link SqlHandler}) whenever modifications
 * are made.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <ul>
 *     <li>Use {@link #getActiveNickname(UUID)} to get the current nickname of a player.</li>
 *     <li>Use {@link #getSavedNicknames(UUID)} to get all saved nicknames of a player.</li>
 *     <li>Call {@link #setActiveNickname(UUID, String, String)} to set a nickname.</li>
 *     <li>Call {@link #saveNickname(UUID, String, String)} to persist additional nicknames.</li>
 * </ul>
 *
 * <p>
 * Nicknames are only cached while the player is online. For offline players,
 * modifications are still persisted to SQL, but skipped in the in-memory cache.
 * </p>
 */
public class Cache {
    private static Cache instance;
    private static final Logger logger = SimpleNicks.getInstance().getSLF4JLogger();

    public Cache() {
    }

    public static Cache getInstance() {
        if (instance == null) instance = new Cache();
        return instance;
    }

    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    private final HashMap<UUID, Nickname> activeNicknames = new HashMap<>();
    private final HashMap<UUID, List<Nickname>> savedNicknames = new HashMap<>();

    /**
     * Loads a player's active nickname from SQL and caches it if the player is online.
     *
     * @param uuid player UUID
     * @see SqlHandler#getCurrentNicknameForPlayer(UUID)
     */

    public void loadCurrentNickname(@NotNull UUID uuid) {
        debug("Loading current nickname for UUID %s", uuid);
        Nickname currentNick = SqlHandler.getInstance().getCurrentNicknameForPlayer(uuid);
        if (currentNick == null) {
            debug("No current nickname found in SQL for UUID %s", uuid);
            return;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; not caching current nickname for UUID %s", uuid);
            return;
        }
        activeNicknames.put(uuid, currentNick);
        debug("Cached current nickname '%s' for UUID %s", currentNick.getNickname(), uuid);
    }

    /**
     * Loads all saved nicknames for a player from SQL and caches them if the player is online.
     *
     * @param uuid player UUID
     * @see SqlHandler#getSavedNicknamesForPlayer(UUID)
     */
    public void loadSavedNicknames(@NotNull UUID uuid) {
        debug("Loading saved nicknames for UUID %s", uuid);
        List<Nickname> savedNames = SqlHandler.getInstance().getSavedNicknamesForPlayer(uuid);
        if (savedNames == null || savedNames.isEmpty()) {
            debug("No saved nicknames found for UUID %s", uuid);
            return;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; not caching saved nicknames for UUID %s", uuid);
            return;
        }
        savedNicknames.put(uuid, savedNames);
        debug("Cached %d saved nicknames for UUID %s", savedNames.size(), uuid);
    }

    /**
     * Gets the currently active nickname from the cache.
     *
     * @param uuid player UUID
     * @return the active nickname, or {@code null} if none is set or cached
     */
    @Nullable
    public Nickname getActiveNickname(@NotNull UUID uuid) {
        debug("Getting active nickname for UUID %s", uuid);
        if (activeNicknames.containsKey(uuid)) {
            debug("Active nickname hit in cache for UUID %s", uuid);
            return activeNicknames.get(uuid);
        }
        debug("Active nickname cache miss for UUID %s", uuid);
        return null;
    }

    /**
     * Gets all saved nicknames for the player from the cache.
     *
     * @param uuid player UUID
     * @return list of nicknames; empty if none are cached
     */
    @NotNull
    public List<Nickname> getSavedNicknames(@NotNull UUID uuid) {
        debug("Getting saved nicknames for UUID %s", uuid);
        if (savedNicknames.containsKey(uuid)) {
            debug("Saved nicknames exists for UUID %s, Nicknames:", uuid, savedNicknames.get(uuid));
            return savedNicknames.get(uuid);
        }
        debug("No Saved Nicknames for UUID %s", uuid);
        return new ArrayList<>();
    }

    /**
     * Sets the player's active nickname.
     * <p>
     * Persists to SQL and updates the in-memory cache if the player is online.
     * The {@code nickname} may contain MiniMessage tags; normalization strips tags
     * and lowercases for uniqueness checks.
     * </p>
     *
     * @param uuid     player UUID
     * @param username player's last known username
     * @param nickname formatted nickname (may include MiniMessage tags)
     * @return {@code true} if the nickname was successfully stored; {@code false} otherwise
     * @see SqlHandler#setActiveNickname(UUID, String, String, String)
     * @see SqlHandler#updatePlayerTable(UUID, String)
     */

    public boolean setActiveNickname(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname) {
        debug("Setting active nickname '%s' for UUID %s", nickname, uuid);
        String normalizedNick = miniMessage.stripTags(nickname).toLowerCase();
        Nickname nick = new Nickname(nickname, normalizedNick);
        boolean sqlActiveNameSet = SqlHandler.getInstance().setActiveNickname(uuid, username, nickname, normalizedNick);
        if (!sqlActiveNameSet) {
            debug("Failed to update active nickname in SQL for UUID %s", uuid);
            return false;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; not caching active nickname for UUID %s", uuid);
            return true;
        }
        activeNicknames.put(uuid, nick);
        debug("Cached active nickname '%s' for UUID %s", nickname, uuid);
        return true;
    }

    /**
     * Deletes a saved nickname for a player.
     * <p>
     * Persists the deletion in SQL and updates the in-memory cache if present.
     * </p>
     *
     * @param uuid     player UUID
     * @param nickname nickname (as originally saved, formatting included)
     * @return {@code true} if the nickname was deleted; {@code false} if it didn't exist or SQL failed
     * @see SqlHandler#deleteNickname(UUID, String)
     */

    public boolean deleteSavedNickname(@NotNull UUID uuid, @NotNull String nickname) {
        debug("Deleting saved nickname '%s' for UUID %s", nickname, uuid);
        boolean sqlDeleted = SqlHandler.getInstance().deleteNickname(uuid, nickname);
        if (!sqlDeleted) {
            debug("SQL delete failed for nickname '%s' UUID %s", nickname, uuid);
            return false;
        }
        if (!savedNicknames.containsKey(uuid)) {
            debug("Nickname '%s' not found in cache for UUID %s", nickname, uuid);
            return false;
        }
        List<Nickname> userSavedNicknames = savedNicknames.get(uuid);
        userSavedNicknames.removeIf(name -> name.getNickname().equals(nickname));
        if (playerIsOffline(uuid)) {
            debug("Player is offline; skipping cache re-insertion after nickname delete for UUID %s", uuid);
            return true;
        }
        savedNicknames.put(uuid, userSavedNicknames);
        debug("Nickname '%s' deleted and cache updated for UUID %s", nickname, uuid);
        return true;
    }

    /**
     * Clears the player's current (active) nickname.
     * <p>
     * Persists to SQL and removes from cache if the player is online.
     * </p>
     *
     * @param uuid player UUID
     * @return {@code true} if a record was cleared; {@code false} otherwise
     * @see SqlHandler#clearActiveNickname(UUID)
     */
    public boolean clearCurrentNickname(@NotNull UUID uuid) {
        debug("Clearing current nickname for UUID %s", uuid);
        boolean sqlNickRemoved = SqlHandler.getInstance().clearActiveNickname(uuid);
        if (!sqlNickRemoved) {
            debug("Failed to clear nickname in SQL for UUID %s", uuid);
            return false;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; skipping cache update for UUID %s", uuid);
            return true;
        }
        activeNicknames.remove(uuid);
        debug("Removed nickname from cache for UUID %s", uuid);
        return true;
    }

    /**
     * Resolves all UUIDs of players who currently use a given normalized nickname.
     *
     * @param nickname normalized nickname (tags stripped & lowercased)
     * @return list of UUIDs; empty if none found or SQL error
     * @see SqlHandler#getUuidsOfNickname(String)
     */

    @NotNull
    public List<UUID> getUuidOfNormalizedName(@NotNull String nickname) {
        debug("Getting UUIDs for normalized nickname '%s'", nickname);
        List<UUID> uuids = SqlHandler.getInstance().getUuidsOfNickname(nickname);
        if (uuids == null) {
            debug("No UUIDs found for nickname '%s'", nickname);
            return new ArrayList<>();
        }
        debug("Found %d UUIDs for nickname '%s'", uuids.size(), nickname);
        return uuids;
    }

    /**
     * Checks if a normalized nickname is already in use by another online player.
     *
     * @param uuid           player UUID to exclude from the check
     * @param normalizedNick normalized nickname string
     * @return {@code true} if nickname is in use, {@code false} otherwise
     */
    public boolean nickInUseOnlinePlayers(@Nullable UUID uuid, @NotNull String normalizedNick) {
        debug("Checking if nickname '%s' is in use (excluding UUID %s)", normalizedNick, uuid);
        for (UUID playerUuid : activeNicknames.keySet()) {
            if (playerUuid.equals(uuid)) continue;
            if (activeNicknames.get(playerUuid).getNormalizedNickname().equalsIgnoreCase(normalizedNick)) {
                debug("Nickname '%s' is already in use by UUID %s", normalizedNick, playerUuid);
                return true;
            }
        }
        debug("Nickname '%s' is not in use by any online player", normalizedNick);
        return false;
    }

    /**
     * Saves an additional nickname for a player.
     * <p>
     * Persists to SQL and updates cache if the player is online.
     * </p>
     *
     * @param uuid     player UUID
     * @param username player's last known username
     * @param nickname formatted nickname (may include MiniMessage tags)
     * @return {@code true} if saved successfully; {@code false} otherwise
     * @see SqlHandler#saveNickname(UUID, String, String, String)
     * @see SqlHandler#updatePlayerTable(UUID, String)
     */
    public boolean saveNickname(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname) {
        debug("Saving nickname '%s' for UUID %s", nickname, uuid);
        String normalized = miniMessage.stripTags(nickname).toLowerCase();
        Nickname nick = new Nickname(nickname, normalized);
        List<Nickname> userSavedNicknames = getSavedNicknames(uuid);
        userSavedNicknames.add(nick);
        boolean savedToSql = SqlHandler.getInstance().saveNickname(uuid, username, nickname, normalized);
        if (!savedToSql) {
            debug("Failed to save nickname '%s' to SQL for UUID %s", nickname, uuid);
            return false;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; skipping cache update after saving nickname for UUID %s", uuid);
            return true;
        }
        savedNicknames.put(uuid, userSavedNicknames);
        debug("Nickname '%s' saved and cached for UUID %s", nickname, uuid);
        return true;
    }


    /**
     * Gets the number of saved nicknames for a player in the cache.
     *
     * @param uuid player UUID
     * @return nickname count, or 0 if none are cached
     */
    public int getSavedNickCount(@NotNull UUID uuid) {
        debug("Getting saved nickname count for UUID %s", uuid);
        if (savedNicknames.containsKey(uuid)) {
            int size = savedNicknames.get(uuid).size();
            debug("Found %d saved nicknames in cache for UUID %s", size, uuid);
            return size;
        }
        debug("No saved nicknames in cache for UUID %s", uuid);
        return 0;
    }

    /**
     * Removes all cached nickname data for a player.
     * <p>
     * Used when a player disconnects to free memory.
     * </p>
     *
     * @param uuid player UUID
     */
    public void removePlayerFromCache(@NotNull UUID uuid) {
        debug("Removing player from cache for UUID %s", uuid);
        savedNicknames.remove(uuid);
        activeNicknames.remove(uuid);
    }

    /**
     * Gets a read-only map of all online players and their active nicknames.
     *
     * @return unmodifiable map of UUID → active nickname
     */
    @NotNull
    public Map<UUID, Nickname> getOnlineNicknames(){
        debug("Getting all online nicknames from cache");
        return Collections.unmodifiableMap(activeNicknames);
    }

    private boolean playerIsOffline(@NotNull UUID uuid){
        return Bukkit.getPlayer(uuid) == null;
    }

    private void debug(@NotNull String message, @Nullable Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            String messageToSend = String.format(message, args);
            logger.info("[CACHE DEBUG] {}", messageToSend);
        }
    }


}
