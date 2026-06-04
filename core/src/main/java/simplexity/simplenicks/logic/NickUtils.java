package simplexity.simplenicks.logic;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.platform.PlayerInfo;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.ColorTag;
import simplexity.simplenicks.util.FormatTag;
import simplexity.simplenicks.util.NickPermission;

import org.jetbrains.annotations.Nullable;

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

    private static MiniMessage mm() {
        return SimpleNicksCore.get().miniMessage();
    }

    /**
     * Performs all configured checks on a nickname, including length, regex,
     * username conflicts, and nickname protection. Throws a {@link CommandSyntaxException}
     * if any of the checks fail.
     *
     * @param sender   the sender attempting to set the nickname
     * @param nickname the nickname to validate
     * @throws CommandSyntaxException if any of the nickname checks fail
     */
    public static void nicknameChecks(@NotNull SenderContext sender, @NotNull Nickname nickname) throws CommandSyntaxException {
        String normalizedNick = nickname.getNormalizedNickname();
        if (normalizedNick.isEmpty()) {
            throw Exceptions.emptyNickAfterParse();
        }

        boolean bypassUsername = sender.hasPermission(NickPermission.NICK_BYPASS_USERNAME.getPermissionKey());
        boolean bypassLength = sender.hasPermission(NickPermission.NICK_BYPASS_LENGTH.getPermissionKey());
        boolean bypassRegex = sender.hasPermission(NickPermission.NICK_BYPASS_REGEX.getPermissionKey());
        boolean bypassNickProtection = sender.hasPermission(NickPermission.NICK_BYPASS_NICK_PROTECTION.getPermissionKey());

        if (!bypassUsername && ConfigHandler.getInstance().isUsernameProtection() && isProtectedUsername(normalizedNick)) {
            throw Exceptions.nicknameSomeonesUsername(normalizedNick);
        }
        if (!bypassLength && normalizedNick.length() > ConfigHandler.getInstance().getMaxLength()) {
            throw Exceptions.lengthError(normalizedNick);
        }
        if (!bypassRegex && !passesRegexCheck(normalizedNick)) {
            throw Exceptions.regexError(normalizedNick);
        }
        if (!bypassNickProtection) {
            if (ConfigHandler.getInstance().shouldOnlineNicksBeProtected() && someoneOnlineUsingThis(sender, normalizedNick)) {
                throw Exceptions.someoneUsingThatNickname(normalizedNick);
            }
            if (ConfigHandler.getInstance().shouldOfflineNicksBeProtected() && someoneSavedUsingThis(sender, normalizedNick)) {
                throw Exceptions.someoneUsingThatNickname(normalizedNick);
            }
        }
    }

    /**
     * Updates a player's display name and optionally their tab list name to reflect their
     * active nickname.
     *
     * @param uuid the UUID of the player whose display name should be refreshed
     * @return true if the player's display name was successfully refreshed, false if the player is offline
     */
    public static boolean refreshDisplayName(@NotNull UUID uuid) {
        if (!SimpleNicksCore.get().platform().isPlayerOnline(uuid)) return false;
        Nickname nickname = Cache.getInstance().getActiveNickname(uuid);
        if (nickname == null) {
            SimpleNicksCore.get().platform().clearDisplayName(uuid);
            return true;
        }
        Component displayName = mm().deserialize(ConfigHandler.getInstance().getNickPrefix())
                .append(mm().deserialize(nickname.getNickname()));
        SimpleNicksCore.get().platform().setDisplayName(uuid, displayName);
        if (ConfigHandler.getInstance().shouldNickTablist()) {
            SimpleNicksCore.get().platform().setTablistName(uuid, mm().deserialize(nickname.getNickname()));
        }
        return true;
    }

    /**
     * Checks whether the given nickname only uses tags and formatting that the sender
     * has permission to use.
     *
     * @param user the sender attempting to use the nickname
     * @param nick the nickname to validate
     * @return true if the nickname only uses allowed tags, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidTags(@NotNull SenderContext user, @NotNull String nick) {
        TagResolver.Builder resolver = TagResolver.builder();

        for (ColorTag colorTag : ColorTag.values()) {
            if (user.hasPermission(colorTag.getPermissionKey()) || !ConfigHandler.getInstance().isColorRequiresPermission()) {
                resolver.resolver(colorTag.getTagResolver());
            }
        }
        for (FormatTag formatTag : FormatTag.values()) {
            if (user.hasPermission(formatTag.getPermissionKey()) || !ConfigHandler.getInstance().isFormatRequiresPermission()) {
                resolver.resolver(formatTag.getTagResolver());
            }
        }

        MiniMessage parser = MiniMessage.builder().strict(false).tags(resolver.build()).build();

        Component defaultParsed = mm().deserialize(nick);
        String defaultSerialized = mm().serialize(defaultParsed);
        Component permissionParsed = parser.deserialize(nick);
        String permissionSerialized = mm().serialize(permissionParsed);

        return defaultSerialized.equals(permissionSerialized);
    }

    /**
     * Converts a nickname into a "normalized" version by stripping all MiniMessage tags
     * and converting to lowercase. Used for comparisons and storage.
     *
     * @param nickname the nickname to normalize
     * @return the normalized nickname string
     */
    public static String normalizeNickname(@NotNull String nickname) {
        return mm().stripTags(nickname).toLowerCase();
    }

    /**
     * Retrieves players who have a specific normalized nickname set as their active nickname.
     * <p>
     * Uses {@link SqlHandler#playerSaveExists(UUID)} instead of platform APIs to determine
     * whether a UUID belongs to a known player.
     * </p>
     *
     * @param normalizedNickname the normalized nickname to search for
     * @return a list of {@link PlayerInfo} for players who use the nickname
     */
    @NotNull
    public static List<PlayerInfo> getPlayersByNickname(@NotNull String normalizedNickname) {
        List<UUID> usersWithThisName = SqlHandler.getInstance().getUuidsOfNickname(normalizedNickname);
        if (usersWithThisName == null || usersWithThisName.isEmpty()) return new ArrayList<>();
        List<PlayerInfo> result = new ArrayList<>();
        for (UUID uuid : usersWithThisName) {
            if (!SqlHandler.getInstance().playerSaveExists(uuid)) continue;
            String username = SimpleNicksCore.get().platform().getPlayerUsername(uuid)
                    .orElseGet(() -> uuid.toString());
            long lastLogin = SqlHandler.getInstance().getLastLoginMillis(uuid);
            result.add(new PlayerInfo(uuid, username, lastLogin));
        }
        return result;
    }

    /**
     * Checks if a normalized nickname passes the regex pattern defined in the configuration.
     *
     * @param normalizedNick the normalized nickname to validate
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
     * @param normalizedName the normalized nickname to check
     * @return true if the nickname matches a protected username, false otherwise
     */
    public static boolean isProtectedUsername(@NotNull String normalizedName) {
        normalizedName = normalizedName.toLowerCase();
        long expireTime = ConfigHandler.getInstance().getUsernameProtectionTime() == -1
                ? System.currentTimeMillis() - ConfigHandler.getInstance().getUsernameProtectionTime()
                : -1;
        return SqlHandler.getInstance().lastLoginOfUsername(normalizedName, expireTime) != null;
    }

    /**
     * Checks if an online player (other than the sender) is currently using the given
     * normalized nickname.
     *
     * @param sender         the sender attempting to set the nickname
     * @param normalizedNick the normalized nickname to check
     * @return true if another online player is using this nickname, false otherwise
     */
    public static boolean someoneOnlineUsingThis(@NotNull SenderContext sender, @NotNull String normalizedNick) {
        UUID playerUuid = sender.getUuid().orElse(null);
        return Cache.getInstance().nickInUseOnlinePlayers(playerUuid, normalizedNick);
    }

    /**
     * Checks if the given normalized nickname is already saved by another player and is
     * protected based on offline nickname protection settings.
     *
     * @param sender         the sender attempting to set the nickname
     * @param normalizedNick the normalized nickname to check
     * @return true if the nickname is already saved and protected, false otherwise
     */
    public static boolean someoneSavedUsingThis(@NotNull SenderContext sender, @NotNull String normalizedNick) {
        UUID senderUuid = sender.getUuid().orElse(null);
        List<UUID> uuidsWithThis = SqlHandler.getInstance().nickAlreadySavedTo(senderUuid, normalizedNick);
        if (uuidsWithThis == null || uuidsWithThis.isEmpty()) return false;
        for (UUID uuid : uuidsWithThis) {
            if (SqlHandler.getInstance().lastLoginOfUuid(uuid, ConfigHandler.getInstance().getOfflineNickProtectionTime()) != null) {
                return true;
            }
        }
        return false;
    }
}
