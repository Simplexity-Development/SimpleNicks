package simplexity.simplenicks.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.util.Constants;

import java.util.List;

public class AdminNick implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendRichMessage(Message.ERROR_NOT_ENOUGH_ARGS.getMessage());
            return false;
        }
        Player providedPlayer = validateProvidedPlayer(args[0], sender);
        if (providedPlayer == null) return false;
        String commandArg = args[1].toLowerCase();
        switch (commandArg) {

        }





        return false;
    }

    // /adminnick user set name
    private void handleSet(CommandSender sender, Player target, String[] args){
        if (!sender.hasPermission(Constants.NICK_SET_OTHERS)) {
            sender.sendRichMessage(Message.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        if (args.length < 3) {
            sender.sendRichMessage("");
        }


    }



    @Nullable
    private Player validateProvidedPlayer(String string, CommandSender sender) {
        Player player = Bukkit.getPlayer(string);
        if (player != null) return player;
        List<Player> playersFromName = NickUtils.getInstance().getOnlinePlayersByNickname(string);
        if (playersFromName.isEmpty()) {
            sender.sendRichMessage(
                    Message.ERROR_INVALID_PLAYER.getMessage()
            );
            return null;
        }
        if (playersFromName.size() > 1) {
            sender.sendRichMessage(
                    Message.ERROR_MULTIPLE_PLAYERS_BY_THAT_NAME.getMessage()
            );
            return null;
        }
        return playersFromName.getFirst();
    }

    @Nullable
    private String getPermissionProcessedNickname(String nickname, CommandSender sender, Player player){
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_FULL)) {
            return nickname;
        }
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_BASIC)) {
            return NickUtils.getInstance().cleanNonPermittedTags(sender, nickname);
        }
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_RESTRICTIVE)) {
            return NickUtils.getInstance().cleanNonPermittedTags(player, nickname);
        }
        return null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
