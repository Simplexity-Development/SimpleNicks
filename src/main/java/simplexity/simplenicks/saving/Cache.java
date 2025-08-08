package simplexity.simplenicks.saving;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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
     * Loads the player's active nickname from SQL and into cache
     *
     * @param uuid player UUID
     */

    public void loadCurrentNickname(UUID uuid) {
        debug("Loading current nickname for UUID {}", uuid);
        Nickname currentNick = SqlHandler.getInstance().getCurrentNicknameForPlayer(uuid);
        if (currentNick == null) {
            debug("No current nickname found in SQL for UUID {}", uuid);
            return;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; not caching current nickname for UUID {}", uuid);
            return;
        }
        activeNicknames.put(uuid, currentNick);
        debug("Cached current nickname '{}' for UUID {}", currentNick.getNickname(), uuid);
    }

    /**
     * Gets the active nickname from cache,
     *
     * @param uuid Player uuid
     * @return Nickname player has set, null if no nickname is set
     */
    @Nullable
    public Nickname getActiveNickname(UUID uuid) {
        debug("Getting active nickname for UUID {}", uuid);
        if (activeNicknames.containsKey(uuid)) {
            debug("Active nickname hit in cache for UUID {}", uuid);
            return activeNicknames.get(uuid);
        }
        debug("Active nickname cache miss for UUID {}", uuid);
        return null;
    }

    /**
     * Loads a player's saved nicknames into the cache from SQL
     *
     * @param uuid Player's UUID
     */
    public void loadSavedNicknames(UUID uuid) {
        debug("Loading saved nicknames for UUID {}", uuid);
        List<Nickname> savedNames = SqlHandler.getInstance().getSavedNicknamesForPlayer(uuid);
        if (savedNames == null || savedNames.isEmpty()) {
            debug("No saved nicknames found for UUID {}", uuid);
            return;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; not caching saved nicknames for UUID {}", uuid);
            return;
        }
        savedNicknames.put(uuid, savedNames);
        debug("Cached {} saved nicknames for UUID {}", savedNames.size(), uuid);
    }

    /**
     * Gets the saved nicknames from the cache
     *
     * @param uuid Player UUID
     * @return List<Nickname> - if no nicks exist, returns empty list
     */
    public List<Nickname> getSavedNicknames(UUID uuid) {
        debug("Getting saved nicknames for UUID {}", uuid);
        if (savedNicknames.containsKey(uuid)) {
            debug("Saved nicknames exists for UUID {}, Nicknames:", uuid, savedNicknames.get(uuid));
            return savedNicknames.get(uuid);
        }
        debug("No Saved Nicknames for UUID {}", uuid);
        return new ArrayList<>();
    }

    /**
     * Sets the active nickname for the player
     *
     * @param uuid     Player UUID
     * @param nickname Version of the nickname with tags included
     * @return boolean - whether nickname was successfully set or not
     */
    public boolean setActiveNickname(UUID uuid, String username, String nickname) {
        debug("Setting active nickname '{}' for UUID {}", nickname, uuid);
        String normalizedNick = miniMessage.stripTags(nickname);
        Nickname nick = new Nickname(nickname, normalizedNick);
        boolean sqlActiveNameSet = SqlHandler.getInstance().setActiveNickname(uuid, username, nickname, normalizedNick);
        if (!sqlActiveNameSet) {
            debug("Failed to update active nickname in SQL for UUID {}", uuid);
            return false;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; not caching active nickname for UUID {}", uuid);
            return true;
        }
        activeNicknames.put(uuid, nick);
        debug("Cached active nickname '{}' for UUID {}", nickname, uuid);
        return true;
    }

    public boolean deleteSavedNickname(UUID uuid, String nickname) {
        debug("Deleting saved nickname '{}' for UUID {}", nickname, uuid);
        boolean sqlDeleted = SqlHandler.getInstance().deleteNickname(uuid, nickname);
        if (!sqlDeleted) {
            debug("SQL delete failed for nickname '{}' UUID {}", nickname, uuid);
            return false;
        }
        if (!savedNicknames.containsKey(uuid)) {
            debug("Nickname '{}' not found in cache for UUID {}", nickname, uuid);
            return false;
        }
        List<Nickname> userSavedNicknames = savedNicknames.get(uuid);
        userSavedNicknames.removeIf(name -> name.getNickname().equals(nickname));
        if (playerIsOffline(uuid)) {
            debug("Player is offline; skipping cache re-insertion after nickname delete for UUID {}", uuid);
            return true;
        }
        savedNicknames.put(uuid, userSavedNicknames);
        debug("Nickname '{}' deleted and cache updated for UUID {}", nickname, uuid);
        return true;
    }

    public boolean clearCurrentNickname(UUID uuid) {
        debug("Clearing current nickname for UUID {}", uuid);
        boolean sqlNickRemoved = SqlHandler.getInstance().clearActiveNickname(uuid);
        if (!sqlNickRemoved) {
            debug("Failed to clear nickname in SQL for UUID {}", uuid);
            return false;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; skipping cache update for UUID {}", uuid);
            return true;
        }
        activeNicknames.remove(uuid);
        debug("Removed nickname from cache for UUID {}", uuid);
        return true;
    }

    public List<UUID> getUuidOfNormalizedName(String nickname) {
        debug("Getting UUIDs for normalized nickname '{}'", nickname);
        List<UUID> uuids = SqlHandler.getInstance().getUuidsOfNickname(nickname);
        if (uuids == null) {
            debug("No UUIDs found for nickname '{}'", nickname);
            return new ArrayList<>();
        }
        debug("Found {} UUIDs for nickname '{}'", uuids.size(), nickname);
        return uuids;
    }

    public boolean nickInUseOnlinePlayers(@Nullable UUID uuid, String normalizedNick) {
        debug("Checking if nickname '{}' is in use (excluding UUID {})", normalizedNick, uuid);
        for (UUID playerUuid : activeNicknames.keySet()) {
            if (playerUuid.equals(uuid)) continue;
            if (activeNicknames.get(playerUuid).getNormalizedNickname().equals(normalizedNick)) {
                debug("Nickname '{}' is already in use by UUID {}", normalizedNick, playerUuid);
                return true;
            }
        }
        debug("Nickname '{}' is not in use by any online player", normalizedNick);
        return false;
    }

    public boolean saveNickname(UUID uuid, String username, String nickname) {
        debug("Saving nickname '{}' for UUID {}", nickname, uuid);
        String strippedNick = miniMessage.stripTags(nickname);
        Nickname nick = new Nickname(nickname, strippedNick);
        List<Nickname> userSavedNicknames = getSavedNicknames(uuid);
        userSavedNicknames.add(nick);
        boolean savedToSql = SqlHandler.getInstance().saveNickname(uuid, username, nickname, strippedNick);
        if (!savedToSql) {
            debug("Failed to save nickname '{}' to SQL for UUID {}", nickname, uuid);
            return false;
        }
        if (playerIsOffline(uuid)) {
            debug("Player is offline; skipping cache update after saving nickname for UUID {}", uuid);
            return true;
        }
        savedNicknames.put(uuid, userSavedNicknames);
        debug("Nickname '{}' saved and cached for UUID {}", nickname, uuid);
        return true;
    }

    public int getSavedNickCount(UUID uuid) {
        debug("Getting saved nickname count for UUID {}", uuid);
        if (savedNicknames.containsKey(uuid)) {
            int size = savedNicknames.get(uuid).size();
            debug("Found {} saved nicknames in cache for UUID {}", size, uuid);
            return size;
        }
        debug("No saved nicknames in cache for UUID {}", uuid);
        return 0;
    }

    public void removePlayerFromCache(UUID uuid) {
        debug("Removing player from cache for UUID {}", uuid);
        savedNicknames.remove(uuid);
        activeNicknames.remove(uuid);
    }

    public Map<UUID, Nickname> getOnlineNicknames(){
        debug("Getting all online nicknames from cache");
        return Collections.unmodifiableMap(activeNicknames);
    }

    private boolean playerIsOffline(UUID uuid){
        return Bukkit.getPlayer(uuid) == null;
    }

    private void debug(String message, Object... args) {
        if (ConfigHandler.getInstance().isDebugMode()) {
            logger.info("[CACHE DEBUG] {}, {}", message, args);
        }
    }


}
