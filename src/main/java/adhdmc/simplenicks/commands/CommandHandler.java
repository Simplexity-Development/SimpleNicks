package adhdmc.simplenicks.commands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.config.ConfigDefaults;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();

        // Arguments Check
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(ConfigDefaults.Message.INVALID_COMMAND.getMessage())); // Invalid Arguments
            return true;
        }

        // Execute Command
        SubCommand subCommand = SimpleNicks.getSubCommands().getOrDefault(args[0].toLowerCase(Locale.ENGLISH), null);
        if (subCommand == null) {
            sender.sendMessage(miniMessage.deserialize(ConfigDefaults.Message.INVALID_COMMAND.getMessage())); // Invalid SubCommand
            return true;
        }
        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
}

