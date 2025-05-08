package simplexity.simplenicks.commands.admin;

import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.saving.NickHandler;

import java.util.List;

public class AdminNick implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendRichMessage(Message.NOT_ENOUGH_ARGS.getMessage());
            return false;
        }


        return false;
    }

    private Player validateProvidedPlayer(String string, CommandSender sender) {
        Player player = Bukkit.getPlayer(string);
        if (player != null) return player;
        List<Player> playersFromName = NickHandler.getInstance().getOnlinePlayersByNickname(string);
        if (playersFromName.isEmpty()) {
            sender.sendRichMessage(
                    Message.INVALID_PLAYER.getMessage()
            );
            return null;
        }
        if (playersFromName.size() > 1) {

        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
