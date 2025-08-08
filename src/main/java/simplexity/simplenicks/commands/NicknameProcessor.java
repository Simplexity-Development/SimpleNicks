package simplexity.simplenicks.commands;

import org.bukkit.OfflinePlayer;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.saving.SqlHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
public class NicknameProcessor {
    private static NicknameProcessor instance;

    public NicknameProcessor() {
    }

    public static NicknameProcessor getInstance() {
        if (instance == null) instance = new NicknameProcessor();
        return instance;
    }

    public boolean setNickname(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        String username = player.getName();
        return Cache.getInstance().setActiveNickname(playerUuid, username, nickname);
    }

    public boolean resetNickname(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        return Cache.getInstance().clearCurrentNickname(playerUuid);
    }

    public boolean saveNickname(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        String username = player.getName();
        return Cache.getInstance().saveNickname(playerUuid, username, nickname);
    }

    public boolean deleteNickname(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        return Cache.getInstance().deleteSavedNickname(playerUuid, nickname);
    }

    public List<Nickname> getSavedNicknames(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getSavedNicknames(playerUuid);
        List<Nickname> nicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(playerUuid);
        if (nicks == null) return new ArrayList<>();
        return nicks;
    }

    @Nullable
    public Nickname getCurrentNickname(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getActiveNickname(playerUuid);
        return SqlHandler.getInstance().getCurrentNicknameForPlayer(playerUuid);
    }

    public int getCurrentSavedNickCount(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getSavedNickCount(playerUuid);
        List<Nickname> savedNicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(playerUuid);
        if (savedNicks == null || savedNicks.isEmpty()) return 0;
        return savedNicks.size();
    }


    public boolean playerAlreadySavedThis(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        return SqlHandler.getInstance().userAlreadySavedThisName(playerUuid, nickname);
    }


}
