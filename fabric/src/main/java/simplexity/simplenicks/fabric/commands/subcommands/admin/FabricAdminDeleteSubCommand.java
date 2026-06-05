package simplexity.simplenicks.fabric.commands.subcommands.admin;

import net.minecraft.server.players.NameAndId;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.fabric.commands.subcommands.FabricSubCommand;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.Collection;

public class FabricAdminDeleteSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("delete").requires(this::canExecute)
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .suggests((ctx, builder) -> {
                            ctx.getSource().getServer().getPlayerList().getPlayers()
                                    .forEach(p -> builder.suggest(p.getGameProfile().name()));
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("nickname", StringArgumentType.greedyString())
                                .suggests((ctx, builder) -> {
                                    Collection<NameAndId> profiles;
                                    try {
                                        profiles = GameProfileArgument.getGameProfiles(ctx, "player");
                                    } catch (CommandSyntaxException e) {
                                        return builder.buildFuture();
                                    }
                                    profiles.stream().findFirst().ifPresent(profile -> {
                                        boolean online = ctx.getSource().getServer()
                                                .getPlayerList().getPlayer(profile.id()) != null;
                                        NicknameProcessor.getInstance()
                                                .getSavedNicknames(profile.id(), online)
                                                .forEach(n -> builder.suggest(n.getNickname()));
                                    });
                                    return builder.buildFuture();
                                })
                                .executes(this::execute)
                        )
                )
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(ctx, "player");
        NameAndId target = profiles.stream().findFirst().orElseThrow(
                () -> Exceptions.invalidPlayerSpecified(null));
        String raw = StringArgumentType.getString(ctx, "nickname");
        Nickname nickname = new Nickname(raw, NickUtils.normalizeNickname(raw));
        CommandSourceStack source = ctx.getSource();
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance()
                    .deleteNickname(target.id(), nickname.getNickname());
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    ServerPlayer onlineTarget = source.getServer().getPlayerList().getPlayer(target.id());
                    if (onlineTarget != null) {
                        NickUtils.refreshDisplayName(target.id());
                        sendComponentToPlayer(onlineTarget, parseAdminMessage(
                                LocaleMessage.DELETED_BY_INITIATOR.getMessage(),
                                nickname.getNickname(), source, target));
                    }
                    sendToSource(source, parseAdminMessage(LocaleMessage.DELETED_TARGET.getMessage(),
                            nickname.getNickname(), source, target));
                });
            } else {
                SimpleNicksCore.get().platform().runSync(() ->
                        sendFeedback(source, LocaleMessage.ERROR_DELETE_FAILURE, nickname));
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        return FabricPermissions.check(source, NickPermission.NICK_ADMIN_DELETE);
    }
}
