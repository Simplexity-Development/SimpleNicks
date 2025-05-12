package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public interface SubCommand {

    /**
     * Attaches this subcommand to the given root.
     * @param root Root of the command builder
     */
    void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> root);

    /**
     * Defines the execution logic for this command.
     * @param ctx CommandSourceStack context
     * @return 1 indicating Success
     * @throws CommandSyntaxException On failure
     */
    int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    /**
     * Defines the "can use" logic.<br/>
     * ie: Is a player? Has permission?
     * @param css CommandSourceStack
     * @return true if the command can executed, false otherwise
     */
    boolean canExecute(@NotNull CommandSourceStack css);

    /**
     * Defines suggestions that can be provided to the client.<br/>
     * This is not necessary for every command.<br/>
     * This function can be defined elsewhere and method referenced instead of using this interface.<br/>
     * ie: nicknameArg::suggestOwnNicknames
     * @param context Command context
     * @param builder SuggestionsBuilder object for adding suggestions to
     * @return Suggestions as a CompletableFuture
     * @param <S> For Paper, generally CommandSourceStack
     */
    @SuppressWarnings("unused")
    default <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        throw new NotImplementedException("listSuggestions was used, but not implemented.");
    }

}
