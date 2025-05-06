package simplexity.simplenicks.saving;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import simplexity.simplenicks.SimpleNicks;

import java.util.ArrayList;
import java.util.List;

public class PlayerPDC extends AbstractSaving {
    public static final NamespacedKey nickNameSave = new NamespacedKey(SimpleNicks.getInstance(), "nickname");

    @Override
    public void init() {
    }

    @Override
    public String getNickname(OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) return null;
        PersistentDataContainer pdc = offlinePlayer.getPlayer().getPersistentDataContainer();
        return pdc.get(nickNameSave, PersistentDataType.STRING);
    }

    @Override
    public boolean setNickname(OfflinePlayer offlinePlayer, String nickname) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) return false;
        PersistentDataContainer pdc = offlinePlayer.getPlayer().getPersistentDataContainer();
        pdc.set(nickNameSave, PersistentDataType.STRING, nickname);
        return true;
    }

    @Override
    public boolean saveNickname(OfflinePlayer offlinePlayer, String nickname) {
        return false;
    }

    @Override
    public boolean deleteNickname(OfflinePlayer offlinePlayer, String nickname) {
        return false;
    }

    @Override
    public boolean resetNickname(OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) return false;
        PersistentDataContainer pdc = offlinePlayer.getPlayer().getPersistentDataContainer();
        pdc.remove(nickNameSave);
        return true;
    }

    @Override
    public List<String> getSavedNicknames(OfflinePlayer offlinePlayer) {
        return new ArrayList<>();
    }
}
