package simplexity.simplenicks.platform;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.Optional;
import java.util.UUID;

/**
 * Abstracts all platform-specific Brigadier operations used by core command logic.
 * <p>
 * One instance per platform is shared across all subcommands.
 * Each method receives the source or context it needs rather than storing it as a field.
 * </p>
 *
 * @param <S> the platform's CommandSourceStack type
 */
public interface BrigadierAdapter<S> {

    // -------------------------------------------------------------------------
    // Source extraction
    // -------------------------------------------------------------------------

    /**
     * Returns the UUID of the player executing the command, or empty if the source is not a player.
     *
     * @param source the command source
     * @return player UUID, or empty for console/command-block sources
     */
    @NotNull
    Optional<UUID> getPlayerUuid(@NotNull S source);

    /**
     * Returns the username of the player executing the command, or empty if not a player.
     *
     * @param source the command source
     * @return player username, or empty for non-player sources
     */
    @NotNull
    Optional<String> getPlayerUsername(@NotNull S source);

    /**
     * Returns {@code true} if the source represents an in-game player.
     *
     * @param source the command source
     * @return {@code true} if the source is a player
     */
    boolean isPlayer(@NotNull S source);

    // -------------------------------------------------------------------------
    // Delegation
    // -------------------------------------------------------------------------

    /**
     * Wraps the source in a {@link SenderContext} for use by core logic (messaging, permissions).
     *
     * @param source the command source
     * @return a platform-backed sender context
     */
    @NotNull
    SenderContext senderContext(@NotNull S source);

    /**
     * Returns {@code true} if the source holds the given permission.
     *
     * @param source     the command source
     * @param permission the permission to check
     * @return {@code true} if the permission is granted
     */
    boolean hasPermission(@NotNull S source, @NotNull NickPermission permission);

    // -------------------------------------------------------------------------
    // Argument parsing
    // -------------------------------------------------------------------------

    /**
     * Extracts a {@link Nickname} from the named argument in the context.
     * <p>
     * Paper: the custom argument type already produced a {@code Nickname}.
     * Fabric: parses a raw string into a {@code Nickname} on the fly.
     * </p>
     *
     * @param ctx     the command context
     * @param argName the argument name
     * @return the resolved nickname
     */
    @NotNull
    Nickname getNickname(@NotNull CommandContext<S> ctx, @NotNull String argName);

    /**
     * Resolves a {@link PlayerTarget} from the named offline-player argument.
     *
     * @param ctx     the command context
     * @param argName the argument name
     * @return the resolved player target, or empty if the player could not be found
     * @throws CommandSyntaxException if the argument value is syntactically invalid
     */
    @NotNull
    Optional<PlayerTarget> getPlayerTarget(@NotNull CommandContext<S> ctx, @NotNull String argName)
            throws CommandSyntaxException;

    // -------------------------------------------------------------------------
    // Brigadier message
    // -------------------------------------------------------------------------

    /**
     * Converts an Adventure {@link Component} to a Brigadier {@link Message} that the platform
     * renders with full MiniMessage formatting when displaying a command syntax exception.
     *
     * @param component the component to wrap
     * @return a Brigadier message
     */
    @NotNull
    Message brigadierMessage(@NotNull Component component);

    // -------------------------------------------------------------------------
    // Literal builder
    // -------------------------------------------------------------------------

    /**
     * Creates a Brigadier literal argument builder for the given name.
     * Abstracts over {@code io.papermc.paper.command.brigadier.Commands.literal()} (Paper)
     * and {@code net.minecraft.commands.Commands.literal()} (Fabric).
     *
     * @param name the literal string
     * @return a new literal argument builder
     */
    @NotNull
    LiteralArgumentBuilder<S> literal(@NotNull String name);

    // -------------------------------------------------------------------------
    // Argument builder factories
    // -------------------------------------------------------------------------
    // Each method accepts the executor and returns a fully-configured node ready
    // for parent.then(). This sidesteps the ArgumentBuilder<S,?> wildcard-chaining
    // problem that would arise if executors were attached by the caller.

    /**
     * Returns a nickname argument node with own-nickname suggestions and the given executor.
     *
     * @param argName  the argument name
     * @param executor the command executor to attach
     * @return a configured argument builder
     */
    @NotNull
    ArgumentBuilder<S, ?> nicknameArgument(@NotNull String argName, @NotNull Command<S> executor);

    /**
     * Returns a nickname argument node with the given suggestion provider and executor.
     *
     * @param argName     the argument name
     * @param suggestions the suggestion provider
     * @param executor    the command executor to attach
     * @return a configured argument builder
     */
    @NotNull
    ArgumentBuilder<S, ?> nicknameArgument(@NotNull String argName,
            @NotNull SuggestionProvider<S> suggestions, @NotNull Command<S> executor);

    /**
     * Returns an offline-player argument node with online-player suggestions and the given executor.
     *
     * @param argName  the argument name
     * @param executor the command executor to attach
     * @return a configured argument builder
     */
    @NotNull
    ArgumentBuilder<S, ?> playerArgument(@NotNull String argName, @NotNull Command<S> executor);

    /**
     * Returns a two-level tree: {@code <player>} → {@code <nickname>} with the given executor on
     * the nickname node. Used by admin subcommands that take both a player and a nickname.
     *
     * @param playerArgName   the player argument name
     * @param nicknameArgName the nickname argument name
     * @param executor        the command executor to attach to the nickname node
     * @return the player argument builder with the nickname node chained
     */
    @NotNull
    ArgumentBuilder<S, ?> playerThenNicknameArgument(@NotNull String playerArgName,
            @NotNull String nicknameArgName, @NotNull Command<S> executor);

    /**
     * Returns a two-level tree: {@code <player>} → {@code <nickname>} where nickname suggestions
     * are the target player's saved nicknames. Used by admin delete.
     *
     * @param playerArgName   the player argument name
     * @param nicknameArgName the nickname argument name
     * @param executor        the command executor to attach to the nickname node
     * @return the player argument builder with the nickname node chained
     */
    @NotNull
    ArgumentBuilder<S, ?> playerThenTargetNicknameArgument(@NotNull String playerArgName,
            @NotNull String nicknameArgName, @NotNull Command<S> executor);
}
