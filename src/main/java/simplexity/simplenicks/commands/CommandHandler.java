package simplexity.simplenicks.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.LocaleHandler;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements TabExecutor {
    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    private final ArrayList<String> tabComplete = new ArrayList<>();

    // /nick <arg> <user> <arg>
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNotEnoughArgs(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));

            return false;
        }
        if (args.length >= 2) {
            if (runningOnOther(sender, args)) {
                return true;
            }
        }
        commandOnSelf(sender, args);
        return true;

    }

    private boolean runningOnOther(CommandSender sender, String[] args) {
        Player player = SimpleNicks.getInstance().getServer().getPlayer(args[1]);
        if (player != null) {
            commandOnOther(sender, args, player);
            return true;
        }
        return false;
    }

    private void commandOnOther(CommandSender sender, String[] args, Player player) {
        String command = args[0].toLowerCase();
        if (!SimpleNicks.getSubCommands().containsKey(command)) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidCommand(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));
            return;
        }
        SubCommand subCommand = SimpleNicks.getSubCommands().get(command);
        if (!sender.hasPermission(subCommand.adminPermission)) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));
            return;
        }
        subCommand.executeOnOther(sender, player, args);
    }

    private void commandOnSelf(CommandSender sender, String[] args) {
        String command = args[0].toLowerCase();
        Player player = null;
        if (!SimpleNicks.getSubCommands().containsKey(command)) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidCommand(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));
            return;
        }
        SubCommand subCommand = SimpleNicks.getSubCommands().get(command);
        if (!sender.hasPermission(subCommand.basicPermission)) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));
            return;
        }
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (player == null && !subCommand.canRunWithoutPlayer()) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getMustBePlayer(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));
            return;
        }
        subCommand.executeOnSelf(sender, player, args);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        tabComplete.clear();
        int argsLength = args.length;
        switch (argsLength) {
            case 0, 1 -> {
                for (String key : SimpleNicks.getSubCommands().keySet()) {
                    if (sender.hasPermission(SimpleNicks.getSubCommands().get(key).basicPermission) ||
                            sender.hasPermission(SimpleNicks.getSubCommands().get(key).getAdminPermission())) {
                        tabComplete.add(key);
                    }
                }
            }
            case 2 -> {
                if (SimpleNicks.getSubCommands().containsKey(args[0].toLowerCase())) {
                    SubCommand subCommand = SimpleNicks.getSubCommands().get(args[0].toLowerCase());
                    if (sender.hasPermission(subCommand.adminPermission)) {
                        return null;
                    }
                }
            }
            case 3 -> {
                if (SimpleNicks.getSubCommands().containsKey(args[0].toLowerCase())) {
                    SubCommand subCommand = SimpleNicks.getSubCommands().get(args[0].toLowerCase());
                    return subCommand.tabComplete(sender);
                }
            }
        }
        return tabComplete;
    }
}
