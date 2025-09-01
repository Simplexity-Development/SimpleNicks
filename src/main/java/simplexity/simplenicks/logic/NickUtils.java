package simplexity.simplenicks.logic;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.ColorTag;
import simplexity.simplenicks.util.FormatTag;
import simplexity.simplenicks.util.NickPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for handling nicknames in SimpleNicks.
 * <p>
 * This class contains methods for validating, normalizing, and retrieving nicknames,
 * as well as refreshing player display names in-game. Most methods are intended for
 * internal use, but could be useful for plugin developers interacting with nicknames.
 * </p>
 */
@SuppressWarnings("UnusedReturnValue")
public class NickUtils {

    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();


    /**
     * Performs all configured checks on a nickname, including length, regex,
     * username conflicts, and nickname protection. Throws a {@link CommandSyntaxException}
     * if any of the checks fail.
     *
     * @param sender   The sender attempting to set the nickname
     * @param nickname The nickname to validate
     * @throws CommandSyntaxException if any of the nickname checks fail
     */
    public static void nicknameChecks(@NotNull CommandSender sender, @NotNull Nickname nickname) throws CommandSyntaxException {
        String normalizedNick = nickname.getNormalizedNickname();
        if (normalizedNick.isEmpty()) {
            throw Exceptions.ERROR_EMPTY_NICK_AFTER_PARSE.create();
        }

        boolean bypassUsername = sender.hasPermission(NickPermission.NICK_BYPASS_USERNAME.getPermission());
        boolean bypassLength = sender.hasPermission(NickPermission.NICK_BYPASS_LENGTH.getPermission());
        boolean bypassRegex = sender.hasPermission(NickPermission.NICK_BYPASS_REGEX.getPermission());
        boolean bypassNickProtection = sender.hasPermission(NickPermission.NICK_BYPASS_NICK_PROTECTION.getPermission());

        if (!bypassUsername) {
            if (thisIsSomeonesUsername(normalizedNick))
                throw Exceptions.ERROR_NICKNAME_IS_SOMEONES_USERNAME.create(normalizedNick);
        }
        if (!bypassLength) {
            if (normalizedNick.length() > ConfigHandler.getInstance().getMaxLength())
                throw Exceptions.ERROR_LENGTH.create(normalizedNick);
        }
        if (!bypassRegex) {
            if (!passesRegexCheck(normalizedNick)) throw Exceptions.ERROR_REGEX.create(normalizedNick);
        }
        if (ConfigHandler.getInstance().shouldOnlineNicksBeProtected() && !bypassNickProtection) {
            if (someoneOnlineUsingThis(sender, normalizedNick))
                throw Exceptions.ERROR_SOMEONE_USING_THAT_NICKNAME.create(normalizedNick);
        }
        if (ConfigHandler.getInstance().shouldOfflineNicksBeProtected() && !bypassNickProtection) {
            if (someoneSavedUsingThis(sender, normalizedNick))
                throw Exceptions.ERROR_SOMEONE_USING_THAT_NICKNAME.create(normalizedNick);
        }
    }

    /**
     * Updates a player's display name and optionally their tab list name to reflect their
     * active nickname.
     *
     * @param uuid The UUID of the player whose display name should be refreshed
     * @return true if the player's display name was successfully refreshed, false if the player is offline
     */
    public static boolean refreshDisplayName(@NotNull UUID uuid) {
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


    /**
     * Checks whether the given nickname only uses tags and formatting that the player
     * has permission to use.
     *
     * @param user The command sender attempting to use the nickname
     * @param nick The nickname to validate
     * @return true if the nickname only uses allowed tags, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidTags(@NotNull CommandSender user, @NotNull String nick) {
        TagResolver.Builder resolver = TagResolver.builder();
        for (ColorTag colorTag : ColorTag.values()) {
            if (user.hasPermission(colorTag.getPermission()) || !ConfigHandler.getInstance().isColorRequiresPermission()) {
                resolver.resolver(colorTag.getTagResolver());
            }
        }
        for (FormatTag formatTag : FormatTag.values()) {
            if (user.hasPermission(formatTag.getPermission()) || !ConfigHandler.getInstance().isFormatRequiresPermission()) {
                resolver.resolver(formatTag.getTagResolver());
            }
        }
        MiniMessage parser = MiniMessage.builder().strict(false).tags(resolver.build()).build();

        Component defaultParsed = SimpleNicks.getDefaultParser().deserialize(nick);
        String defaultSerialized = miniMessage.serialize(defaultParsed);
        Component permissionParsed = parser.deserialize(nick);
        String permissionSerialized = miniMessage.serialize(permissionParsed);

        return defaultSerialized.equals(permissionSerialized);
    }

    /**
     * Converts a nickname into a "normalized" version by stripping all MiniMessage tags
     * and converting to lowercase. Used for comparisons and storage.
     *
     * @param nickname The nickname to normalize
     * @return The normalized nickname string
     */
    public static String normalizeNickname(@NotNull String nickname) {
        return miniMessage.stripTags(nickname).toLowerCase();
    }

    /**
     * Retrieves offline players who have saved a specific normalized nickname.
     *
     * @param normalizedNickname The normalized nickname to search for
     * @return A list of OfflinePlayer objects who have used the nickname,
     * or null if no players were found
     */
    @NotNull
    public static List<OfflinePlayer> getOfflinePlayersByNickname(@NotNull String normalizedNickname) {
        List<UUID> usersWithThisName = SqlHandler.getInstance().getUuidsOfNickname(normalizedNickname);
        if (usersWithThisName == null || usersWithThisName.isEmpty()) return new ArrayList<>();
        List<OfflinePlayer> playersByNick = new ArrayList<>();
        for (UUID uuid : usersWithThisName) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (!offlinePlayer.hasPlayedBefore()) continue;
            playersByNick.add(offlinePlayer);
        }
        return playersByNick;
    }

    /**
     * Checks if a normalized nickname passes the regex pattern defined in the configuration.
     *
     * @param normalizedNick The normalized nickname to validate
     * @return true if the nickname matches the regex, false otherwise
     */
    public static boolean passesRegexCheck(@NotNull String normalizedNick) {
        Pattern configRegex = ConfigHandler.getInstance().getRegex();
        return configRegex.matcher(normalizedNick).matches();
    }

    /**
     * Determines whether a given normalized nickname matches a recently-used username.
     * This prevents nicknames from being set to usernames of other players within the
     * protection period.
     *
     * @param normalizedName The normalized nickname to check
     * @return true if the nickname matches a protected username, false otherwise
     */
    public static boolean thisIsSomeonesUsername(@NotNull String normalizedName) {
        long protectionTime = ConfigHandler.getInstance().getUsernameProtectionTime();
        if (protectionTime < 0) return false;
        normalizedName = normalizedName.toLowerCase();
        return SqlHandler.getInstance().lastLoginOfUsername(normalizedName, ConfigHandler.getInstance().getUsernameProtectionTime()) != null;
    }

    /**
     * Checks if an online player (other than the sender) is currently using the given
     * normalized nickname.
     *
     * @param sender         The command sender attempting to set the nickname
     * @param normalizedNick The normalized nickname to check
     * @return true if another online player is using this nickname, false otherwise
     */
    public static boolean someoneOnlineUsingThis(@NotNull CommandSender sender, @NotNull String normalizedNick) {
        UUID playerUuid = null;
        if (sender instanceof Player playerSender) playerUuid = playerSender.getUniqueId();
        return Cache.getInstance().nickInUseOnlinePlayers(playerUuid, normalizedNick);
    }

    /**
     * Checks if the given normalized nickname is already saved by another player and is
     * protected based on offline nickname protection settings.
     *
     * @param sender         The command sender attempting to set the nickname
     * @param normalizedNick The normalized nickname to check
     * @return true if the nickname is already saved and protected, false otherwise
     */
    public static boolean someoneSavedUsingThis(@NotNull CommandSender sender, @NotNull String normalizedNick) {
        UUID senderUuid = null;
        if (sender instanceof Player playerSender) senderUuid = playerSender.getUniqueId();
        List<UUID> uuidsWithThis = SqlHandler.getInstance().nickAlreadySavedTo(senderUuid, normalizedNick);
        if (uuidsWithThis == null || uuidsWithThis.isEmpty()) return false;
        for (UUID uuid : uuidsWithThis) {
            if (SqlHandler.getInstance().lastLoginOfUuid(uuid, ConfigHandler.getInstance().getOfflineNickProtectionTime()) != null)
                return true;
        }
        return false;
    }


}
