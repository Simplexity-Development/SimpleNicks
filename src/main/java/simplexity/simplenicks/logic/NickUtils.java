package simplexity.simplenicks.logic;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.saving.AbstractSaving;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.Constants;
import simplexity.simplenicks.util.TagPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedReturnValue")
public class NickUtils {

    private static NickUtils instance;

    private AbstractSaving saveHandler;
    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    private NickUtils() {
    }

    public static NickUtils getInstance() {
        if (instance != null) return instance;
        instance = new NickUtils();
        return instance;
    }

    public void nicknameChecks(CommandSender sender, Nickname nickname) throws CommandSyntaxException {
        if (nickname.normalizedNickname().isEmpty()) {
            throw Exceptions.ERROR_EMPTY_NICK_AFTER_PARSE.create();
        }
        if (!sender.hasPermission(Constants.NICK_USERNAME_BYPASS) && thisIsSomeonesUsername(nickname.normalizedNickname())) {
            throw Exceptions.ERROR_NICKNAME_IS_SOMEONES_USERNAME.create(nickname.normalizedNickname());
        }
        if (!sender.hasPermission(Constants.NICK_LENGTH_BYPASS) && nickname.normalizedNickname().length() > ConfigHandler.getInstance().getMaxLength()) {
            throw Exceptions.ERROR_LENGTH.create(nickname);
        }
        if (!sender.hasPermission(Constants.NICK_REGEX_BYPASS) && !passesRegexCheck(nickname.normalizedNickname())) {
            throw Exceptions.ERROR_REGEX.create(nickname);
        }
        if (!sender.hasPermission(Constants.NICK_PROTECTION_BYPASS) && someoneOnlineUsingThis(sender, nickname.normalizedNickname())) {
            throw Exceptions.ERROR_SOMEONE_USING_THAT_NICKNAME.create(nickname);
        }
        if (!sender.hasPermission(Constants.NICK_PROTECTION_BYPASS) && someoneSavedUsingThis(sender, nickname.normalizedNickname())) {
            throw Exceptions.ERROR_NICKNAME_IS_SOMEONES_USERNAME.create(nickname);
        }
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


    public List<Player> getOnlinePlayersByNickname(String nickname) {
        List<UUID> usersWithThisName = Cache.getInstance().getUuidOfNormalizedName(nickname);
        if (usersWithThisName.isEmpty()) return new ArrayList<>();
        List<Player> playersByNick = new ArrayList<>();
        for (UUID uuid : usersWithThisName) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            playersByNick.add(player);
        }
        return playersByNick;
    }

    public List<OfflinePlayer> getOfflinePlayersByNickname(String nickname) {
        List<UUID> usersWithThisName = Cache.getInstance().getUuidOfNormalizedName(nickname);
        if (usersWithThisName.isEmpty()) return new ArrayList<>();
        List<OfflinePlayer> playersByNick = new ArrayList<>();
        for (UUID uuid : usersWithThisName) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (!offlinePlayer.hasPlayedBefore()) continue;
            playersByNick.add(offlinePlayer);
        }
        return playersByNick;
    }

    public boolean passesRegexCheck(String normalizedNick) {
        Pattern configRegex = ConfigHandler.getInstance().getRegex();
        Matcher matcher = configRegex.matcher(normalizedNick);
        return !matcher.find();
    }

    public boolean thisIsSomeonesUsername(String normalizedName) {
        long protectionTime = ConfigHandler.getInstance().getUsernameProtectionTime();
        if (protectionTime < 0) return false;
        OfflinePlayer player = Bukkit.getOfflinePlayer(normalizedName);
        long lastSeen = player.getLastSeen();
        if (lastSeen == 0) return false;
        long timeSinceSeen = System.currentTimeMillis() - lastSeen;
        return timeSinceSeen <= protectionTime;
    }

    public boolean someoneOnlineUsingThis(CommandSender sender, String normalizedNick) {
        if (!ConfigHandler.getInstance().shouldOnlineNicksBeProtected()) return false;
        UUID playerUuid = null;
        if (sender instanceof Player playerSender) playerUuid = playerSender.getUniqueId();
        return Cache.getInstance().nickInUseOnlinePlayers(playerUuid, normalizedNick);
    }

    public boolean someoneSavedUsingThis(CommandSender sender, String nickname) {
        if (!ConfigHandler.getInstance().shouldOfflineNicksBeProtected()) return false;
        UUID playerUuid = null;
        if (sender instanceof Player playerSender) playerUuid = playerSender.getUniqueId();
        String strippedNick = miniMessage.stripTags(nickname);
        UUID userWhoHasThis = SqlHandler.getInstance().nickAlreadySavedTo(playerUuid, strippedNick);
        if (userWhoHasThis == null) return false;
        OfflinePlayer player = Bukkit.getOfflinePlayer(userWhoHasThis);
        long lastSeen = player.getLastSeen();
        long timeToProtect = ConfigHandler.getInstance().getOfflineNickProtectionTime();
        long currentTime = System.currentTimeMillis();
        return currentTime - lastSeen < timeToProtect;
    }


}
