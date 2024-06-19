package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.Config;
import adhdmc.simplenicks.config.LocaleHandler;
import adhdmc.simplenicks.util.NickHandler;
import adhdmc.simplenicks.util.SNPerm;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Save extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    public Save() {
        super("save", "Saves the current or a provided nickname.", "/nick save [nickname]", SNPerm.NICK_SAVE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Player Check
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getConsoleCannotRun(), Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Invalid Usage (Not a Player)
            return;
        }
        if (NickHandler.getInstance().getSavedNicknames(player).size() >= Config.getInstance().getMaxSaves()) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickSaveFailureTooMany(), Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Invalid Usage (Not a Player)
            return;
        }
        String nickname = NickHandler.getInstance().getNickname(player);
        if (!NickHandler.getInstance().saveNickname(player, nickname)) {
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickSaveFailure(), Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
            return;
        }
        player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickSaveSuccess(),  Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()), Placeholder.parsed("nickname", nickname)));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
