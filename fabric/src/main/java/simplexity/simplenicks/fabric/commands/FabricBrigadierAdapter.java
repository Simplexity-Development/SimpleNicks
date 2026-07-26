package simplexity.simplenicks.fabric.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.players.NameAndId;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.NickSuggestionProviders;
import simplexity.simplenicks.fabric.platform.FabricPlatformAdapter;
import simplexity.simplenicks.fabric.platform.FabricSenderContext;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.PlayerTarget;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link BrigadierAdapter} implementation for the Fabric platform.
 * <p>
 * The command tree is built (via {@code CommandRegistrationCallback}) before {@link SimpleNicksCore} is initialized,
 * so audiences are resolved lazily on use rather than captured at construction time.
 * </p>
 */
public class FabricBrigadierAdapter implements BrigadierAdapter<CommandSourceStack> {

    @Override
    public @NotNull Optional<UUID> getPlayerUuid(@NotNull CommandSourceStack source) {
        return source.getPlayer() != null
                ? Optional.of(source.getPlayer().getUUID())
                : Optional.empty();
    }

    @Override
    public @NotNull Optional<String> getPlayerUsername(@NotNull CommandSourceStack source) {
        return source.getPlayer() != null
                ? Optional.of(source.getPlayer().getGameProfile().name())
                : Optional.empty();
    }

    @Override
    public boolean isPlayer(@NotNull CommandSourceStack source) {
        return source.getPlayer() != null;
    }

    @Override
    public @NotNull SenderContext senderContext(@NotNull CommandSourceStack source) {
        return new FabricSenderContext(source);
    }

    @Override
    public boolean hasPermission(@NotNull CommandSourceStack source, @NotNull NickPermission permission) {
        return FabricPermissions.check(source, permission);
    }

    @Override
    public @NotNull Nickname getNickname(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String argName) {
        String rawNickname = StringArgumentType.getString(ctx, argName);
        return new Nickname(rawNickname, NickUtils.normalizeNickname(rawNickname));
    }

    @Override
    public @NotNull Optional<PlayerTarget> getPlayerTarget(
            @NotNull CommandContext<CommandSourceStack> ctx, @NotNull String argName)
            throws CommandSyntaxException {
        Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(ctx, argName);
        return profiles.stream()
                .findFirst()
                .map(profile -> new PlayerTarget(profile.id(), profile.name()));
    }

    @Override
    public @NotNull Message brigadierMessage(@NotNull Component component) {
        FabricPlatformAdapter platform = (FabricPlatformAdapter) SimpleNicksCore.get().platform();
        return platform.getAudiences().asNative(component);
    }

    @Override
    public @NotNull LiteralArgumentBuilder<CommandSourceStack> literal(@NotNull String name) {
        return Commands.literal(name);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> nicknameArgument(
            @NotNull String argName, @NotNull Command<CommandSourceStack> executor) {
        return Commands.argument(argName, StringArgumentType.greedyString())
                .suggests(NickSuggestionProviders.ownSavedNicks(this))
                .executes(executor);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> nicknameArgument(
            @NotNull String argName,
            @NotNull SuggestionProvider<CommandSourceStack> suggestions,
            @NotNull Command<CommandSourceStack> executor) {
        return Commands.argument(argName, StringArgumentType.greedyString())
                .suggests(suggestions)
                .executes(executor);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> playerArgument(
            @NotNull String argName, @NotNull Command<CommandSourceStack> executor) {
        return Commands.argument(argName, GameProfileArgument.gameProfile())
                .suggests((ctx, builder) -> {
                    ctx.getSource().getServer().getPlayerList().getPlayers()
                            .forEach(player -> builder.suggest(player.getGameProfile().name()));
                    return builder.buildFuture();
                })
                .executes(executor);
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> playerThenNicknameArgument(
            @NotNull String playerArgName, @NotNull String nicknameArgName,
            @NotNull Command<CommandSourceStack> executor) {
        return Commands.argument(playerArgName, GameProfileArgument.gameProfile())
                .suggests((ctx, builder) -> {
                    ctx.getSource().getServer().getPlayerList().getPlayers()
                            .forEach(player -> builder.suggest(player.getGameProfile().name()));
                    return builder.buildFuture();
                })
                .then(Commands.argument(nicknameArgName, StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            try {
                                GameProfileArgument.getGameProfiles(ctx, playerArgName).stream()
                                        .findFirst()
                                        .ifPresent(profile -> {
                                            boolean online = ctx.getSource().getServer()
                                                    .getPlayerList().getPlayer(profile.id()) != null;
                                            NicknameProcessor.getInstance()
                                                    .getSavedNicknames(profile.id(), online)
                                                    .forEach(savedNickname -> builder.suggest(savedNickname.getNickname()));
                                        });
                            } catch (CommandSyntaxException ignored) {
                            }
                            return builder.buildFuture();
                        })
                        .executes(executor));
    }

    @Override
    public @NotNull ArgumentBuilder<CommandSourceStack, ?> playerThenTargetNicknameArgument(
            @NotNull String playerArgName, @NotNull String nicknameArgName,
            @NotNull Command<CommandSourceStack> executor) {
        return Commands.argument(playerArgName, GameProfileArgument.gameProfile())
                .suggests((ctx, builder) -> {
                    ctx.getSource().getServer().getPlayerList().getPlayers()
                            .forEach(player -> builder.suggest(player.getGameProfile().name()));
                    return builder.buildFuture();
                })
                .then(Commands.argument(nicknameArgName, StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            try {
                                GameProfileArgument.getGameProfiles(ctx, playerArgName).stream()
                                        .findFirst()
                                        .ifPresent(profile -> {
                                            boolean online = ctx.getSource().getServer()
                                                    .getPlayerList().getPlayer(profile.id()) != null;
                                            NicknameProcessor.getInstance()
                                                    .getSavedNicknames(profile.id(), online)
                                                    .forEach(savedNickname -> builder.suggest(savedNickname.getNickname()));
                                        });
                            } catch (CommandSyntaxException ignored) {
                            }
                            return builder.buildFuture();
                        })
                        .executes(executor));
    }
}
