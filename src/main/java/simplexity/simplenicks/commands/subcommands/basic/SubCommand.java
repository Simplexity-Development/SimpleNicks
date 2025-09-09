package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public interface SubCommand {

    /**
     * Attaches this subcommand to the given parent.
     *
     * @param parent Parent node of the command builder
     */
    void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent);

    /**
     * Defines the execution logic for this command.
     *
     * @param ctx CommandSourceStack context
     * @return 1 indicating Success
     * @throws CommandSyntaxException On failure
     */
    int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    /**
     * Defines the "can use" logic.<br/>
     * ie: Is a player? Has permission?
     *
     * @param css CommandSourceStack
     * @return true if the command can executed, false otherwise
     */
    boolean canExecute(@NotNull CommandSourceStack css);

    /**
     * Sends a feedback message to the player, confirming the command went through properly
     *
     * @param player        Player
     * @param localeMessage Message
     * @param nickname      Nickname
     */
    default void sendFeedback(@NotNull Player player, @Nullable LocaleMessage localeMessage, @Nullable Nickname nickname) {
        if (nickname == null) nickname = new Nickname("", "");
        if (localeMessage == null || localeMessage.getMessage().isEmpty()) return;
        player.sendRichMessage(
                localeMessage.getMessage(),
                Placeholder.parsed("value", nickname.getNickname())
        );
    }

    /**
     * Parses a message for admin commands.
     *
     * @param message   Message to parse
     * @param value     Placeholder value from message, usually nickname, sometimes something else like a config value
     * @param initiator CommandSender who initiated the command, the admin
     * @param target    OfflinePlayer who this command is being run on
     * @return Component parsed message
     */
    default Component parseAdminMessage(@NotNull String message, @NotNull String value, @NotNull CommandSender initiator, @NotNull OfflinePlayer target) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();
        Component initiatorName;
        String targetUserName = target.getName();
        if (targetUserName == null) {
            targetUserName = "[Username not found, idk how but you got this error.]";
        }
        if (initiator instanceof Player playerInitiator) {
            initiatorName = playerInitiator.displayName();
        } else {
            initiatorName = miniMessage.deserialize(LocaleMessage.SERVER_DISPLAY_NAME.getMessage());
        }
        return miniMessage.deserialize(message,
                Placeholder.parsed("value", value),
                Placeholder.component("initiator", initiatorName),
                Placeholder.parsed("target", targetUserName)
        );
    }

    /**
     * Defines suggestions that can be provided to the client.<br/>
     * This is not necessary for every command.<br/>
     * This function can be defined elsewhere and method referenced instead of using this interface.<br/>
     * ie: nicknameArg::suggestOwnNicknames
     *
     * @param context Command context
     * @param builder SuggestionsBuilder object for adding suggestions to
     * @param <S>     For Paper, generally CommandSourceStack
     * @return Suggestions as a CompletableFuture
     */
    @SuppressWarnings("unused")
    default <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        throw new NotImplementedException("listSuggestions was used, but not implemented.");
    }

    default void refreshName(@NotNull OfflinePlayer player) {
        if (!player.isOnline()) return;
        Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> NickUtils.refreshDisplayName(player.getUniqueId()));
    }

    default boolean permissionNotRequired() {
        return !ConfigHandler.getInstance().isNickRequiresPermission();
    }
}
