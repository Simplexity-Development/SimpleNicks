package simplexity.simplenicks.commands.subcommands;

import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.TagPermissions;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;
/*
public class Help extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    public Help() {
        super("help", "Help command for SimpleNicks", "/nick help", TagPermissions.NICK_COMMAND);
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
*/