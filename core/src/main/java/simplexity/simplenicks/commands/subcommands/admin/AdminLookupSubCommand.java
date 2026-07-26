package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.config.MessageUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.PlayerTarget;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.List;

public class AdminLookupSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public AdminLookupSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("lookup").requires(this::canExecute)
                .then(adapter.playerArgument("player", this::execute)));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        SenderContext senderCtx = adapter.senderContext(source);
        PlayerTarget target = adapter.getPlayerTarget(ctx, "player")
                .orElseThrow(() -> Exceptions.invalidPlayerSpecified(adapter, ""));
        boolean isOnline = SimpleNicksCore.get().platform().isPlayerOnline(target.id());
        SimpleNicksCore.get().platform().runAsync(() -> {
            Nickname currentNickname = NicknameProcessor.getInstance()
                    .getCurrentNickname(target.id(), isOnline);
            List<Nickname> savedNicknames = NicknameProcessor.getInstance()
                    .getSavedNicknames(target.id(), isOnline);
            senderCtx.sendMessage(lookupInfoComponent(target.name(), currentNickname, savedNicknames));
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.hasPermission(source, NickPermission.NICK_ADMIN_LOOKUP);
    }

    @NotNull
    private Component lookupInfoComponent(@NotNull String username, @Nullable Nickname currentNick,
            @Nullable List<Nickname> savedNames) {
        String nickname;
        if (currentNick == null) {
            if (savedNames == null || savedNames.isEmpty()) {
                return SimpleNicksCore.get().miniMessage()
                        .deserialize(LocaleMessage.ERROR_NO_PLAYERS_WITH_THIS_NAME.getMessage());
            }
            nickname = LocaleMessage.INSERT_NONE.getMessage();
        } else {
            nickname = currentNick.getNickname();
        }
        String infoString = LocaleMessage.LOOKUP_HEADER.getMessage()
                + LocaleMessage.LOOKUP_CURRENT.getMessage()
                + LocaleMessage.LOOKUP_SAVED.getMessage();
        return SimpleNicksCore.get().miniMessage().deserialize(infoString,
                Placeholder.unparsed("username", username),
                Placeholder.parsed("name", nickname),
                MessageUtils.savedNickListResolver(savedNames));
    }
}
