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
    private final HashMap<UUID, ArrayList<Nickname>> savedNicknames = new HashMap<>();

    @Nullable
    public Nickname getActiveNickname(UUID uuid){
        if (activeNicknames.containsKey(uuid)) return activeNicknames.get(uuid);
        Nickname currentNick = SqlHandler.getInstance().getCurrentNicknameForPlayer(uuid);
        if (currentNick == null) return null;
        activeNicknames.put(uuid, currentNick);
        return currentNick;
    }

    public boolean setActiveNickname(UUID uuid, String nickname){
        String normalizedNick = miniMessage.stripTags(nickname);
        Nickname nick = new Nickname(nickname, normalizedNick);
        activeNicknames.put(uuid, nick);
        return SqlHandler.getInstance().setActiveNickname(uuid, nickname, normalizedNick);
    }

    public boolean deleteSavedNickname(String nickname, UUID uuid) {
        ArrayList<Nickname> userSavedNicknames = getSavedNicknames(uuid);
        userSavedNicknames.removeIf(name -> name.nickname().equals(nickname));
        savedNicknames.put(uuid, userSavedNicknames);
        return SqlHandler.getInstance().deleteNickname(uuid, nickname);
    }

    public boolean clearCurrentNickname(UUID uuid) {
        activeNicknames.remove(uuid);
        return SqlHandler.getInstance().clearActiveNickname(uuid);
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
        ArrayList<Nickname> userSavedNicknames = getSavedNicknames(uuid);
        userSavedNicknames.add(nick);
        savedNicknames.put(uuid, userSavedNicknames);
        return SqlHandler.getInstance().saveNickname(uuid, name, strippedNick);
    }

    public ArrayList<Nickname> getSavedNicknames(UUID uuid) {
        if (savedNicknames.containsKey(uuid)) return savedNicknames.get(uuid);
        ArrayList<Nickname> userSavedNicknames = SqlHandler.getInstance().getSavedNicknamesForPlayer(uuid);
        savedNicknames.put(uuid, userSavedNicknames);
        return userSavedNicknames;
    }

    public void removePlayerFromCache(UUID uuid){
        savedNicknames.remove(uuid);
        activeNicknames.remove(uuid);
    }
}
