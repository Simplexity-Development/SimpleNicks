package simplexity.simplenicks.util.saving;

import simplexity.simplenicks.SimpleNicks;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PlayerPDC extends AbstractSaving {
    public static final NamespacedKey nickNameSave = new NamespacedKey(SimpleNicks.getInstance(), "nickname");

    @Override
    public void init() {
    }

    @Override
    public String getNickname(OfflinePlayer p) {
        Player player = p.getPlayer();
        if (player == null) return null;
        PersistentDataContainer pdc = p.getPlayer().getPersistentDataContainer();
        return pdc.get(nickNameSave, PersistentDataType.STRING);
    }

    @Override
    public boolean setNickname(OfflinePlayer p, String nickname) {
        Player player = p.getPlayer();
        if (player == null) return false;
        PersistentDataContainer pdc = p.getPlayer().getPersistentDataContainer();
        pdc.set(nickNameSave, PersistentDataType.STRING, nickname);
        return true;
    }

    @Override
    public boolean saveNickname(OfflinePlayer p, String nickname) {
        return false;
    }

    @Override
    public boolean deleteNickname(OfflinePlayer p, String nickname) {
        return false;
    }

    @Override
    public boolean resetNickname(OfflinePlayer p) {
        Player player = p.getPlayer();
        if (player == null) return false;
        PersistentDataContainer pdc = p.getPlayer().getPersistentDataContainer();
        pdc.remove(nickNameSave);
        return true;
    }

    @Override
    public List<String> getSavedNicknames(OfflinePlayer p) {
        return new ArrayList<>();
    }
}
