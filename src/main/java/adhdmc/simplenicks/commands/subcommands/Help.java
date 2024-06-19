package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.LocaleHandler;
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
        sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHelpBase(), Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
        sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHelpSet()));
        sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHelpReset()));
        sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getHelpMinimessage()));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
