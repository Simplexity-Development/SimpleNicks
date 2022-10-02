package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reset extends SubCommand {
    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Reset() {
        super("reset", "Resets a nickname", "/nick reset");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
