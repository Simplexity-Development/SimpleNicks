package adhdmc.simplenicks.commands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.config.ConfigValidator;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import adhdmc.simplenicks.config.ConfigValidator.Message;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;


public class CommandHandler implements CommandExecutor {

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

}

