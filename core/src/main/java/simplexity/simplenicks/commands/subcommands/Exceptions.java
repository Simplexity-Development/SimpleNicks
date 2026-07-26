package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.platform.BrigadierAdapter;

/**
 * Factory methods for Brigadier {@link CommandSyntaxException} instances.
 * <p>
 * All methods accept a {@link BrigadierAdapter} so exception messages are rendered as styled
 * Adventure components rather than plain text. Methods are used instead of static fields to
 * avoid class-load timing issues with {@link SimpleNicksCore}.
 * </p>
 */
public class Exceptions {

    private static Component deserializeComponent(@NotNull String miniMessage) {
        return SimpleNicksCore.get().miniMessage().deserialize(miniMessage);
    }

    public static <S> CommandSyntaxException nickIsNull(@NotNull BrigadierAdapter<S> adapter) {
        return new SimpleCommandExceptionType(
                adapter.brigadierMessage(deserializeComponent(LocaleMessage.ERROR_NICK_IS_NULL.getMessage()))
        ).create();
    }

    public static <S> CommandSyntaxException emptyNickAfterParse(@NotNull BrigadierAdapter<S> adapter) {
        return new SimpleCommandExceptionType(
                adapter.brigadierMessage(deserializeComponent(LocaleMessage.ERROR_INVALID_NICK_EMPTY.getMessage()))
        ).create();
    }

    public static <S> CommandSyntaxException cannotSave(@NotNull BrigadierAdapter<S> adapter) {
        return new SimpleCommandExceptionType(
                adapter.brigadierMessage(deserializeComponent(LocaleMessage.ERROR_SAVE_FAILURE.getMessage()))
        ).create();
    }

    public static <S> CommandSyntaxException tooManySavedNames(@NotNull BrigadierAdapter<S> adapter) {
        return new SimpleCommandExceptionType(
                adapter.brigadierMessage(deserializeComponent(LocaleMessage.ERROR_TOO_MANY_TO_SAVE.getMessage()))
        ).create();
    }

    public static <S> CommandSyntaxException tagsNotPermitted(@NotNull BrigadierAdapter<S> adapter) {
        return new SimpleCommandExceptionType(
                adapter.brigadierMessage(deserializeComponent(LocaleMessage.ERROR_INVALID_TAGS.getMessage()))
        ).create();
    }

    public static <S> CommandSyntaxException alreadySaved(@NotNull BrigadierAdapter<S> adapter) {
        return new SimpleCommandExceptionType(
                adapter.brigadierMessage(deserializeComponent(LocaleMessage.ERROR_ALREADY_SAVED.getMessage()))
        ).create();
    }

    public static <S> CommandSyntaxException lengthError(@NotNull BrigadierAdapter<S> adapter, Object nickname) {
        Component message = SimpleNicksCore.get().miniMessage().deserialize(
                LocaleMessage.ERROR_INVALID_NICK_LENGTH.getMessage(),
                Placeholder.unparsed("value", String.valueOf(ConfigHandler.getInstance().getMaxLength())),
                Placeholder.unparsed("name", String.valueOf(nickname)));
        return new SimpleCommandExceptionType(adapter.brigadierMessage(message)).create();
    }

    public static <S> CommandSyntaxException regexError(@NotNull BrigadierAdapter<S> adapter, Object nickname) {
        Component message = SimpleNicksCore.get().miniMessage().deserialize(
                LocaleMessage.ERROR_INVALID_NICK.getMessage(),
                Placeholder.unparsed("regex", ConfigHandler.getInstance().getRegexString()));
        return new SimpleCommandExceptionType(adapter.brigadierMessage(message)).create();
    }

    public static <S> CommandSyntaxException invalidPlayerSpecified(
            @NotNull BrigadierAdapter<S> adapter, Object playerName) {
        Component message = SimpleNicksCore.get().miniMessage().deserialize(
                LocaleMessage.ERROR_INVALID_PLAYER.getMessage(),
                Placeholder.unparsed("player_name", String.valueOf(playerName)));
        return new SimpleCommandExceptionType(adapter.brigadierMessage(message)).create();
    }

    public static <S> CommandSyntaxException nicknameSomeonesUsername(
            @NotNull BrigadierAdapter<S> adapter, Object nickname) {
        Component message = SimpleNicksCore.get().miniMessage().deserialize(
                LocaleMessage.ERROR_INVALID_OTHER_PLAYERS_USERNAME.getMessage(),
                Placeholder.unparsed("value", String.valueOf(nickname)));
        return new SimpleCommandExceptionType(adapter.brigadierMessage(message)).create();
    }

    public static <S> CommandSyntaxException someoneUsingThatNickname(
            @NotNull BrigadierAdapter<S> adapter, Object nickname) {
        Component message = SimpleNicksCore.get().miniMessage().deserialize(
                LocaleMessage.ERROR_INVALID_OTHER_PLAYERS_NICKNAME.getMessage(),
                Placeholder.unparsed("value", String.valueOf(nickname)));
        return new SimpleCommandExceptionType(adapter.brigadierMessage(message)).create();
    }
}
