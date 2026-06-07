package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.util.NickPermission;

import java.util.UUID;

public class ResetSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public ResetSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("reset").requires(this::canExecute)
                .executes(this::execute));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) {
        S source = ctx.getSource();
        UUID uuid = adapter.getPlayerUuid(source).orElseThrow();
        SenderContext sender = adapter.senderContext(source);
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance().resetNickname(uuid);
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    NickUtils.refreshDisplayName(uuid);
                    sendFeedback(sender, LocaleMessage.RESET_SELF, null);
                });
            } else {
                sendFeedback(sender, LocaleMessage.ERROR_RESET_FAILURE, null);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.isPlayer(source)
                && (permissionNotRequired() || adapter.hasPermission(source, NickPermission.NICK_SET));
    }
}
