package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.util.SNMessage;
import adhdmc.simplenicks.util.SNPerm;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Help() {
        super("help", "Help command for SimpleNicks", "/nick help", SNPerm.NICK_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(miniMessage.deserialize(SNMessage.HELP_BASE.getMessage(), Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage())));
        sender.sendMessage(miniMessage.deserialize(SNMessage.HELP_SET.getMessage()));
        sender.sendMessage(miniMessage.deserialize(SNMessage.HELP_RESET.getMessage()));
        sender.sendMessage(miniMessage.deserialize(SNMessage.HELP_MINIMESSAGE.getMessage()));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
