package simplexity.simplenicks.platform;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.arguments.OfflinePlayerArgument;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.Optional;
import java.util.UUID;

/**
 * {@link BrigadierAdapter} implementation for the Paper platform.
 */
@SuppressWarnings("UnstableApiUsage")
public class PaperBrigadierAdapter implements BrigadierAdapter<CommandSourceStack> {

    @Override
    public @NotNull Optional<UUID> getPlayerUuid(@NotNull CommandSourceStack source) {
        return source.getSender() instanceof Player player ? Optional.of(player.getUniqueId()) : Optional.empty();
    }

    @Override
    public @NotNull Optional<String> getPlayerUsername(@NotNull CommandSourceStack source) {
        return source.getSender() instanceof Player player ? Optional.of(player.getName()) : Optional.empty();
    }

    @Override
    public boolean isPlayer(@NotNull CommandSourceStack source) {
        return source.getSender() instanceof Player;
    }

    @Override
    public @NotNull SenderContext senderContext(@NotNull CommandSourceStack source) {
        return new PaperSenderContext(source.getSender());
    }

    @Override
    public boolean hasPermission(@NotNull CommandSourceStack source, @NotNull NickPermission permission) {
        return source.getSender().hasPermission(permission.getPermissionKey());
    }

    @Override
    public @NotNull Nickname getNickname(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String argName) {
        return ctx.getArgument(argName, Nickname.class);
    }

    @Override
    public @NotNull Optional<PlayerTarget> getPlayerTarget(
            @NotNull CommandContext<CommandSourceStack> ctx, @NotNull String argName)
            throws CommandSyntaxException {
        OfflinePlayer player = ctx.getArgument(argName, OfflinePlayer.class);
        if (player.getName() == null) return Optional.empty();
        return Optional.of(new PlayerTarget(player.getUniqueId(), player.getName()));
    }

    @Override
    public @NotNull Message brigadierMessage(@NotNull Component component) {
        return MessageComponentSerializer.message().serialize(component);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> literal(@NotNull String name) {
        return Commands.literal(name);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> nicknameArgument(
            @NotNull String argName, @NotNull Command<CommandSourceStack> executor) {
        NicknameArgument nicknameArg = new NicknameArgument();
        return Commands.argument(argName, nicknameArg)
                .suggests(nicknameArg::suggestOwnNicknames)
                .executes(executor);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> nicknameArgument(
            @NotNull String argName,
            @NotNull SuggestionProvider<CommandSourceStack> suggestions,
            @NotNull Command<CommandSourceStack> executor) {
        NicknameArgument nicknameArg = new NicknameArgument();
        return Commands.argument(argName, nicknameArg)
                .suggests(suggestions)
                .executes(executor);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> playerArgument(
            @NotNull String argName, @NotNull Command<CommandSourceStack> executor) {
        OfflinePlayerArgument playerArg = new OfflinePlayerArgument();
        return Commands.argument(argName, playerArg)
                .suggests(playerArg::suggestOnlinePlayers)
                .executes(executor);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> playerThenNicknameArgument(
            @NotNull String playerArgName, @NotNull String nicknameArgName,
            @NotNull Command<CommandSourceStack> executor) {
        OfflinePlayerArgument playerArg = new OfflinePlayerArgument();
        NicknameArgument nicknameArg = new NicknameArgument();
        return Commands.argument(playerArgName, playerArg)
                .suggests(playerArg::suggestOnlinePlayers)
                .then(Commands.argument(nicknameArgName, nicknameArg)
                        .suggests(nicknameArg::suggestOwnAndOtherNicknames)
                        .executes(executor));
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> playerThenTargetNicknameArgument(
            @NotNull String playerArgName, @NotNull String nicknameArgName,
            @NotNull Command<CommandSourceStack> executor) {
        OfflinePlayerArgument playerArg = new OfflinePlayerArgument();
        NicknameArgument nicknameArg = new NicknameArgument();
        return Commands.argument(playerArgName, playerArg)
                .suggests(playerArg::suggestOnlinePlayers)
                .then(Commands.argument(nicknameArgName, nicknameArg)
                        .suggests(nicknameArg::suggestOtherNicknames)
                        .executes(executor));
    }
}
