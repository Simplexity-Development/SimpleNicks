package adhdmc.simplenicks.commands.subcommands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.SubCommand;
import adhdmc.simplenicks.config.ConfigUtils;
import adhdmc.simplenicks.util.Message;
import adhdmc.simplenicks.util.SimpleNickPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Set extends SubCommand {

    public static final NamespacedKey nickNameSave = new NamespacedKey(SimpleNicks.getInstance(), "nickname");

    private final int MAX_NICKNAME_LENGTH = ConfigUtils.getMaxNicknameLength();// TODO: Change this temporary constant into a config option.
    private final String NICKNAME_REGEX = ConfigUtils.getNicknameRegex(); // TODO: Change this temporary constant into a config option.

    public Set() {
        super("set", "sets a nickname", "/nick set", SimpleNickPermission.NICK_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();

        // Player Check
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize(Message.CONSOLE_CANNOT_RUN.getMessage())); // Invalid Usage (Not a Player)
            return;
        }

        // Arguments Check
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(Message.NO_ARGUMENTS.getMessage())); // Invalid Arguments
            return;
        }
        if (args.length > 2) {
            sender.sendMessage(miniMessage.deserialize(Message.TOO_MANY_ARGUMENTS.getMessage())); // Too Many Arguments
            return;
        }
        if (args.length == 2 && !sender.hasPermission(SimpleNickPermission.NICK_ADMIN.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(Message.NO_PERMISSION.getMessage())); // No Permission
            return;
        }
        if (!sender.hasPermission(SimpleNickPermission.NICK_COMMAND.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(Message.NO_PERMISSION.getMessage())); // No Permission to set own
            return;
        }

        // Nickname Validity Check
        String nicknameStripped = miniMessage.stripTags(args[0]);
        // TODO: Allow regex to be modifiable by config.
        // TODO: Check if the person has permissions to use the tags, perms & their connected tags are in ConfigDefaults - RhythmicSys
        if (!nicknameStripped.matches(NICKNAME_REGEX)) {
            sender.sendMessage(miniMessage.deserialize(Message.INVALID_NICK_REGEX.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Non-Alphanumeric Nickname
            return;
        }
        if (nicknameStripped.length() > MAX_NICKNAME_LENGTH) {
            sender.sendMessage(miniMessage.deserialize(Message.INVALID_NICK_TOO_LONG.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Nickname Too Long
            return;
        }
        // Valid Player Check
        Player player = (args.length == 1) ? (Player) sender : SimpleNicks.getInstance().getServer().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(miniMessage.deserialize(Message.INVALID_PLAYER.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage()))); // Invalid Player
            return;
        }
        // Check against cached usernames
        if (!player.hasPermission(SimpleNickPermission.NICK_USERNAME_BYPASS.getPermission()) && (SimpleNicks.getInstance().getServer().getOfflinePlayerIfCached(nicknameStripped) != null) && !(nicknameStripped.equals(player.getName()))){
            sender.sendMessage(miniMessage.deserialize(Message.CANNOT_NICK_USERNAME.getMessage(), Placeholder.parsed("name", nicknameStripped), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
            return;
        }

        // Set Nickname
        // Saves to PDC
        //temporary saving option
        String nickToSave = args[0];
        //TODO: config option for admin settings to be restricted to their own permissions
        Component nickname;
        if (sender.hasPermission(SimpleNickPermission.NICK_ADMIN.getPermission())) {
            nickname = miniMessage.deserialize(args[0]);
        } else {
            Component fullyParsed = miniMessage.deserialize(args[0]);
            Component permissionParsed = parseMessageContent(player, args[0]);
            if (fullyParsed.equals(permissionParsed)){
                nickname = permissionParsed;
            } else {
                sender.sendMessage(miniMessage.deserialize(Message.INVALID_TAGS.getMessage(), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
                return;
            }
        }
        player.getPersistentDataContainer().set(nickNameSave, PersistentDataType.STRING, nickToSave);
        //---
        player.displayName(nickname);
        player.sendMessage(miniMessage.deserialize(Message.NICK_CHANGED_SELF.getMessage(), Placeholder.component("nickname", nickname), Placeholder.parsed("prefix", Message.PREFIX.getMessage())));
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        ArrayList<String> tabComplete = new ArrayList<>();
        if (args.length == 2 && sender.hasPermission(SimpleNickPermission.NICK_ADMIN.getPermission())) {
            return null;
        }
        if (args.length == 1) {
            String name = sender.getName();
            addValidTabOption(sender, args[0], "<b>" + name, SimpleNickPermission.NICK_BOLD, tabComplete);
            addValidTabOption(sender, args[0], "<i>" + name, SimpleNickPermission.NICK_ITALIC, tabComplete);
            addValidTabOption(sender, args[0], "<u>" + name, SimpleNickPermission.NICK_UNDERLINE, tabComplete);
            addValidTabOption(sender, args[0], "<obf>" + name, SimpleNickPermission.NICK_OBFUSCATED, tabComplete);
            addValidTabOption(sender, args[0], "<st>" + name, SimpleNickPermission.NICK_STRIKETHROUGH, tabComplete);
            addValidTabOption(sender, args[0], "<gradient:dark_purple:blue>" + name, SimpleNickPermission.NICK_GRADIENT, tabComplete);
            addValidTabOption(sender, args[0], "<#FFC0CB>" + name, SimpleNickPermission.NICK_COLOR, tabComplete);
            addValidTabOption(sender, args[0], "<blue>" + name, SimpleNickPermission.NICK_COLOR, tabComplete);
            addValidTabOption(sender, args[0], "<rainbow>" + name, SimpleNickPermission.NICK_RAINBOW, tabComplete);
        }
        return tabComplete;
    }

    private void addValidTabOption(CommandSender sender, String arg, String option, SimpleNickPermission perm, ArrayList<String> list) {
        if (sender.hasPermission(perm.getPermission())) {
            if (option.startsWith(arg)) list.add(option);
        }
    }
    //Stolen from https://github.com/YouHaveTrouble/JustChat/blob/master/src/main/java/me/youhavetrouble/justchat/JustChatListener.java#L78
    private Component parseMessageContent(Player player, String rawMessage) {
        TagResolver.Builder tagResolver = TagResolver.builder();

        for(SimpleNickPermission perm : SimpleNickPermission.values()) {
            if (player.hasPermission(perm.getPermission()) && perm.getTagResolver() != null) {
                tagResolver.resolver(perm.getTagResolver());
            }
        }
        MiniMessage nameParser = MiniMessage.builder().tags(tagResolver.build()).build();
        return nameParser.deserialize(rawMessage);
    }

}
