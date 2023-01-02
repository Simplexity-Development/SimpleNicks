package adhdmc.simplenicks.util.saving;

import org.bukkit.OfflinePlayer;

public interface AbstractSaving {

    String getNickname(OfflinePlayer p);
    boolean setNickname(OfflinePlayer p, String nickname);
    boolean resetNickname(OfflinePlayer p);

}
