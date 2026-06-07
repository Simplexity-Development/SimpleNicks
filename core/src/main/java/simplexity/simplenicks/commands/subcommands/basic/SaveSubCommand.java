package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.commands.subcommands.NickSuggestionProviders;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.UUID;

public class SaveSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public SaveSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("save").requires(this::canExecute)
                .executes(this::execute)
                .then(adapter.nicknameArgument("nickname",
                        NickSuggestionProviders.ownSavedNicks(adapter),
                        this::executeWithArgument)));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        UUID uuid = adapter.getPlayerUuid(source).orElseThrow();
        String username = adapter.getPlayerUsername(source).orElse(uuid.toString());
        SenderContext sender = adapter.senderContext(source);
        Nickname nickname = NicknameProcessor.getInstance().getCurrentNickname(uuid, true);
        if (nickname == null) throw Exceptions.cannotSave(adapter);
        checkSaveSlots(uuid);
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean saved = NicknameProcessor.getInstance().saveNickname(uuid, username, nickname.getNickname());
            if (saved) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    NickUtils.refreshDisplayName(uuid);
                    sendFeedback(sender, LocaleMessage.SAVE_NICK, nickname);
                });
            } else {
                sendFeedback(sender, LocaleMessage.ERROR_SAVE_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    public int executeWithArgument(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        UUID uuid = adapter.getPlayerUuid(source).orElseThrow();
        String username = adapter.getPlayerUsername(source).orElse(uuid.toString());
        SenderContext sender = adapter.senderContext(source);
        Nickname nickname = adapter.getNickname(ctx, "nickname");
        NickUtils.nicknameChecks(adapter, sender, nickname);
        checkSaveSlots(uuid);
        if (NicknameProcessor.getInstance().playerAlreadySavedThis(uuid, nickname.getNickname())) {
            throw Exceptions.alreadySaved(adapter);
        }
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean saved = NicknameProcessor.getInstance().saveNickname(uuid, username, nickname.getNickname());
            if (saved) {
                sendFeedback(sender, LocaleMessage.SAVE_NICK, nickname);
            } else {
                sendFeedback(sender, LocaleMessage.ERROR_SAVE_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.isPlayer(source)
                && (permissionNotRequired() || adapter.hasPermission(source, NickPermission.NICK_SAVE));
    }

    private void checkSaveSlots(@NotNull UUID uuid) throws CommandSyntaxException {
        int currentUsed = NicknameProcessor.getInstance().getCurrentSavedNickCount(uuid, true);
        if (currentUsed >= ConfigHandler.getInstance().getMaxSaves()) throw Exceptions.tooManySavedNames(adapter);
    }
}
