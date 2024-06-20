package simplexity.simplenicks.commands.subcommands;

import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.TagPermissions;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;
/*
public class Reload extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    public Reload() {
        super("reload", "Reloads SimpleNicks Config and Locale", "/nick reload", TagPermissions.NICK_RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(TagPermissions.NICK_RELOAD.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission(), Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
            return;
        }
        SimpleNicks.configReload();
        sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getConfigReload(), Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
*/