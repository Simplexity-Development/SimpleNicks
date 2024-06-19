package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.LocaleHandler;
import adhdmc.simplenicks.util.SNPerm;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Reload() {
        super("reload", "Reloads SimpleNicks Config and Locale", "/nick reload", SNPerm.NICK_RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(SNPerm.NICK_RELOAD.getPermission())){
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
