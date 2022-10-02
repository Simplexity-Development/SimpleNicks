package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {
    public Reload(String name, String description, String syntax) {
        super(name, description, syntax);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
