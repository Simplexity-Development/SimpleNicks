package simplexity.simplenicks.saving;

import net.kyori.adventure.text.minimessage.MiniMessage;
import simplexity.simplenicks.SimpleNicks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Cache {
    private static Cache instance;

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
        Nickname currentNick = SqlHandler.getInstance().getCurrentNicknameForPlayer(uuid);
        if (currentNick == null) return;
        activeNicknames.put(uuid, currentNick);
    }

    /**
     * Gets the active nickname from cache,
     *
     * @param uuid Player uuid
     * @return Nickname player has set, null if no nickname is set
     */
    @Nullable
    public Nickname getActiveNickname(UUID uuid) {
        if (activeNicknames.containsKey(uuid)) return activeNicknames.get(uuid);
        return null;
    }

    /**
     * Loads a player's saved nicknames into the cache from SQL
     *
     * @param uuid Player's UUID
     */
    public void loadSavedNicknames(UUID uuid) {
        List<Nickname> savedNames = SqlHandler.getInstance().getSavedNicknamesForPlayer(uuid);
        if (savedNames == null || savedNames.isEmpty()) return;
        savedNicknames.put(uuid, savedNames);
    }

    /**
     * Gets the saved nicknames from the cache
     *
     * @param uuid Player UUID
     * @return List<Nickname> - if no nicks exist, returns empty list
     */
    public List<Nickname> getSavedNicknames(UUID uuid) {
        if (savedNicknames.containsKey(uuid)) return savedNicknames.get(uuid);
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
        String normalizedNick = miniMessage.stripTags(nickname);
        Nickname nick = new Nickname(nickname, normalizedNick);
        boolean sqlActiveNameSet = SqlHandler.getInstance().setActiveNickname(uuid, username, nickname, normalizedNick);
        if (!sqlActiveNameSet) return false;
        activeNicknames.put(uuid, nick);
        return true;
    }

    public boolean deleteSavedNickname(UUID uuid, String nickname) {
        boolean sqlDeleted = SqlHandler.getInstance().deleteNickname(uuid, nickname);
        if (!sqlDeleted) return false;
        if (!savedNicknames.containsKey(uuid)) return false;
        List<Nickname> userSavedNicknames = savedNicknames.get(uuid);
        userSavedNicknames.removeIf(name -> name.getNickname().equals(nickname));
        savedNicknames.put(uuid, userSavedNicknames);
        return true;
    }

    public boolean clearCurrentNickname(UUID uuid) {
        boolean sqlNickRemoved = SqlHandler.getInstance().clearActiveNickname(uuid);
        if (!sqlNickRemoved) return false;
        activeNicknames.remove(uuid);
        return true;
    }

    public List<UUID> getUuidOfNormalizedName(String nickname) {
        List<UUID> uuids = SqlHandler.getInstance().getUuidsOfNickname(nickname);
        if (uuids == null) return new ArrayList<>();
        return uuids;
    }

    public boolean nickInUseOnlinePlayers(@Nullable UUID uuid, String normalizedNick) {
        for (UUID playerUuid : activeNicknames.keySet()) {
            if (playerUuid.equals(uuid)) continue;
            if (activeNicknames.get(playerUuid).getNormalizedNickname().equals(normalizedNick)) return true;
        }
        return false;
    }

    public boolean saveNickname(UUID uuid, String username, String nickname) {
        String strippedNick = miniMessage.stripTags(nickname);
        Nickname nick = new Nickname(nickname, strippedNick);
        List<Nickname> userSavedNicknames = getSavedNicknames(uuid);
        userSavedNicknames.add(nick);
        boolean savedToSql = SqlHandler.getInstance().saveNickname(uuid, username, nickname, strippedNick);
        if (!savedToSql) return false;
        savedNicknames.put(uuid, userSavedNicknames);
        return true;
    }

    public int getSavedNickCount(UUID uuid) {
        if (savedNicknames.containsKey(uuid)) return savedNicknames.get(uuid).size();
        return 0;
    }

    public void removePlayerFromCache(UUID uuid) {
        savedNicknames.remove(uuid);
        activeNicknames.remove(uuid);
    }


}
