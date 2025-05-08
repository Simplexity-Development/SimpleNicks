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

    public void loadCurrentNickname(UUID uuid) {
        Nickname currentNick = SqlHandler.getInstance().getCurrentNicknameForPlayer(uuid);
        if (currentNick == null) return;
        activeNicknames.put(uuid, currentNick);
    }

    @Nullable
    public Nickname getActiveNickname(UUID uuid) {
        if (activeNicknames.containsKey(uuid)) return activeNicknames.get(uuid);
        return null;
    }

    public void loadSavedNicknames(UUID uuid) {
        List<Nickname> savedNames = SqlHandler.getInstance().getSavedNicknamesForPlayer(uuid);
        if (savedNames == null || savedNames.isEmpty()) return;
        savedNicknames.put(uuid, savedNames);
    }

    public List<Nickname> getSavedNicknames(UUID uuid) {
        if (savedNicknames.containsKey(uuid)) return savedNicknames.get(uuid);
        return new ArrayList<>();
    }

    public boolean setActiveNickname(UUID uuid, String nickname) {
        String normalizedNick = miniMessage.stripTags(nickname);
        Nickname nick = new Nickname(nickname, normalizedNick);
        boolean sqlActiveNameSet = SqlHandler.getInstance().setActiveNickname(uuid, nickname, normalizedNick);
        if (!sqlActiveNameSet) return false;
        activeNicknames.put(uuid, nick);
        return true;
    }

    public boolean deleteSavedNickname(String nickname, UUID uuid) {
        boolean sqlDeleted = SqlHandler.getInstance().deleteNickname(uuid, nickname);
        if (!sqlDeleted) return false;
        if (!savedNicknames.containsKey(uuid)) return false;
        List<Nickname> userSavedNicknames = savedNicknames.get(uuid);
        userSavedNicknames.removeIf(name -> name.nickname().equals(nickname));
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

    public boolean userAlreadySavedThis(String name, UUID uuid) {
        return SqlHandler.getInstance().userAlreadySavedThisName(uuid, name);
    }

    public boolean nickInUseActiveStorage(String name, UUID uuid) {
        String strippedNick = miniMessage.stripTags(name);
        return SqlHandler.getInstance().nickAlreadyExists(strippedNick, uuid);
    }

    public boolean nickInUseOnlinePlayers(String name, UUID uuid) {
        String strippedNick = miniMessage.stripTags(name);
        for (UUID playerUuid : activeNicknames.keySet()) {
            if (playerUuid.equals(uuid)) continue;
            if (activeNicknames.get(playerUuid).normalizedNickname().equals(strippedNick)) return true;
        }
        return false;
    }

    public boolean saveNickname(String name, UUID uuid) {
        String strippedNick = miniMessage.stripTags(name);
        Nickname nick = new Nickname(name, strippedNick);
        List<Nickname> userSavedNicknames = getSavedNicknames(uuid);
        userSavedNicknames.add(nick);
        boolean savedToSql = SqlHandler.getInstance().saveNickname(uuid, name, strippedNick);
        if (!savedToSql) return false;
        savedNicknames.put(uuid, userSavedNicknames);
        return true;
    }

    public void removePlayerFromCache(UUID uuid) {
        savedNicknames.remove(uuid);
        activeNicknames.remove(uuid);
    }
}
