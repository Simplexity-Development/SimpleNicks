package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.Constants;
import simplexity.simplenicks.util.NickHandler;
import simplexity.simplenicks.util.TagPermission;

import java.util.ArrayList;
import java.util.List;

public class Set extends SubCommand {
    MiniMessage serializer = MiniMessage.builder().tags(TagResolver.empty()).build();

    public Set(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {
        if (!validateArgsLength(sender, player, args, 3)) {
            return;
        }
        String nickname = args[2];
        Component nickComponent = Component.empty();
        if (sender.hasPermission(Constants.NICK_OTHERS_FULL)) {
            nickComponent = miniMessage.deserialize(nickname);
        } else if (sender.hasPermission(Constants.NICK_OTHERS_BASIC)) {
            nickComponent = getNickComponent(sender, nickname);
        } else if (sender.hasPermission(Constants.NICK_OTHERS_RESTRICTIVE)){
            nickComponent = getNickComponent(player, nickname);
        }
        if (setPlayerNick(player, nickComponent)) {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getChangedOther(), nickname));
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getChangedByOther(), nickname));
        } else {
            sender.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getInvalidTags(), nickname));
        }
    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        if (!validateArgsLength(sender, player, args, 2)) return;
        String nickname = args[1];
        Component nickComponent = getNickComponent(sender, nickname);
        if (setPlayerNick(player, nickComponent)) {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getChangedSelf(), nickname));
        } else {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getInvalidTags(), nickname));
        }
    }

    private boolean setPlayerNick(Player player, Component nickComponent) {
        if (nickComponent == null) {
            return false;
        }
        String nickToSave = miniMessage.serialize(nickComponent);
        NickHandler.getInstance().setNickname(player, nickToSave);
        return true;
    }

    private boolean validateArgsLength(CommandSender sender, Player player, String[] args, int minArgsLength) {
        if (args.length < minArgsLength) {
            parsedMessage(sender, player, LocaleHandler.getInstance().getNotEnoughArgs(), "");
            return false;
        }
        return true;
    }


    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, Player playerPlaceholder) {
        if (playerPlaceholder == null) {
            return null;
        }
        List<String> savedNickNames = NickHandler.getInstance().getSavedNicknames(playerPlaceholder);
        return (ArrayList<String>) savedNickNames;
    }

    private Component getNickComponent(CommandSender user, String nick) {
        int permissionCount = 0;
        String strippedMessage = miniMessage.stripTags(nick);
        TagResolver.Builder resolverBuilder = TagResolver.builder();
        Component finalNick = null;
        for (TagPermission tagPermission : TagPermission.values()) {
            if (!user.hasPermission(tagPermission.getPermission())) {
                continue;
            }
            permissionCount++;
            resolverBuilder.resolver(tagPermission.getTagResolver());
            finalNick = serializer.deserialize(nick, tagPermission.getTagResolver());
        }
        if (permissionCount == 0) {
            return Component.text(strippedMessage);
        }
        String plainNick = PlainTextComponentSerializer.plainText().serialize(finalNick);
        if (!plainNick.equals(strippedMessage)) {
            return null;
        }
        return finalNick;
    }

}
