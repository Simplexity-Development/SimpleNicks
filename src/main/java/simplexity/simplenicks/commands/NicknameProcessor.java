package simplexity.simplenicks.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.logic.NickUtils;
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

    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    public boolean setNickname(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        String username = player.getName();
        boolean online = player.isOnline();
        if (online) {
            boolean success = Cache.getInstance().setActiveNickname(playerUuid, username, nickname);
            if (!success) return false;
            NickUtils.getInstance().refreshNickname(playerUuid);
        }
        String normalizedNick = miniMessage.stripTags(nickname);
        return SqlHandler.getInstance().setActiveNickname(playerUuid, username, nickname, normalizedNick).join();
    }

    public boolean resetNickname(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        boolean success = Cache.getInstance().clearCurrentNickname(playerUuid);
        if (!success) return false;
        if (online) {
            NickUtils.getInstance().refreshNickname(playerUuid);
            return true;
        }
        return true;
    }

    public boolean saveNickname(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        String username = player.getName();
        boolean online = player.isOnline();
        boolean success = Cache.getInstance().saveNickname(playerUuid, username, nickname);
        if (!success) return false;
        if (online) {
            NickUtils.getInstance().refreshNickname(playerUuid);
            return true;
        }
        return true;
    }

    public boolean deleteNickname(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        boolean success = Cache.getInstance().deleteSavedNickname(playerUuid, nickname);
        if (!success) return false;
        if (online) {
            NickUtils.getInstance().refreshNickname(playerUuid);
            return true;
        }
        return true;
    }

    public List<Nickname> getSavedNicknames(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getSavedNicknames(playerUuid);
        List<Nickname> nicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(playerUuid).join();
        if (nicks == null) return new ArrayList<>();
        return nicks;
    }

    @Nullable
    public Nickname getCurrentNickname(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getActiveNickname(playerUuid);
        return SqlHandler.getInstance().getCurrentNicknameForPlayer(playerUuid).join();
    }

    public int getCurrentSavedNickCount(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        boolean online = player.isOnline();
        if (online) return Cache.getInstance().getSavedNickCount(playerUuid);
        List<Nickname> savedNicks = SqlHandler.getInstance().getSavedNicknamesForPlayer(playerUuid).join();
        if (savedNicks == null || savedNicks.isEmpty()) return 0;
        return savedNicks.size();
    }


    public boolean playerAlreadySavedThis(OfflinePlayer player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        return SqlHandler.getInstance().userAlreadySavedThisName(playerUuid, nickname).join();
    }


}
