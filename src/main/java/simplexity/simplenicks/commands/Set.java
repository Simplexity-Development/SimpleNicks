package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;
import simplexity.simplenicks.saving.NickHandler;
import simplexity.simplenicks.util.TagPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Set extends SubCommand {
    Logger logger = SimpleNicks.getSimpleNicksLogger();

    public Set(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {
        if (!isValidArgsLength(sender, player, args, 3)) {
            return;
        }
        String nickname = args[2];
        nickname = processName(nickname, sender, player);
        if (!passesChecks(sender, nickname, player)) {
            return;
        }
        if (setPlayerNick(player, nickname)) {
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
        nickname = processName(nickname, sender, player);
        if (setPlayerNick(player, nickname)) {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getChangedSelf(), nickname));
        } else {
            player.sendMessage(parsedMessage(sender, player, LocaleHandler.getInstance().getInvalidTags(), nickname));
        }
    }

    private boolean setPlayerNick(Player player, String nickString) {
        if (nickString == null) {
            return false;
        }
        UUID playerUuid = player.getUniqueId();
        //todo Checks for existing versions of this nick
        //Cache.getInstance().nickInUseActiveStorage(nickString, playerUuid);
        return Cache.getInstance().setActiveNickname(playerUuid, nickString);
    }

    private String processName(String nickArg, CommandSender sender, Player player) {
        if (sender.hasPermission(Constants.NICK_OTHERS_FULL)) return nickArg;
        if (sender.hasPermission(Constants.NICK_OTHERS_BASIC)) {
            return NickHandler.getInstance().cleanNonPermittedTags(sender, nickArg);
        }
        if (sender.hasPermission(Constants.NICK_OTHERS_RESTRICTIVE)) {
            return NickHandler.getInstance().cleanNonPermittedTags(player, nickArg);
        }
        return null;
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

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args, @NotNull Player player) {
        ArrayList<String> savedNickNames = new ArrayList<>();
        for (Nickname nick : Cache.getInstance().getSavedNicknames(player.getUniqueId())) {
            savedNickNames.add(nick.nickname());
        }
        return savedNickNames;
    }

}
