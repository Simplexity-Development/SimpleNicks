package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Reusable Brigadier suggestion providers for nickname and player arguments.
 */
public final class NickSuggestionProviders {

    private NickSuggestionProviders() {}

    /**
     * Suggests the calling player's own saved nicknames.
     * Used by {@code /nick delete} and as Fabric's fallback for {@code /nick set}.
     *
     * @param adapter the adapter used to resolve the player UUID from the source
     * @param <S>     the platform's CommandSourceStack type
     * @return a suggestion provider
     */
    @NotNull
    public static <S> SuggestionProvider<S> ownSavedNicks(@NotNull BrigadierAdapter<S> adapter) {
        return (ctx, builder) -> {
            adapter.getPlayerUuid(ctx.getSource()).ifPresent(uuid -> {
                List<Nickname> saved = NicknameProcessor.getInstance().getSavedNicknames(uuid, true);
                saved.forEach(savedNickname -> builder.suggest(savedNickname.getNickname()));
            });
            return builder.buildFuture();
        };
    }

    /**
     * Suggests the normalized nicknames of all online players that currently have an active nickname.
     * Used by {@code /nick who}.
     *
     * @param <S> the platform's CommandSourceStack type
     * @return a suggestion provider
     */
    @NotNull
    public static <S> SuggestionProvider<S> activeOnlineNicks() {
        return (ctx, builder) -> {
            Cache.getInstance().getOnlineNicknames().values()
                    .forEach(activeNickname -> builder.suggest(activeNickname.getNormalizedNickname()));
            return builder.buildFuture();
        };
    }
}
