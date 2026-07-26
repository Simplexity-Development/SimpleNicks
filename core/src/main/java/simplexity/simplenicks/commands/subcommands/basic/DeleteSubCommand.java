package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.NickSuggestionProviders;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.UUID;

public class DeleteSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public DeleteSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("delete").requires(this::canExecute)
                .then(adapter.nicknameArgument("nickname",
                        NickSuggestionProviders.ownSavedNicks(adapter),
                        this::execute)));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        UUID uuid = adapter.getPlayerUuid(source).orElseThrow();
        SenderContext sender = adapter.senderContext(source);
        Nickname nickname = adapter.getNickname(ctx, "nickname");
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance().deleteNickname(uuid, nickname.getNickname());
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    NickUtils.refreshDisplayName(uuid);
                    sendFeedback(sender, LocaleMessage.DELETE_SELF, nickname);
                });
            } else {
                sendFeedback(sender, LocaleMessage.ERROR_DELETE_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.isPlayer(source)
                && (permissionNotRequired() || adapter.hasPermission(source, NickPermission.NICK_SAVE));
    }
}
