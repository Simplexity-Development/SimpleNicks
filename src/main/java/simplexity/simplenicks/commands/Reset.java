package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.NickHandler;

import java.util.ArrayList;

public class Reset extends SubCommand {

    public Reset(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {
        resetName(player);
        sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getResetOther(), player.getName()));
        player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getResetByOther(), player.getName()));
    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        resetName(player);
        player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getResetSelf(), player.getName()));
    }

    public void resetName(Player player) {
        player.displayName(null);
        Cache.getInstance().clearCurrentNickname(player.getUniqueId());
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, Player player) {
        return null;
    }
}
