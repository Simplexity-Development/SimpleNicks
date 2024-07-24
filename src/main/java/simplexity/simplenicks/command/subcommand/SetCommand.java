package simplexity.simplenicks.command.subcommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.command.SubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.util.Constants;
import simplexity.simplenicks.util.TagPermission;

import java.util.List;
import java.util.regex.Pattern;

public class SetCommand extends SubCommand {
    public SetCommand() {
        super("set", "Set a nickname.", "/nick set <nickname> [player]");
    }

    // FULL: Who cares about permissions? (MiniMessage.miniMessage().deserialize())
    // BASIC: Use my permissions to change target nickname.
    // RESTRICTIVE: Use target permissions to change target nickname.

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 2 || args.length == 0) {
            // TODO: Message player, invalid argument count.
            return;
        }

        CommandSender permissionHolder = null;
        Component nickname = null;
        Player target = null;
        MiniMessage parser = null;

        if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                // TODO: Message player, invalid player name.
                return;
            }
            if (sender.hasPermission(Constants.NICK_OTHERS_FULL)) {
                permissionHolder = sender;
                parser = MiniMessage.miniMessage();
                nickname = parser.deserialize(args[0]);
            }
            else if (sender.hasPermission(Constants.NICK_OTHERS_BASIC)) {
                permissionHolder = sender;
                parser = getParser(permissionHolder);
                nickname = getNickname(permissionHolder, args[0]);
            }
            else if (sender.hasPermission(Constants.NICK_OTHERS_RESTRICTIVE)) {
                permissionHolder = target;
                parser = getParser(permissionHolder);
                nickname = getNickname(permissionHolder, args[0]);
            }
            else {
                // TODO: Message player, no permission (FULL, BASIC, RESTRICTIVE)
                return;
            }
        }
        else {
            if (!(sender instanceof Player)) {
                // TODO: Message sender, cannot set nickname on non-player.
                return;
            }
            target = (Player) sender;
            permissionHolder = sender;
            nickname = getNickname(sender, args[0]);
        }

        if (!matchesRegex(nickname, permissionHolder, parser)) {
            // TODO: Message sender, fails regex check.
        }

        target.displayName(nickname);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of(); // TODO
    }

    private MiniMessage getParser(@NotNull CommandSender sender) {

        TagResolver.Builder builder = TagResolver.builder();

        for (TagPermission permission : TagPermission.values()) {
            if (sender.hasPermission(permission.getPermission())) builder.resolver(permission.getTagResolver());
        }

        return MiniMessage.builder().tags(builder.build()).build();

    }

    private Component getNickname(@NotNull CommandSender sender, @NotNull String nickname) {

        TagResolver.Builder builder = TagResolver.builder();
        TagResolver.Builder stripper = TagResolver.builder();

        for (TagPermission permission : TagPermission.values()) {
            if (sender.hasPermission(permission.getPermission())) builder.resolver(permission.getTagResolver());
            else stripper.resolver(permission.getTagResolver());
        }

        nickname = MiniMessage.builder().tags(stripper.build()).build().stripTags(nickname);
        return MiniMessage.builder().tags(builder.build()).build().deserialize(nickname);

    }

    private boolean matchesRegex(Component component, CommandSender sender, MiniMessage parser) {
        if (sender.hasPermission(Constants.NICK_REGEX_BYPASS)) return true;
        String plainName = parser.serialize(component);
        plainName = parser.stripTags(plainName);
        Pattern pattern = ConfigHandler.getInstance().getRegex();
        return pattern.matcher(plainName).matches();
    }
}
