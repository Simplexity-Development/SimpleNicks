package simplexity.simplenicks.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class NicknameArgument implements CustomArgumentType<Nickname,String> {

    private static final DynamicCommandExceptionType ERROR_LENGTH = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    Component.text("Nicknames must be <=" + ConfigHandler.getInstance().getMaxLength() + " after tags are stripped. Your nickname stripped is: " + nickname)
            )
    );

    private static final DynamicCommandExceptionType ERROR_REGEX = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    Component.text("The nickname " + nickname + " is invalid.")
            )
    );

    @Override
    public @NotNull Nickname parse(@NotNull StringReader stringReader) throws CommandSyntaxException {
        throw new UnsupportedOperationException("This method will never be called.");
    }

    @Override
    public <S> @NotNull Nickname parse(final StringReader reader, final @NotNull S source) throws CommandSyntaxException {
        String nickname = reader.readQuotedString();
        String normalizedNickname = SimpleNicks.getMiniMessage().stripTags(nickname);
        Player player = (Player) ((CommandSourceStack) source).getSender();
        if (!player.hasPermission(Constants.NICK_LENGTH_BYPASS) && normalizedNickname.length() > ConfigHandler.getInstance().getMaxLength()) throw ERROR_LENGTH.create(normalizedNickname);
        if (!player.hasPermission(Constants.NICK_REGEX_BYPASS) && !ConfigHandler.getInstance().getRegex().matcher(normalizedNickname).matches()) throw ERROR_REGEX.create(normalizedNickname);
        return new Nickname(nickname, normalizedNickname);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        OfflinePlayer player = (OfflinePlayer) css.getSender();
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();
        for (Nickname nickname : NicknameProcessor.getInstance().getSavedNicknames(player)) {
            String suggestion = "\"" + nickname.nickname() + "\"";
            String suggestionStripped = "\"" + nickname.normalizedNickname() + "\"";
            if (suggestionStripped.toLowerCase().contains(builder.getRemainingLowerCase()) || suggestion.toLowerCase().contains(builder.getRemainingLowerCase())) {
                builder.suggest(
                        suggestion,
                        MessageComponentSerializer.message().serialize(
                                miniMessage.deserialize("Preview: " + nickname.nickname())
                        )
                );
            }
        }
        return builder.buildFuture();
    }

}
