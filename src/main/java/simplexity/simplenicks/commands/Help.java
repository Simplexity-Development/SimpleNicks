package simplexity.simplenicks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.config.LocaleHandler;

import java.util.ArrayList;

public class Help extends SubCommand {

    public Help(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void execute(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getShownHelp(), player.getName()));
        player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getHelpMessage(), ""));
    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getHelpMessage(), ""));
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, Player player) {
        return null;
    }
}
