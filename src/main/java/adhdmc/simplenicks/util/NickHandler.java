package adhdmc.simplenicks.util;

import adhdmc.simplenicks.config.Config;
import adhdmc.simplenicks.util.saving.AbstractSaving;
import adhdmc.simplenicks.util.saving.PlayerPDC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class NickHandler {

    private static NickHandler instance;

    private AbstractSaving saveHandler;

    private NickHandler() {}

    public static NickHandler getInstance() {
        if (instance != null) return instance;
        instance = new NickHandler();
        instance.loadSavingType();
        return instance;
    }

    public String getNickname(OfflinePlayer p) { return saveHandler.getNickname(p); }
    public boolean setNickname(OfflinePlayer p, String nickname) {
        if (!saveHandler.setNickname(p, nickname)) return false;
        refreshNickname(p);
        return true;
    }

    public boolean resetNickname(OfflinePlayer p) {
        if (!saveHandler.resetNickname(p)) return false;
        refreshNickname(p);
        return true;
    }

    public boolean refreshNickname(OfflinePlayer p) {
        Player player = p.getPlayer();
        if (player == null) return false;
        String nicknameRaw = getNickname(p);
        if (nicknameRaw == null || nicknameRaw.isBlank()) player.displayName(null);
        else player.displayName(MiniMessage.miniMessage().deserialize(nicknameRaw));
        return true;
    }

    public void loadSavingType() {
        switch (Config.getInstance().getSavingType()) {
            case PDC -> saveHandler = new PlayerPDC();
            // TODO: Implement FILE
            default -> saveHandler = new PlayerPDC();
        }
    }
}
