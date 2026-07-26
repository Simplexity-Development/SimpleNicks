package simplexity.simplenicks.commands.subcommands.basic;

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
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.UUID;

public class SetSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public SetSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("set").requires(this::canExecute)
                .then(adapter.nicknameArgument("nickname", this::execute)));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        UUID uuid = adapter.getPlayerUuid(source).orElseThrow(() -> Exceptions.invalidPlayerSpecified(adapter, ""));
        String username = adapter.getPlayerUsername(source).orElse(uuid.toString());
        Nickname nickname = adapter.getNickname(ctx, "nickname");
        if (nickname.getNormalizedNickname().isEmpty()) throw Exceptions.nickIsNull(adapter);
        SenderContext sender = adapter.senderContext(source);
        if (!NickUtils.isValidTags(sender, nickname.getNickname())) throw Exceptions.tagsNotPermitted(adapter);
        NickUtils.nicknameChecks(adapter, sender, nickname);
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean succeeded = NicknameProcessor.getInstance().setNickname(uuid, username, nickname.getNickname());
            if (succeeded) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    refreshName(uuid, true);
                    sendFeedback(sender, LocaleMessage.SET_SELF, nickname);
                });
            } else {
                sendFeedback(sender, LocaleMessage.ERROR_SET_FAILURE, nickname);
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
