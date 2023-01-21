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

import java.util.List;

public class Reset extends SubCommand {
    public Reset() {
        super("reset", "Resets a nickname", "/nick reset", SNPerm.NICK_RESET);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();

        // Player Check
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.CONSOLE_CANNOT_RUN.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()))); // Invalid Usage (Not a Player)
            return;
        }
        // Arguments Check
        if (args.length > 1) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.TOO_MANY_ARGUMENTS.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()))); // Too Many Arguments
            return;
        }
        // Admin Check
        if (args.length == 1 && !sender.hasPermission(SNPerm.NICK_RESET_OTHERS.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.NO_PERMISSION.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()))); // No Permission
            return;
        }
        // Valid Player Check
        Player player = (args.length == 0) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.INVALID_PLAYER.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()))); // Invalid Player
            return;
        }
        if (sender == player && !sender.hasPermission(SNPerm.NICK_RESET.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.TOO_MANY_ARGUMENTS.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()))); // No Permission
            return;
        }
        // Set Nickname
        // Saved to player
        NickHandler.getInstance().resetNickname(player);
        player.displayName(miniMessage.deserialize(player.getName()));
        if (player != sender) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.NICK_RESET_OTHER.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()),
                    Placeholder.parsed("username", player.getName())));
            player.sendMessage(miniMessage.deserialize(SNMessage.NICK_RESET_BY_OTHER.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage()),
                    Placeholder.component("username", ((Player) sender).displayName())));
        } else {
            player.sendMessage(miniMessage.deserialize(SNMessage.NICK_RESET_SELF.getMessage(),
                    Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage())));
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
