package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.LocaleHandler;

public abstract class AbstractNickCommand implements CommandExecutor {
    public final Permission basicPermission;
    public final Permission adminPermission;
    public final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    public AbstractNickCommand(Permission basicPermission, Permission adminPermission) {
        this.basicPermission = basicPermission;
        this.adminPermission = adminPermission;
    }

    // command <player> <thing>
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            Player player = getPlayerIfUserIsAdmin(sender, args[0]);
            if (player != null) {
                runLogic(player, sender, args, true);
                return true;
            }
        }
        if (!isPlayerAndHasPermission(sender)) return false;
        Player playerSender = (Player) sender;
        runLogic(playerSender, sender, args, false);
        return true;
    }


    private Player getPlayerIfUserIsAdmin(CommandSender sender, String string) {
        if (!sender.hasPermission(adminPermission)) return null;
        return SimpleNicks.getSimpleNicksServer().getPlayer(string);
    }

    private boolean isPlayerAndHasPermission(CommandSender sender) {
        if (!sender.hasPermission(basicPermission)) {
            miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission());
            return false;
        }
        if (!(sender instanceof Player)) {
            miniMessage.deserialize(LocaleHandler.getInstance().getMustBePlayer());
            return false;
        }
        return true;
    }

    public Component parseMessage(CommandSender sender, Player player, String value, String message) {
        String pluginPrefix = LocaleHandler.getInstance().getPluginPrefix();
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
        if (value == null) {
            value = "";
        }
        return miniMessage.deserialize(message,
                Placeholder.parsed("prefix", pluginPrefix),
                Placeholder.component("initiator", senderName),
                Placeholder.component("target", playerName),
                Placeholder.parsed("value", value));
    }

    public abstract void runLogic(Player player, CommandSender sender, String[] args, boolean runningOnOther);
}
