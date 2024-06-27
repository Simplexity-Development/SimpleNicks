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
            sendMessage(sender, LocaleHandler.getInstance().getNotEnoughArgs());
            return false;
        }
        if (args.length >= 2) {
            Player player = getPlayerFromArgs(args);
            if (player != null) {
                commandOnOther(sender, args, player);
                return true;
            }
        }
        commandOnSelf(sender, args);
        return true;

    }

    private Player getPlayerFromArgs(String[] args) {
        return SimpleNicks.getInstance().getServer().getPlayer(args[1]);
    }

    private void commandOnOther(CommandSender sender, String[] args, Player player) {
        String command = args[0].toLowerCase();
        SubCommand subCommand = SimpleNicks.getSubCommands().get(command);
        if (subCommand == null) {
            sendMessage(sender, LocaleHandler.getInstance().getInvalidCommand());
            return;
        }
        if (!sender.hasPermission(subCommand.adminPermission)) {
            sendMessage(sender, LocaleHandler.getInstance().getNoPermission());
            return;
        }
        subCommand.executeOnOther(sender, player, args);
    }

    private void commandOnSelf(CommandSender sender, String[] args) {
        String command = args[0].toLowerCase();
        SubCommand subCommand = SimpleNicks.getSubCommands().get(command);
        if (subCommand == null) {
            sendMessage(sender, LocaleHandler.getInstance().getInvalidCommand());
            return;
        }
        if (!sender.hasPermission(subCommand.basicPermission)) {
            sendMessage(sender, LocaleHandler.getInstance().getNoPermission());
            return;
        }
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (player == null && !subCommand.canRunWithoutPlayer()) {
            sendMessage(sender, LocaleHandler.getInstance().getMustBePlayer());
            return;
        }
        subCommand.executeOnSelf(sender, player, args);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        tabComplete.clear();
        Player player = getPlayerForTabComplete(sender, args);
        switch (args.length) {
            case 0, 1 -> {
                addSubCommandsToTabComplete(sender);
            }
            case 2 -> {
                SubCommand subCommand = SimpleNicks.getSubCommands().get(args[0].toLowerCase());
                if (SimpleNicks.getSubCommands().containsKey(args[0].toLowerCase())) {
                    if (sender.hasPermission(subCommand.adminPermission) && (player != null)) {
                        return null;
                    } else {
                        return subCommand.tabComplete(sender, args, player);
                    }
                }
            }
            case 3 -> {
                SubCommand subCommand = SimpleNicks.getSubCommands().get(args[0].toLowerCase());
                if (subCommand == null) {
                    return null;
                }
                return subCommand.tabComplete(sender, args, player);
            }
        }

        return tabComplete;
    }

    private Player getPlayerForTabComplete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return null;
        }
        Player player = getPlayerFromArgs(args);
        if (player != null) {
            return player;
        }
        if (sender instanceof Player playerSender) {
            return playerSender;
        }
        return null;
    }

    private void addSubCommandsToTabComplete(CommandSender sender) {
        for (String key : SimpleNicks.getSubCommands().keySet()) {
            SubCommand subCommand = SimpleNicks.getSubCommands().get(key);
            if (sender.hasPermission(subCommand.basicPermission) || sender.hasPermission(subCommand.adminPermission)) {
                tabComplete.add(key);
            }
        }
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(miniMessage.deserialize(message,
                Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));
    }
}
