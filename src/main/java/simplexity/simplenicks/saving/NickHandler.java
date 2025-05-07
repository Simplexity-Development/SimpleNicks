package simplexity.simplenicks.saving;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.util.TagPermission;

import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
public class NickHandler {

    private static NickHandler instance;

    private AbstractSaving saveHandler;
    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    private NickHandler() {
    }

    public static NickHandler getInstance() {
        if (instance != null) return instance;
        instance = new NickHandler();
        return instance;
    }


    public boolean refreshNickname(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        Nickname nickname = Cache.getInstance().getActiveNickname(uuid);
        if (nickname == null) {
            player.displayName(null);
            return true;
        }
        player.displayName(miniMessage.deserialize(ConfigHandler.getInstance().getNickPrefix() + nickname.nickname()));
        if (ConfigHandler.getInstance().shouldNickTablist()) {
            player.playerListName(miniMessage.deserialize(nickname.nickname()));
        }
        return true;
    }

    public String cleanNonPermittedTags(CommandSender user, String nick) {
        int permissionCount = 0;
        TagResolver.Builder resolver = TagResolver.builder();
        for (TagPermission tagPermission : TagPermission.values()) {
            if (user.hasPermission(tagPermission.getPermission())) {
                permissionCount++;
                resolver.resolver(tagPermission.getTagResolver());
            }
        }
        if (permissionCount == 0) {
            return miniMessage.stripTags(nick);
        }
        MiniMessage parser = MiniMessage.builder()
                .strict(false)
                .tags(resolver.build())
                .build();
        Component permissionParsed = parser.deserialize(nick);
        return miniMessage.serialize(permissionParsed);
    }
}
