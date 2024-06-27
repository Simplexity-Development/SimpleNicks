package simplexity.simplenicks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.NickHandler;

import java.util.ArrayList;
import java.util.List;

public class Save extends SubCommand{
    public Save(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {
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
        String nameToSave = miniMessage.serialize(player.displayName());
        List<String> nameList = NickHandler.getInstance().getSavedNicknames(player);
        if (nameList.contains(nameToSave)) {
            return false;
        }
        if (nameList.size() >= ConfigHandler.getInstance().getMaxSaves()) {
            return false;
        }
        NickHandler.getInstance().saveNickname(player, nameToSave);
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, Player playerPlaceholder) {
        return null;
    }
}
