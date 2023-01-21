package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.Config;
import adhdmc.simplenicks.util.Message;
import adhdmc.simplenicks.util.NickHandler;
import adhdmc.simplenicks.util.SimpleNickPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Save extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Save() {
        super("save", "Saves the current or a provided nickname.", "/nick save [nickname]", SimpleNickPermission.NICK_SAVE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Player Check
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(Message.CONSOLE_CANNOT_RUN.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Invalid Usage (Not a Player)
            return;
        }
        if (NickHandler.getInstance().getSavedNicknames(player).size() >= Config.getInstance().getMaxSaves()) {
            sender.sendMessage(miniMessage.deserialize(Message.NICK_SAVE_FAILURE_TOO_MANY.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Invalid Usage (Not a Player)
            return;
        }
        String nickname = NickHandler.getInstance().getNickname(player);
        if (!NickHandler.getInstance().saveNickname(player, nickname)) {
            player.sendMessage(miniMessage.deserialize(Message.NICK_SAVE_FAILURE.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
        }
        // TODO: Add command feedback for successful execution of this command.
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
