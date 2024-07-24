package simplexity.simplenicks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.command.subcommand.SetCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements TabExecutor {

    private static HashMap<String, SubCommand> subCommandList = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            // TODO: Help
            return true;
        }
        String subCommand = args[0].toLowerCase();
        if (subCommandList.containsKey(subCommand)) {
            subCommandList.get(subCommand).execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            // TODO: Send invalid command message to player.
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return new ArrayList<>();
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (String cmd : subCommandList.keySet()) {
                // TODO: if (sender.hasPermission()) && cmd.contains(args[0])) list.add(cmd);
            }
            return list;
        }
        String subcommand = args[0].toLowerCase();
        if (subCommandList.containsKey(subcommand) /* TODO: && Permission Check */) {
            return subCommandList.get(subcommand).tabComplete(sender, args);
        }
        return new ArrayList<>();
    }

    public static void registerSubCommands() {
        subCommandList.clear();
        subCommandList.put("set", new SetCommand());
    }
}
