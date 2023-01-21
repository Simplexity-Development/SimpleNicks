package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.util.SNMessage;
import adhdmc.simplenicks.util.NickHandler;
import adhdmc.simplenicks.util.SNPerm;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Delete extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Delete() {
        super("delete", "Deletes a given nickname.", "/nick delete <nickname>", SNPerm.NICK_DELETE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Player Check
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.CONSOLE_CANNOT_RUN.getMessage(), Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()))); // Invalid Usage (Not a Player)
            return;
        }
        String nickname = NickHandler.getInstance().getNickname(player);
        if (!NickHandler.getInstance().deleteNickname(player, nickname)) {
            player.sendMessage(miniMessage.deserialize(SNMessage.NICK_DELETE_FAILURE.getMessage(), Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage())));
        }
        player.sendMessage(miniMessage.deserialize(SNMessage.NICK_DELETE_SUCCESS.getMessage(), Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()), Placeholder.parsed("nickname", nickname)));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();
        if (args.length == 1) return NickHandler.getInstance().getSavedNicknames(player);
        return new ArrayList<>();
    }
}
