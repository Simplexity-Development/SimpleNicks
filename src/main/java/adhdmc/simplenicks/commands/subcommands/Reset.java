package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Reset extends SubCommand {
    public Reset() {
        super("reset", "Resets a nickname", "/nick reset");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();

        // Player Check
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: NOT A PLAYER")); // Invalid Usage (Not a Player)
            return;
        }

        // Arguments Check
        if (args.length > 1) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: TOO MANY ARGUMENTS")); // Too Many Arguments
            return;
        }
        // TODO: Pull permissions from a common place.
        if (args.length == 1 && !sender.hasPermission("simplenicks.admin")) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: NO PERMISSION")); // No Permission
            return;
        }

        // Valid Player Check
        Player player = (args.length == 0) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: PLAYER IS INVALID / NOT ONLINE")); // Invalid Player
            return;
        }

        // Set Nickname
        // TODO: Save to Player
        player.displayName(miniMessage.deserialize(player.getName()));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
