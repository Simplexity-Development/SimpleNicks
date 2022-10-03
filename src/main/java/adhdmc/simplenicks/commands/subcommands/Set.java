package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Set extends SubCommand {

    private static final int MAX_NICKNAME_LENGTH = 30; // TODO: Change this temporary constant into a config option.
    public static final String NICKNAME_REGEX = "[A-Za-z0-9_]+"; // TODO: Change this temporary constant into a config option.

    public Set() {
        super("set", "sets a nickname", "/nick set");
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
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: NO ARGUMENTS")); // Invalid Arguments
            return;
        }
        if (args.length > 2) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: TOO MANY ARGUMENTS")); // Too Many Arguments
            return;
        }
        // TODO: Pull permissions from a common place.
        if (args.length == 2 && !sender.hasPermission("simplenicks.admin")) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: NO PERMISSION")); // No Permission
            return;
        }

        // Nickname Validity Check
        String nicknameStripped = miniMessage.stripTags(args[0]);
        // TODO: Allow regex to be modifiable by config.
        if (!nicknameStripped.matches(NICKNAME_REGEX)) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: NICKNAME MUST BE ALPHANUMERIC")); // Non-Alphanumeric Nickname
            return;
        }
        if (nicknameStripped.length() > MAX_NICKNAME_LENGTH) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: NICKNAME TOO LONG (>" + MAX_NICKNAME_LENGTH + ")")); // Nickname Too Long
            return;
        }

        // Valid Player Check
        Player player = (args.length == 1) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize("<gray>PLACEHOLDER: PLAYER IS INVALID / NOT ONLINE")); // Invalid Player
            return;
        }

        // Set Nickname
        // TODO: Save to Player
        Component nickname = miniMessage.deserialize(args[0]);
        player.displayName(nickname);
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
