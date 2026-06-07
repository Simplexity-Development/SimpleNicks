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
import simplexity.simplenicks.util.NickPermission;

public class AdminResetSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public AdminResetSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("reset").requires(this::canExecute)
                .then(adapter.playerArgument("player", this::execute)));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        SenderContext senderCtx = adapter.senderContext(source);
        PlayerTarget target = adapter.getPlayerTarget(ctx, "player")
                .orElseThrow(() -> Exceptions.invalidPlayerSpecified(adapter, ""));
        String targetName = !target.name().isEmpty() ? target.name() : "[Username not found]";
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance().resetNickname(target.id());
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    if (SimpleNicksCore.get().platform().isPlayerOnline(target.id())) {
                        NickUtils.refreshDisplayName(target.id());
                        SimpleNicksCore.get().platform().sendMessageToPlayer(target.id(),
                                parseAdminMessage(LocaleMessage.RESET_BY_INITIATOR.getMessage(),
                                        "", senderCtx, targetName));
                    }
                    senderCtx.sendMessage(parseAdminMessage(LocaleMessage.RESET_TARGET.getMessage(),
                            "", senderCtx, targetName));
                });
            } else {
                sendFeedback(senderCtx, LocaleMessage.ERROR_RESET_FAILURE, null);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.hasPermission(source, NickPermission.NICK_ADMIN_RESET);
    }
}
