package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.config.MessageUtils;

/**
 * Factory methods for Brigadier {@link CommandSyntaxException} instances.
 * <p>
 * Exception messages are rendered as plain text by stripping MiniMessage tags,
 * which works correctly on both Paper and Fabric. Methods are used instead of
 * static fields to avoid class-load timing issues with {@link SimpleNicksCore}.
 * </p>
 */
public class Exceptions {

    private static String strip(String miniMessage) {
        return SimpleNicksCore.get().miniMessage().stripTags(miniMessage);
    }

    public static CommandSyntaxException nickIsNull() {
        return new SimpleCommandExceptionType(
                () -> strip(LocaleMessage.ERROR_NICK_IS_NULL.getMessage())
        ).create();
    }

    public static CommandSyntaxException emptyNickAfterParse() {
        return new SimpleCommandExceptionType(
                () -> strip(LocaleMessage.ERROR_INVALID_NICK_EMPTY.getMessage())
        ).create();
    }

    public static CommandSyntaxException cannotSave() {
        return new SimpleCommandExceptionType(
                () -> strip(LocaleMessage.ERROR_SAVE_FAILURE.getMessage())
        ).create();
    }

    public static CommandSyntaxException tooManySavedNames() {
        return new SimpleCommandExceptionType(
                () -> strip(LocaleMessage.ERROR_TOO_MANY_TO_SAVE.getMessage())
        ).create();
    }

    public static CommandSyntaxException tagsNotPermitted() {
        return new SimpleCommandExceptionType(
                () -> strip(LocaleMessage.ERROR_INVALID_TAGS.getMessage())
        ).create();
    }

    public static CommandSyntaxException alreadySaved() {
        return new SimpleCommandExceptionType(
                () -> strip(LocaleMessage.ERROR_ALREADY_SAVED.getMessage())
        ).create();
    }

    public static CommandSyntaxException lengthError(Object nickname) {
        return new DynamicCommandExceptionType(
                nick -> () -> strip(SimpleNicksCore.get().miniMessage().stripTags(
                        LocaleMessage.ERROR_INVALID_NICK_LENGTH.getMessage()
                                .replace("<value>", String.valueOf(ConfigHandler.getInstance().getMaxLength()))
                                .replace("<name>", nick.toString())
                ))
        ).create(nickname);
    }

    public static CommandSyntaxException regexError(Object nickname) {
        return new DynamicCommandExceptionType(
                nick -> () -> strip(
                        LocaleMessage.ERROR_INVALID_NICK.getMessage()
                                .replace("<regex>", ConfigHandler.getInstance().getRegexString())
                )
        ).create(nickname);
    }

    public static CommandSyntaxException invalidPlayerSpecified(Object playerName) {
        return new DynamicCommandExceptionType(
                name -> () -> strip(
                        LocaleMessage.ERROR_INVALID_PLAYER.getMessage()
                                .replace("<player_name>", name.toString())
                )
        ).create(playerName);
    }

    public static CommandSyntaxException nicknameSomeonesUsername(Object nickname) {
        return new DynamicCommandExceptionType(
                nick -> () -> strip(
                        LocaleMessage.ERROR_INVALID_OTHER_PLAYERS_USERNAME.getMessage()
                                .replace("<value>", nick.toString())
                )
        ).create(nickname);
    }

    public static CommandSyntaxException someoneUsingThatNickname(Object nickname) {
        return new DynamicCommandExceptionType(
                nick -> () -> strip(
                        LocaleMessage.ERROR_INVALID_OTHER_PLAYERS_NICKNAME.getMessage()
                                .replace("<value>", nick.toString())
                )
        ).create(nickname);
    }
}
