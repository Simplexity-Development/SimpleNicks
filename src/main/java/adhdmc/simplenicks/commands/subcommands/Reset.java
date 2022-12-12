package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.util.Message;
import adhdmc.simplenicks.util.SimpleNickPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Reset extends SubCommand {
    public Reset() {
        super("reset", "Resets a nickname", "/nick reset", SimpleNickPermission.NICK_RESET);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();

        // Player Check
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize(Message.CONSOLE_CANNOT_RUN.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Invalid Usage (Not a Player)
            return;
        }
        // Arguments Check
        if (args.length > 1) {
            sender.sendMessage(miniMessage.deserialize(Message.TOO_MANY_ARGUMENTS.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Too Many Arguments
            return;
        }
        // Admin Check
        if (args.length == 1 && !sender.hasPermission(SimpleNickPermission.NICK_RESET_OTHERS.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(Message.NO_PERMISSION.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // No Permission
            return;
        }
        // Valid Player Check
        Player player = (args.length == 0) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize(Message.INVALID_PLAYER.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Invalid Player
            return;
        }
        if (sender == player && !sender.hasPermission(SimpleNickPermission.NICK_RESET.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(Message.TOO_MANY_ARGUMENTS.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // No Permission
            return;
        }
        // Set Nickname
        // Saved to player
        String playerPDCString = player.getPersistentDataContainer().get(Set.nickNameSave, PersistentDataType.STRING);
        if (playerPDCString != null)
            player.getPersistentDataContainer().remove(Set.nickNameSave); //held name for temp saving option
        player.displayName(miniMessage.deserialize(player.getName()));
        if (player != sender) {
            sender.sendMessage(miniMessage.deserialize(Message.NICK_RESET_OTHER.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()),
                    Placeholder.parsed("username", player.getName())));
            player.sendMessage(miniMessage.deserialize(Message.NICK_RESET_BY_OTHER.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage()),
                    Placeholder.component("username", ((Player) sender).displayName())));
        } else {
            player.sendMessage(miniMessage.deserialize(Message.NICK_RESET_SELF.getMessage(),
                    Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
