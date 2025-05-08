package simplexity.simplenicks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.NickHandler;
import simplexity.simplenicks.saving.Nickname;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Save extends SubCommand{
    public Save(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void execute(CommandSender sender, Player player, String[] args) {
        if (savePlayerNick(player)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getSaveNick(), miniMessage.serialize(player.displayName())));
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getSaveNick(), miniMessage.serialize(player.displayName())));
        } else {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getSaveFailure(), String.valueOf(ConfigHandler.getInstance().getMaxSaves())));
        }
    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        if (savePlayerNick(player)) {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getSaveNick(), miniMessage.serialize(player.displayName())));
        } else {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getSaveFailure(), String.valueOf(ConfigHandler.getInstance().getMaxSaves())));
        }
    }

    public boolean savePlayerNick(Player player) {
        UUID playerUuid = player.getUniqueId();
        Nickname nick = Cache.getInstance().getActiveNickname(playerUuid);
        if (nick == null) {
            return false;
        }
        List<Nickname> nameList = Cache.getInstance().getSavedNicknames(playerUuid);
        if (nameList == null) nameList = new ArrayList<>();
        if (nameList.size() >= ConfigHandler.getInstance().getMaxSaves()) {
            return false;
        }
        Cache.getInstance().saveNickname(nick.nickname(), playerUuid);
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, Player player) {
        return null;
    }
}
