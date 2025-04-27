package simplexity.simplenicks.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.util.saving.AbstractSaving;
import simplexity.simplenicks.util.saving.PlayerPDC;
import simplexity.simplenicks.util.saving.YMLFile;

import java.util.List;

public class NickHandler {

    private static NickHandler instance;

    private AbstractSaving saveHandler;
    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    private NickHandler() {
    }

    public static NickHandler getInstance() {
        if (instance != null) return instance;
        instance = new NickHandler();
        instance.loadSavingType();
        return instance;
    }

    public String getNickname(OfflinePlayer p) {
        return saveHandler.getNickname(p);
    }

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
        if (nicknameRaw == null || nicknameRaw.isBlank()) {
            player.displayName(null);
        } else {
            player.displayName(miniMessage.deserialize(ConfigHandler.getInstance().getNickPrefix() + nicknameRaw));
            if (ConfigHandler.getInstance().shouldNickTablist()) {
                player.playerListName(miniMessage.deserialize(nicknameRaw));
            }
        }

        return true;
    }

    public void loadSavingType() {
        switch (ConfigHandler.getInstance().getSavingType()) {
            case PDC -> saveHandler = new PlayerPDC();
            case FILE -> saveHandler = new YMLFile();
        }
        saveHandler.init();
    }

    public boolean saveNickname(OfflinePlayer p, String nickname) {
        return saveHandler.saveNickname(p, nickname);
    }

    public boolean deleteNickname(OfflinePlayer p, String nickname) {
        return saveHandler.deleteNickname(p, nickname);
    }

    public List<String> getSavedNicknames(OfflinePlayer p) {
        return saveHandler.getSavedNicknames(p);
    }
}
