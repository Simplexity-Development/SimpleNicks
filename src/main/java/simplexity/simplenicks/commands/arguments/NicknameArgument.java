package simplexity.simplenicks.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.saving.Nickname;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class NicknameArgument implements CustomArgumentType<Nickname,String> {

    @Override
    public @NotNull Nickname parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String nickname = reader.readQuotedString();
        String normalizedNickname = SimpleNicks.getMiniMessage().stripTags(nickname);
        return new Nickname(nickname, normalizedNickname);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    /**
     * Provides suggestions for nicknames based on the CommandSender
     * @param context Command context
     * @param builder SuggestionsBuilder object for adding suggestions to
     * @return Suggestions as a CompletableFuture
     * @param <S> For Paper, generally CommandSourceStack
     */
    public <S> @NotNull CompletableFuture<Suggestions> suggestOwnNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
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
