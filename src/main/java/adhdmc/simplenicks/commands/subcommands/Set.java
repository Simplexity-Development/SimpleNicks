package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Set extends SubCommand {
    public Set() {
        super("set", "sets a nickname", "/nick set");
    }

    @Override
    public void execute(CommandSender player, String[] args) {

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
