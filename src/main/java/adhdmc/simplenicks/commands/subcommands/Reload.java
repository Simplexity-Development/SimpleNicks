package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.ConfigUtils;
import adhdmc.simplenicks.config.Locale;
import adhdmc.simplenicks.util.Message;
import adhdmc.simplenicks.util.SimpleNickPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Reload() {
        super("reload", "Reloads SimpleNicks Config and Locale", "/nick reload", SimpleNickPermission.NICK_RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(SimpleNickPermission.NICK_RELOAD.getPermission())){
            sender.sendMessage(miniMessage.deserialize(Message.NO_PERMISSION.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
            return;
        }
        Locale.getInstance().reloadConfig();
        Locale.getInstance().loadLocaleMessages();
        ConfigUtils.reloadConfigValues();
        sender.sendMessage(miniMessage.deserialize(Message.CONFIG_RELOADED.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));


    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
