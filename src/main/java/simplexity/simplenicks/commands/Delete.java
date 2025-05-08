package simplexity.simplenicks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.NickHandler;
import simplexity.simplenicks.saving.Nickname;

import java.util.ArrayList;
import java.util.UUID;

public class Delete extends SubCommand{
    public Delete(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void execute(CommandSender sender, Player player, String[] args) {
        if (!validArgsLength(sender, player, args, 3)) {
            return;
        }
        String nickname = args[2];
        if (removeSavedNick(sender, player, nickname)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getDeleteNick(), nickname));
        }

    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        if (!validArgsLength(sender, player, args, 2)) {
            return;
        }
        String nickname = args[1];
        if (removeSavedNick(sender, player, nickname)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getDeleteNick(), nickname));
        }
    }

    private boolean removeSavedNick(CommandSender sender, Player player, String nickname) {
        UUID playerUuid = player.getUniqueId();
        if (!Cache.getInstance().deleteSavedNickname(nickname, playerUuid)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getNameNonexistent(), nickname));
            return false;
        }
        NickHandler.getInstance().refreshNickname(playerUuid);
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, @NotNull Player player) {
        ArrayList<String> savedNickNames = new ArrayList<>();
        for (Nickname nick : Cache.getInstance().getSavedNicknames(player.getUniqueId())) {
            savedNickNames.add(nick.nickname());
        }
        return savedNickNames;
    }
}
