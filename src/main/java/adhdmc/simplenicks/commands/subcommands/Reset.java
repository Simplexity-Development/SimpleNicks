package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.ConfigDefaults;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

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
            sender.sendMessage(miniMessage.deserialize(ConfigDefaults.Message.CONSOLE_CANNOT_RUN.getMessage())); // Invalid Usage (Not a Player)
            return;
        }
        // Arguments Check
        if (args.length > 1) {
            sender.sendMessage(miniMessage.deserialize(ConfigDefaults.Message.TOO_MANY_ARGUMENTS.getMessage())); // Too Many Arguments
            return;
        }
        // TODO: Pull permissions from a common place.
        if (args.length == 1 && !sender.hasPermission(ConfigDefaults.SimpleNickPermission.NICK_ADMIN.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(ConfigDefaults.Message.NO_PERMISSION.getMessage())); // No Permission
            return;
        }
        // Valid Player Check
        Player player = (args.length == 0) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize(ConfigDefaults.Message.INVALID_PLAYER.getMessage())); // Invalid Player
            return;
        }
        if (sender == player && !sender.hasPermission(ConfigDefaults.SimpleNickPermission.NICK_RESET.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(ConfigDefaults.Message.TOO_MANY_ARGUMENTS.getMessage())); // No Permission
            return;
        }
        // Set Nickname
        // TODO: Save to Player
        String playerPDCString = player.getPersistentDataContainer().get(Set.nickNameSave, PersistentDataType.STRING);
        if (playerPDCString != null) player.getPersistentDataContainer().remove(Set.nickNameSave); //held name for temp saving option
        player.displayName(miniMessage.deserialize(player.getName()));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
