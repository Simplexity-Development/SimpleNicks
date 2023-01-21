package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.util.Message;
import adhdmc.simplenicks.util.NickHandler;
import adhdmc.simplenicks.util.SimpleNickPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Delete extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Delete() {
        super("delete", "Deletes a given nickname.", "/nick delete <nickname>", SimpleNickPermission.NICK_DELETE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Player Check
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(Message.CONSOLE_CANNOT_RUN.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Invalid Usage (Not a Player)
            return;
        }
        String nickname = NickHandler.getInstance().getNickname(player);
        if (!NickHandler.getInstance().deleteNickname(player, nickname)) {
            player.sendMessage(miniMessage.deserialize(Message.NICK_DELETE_FAILURE.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
        }
        // TODO: Add command feedback for successful execution of this command.
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();
        if (args.length == 1) return NickHandler.getInstance().getSavedNicknames(player);
        return new ArrayList<>();
    }
}
