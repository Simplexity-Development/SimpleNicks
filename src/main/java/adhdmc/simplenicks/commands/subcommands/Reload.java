package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.util.SimpleNickPermission;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {
    public Reload() {
        super("reload", "Reloads SimpleNicks Config and Locale", "/nick reload", SimpleNickPermission.NICK_RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
