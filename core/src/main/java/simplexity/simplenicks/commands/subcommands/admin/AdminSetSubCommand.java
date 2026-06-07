package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.PlayerTarget;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

public class AdminSetSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public AdminSetSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("set").requires(this::canExecute)
                .then(adapter.playerThenNicknameArgument("player", "nickname", this::execute)));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        SenderContext senderCtx = adapter.senderContext(source);
        PlayerTarget target = adapter.getPlayerTarget(ctx, "player")
                .orElseThrow(() -> Exceptions.invalidPlayerSpecified(adapter, ""));
        Nickname nickname = adapter.getNickname(ctx, "nickname");
        if (!NickUtils.isValidTags(senderCtx, nickname.getNickname())) throw Exceptions.tagsNotPermitted(adapter);
        NickUtils.nicknameChecks(adapter, senderCtx, nickname);
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance()
                    .setNickname(target.id(), target.name(), nickname.getNickname());
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    if (SimpleNicksCore.get().platform().isPlayerOnline(target.id())) {
                        NickUtils.refreshDisplayName(target.id());
                        SimpleNicksCore.get().platform().sendMessageToPlayer(target.id(),
                                parseAdminMessage(LocaleMessage.SET_BY_INITIATOR.getMessage(),
                                        nickname.getNickname(), senderCtx, target.name()));
                    }
                    senderCtx.sendMessage(parseAdminMessage(LocaleMessage.SET_TARGET.getMessage(),
                            nickname.getNickname(), senderCtx, target.name()));
                });
            } else {
                sendFeedback(senderCtx, LocaleMessage.ERROR_SET_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.hasPermission(source, NickPermission.NICK_ADMIN_SET);
    }
}
