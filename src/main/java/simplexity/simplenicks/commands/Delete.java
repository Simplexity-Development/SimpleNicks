package simplexity.simplenicks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.NickHandler;

import java.util.ArrayList;
import java.util.List;

public class Delete extends SubCommand{
    public Delete(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {
        if (!isValidArgsLength(sender, player, args, 3)) {
            return;
        }
        String nickname = args[2];
        if (removeSavedNick(sender, player, nickname)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getDeleteNick(), nickname));
        }

    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        if (!isValidArgsLength(sender, player, args, 2)) {
            return;
        }
        String nickname = args[1];
        if (removeSavedNick(sender, player, nickname)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getDeleteNick(), nickname));
        }
    }

    private boolean removeSavedNick(CommandSender sender, Player player, String nickname) {
        List<String> savedNicks = NickHandler.getInstance().getSavedNicknames(player);
        if (!savedNicks.contains(nickname)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getNameNonexistent(), nickname));
            return false;
        }
        NickHandler.getInstance().deleteNickname(player, nickname);
        NickHandler.getInstance().refreshNickname(player);
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, Player player) {
        if (player == null) {
            return null;
        }
        List<String> savedNickNames = NickHandler.getInstance().getSavedNicknames(player);
        return (ArrayList<String>) savedNickNames;
    }
}
