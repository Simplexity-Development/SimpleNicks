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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

import java.util.concurrent.CompletableFuture;

/**
 * Custom Brigadier argument type for handling nicknames in commands.
 * <p>
 * Parses a string into a {@link Nickname} object, normalizing it by stripping tags and
 * converting to lowercase. Provides suggestions for nicknames based on saved nicknames
 * of the command sender, other players, or all online players.
 * </p>
 */
@SuppressWarnings("UnstableApiUsage")
public class NicknameArgument implements CustomArgumentType<Nickname, String> {

    @Override
    public @NotNull Nickname parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String nickname = getNativeType().parse(reader);
        String normalizedNickname = SimpleNicksCore.get().miniMessage().stripTags(nickname).toLowerCase();
        return new Nickname(nickname, normalizedNickname);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    public <S> @NotNull CompletableFuture<Suggestions> suggestOwnNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        OfflinePlayer player = (OfflinePlayer) css.getSender();
        addSuggestionsForPlayer(builder, player);
        return builder.buildFuture();
    }

    public <S> @NotNull CompletableFuture<Suggestions> suggestOtherNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);
        if (player == null) return builder.buildFuture();
        addSuggestionsForPlayer(builder, player);
        return builder.buildFuture();
    }

    public <S> @NotNull CompletableFuture<Suggestions> suggestOwnAndOtherNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        OfflinePlayer player = context.getArgument("player", OfflinePlayer.class);
        CommandSourceStack css = (CommandSourceStack) context.getSource();
        if (player == null) return builder.buildFuture();
        addSuggestionsForPlayer(builder, player);
        if (css.getSender() instanceof Player sender) addSuggestionsForPlayer(builder, sender);
        return builder.buildFuture();
    }

    public <S> @NotNull CompletableFuture<Suggestions> suggestAllOnlineNicknames(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        for (Nickname nickname : Cache.getInstance().getOnlineNicknames().values()) {
            builder.suggest(nickname.getNormalizedNickname());
        }
        return builder.buildFuture();
    }

    private void addSuggestionsForPlayer(@NotNull SuggestionsBuilder builder, @NotNull OfflinePlayer player) {
        for (Nickname nickname : NicknameProcessor.getInstance().getSavedNicknames(player.getUniqueId(), player.isOnline())) {
            String suggestion = nickname.getNickname();
            String suggestionStripped = nickname.getNormalizedNickname();
            if (suggestionStripped.toLowerCase().contains(builder.getRemainingLowerCase())
                    || suggestion.toLowerCase().contains(builder.getRemainingLowerCase())) {
                builder.suggest(
                        suggestion,
                        MessageComponentSerializer.message().serialize(
                                SimpleNicksCore.get().miniMessage().deserialize("Preview: " + nickname.getNickname())
                        )
                );
            }
        }
    }
}
