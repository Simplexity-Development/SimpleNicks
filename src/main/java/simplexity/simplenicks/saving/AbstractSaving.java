package simplexity.simplenicks.saving;

import org.bukkit.OfflinePlayer;

import java.util.List;

public abstract class AbstractSaving {

    public abstract void init();

    public abstract String getNickname(OfflinePlayer offlinePlayer);

    public abstract boolean setNickname(OfflinePlayer offlinePlayer, String nickname);

    public abstract boolean saveNickname(OfflinePlayer offlinePlayer, String nickname);

    public abstract boolean deleteNickname(OfflinePlayer offlinePlayer, String nickname);

    public abstract boolean resetNickname(OfflinePlayer offlinePlayer);

    public abstract List<String> getSavedNicknames(OfflinePlayer offlinePlayer);
}
