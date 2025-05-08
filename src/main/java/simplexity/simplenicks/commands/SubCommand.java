package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.Message;

import java.util.ArrayList;

public abstract class SubCommand {
    public final String commandName;
    public final Permission basicPermission;
    public final Permission adminPermission;
    public final boolean canRunWithoutPlayer;
    public final MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public SubCommand(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        this.commandName = commandName;
        this.basicPermission = basicPermission;
        this.adminPermission = adminPermission;
        this.canRunWithoutPlayer = consoleRunNoPlayer;
    }

    public abstract void execute(CommandSender sender, Player player, String[] args, boolean adminCommand);

    public abstract ArrayList<String> tabComplete(CommandSender sender, String[] args, Player player);

    public String getCommandName() {
        return commandName;
    }

    public Permission getBasicPermission() {
        return basicPermission;
    }

    public Permission getAdminPermission() {
        return adminPermission;
    }

    public boolean canRunWithoutPlayer() {
        return canRunWithoutPlayer;
    }

    public Component parsedMessage(CommandSender sender, Player player, String message, String value) {
        Component senderName;
        Component playerName;
        if (sender == null) {
            senderName = Component.empty();
        } else {
            senderName = sender.name();
        }
        if (player == null) {
            playerName = Component.empty();
        } else {
            playerName = player.name();
        }
        return miniMessage.deserialize(message,
                Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage()),
                Placeholder.component("initiator", senderName),
                Placeholder.component("target", playerName),
                Placeholder.parsed("value", value));
    }

    public boolean validArgsLength(String[] args, int minArgsLength) {
        return args.length >= minArgsLength;
    }
}
