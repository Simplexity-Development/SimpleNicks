package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.NickHandler;
import simplexity.simplenicks.util.TagPermission;

import java.util.ArrayList;

public class Set extends SubCommand {
    MiniMessage serializer = MiniMessage.builder().tags(TagResolver.empty()).build();

    public Set(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getNotEnoughArgs(), ""));
            return;
        }
        String nickname = args[2];
        if (setPlayerNick(player, nickname)){
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getChangedOther(), nickname));
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getChangedByOther(), nickname));
        } else {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getInvalidTags(), nickname));
        }
    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getNotEnoughArgs(), ""));
            return;
        }
        String nickname = args[1];
        if (setPlayerNick(player, nickname)){
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getChangedSelf(), nickname));
        } else {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getInvalidTags(), nickname));
        }
    }

    private boolean setPlayerNick(Player player, String nickname) {
        Component nickComponent = getNickComponent(player, nickname);
        if (nickComponent == null) {
            return false;
        }
        String nickToSave = miniMessage.serialize(nickComponent);
        NickHandler.getInstance().setNickname(player, nickToSave);
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender) {
        return null;
    }

    private Component getNickComponent(Player player, String nick) {
        int i = 0;
        String strippedMessage = miniMessage.stripTags(nick);
        TagResolver.Builder resolverBuilder = TagResolver.builder();
        Component finalNick = null;
        for (TagPermission tagPermission : TagPermission.values()) {
            if (!player.hasPermission(tagPermission.getPermission())) {
                continue;
            }
            i++;
            resolverBuilder.resolver(tagPermission.getTagResolver());
            finalNick = serializer.deserialize(nick, tagPermission.getTagResolver());
        }
        if (i == 0) {
            return Component.text(strippedMessage);
        }
        String plainNick = PlainTextComponentSerializer.plainText().serialize(finalNick);
        if (!plainNick.equals(strippedMessage)) {
            return null;
        }
        return finalNick;
    }
}
