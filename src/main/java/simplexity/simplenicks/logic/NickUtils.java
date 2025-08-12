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
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.NickPermission;
import simplexity.simplenicks.util.TagPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedReturnValue")
public class NickUtils {

    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();


    public static void nicknameChecks(CommandSender sender, Nickname nickname) throws CommandSyntaxException {
        String normalizedNick = nickname.getNormalizedNickname();
        if (normalizedNick.isEmpty()) {
            throw Exceptions.ERROR_EMPTY_NICK_AFTER_PARSE.create();
        }
        if (!sender.hasPermission(NickPermission.NICK_BYPASS_USERNAME.getPermission())
            && thisIsSomeonesUsername(normalizedNick)) {
            throw Exceptions.ERROR_NICKNAME_IS_SOMEONES_USERNAME.create(normalizedNick);
        }
        if (!sender.hasPermission(NickPermission.NICK_BYPASS_LENGTH.getPermission())
            && normalizedNick.length() > ConfigHandler.getInstance().getMaxLength()) {
            throw Exceptions.ERROR_LENGTH.create(normalizedNick);
        }
        if (!sender.hasPermission(NickPermission.NICK_BYPASS_REGEX.getPermission())
            && !passesRegexCheck(normalizedNick)) {
            throw Exceptions.ERROR_REGEX.create(normalizedNick);
        }
        if (ConfigHandler.getInstance().shouldOnlineNicksBeProtected()) {
            if (!sender.hasPermission(NickPermission.NICK_BYPASS_NICK_PROTECTION.getPermission())
                && someoneOnlineUsingThis(sender, normalizedNick)) {
                throw Exceptions.ERROR_SOMEONE_USING_THAT_NICKNAME.create(normalizedNick);
            }
        }
        if (ConfigHandler.getInstance().shouldOfflineNicksBeProtected()) {
            if (!sender.hasPermission(NickPermission.NICK_BYPASS_NICK_PROTECTION.getPermission())
                && someoneSavedUsingThis(sender, normalizedNick)) {
                throw Exceptions.ERROR_SOMEONE_USING_THAT_NICKNAME.create(normalizedNick);
            }
        }
    }


    public static boolean refreshDisplayName(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        Nickname nickname = Cache.getInstance().getActiveNickname(uuid);
        if (nickname == null) {
            player.displayName(null);
            return true;
        }
        player.displayName(miniMessage.deserialize(ConfigHandler.getInstance().getNickPrefix() + nickname.getNickname()));
        if (ConfigHandler.getInstance().shouldNickTablist()) {
            player.playerListName(miniMessage.deserialize(nickname.getNickname()));
        }
        return true;
    }


    public static String cleanNonPermittedTags(CommandSender user, String nick) {
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

    public static String normalizeNickname(String nickname){
        return miniMessage.stripTags(nickname).toLowerCase();
    }

    public static List<OfflinePlayer> getOfflinePlayersByNickname(String normalizedNickname) {
        List<UUID> usersWithThisName = SqlHandler.getInstance().getUuidsOfNickname(normalizedNickname);
        if (usersWithThisName == null) return null;
        if (usersWithThisName.isEmpty()) return new ArrayList<>();
        List<OfflinePlayer> playersByNick = new ArrayList<>();
        for (UUID uuid : usersWithThisName) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (!offlinePlayer.hasPlayedBefore()) continue;
            playersByNick.add(offlinePlayer);
        }
        return playersByNick;
    }

    public static boolean passesRegexCheck(String normalizedNick) {
        Pattern configRegex = ConfigHandler.getInstance().getRegex();
        return configRegex.matcher(normalizedNick).matches();
    }

    public static boolean thisIsSomeonesUsername(String normalizedName) {
        long protectionTime = ConfigHandler.getInstance().getUsernameProtectionTime();
        if (protectionTime < 0) return false;
        normalizedName = normalizedName.toLowerCase();
        return SqlHandler.getInstance().lastLoginOfUsername(normalizedName, ConfigHandler.getInstance().getUsernameProtectionTime()) != null;
    }

    public static boolean someoneOnlineUsingThis(CommandSender sender, String normalizedNick) {
        UUID playerUuid = null;
        if (sender instanceof Player playerSender) playerUuid = playerSender.getUniqueId();
        return Cache.getInstance().nickInUseOnlinePlayers(playerUuid, normalizedNick);
    }

    public static boolean someoneSavedUsingThis(CommandSender sender, String normalizedNick) {
        UUID senderUuid = null;
        if (sender instanceof Player playerSender) senderUuid = playerSender.getUniqueId();
        List<UUID> uuidsWithThis = SqlHandler.getInstance().nickAlreadySavedTo(senderUuid, normalizedNick);
        if (uuidsWithThis == null || uuidsWithThis.isEmpty()) return false;
        for (UUID uuid : uuidsWithThis) {
            if (SqlHandler.getInstance().lastLongOfUuid(uuid, ConfigHandler.getInstance().getOfflineNickProtectionTime()) != null)
                return true;
        }
        return false;
    }


}
