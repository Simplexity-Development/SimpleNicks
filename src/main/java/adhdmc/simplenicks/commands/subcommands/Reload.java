package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.util.SNMessage;
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
            sender.sendMessage(miniMessage.deserialize(SNMessage.NO_PERMISSION.getMessage(), Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage())));
            return;
        }
        SimpleNicks.configReload();
        sender.sendMessage(miniMessage.deserialize(SNMessage.CONFIG_RELOADED.getMessage(), Placeholder.parsed("prefix", SNMessage.PREFIX.getMessage())));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
