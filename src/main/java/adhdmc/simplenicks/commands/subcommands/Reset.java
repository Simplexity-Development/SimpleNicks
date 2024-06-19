package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.LocaleHandler;
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
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getConsoleCannotRun(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Invalid Usage (Not a Player)
            return;
        }
        // Arguments Check
        if (args.length > 1) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getTooManyArguments(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Too Many Arguments
            return;
        }
        // Admin Check
        if (args.length == 1 && !sender.hasPermission(SNPerm.NICK_RESET_OTHERS.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // No Permission
            return;
        }
        // Valid Player Check
        Player player = (args.length == 0) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidPlayer(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Invalid Player
            return;
        }
        if (sender == player && !sender.hasPermission(SNPerm.NICK_RESET.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getTooManyArguments(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // No Permission
            return;
        }
        // Set Nickname
        // Saved to player
        NickHandler.getInstance().resetNickname(player);
        player.displayName(miniMessage.deserialize(player.getName()));
        if (player != sender) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickResetOther(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()),
                    Placeholder.parsed("username", player.getName())));
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickResetByOther(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()),
                    Placeholder.component("username", ((Player) sender).displayName())));
        } else {
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickResetSelf(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
