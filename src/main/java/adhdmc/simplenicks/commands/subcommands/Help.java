package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.util.Message;
import adhdmc.simplenicks.util.SimpleNickPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Help() {
        super("help", "Help command for SimpleNicks", "/nick help", SimpleNickPermission.NICK_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(miniMessage.deserialize(Message.HELP_BASE.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
        sender.sendMessage(miniMessage.deserialize(Message.HELP_SET.getMessage()));
        sender.sendMessage(miniMessage.deserialize(Message.HELP_RESET.getMessage()));
        sender.sendMessage(miniMessage.deserialize(Message.HELP_MINIMESSAGE.getMessage()));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
