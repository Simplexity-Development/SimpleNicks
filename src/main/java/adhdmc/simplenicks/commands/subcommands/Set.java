package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.Config;
import adhdmc.simplenicks.config.LocaleHandler;
import adhdmc.simplenicks.util.NickHandler;
import adhdmc.simplenicks.util.SNPerm;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Set extends SubCommand {
    MiniMessage miniMessage = SimpleNicks.getMiniMessage();


    public Set() {
        super("set", "sets a nickname", "/nick set", SNPerm.NICK_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int length = Config.getInstance().getMaxLength();
        Pattern regex = Config.getInstance().getRegex();
        // Player Check
        if (!(sender instanceof Player sendingPlayer)) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getConsoleCannotRun(), Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Invalid Usage (Not a Player)
            return;
        }
        // Arguments Check
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoArguments(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Invalid Arguments
            return;
        }
        if (args.length > 2) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getTooManyArguments(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Too Many Arguments
            return;
        }
        if (args.length == 2 &&
                !(sender.hasPermission(SNPerm.NICK_OTHERS_FULL.getPermission()) ||
                sender.hasPermission(SNPerm.NICK_OTHERS_BASIC.getPermission()) ||
                sender.hasPermission(SNPerm.NICK_OTHERS_RESTRICTIVE.getPermission()))) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // No Permission
            return;
        }
        if (!sender.hasPermission(SNPerm.NICK_COMMAND.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNoPermission(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // No Permission to set own
            return;
        }
        // Nickname Validity Check
        String nicknameStripped = miniMessage.stripTags(args[0]);
        if (!regex.matcher(nicknameStripped).matches()) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidNickRegex(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()),
                    Placeholder.parsed("regex", regex.pattern()))); // Non-Alphanumeric Nickname
            return;
        }
        if (nicknameStripped.length() > length) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidNickTooLong(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()),
                    Placeholder.parsed("value", String.valueOf(length)))); // Nickname Too Long
            return;
        }
        // Valid Player Check
        Player player = (args.length == 1) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidPlayer(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()))); // Invalid Player
            return;
        }
        // Check against cached usernames
        if (!player.hasPermission(SNPerm.NICK_USERNAME_BYPASS.getPermission()) && (SimpleNicks.getInstance().getServer().getOfflinePlayerIfCached(nicknameStripped) != null) && !(nicknameStripped.equals(player.getName()))){
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getCantNickUsername(),
                    Placeholder.parsed("name", nicknameStripped),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
            return;
        }
        // TODO: [Check Requirement] Is this required? Can it be reformatted?
        Component nickname = null;
        //The checks process to go through if someone is nicknaming another player
        if (args.length == 2) {
            if (sendingPlayer.hasPermission(SNPerm.NICK_OTHERS_FULL.getPermission())){
                //Full parse, no regards for the formatting permissions of either player
                nickname = miniMessage.deserialize(args[0]);
            } else if (sendingPlayer.hasPermission(SNPerm.NICK_OTHERS_BASIC.getPermission())) {
                //Basic parse, parses based on the Admin's permissions
                if (parseMessageContent(sendingPlayer, args[0]) == null) {
                    sendingPlayer.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidTags(),
                            Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
                    return;
                }
                nickname = parseMessageContent(sendingPlayer, args[0]);
            } else if (sendingPlayer.hasPermission(SNPerm.NICK_OTHERS_RESTRICTIVE.getPermission())) {
                //Restrictive parse, parses based on the player's permissions
                if (parseMessageContent(player, args[0]) == null) {
                    sendingPlayer.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidTags(),
                            Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
                    return;
                }
                nickname = parseMessageContent(player, args[0]);
            }
        } else {
            //Player nicknaming themselves, based on their own permissions
            if (parseMessageContent(player, args[0]) == null){
                sendingPlayer.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getInvalidTags(),
                        Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
                return;
            } else {
                nickname = parseMessageContent(player, args[0]);
            }
        }
        //idk it says this might be null, I hope it's not but just in case lol
        if (nickname == null) {
            sender.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickIsNull(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
            return;
        }
        // TODO: End [Check Requirement]
        // Set Nickname
        NickHandler.getInstance().setNickname(player, args[0]);
        //Send feedback if an admin is setting someone's name, both to the admin and player
        if (sendingPlayer != player) {
            sendingPlayer.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickChangeOther(),
                    Placeholder.parsed("username", player.getName()),
                    Placeholder.component("nickname", nickname),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickChangedByOther(),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix()),
                    Placeholder.component("username", sendingPlayer.displayName()),
                    Placeholder.component("nickname", nickname)));
        } else {
            //If a player sets their own name
            player.sendMessage(miniMessage.deserialize(LocaleHandler.getInstance().getNickChangedSelf(),
                    Placeholder.component("nickname", nickname),
                    Placeholder.parsed("prefix", LocaleHandler.getInstance().getPrefix())));
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();
        if (args.length == 2 && (sender.hasPermission(SNPerm.NICK_OTHERS_FULL.getPermission()) ||
                sender.hasPermission(SNPerm.NICK_OTHERS_BASIC.getPermission()) ||
                sender.hasPermission(SNPerm.NICK_OTHERS_RESTRICTIVE.getPermission()))) {
            return null;
        }
        if (args.length == 1) return NickHandler.getInstance().getSavedNicknames(player);
        return new ArrayList<>();
    }
    //Stolen from https://github.com/YouHaveTrouble/JustChat/blob/master/src/main/java/me/youhavetrouble/justchat/JustChatListener.java#L78
    private Component parseMessageContent(Player player, String rawMessage) {
        TagResolver.Builder tagResolver = TagResolver.builder();
        //This is stupid but yk whatever
        int tempCheckSolution = 0;
        for(SNPerm perm : SNPerm.values()) {
            if (player.hasPermission(perm.getPermission()) && perm.getTagResolver() != null) {
                tagResolver.resolver(perm.getTagResolver());
                tempCheckSolution = tempCheckSolution + 1;
            }
        }
        MiniMessage nameParser = MiniMessage.builder().tags(tagResolver.build()).build();
        Component fullParsedName = miniMessage.deserialize(rawMessage);
        Component permParsedName = nameParser.deserialize(rawMessage);
        if (tempCheckSolution == 0) {
            String strippedMsg = miniMessage.stripTags(rawMessage);
            return miniMessage.deserialize(strippedMsg);
        }
        if (fullParsedName.equals(permParsedName)) return permParsedName;
        return null;
    }
}
