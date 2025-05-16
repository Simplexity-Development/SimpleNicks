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

@SuppressWarnings("UnstableApiUsage")
public class OfflinePlayerArgument implements CustomArgumentType<OfflinePlayer, String> {

    @Override
    public @NotNull OfflinePlayer parse(@NotNull StringReader reader) throws CommandSyntaxException {
        String playerName = reader.readString();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) throw Exceptions.ERROR_PLAYER_NOT_FOUND.create(playerName);
        return offlinePlayer;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    /**
     * Provides suggestions for players based on the online player list.
     * Will also provide a hover-able element that shows their current nickname.
     *
     * @param ignoredContext <s>Command context</s> Unused
     * @param builder SuggestionsBuilder object for adding suggestions to
     * @param <S>     For Paper, generally CommandSourceStack
     * @return Suggestions as a CompletableFuture
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
