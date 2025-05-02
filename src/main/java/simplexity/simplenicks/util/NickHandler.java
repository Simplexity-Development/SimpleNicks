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

@SuppressWarnings("UnusedReturnValue")
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

    public String getNickname(OfflinePlayer player) {
        return Cache.nicknameCache.get(player.getUniqueId());
    }

    public String loadNickname(OfflinePlayer player){
        return saveHandler.getNickname(player);
    }

    public boolean setNickname(OfflinePlayer player, String nickname) {
        if (!saveHandler.setNickname(player, nickname)) return false;
        Cache.nicknameCache.put(player.getUniqueId(), nickname);
        refreshNickname(player);
        return true;
    }

    public boolean resetNickname(OfflinePlayer player) {
        if (!saveHandler.resetNickname(player)) return false;
        Cache.nicknameCache.remove(player.getUniqueId());
        refreshNickname(player);
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
        saveHandler.init();
    }

    public boolean saveNickname(OfflinePlayer player, String nickname) {
        return saveHandler.saveNickname(player, nickname);
    }

    public boolean deleteNickname(OfflinePlayer player, String nickname) {
        return saveHandler.deleteNickname(player, nickname);
    }

    public List<String> getSavedNicknames(OfflinePlayer player) {
        return saveHandler.getSavedNicknames(player);
    }
}
