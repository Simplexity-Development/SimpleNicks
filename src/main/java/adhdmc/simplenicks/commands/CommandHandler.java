package adhdmc.simplenicks.commands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.util.SNMessage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class CommandHandler implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();

        String[] parsedArgs = parseArgs(args);

        // Arguments Check
        if (parsedArgs.length == 0) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.INVALID_COMMAND.getMessage())); // Invalid Arguments
            return true;
        }
        // Execute Command
        SubCommand subCommand = SimpleNicks.getSubCommands().getOrDefault(parsedArgs[0].toLowerCase(Locale.ENGLISH), null);
        if (subCommand == null) {
            sender.sendMessage(miniMessage.deserialize(SNMessage.INVALID_COMMAND.getMessage())); // Invalid SubCommand
            return true;
        }
        subCommand.execute(sender, Arrays.copyOfRange(parsedArgs, 1, parsedArgs.length));
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

    private String[] parseArgs(String[] args) {
        String command = String.join(" ", args);
        List<String> arguments = new ArrayList<>();
        StringBuilder currentArgument = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            if (c == '\\' && i + 1 < command.length()) {
                currentArgument.append(command.charAt(++i));
            }
            else if (c == '\"') {
                inQuotes = !inQuotes;
            }
            else if (Character.isWhitespace(c) && !inQuotes) {
                if (currentArgument.length() > 0) {
                    arguments.add(currentArgument.toString());
                    currentArgument.setLength(0);
                }
            }
            else {
                currentArgument.append(c);
            }
        }

        if (currentArgument.length() > 0) arguments.add(currentArgument.toString());

        return arguments.toArray(new String[0]);
    }
}

