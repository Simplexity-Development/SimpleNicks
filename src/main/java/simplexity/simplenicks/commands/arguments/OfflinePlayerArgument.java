package simplexity.simplenicks.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;

import java.util.concurrent.CompletableFuture;

/**
 * Custom Brigadier argument type for handling {@link OfflinePlayer} arguments in commands.
 * <p>
 * Parses player names into {@link OfflinePlayer} objects, ensuring the player exists
 * and has played on the server before. Also provides suggestions for online players,
 * showing their current nickname as a hoverable element.
 * </p>
 */
@SuppressWarnings("UnstableApiUsage")
public class OfflinePlayerArgument implements CustomArgumentType<OfflinePlayer, String> {

    /**
     * Parses a player name from the command input into an {@link OfflinePlayer} object.
     *
     * @param reader The Brigadier StringReader containing the command input
     * @return The {@link OfflinePlayer} corresponding to the input
     * @throws CommandSyntaxException if the player does not exist or has never joined
     */
    @Override
    public @NotNull OfflinePlayer parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String playerName = reader.readString();
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        OfflinePlayer offlinePlayer = null;
        for (OfflinePlayer player : offlinePlayers) {
            if (player.getName() == null || player.getName().isEmpty()) continue;
            if (player.getName().equalsIgnoreCase(playerName)) {
                offlinePlayer = player;
                break;
            }
        }
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) throw Exceptions.INVALID_PLAYER_SPECIFIED.create(playerName);
        return offlinePlayer;
    }

    /**
     * Gets the native Brigadier type for this argument.
     * Uses a simple word argument for player names.
     *
     * @return The native {@link ArgumentType} for player names
     */
    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    /**
     * Provides suggestions for online players.
     * Each suggestion includes a hoverable preview of their current nickname.
     *
     * @param ignoredContext Command context (unused)
     * @param builder        SuggestionsBuilder for adding completions
     * @param <S>            Typically {@link io.papermc.paper.command.brigadier.CommandSourceStack}
     * @return A CompletableFuture containing the suggestions
     */
    public <S> @NotNull CompletableFuture<Suggestions> suggestOnlinePlayers(@NotNull CommandContext<S> ignoredContext, @NotNull SuggestionsBuilder builder) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String suggestion = player.getName();
            if (suggestion.toLowerCase().contains(builder.getRemainingLowerCase())) {
                builder.suggest(
                        suggestion,
                        MessageComponentSerializer.message().serialize(
                                miniMessage.deserialize("Current Nickname: " + NicknameProcessor.getInstance().getCurrentNickname(player))
                        )
                );
            }
        }
        return builder.buildFuture();
    }
}
