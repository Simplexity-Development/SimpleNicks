package simplexity.simplenicks.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.saving.Nickname;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class NicknameArgument implements CustomArgumentType<Nickname, String> {


    @Override
    public @NotNull Nickname parse(@NotNull StringReader reader) {
        String nickname = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        String normalizedNickname = SimpleNicks.getMiniMessage().stripTags(nickname);
        return new Nickname(nickname, normalizedNickname);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    /**
     * Provides suggestions for nicknames based on the CommandSender
     *
     * @param context Command context
     * @param builder SuggestionsBuilder object for adding suggestions to
     * @param <S>     For Paper, generally CommandSourceStack
     * @return Suggestions as a CompletableFuture
     */
    public <S> @NotNull CompletableFuture<Suggestions> suggestOwnNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        OfflinePlayer player = (OfflinePlayer) css.getSender();
        addSuggestionsForPlayer(builder, player);
        return builder.buildFuture();
    }

    /**
     * Provides suggestions for nicknames based on the player argument.
     *
     * @param context Command context
     * @param builder SuggestionsBuilder object for adding suggestions to
     * @param <S>     For Paper, generally CommandSourceStack
     * @return Suggestions as a CompletableFuture
     */
    @SuppressWarnings("unused")
    public <S> @NotNull CompletableFuture<Suggestions> suggestOtherNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);
        if (player == null) return builder.buildFuture();
        addSuggestionsForPlayer(builder, player);
        return builder.buildFuture();
    }

    /**
     * Provides suggestions for nicknames based on the player argument and CommandSender.
     *
     * @param context Command context
     * @param builder SuggestionsBuilder object for adding suggestions to
     * @param <S>     For Paper, generally CommandSourceStack
     * @return Suggestions as a CompletableFuture
     */
    public <S> @NotNull CompletableFuture<Suggestions> suggestOwnAndOtherNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        if (player == null) return builder.buildFuture();
        addSuggestionsForPlayer(builder, player);
        if (css.getSender() instanceof Player sender) addSuggestionsForPlayer(builder, sender);
        return builder.buildFuture();
    }

    private void addSuggestionsForPlayer(@NotNull SuggestionsBuilder builder, OfflinePlayer player) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();
        for (Nickname nickname : NicknameProcessor.getInstance().getSavedNicknames(player)) {
            String suggestion = nickname.nickname();
            String suggestionStripped = nickname.normalizedNickname();
            if (suggestionStripped.toLowerCase().contains(builder.getRemainingLowerCase()) || suggestion.toLowerCase().contains(builder.getRemainingLowerCase())) {
                builder.suggest(
                        suggestion,
                        MessageComponentSerializer.message().serialize(
                                miniMessage.deserialize("Preview: " + nickname.nickname())
                        )
                );
            }
        }
    }

}
