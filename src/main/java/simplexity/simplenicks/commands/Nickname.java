package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.util.Constants;
import simplexity.simplenicks.util.NickHandler;
import simplexity.simplenicks.util.TagPermissions;

import java.util.List;
import java.util.regex.Pattern;

public class Nickname extends AbstractNickCommand {

    private final List<String> resetArgs = List.of("reset", "remove", "clear", "off");

    public Nickname(Permission basicPermission, Permission adminPermission) {
        super(basicPermission, adminPermission);
    }

    @Override
    public void runLogic(Player player, CommandSender sender, String[] args, boolean runningOnOther){
        if (runningOnOther) {
            runOnOther(sender, player, args);
        } else {
            runOnSelf(sender, player, args);
        }
    }

    //complete access, admin's perms, other player's perms

    private void runOnOther(CommandSender sender, Player player, String[] args){
        if (args.length < 2) {
            miniMessage.deserialize(LocaleHandler.getInstance().getNotEnoughArgs());
            return;
        }
        if (resetArgs.contains(args[1].toLowerCase())) {
            resetNick(sender, player);
            return;
        }
        String nickname = args[1];
        String strippedNick = miniMessage.stripTags(nickname);
        if (!validInputOrBypass(strippedNick, sender, sender)) return;
        Component nicknameComponent = adminNicknameComponent(nickname, sender, player);
        player.displayName(nicknameComponent);
        NickHandler.getInstance().setNickname(player, nickname);
        sender.sendMessage(parseMessage(sender, player, nickname, LocaleHandler.getInstance().getChangedOther()));
        player.sendMessage(parseMessage(sender, player, nickname, LocaleHandler.getInstance().getChangedByOther()));
    }

    private void runOnSelf(CommandSender sender, Player player, String[] args){
        if (args.length < 1) {
            miniMessage.deserialize(LocaleHandler.getInstance().getNotEnoughArgs());
            return;
        }
        if (resetArgs.contains(args[0].toLowerCase())) {
            resetNick(sender, player);
            return;
        }
        String nickname = args[0];
        String strippedNick = miniMessage.stripTags(nickname);
        if (!validInputOrBypass(strippedNick, sender, player)) return;
        Component nicknameComponent = ownNicknameComponent(nickname, player);
        player.displayName(nicknameComponent);
        NickHandler.getInstance().setNickname(player, nickname);
        sender.sendMessage(parseMessage(sender, player, nickname, LocaleHandler.getInstance().getChangedSelf()));
    }

    private Component ownNicknameComponent(String nickname, Player player){
        TagResolver resolver = TagPermissions.getResolversForPlayer(player);
        if (resolver == null) resolver = TagResolver.empty();
        return miniMessage.deserialize(nickname, resolver);
    }

    private Component adminNicknameComponent(String nickname, CommandSender sender, Player player){
        if (sender.hasPermission(Constants.NICK_OTHERS_FULL)) {
            return miniMessage.deserialize(nickname);
        }
        TagResolver resolver;
        if (sender.hasPermission(Constants.NICK_OTHERS_BASIC)) {
            if (!(sender instanceof Player playerSender)) return miniMessage.deserialize(nickname);
            resolver = TagPermissions.getResolversForPlayer(playerSender);
            if (resolver == null) resolver = TagResolver.empty();
            return miniMessage.deserialize(nickname, resolver);
        }
        if (sender.hasPermission(Constants.NICK_OTHERS_RESTRICTIVE)) {
            resolver = TagPermissions.getResolversForPlayer(player);
            if (resolver == null) resolver = TagResolver.empty();
            return miniMessage.deserialize(nickname, resolver);
        }
        return null;
    }

    private boolean validInputOrBypass(String strippedNick, CommandSender sender, CommandSender permUser, Player player) {
        if (nickIsTooLong(strippedNick) && !permUser.hasPermission(Constants.LENGTH_BYPASS)) {
            sender.sendMessage(parseMessage(null, null, String.valueOf(ConfigHandler.getInstance().getMaxLength()), LocaleHandler.getInstance().getInvalidNickLength()));
            return false;
        }
        if (nickBreaksRegex(strippedNick) && !permUser.hasPermission(Constants.REGEX_BYPASS)) {
            sender.sendMessage(parseMessage(null, null,  ConfigHandler.getInstance().getRegexString(), LocaleHandler.getInstance().getInvalidNick()));
            return false;
        }
        if (nickIsUsername(strippedNick) && !permUser.hasPermission(Constants.USERNAME_BYPASS)){
            sender.sendMessage(parseMessage(null, null, strippedNick, LocaleHandler.getInstance().getOtherPlayersUsername()));

            return false;
        }
        return true;
    }

    private boolean nickIsUsername(String string, Player player){
        OfflinePlayer offlinePlayer = SimpleNicks.getSimpleNicksServer().getOfflinePlayer(string);
        if (offlinePlayer.hasPlayedBefore()) {
            return true;
        }
        return false;
    }

    private boolean nickIsTooLong(String string){
        int maxLength = ConfigHandler.getInstance().getMaxLength();
        if (string.length() > maxLength){
            return true;
        }
        return false;
    }

    private boolean nickBreaksRegex(String string){
        Pattern regex = ConfigHandler.getInstance().getRegex();
        if (!regex.matcher(string).matches()){
            return true;
        }
        return false;
    }

    private void resetNick(CommandSender sender, Player player){
        if (!sender.hasPermission(Constants.NICK_RESET_OTHERS)){
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission()));
            return;
        }
        NickHandler.getInstance().resetNickname(player);
        NickHandler.getInstance().refreshNickname(player);
        player.sendMessage(parseMessage(sender, player, null, LocaleHandler.getInstance().getResetByOther()));
        sender.sendMessage(parseMessage(sender, player, null, LocaleHandler.getInstance().getResetOther()));
    }
}
