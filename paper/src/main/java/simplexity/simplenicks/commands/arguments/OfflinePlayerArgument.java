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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.saving.Nickname;

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

    @Override
    public @NotNull OfflinePlayer parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String playerName = reader.readString();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) return player;
        }
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName() == null || player.getName().isEmpty()) continue;
            if (player.getName().equalsIgnoreCase(playerName)) return player;
        }
        throw Exceptions.invalidPlayerSpecified(playerName);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public <S> @NotNull CompletableFuture<Suggestions> suggestOnlinePlayers(@NotNull CommandContext<S> ignoredContext, @NotNull SuggestionsBuilder builder) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String suggestion = player.getName();
            if (suggestion.toLowerCase().contains(builder.getRemainingLowerCase())) {
                Nickname currentNick = NicknameProcessor.getInstance().getCurrentNickname(player.getUniqueId(), true);
                String nickDisplay = currentNick != null ? currentNick.getNickname() : player.getName();
                builder.suggest(
                        suggestion,
                        MessageComponentSerializer.message().serialize(
                                SimpleNicksCore.get().miniMessage().deserialize("Current Nickname: " + nickDisplay)
                        )
                );
            }
        }
        return builder.buildFuture();
    }
}
