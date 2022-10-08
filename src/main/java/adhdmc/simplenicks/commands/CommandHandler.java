package adhdmc.simplenicks.commands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.config.Locale.Message;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class CommandHandler implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();

        // Arguments Check
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(Message.INVALID_COMMAND.getMessage())); // Invalid Arguments
            return true;
        }

        // Execute Command
        SubCommand subCommand = SimpleNicks.getSubCommands().getOrDefault(args[0].toLowerCase(Locale.ENGLISH), null);
        if (subCommand == null) {
            sender.sendMessage(miniMessage.deserialize(Message.INVALID_COMMAND.getMessage())); // Invalid SubCommand
            return true;
        }
        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        ArrayList<String> tabComplete = new ArrayList<>();
        if (args.length == 1) {
            for (SubCommand subCommand : SimpleNicks.getSubCommands().values()) {
                if (sender.hasPermission(subCommand.getPermission()) && subCommand.getName().startsWith(args[0].toLowerCase(Locale.ENGLISH))) {
                    tabComplete.add(subCommand.getName());
                }
            }
        }
        else {
            SubCommand subCommand = SimpleNicks.getSubCommands().getOrDefault(args[0].toLowerCase(Locale.ENGLISH), null);
            if (subCommand == null) { return tabComplete; }
            if (sender.hasPermission(subCommand.getPermission())) {
                return subCommand.getSubcommandArguments(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return tabComplete;
    }
}

