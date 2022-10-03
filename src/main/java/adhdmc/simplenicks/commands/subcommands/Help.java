package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {
    public Help() {
        super("help", "Help command for SimpleNicks", "/nick help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("<gray>PLACEHOLDER: HELP COMMAND");
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
