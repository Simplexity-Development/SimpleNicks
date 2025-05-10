package simplexity.simplenicks.commands.admin;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.commands.NicknameProcessor;
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
        if (commandArg.equals("set")) {
            handleSet(sender, providedPlayer, args);
        }




        return false;
    }

    // /adminnick user set name
    private void handleSet(CommandSender sender, OfflinePlayer target, String[] args){
        if (!sender.hasPermission(Constants.NICK_SET_OTHERS)) {
            sender.sendRichMessage(Message.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        if (args.length < 3) {
            sender.sendRichMessage(Message.ERROR_NOT_ENOUGH_ARGS.getMessage());
            return;
        }
        String nickname = getPermissionProcessedNickname(args[2], sender, target);
        if (nickname == null) {
            sender.sendRichMessage(Message.ERROR_CANNOT_ACCESS_PLAYERS_PERMISSIONS.getMessage());
            return;
        }
        boolean setSuccessfully = NicknameProcessor.getInstance().setNickname(target, nickname);
        if (!setSuccessfully) {
            sender.sendRichMessage(Message.ERROR_SET_FAILURE.getMessage());
            return;
        }
        sender.sendRichMessage(Message.CHANGED_OTHER.getMessage(),
                Placeholder.parsed("target", target.getName()),
                Placeholder.parsed("value", nickname));
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
    private String getPermissionProcessedNickname(String nickname, CommandSender sender, OfflinePlayer player){
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_FULL)) {
            return nickname;
        }
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_BASIC)) {
            return NickUtils.getInstance().cleanNonPermittedTags(sender, nickname);
        }
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_RESTRICTIVE)) {
            if (!(player instanceof Player onlinePlayer)) return null;
            return NickUtils.getInstance().cleanNonPermittedTags(onlinePlayer, nickname);
        }
        return null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
