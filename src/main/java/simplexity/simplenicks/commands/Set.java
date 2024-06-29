package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.Constants;
import simplexity.simplenicks.util.NickHandler;
import simplexity.simplenicks.util.TagPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Set extends SubCommand {
    MiniMessage serializer = MiniMessage.builder().tags(TagResolver.empty()).build();

    public Set(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {
        if (!isValidArgsLength(sender, player, args, 3)) {
            return;
        }
        String nickname = args[2];
        Component nickComponent = Component.empty();
        if (sender.hasPermission(Constants.NICK_OTHERS_FULL)) {
            nickComponent = miniMessage.deserialize(nickname);
        } else if (sender.hasPermission(Constants.NICK_OTHERS_BASIC)) {
            nickComponent = getNickComponent(sender, nickname);
        } else if (sender.hasPermission(Constants.NICK_OTHERS_RESTRICTIVE)) {
            nickComponent = getNickComponent(player, nickname);
        }
        if (!passesChecks(sender, nickname, player)) {
            return;
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
        if (!isValidArgsLength(sender, player, args, 2)) return;
        String nickname = args[1];
        if (!passesChecks(sender, nickname, player)) {
            return;
        }
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




    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, Player player) {
        if (player == null) {
            return null;
        }
        List<String> savedNickNames = NickHandler.getInstance().getSavedNicknames(player);
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

    private boolean passesChecks(CommandSender sender, String nickname, Player player) {
        String strippedMessage = miniMessage.stripTags(nickname);
        Pattern regexPattern = ConfigHandler.getInstance().getRegex();
        if (strippedMessage.length() > ConfigHandler.getInstance().getMaxLength() && !sender.hasPermission(Constants.NICK_LENGTH_BYPASS)) {
            sender.sendMessage(parsedMessage(sender, null, LocaleHandler.getInstance().getInvalidNickLength(), ""));
            return false;
        }
        if (!regexPattern.matcher(strippedMessage).matches() && !sender.hasPermission(Constants.NICK_REGEX_BYPASS)) {
            sender.sendMessage(parsedMessage(sender, null, LocaleHandler.getInstance().getInvalidNick(), ConfigHandler.getInstance().getRegexString()));
            return false;
        }
        OfflinePlayer playerToCheck = SimpleNicks.getInstance().getServer().getOfflinePlayer(strippedMessage);
        if (!playerToCheck.hasPlayedBefore()) {
            return true;
        }
        if (playerToCheck.getPlayer() == player) {
            return true;

        }
        if (!sender.hasPermission(Constants.NICK_USERNAME_BYPASS)) {
            long lastSeen = playerToCheck.getLastSeen();
            long now = System.currentTimeMillis();
            long diff = now - lastSeen;
            if (diff < ConfigHandler.getInstance().getUsernameProtectionTime()) {
                sender.sendMessage(parsedMessage(sender, null, LocaleHandler.getInstance().getOtherPlayersUsername(), nickname));
                return false;
            }
        }
        return true;
    }

}
