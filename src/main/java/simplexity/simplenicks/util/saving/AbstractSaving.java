package simplexity.simplenicks.util.saving;

import org.bukkit.OfflinePlayer;

import java.util.List;

public abstract class AbstractSaving {

    public abstract void init();

    public abstract String getNickname(OfflinePlayer p);

    public abstract boolean setNickname(OfflinePlayer p, String nickname);

    public abstract boolean saveNickname(OfflinePlayer p, String nickname);

    public abstract boolean deleteNickname(OfflinePlayer p, String nickname);

    public abstract boolean resetNickname(OfflinePlayer p);

    public abstract List<String> getSavedNicknames(OfflinePlayer p);
}
