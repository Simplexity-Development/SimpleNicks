package simplexity.simplenicks.fabric.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.fabric.platform.FabricSenderContext;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

public class FabricSaveSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("save").requires(this::canExecute)
                .executes(this::execute)
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            ServerPlayer player = ctx.getSource().getPlayer();
                            if (player == null) return builder.buildFuture();
                            NicknameProcessor.getInstance()
                                    .getSavedNicknames(player.getUUID(), true)
                                    .forEach(n -> builder.suggest(n.getNickname()));
                            return builder.buildFuture();
                        })
                        .executes(this::executeWithArgument)
                )
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;
        Nickname nickname = NicknameProcessor.getInstance().getCurrentNickname(player.getUUID(), true);
        if (nickname == null) throw Exceptions.cannotSave();
        checkSaveSlots(player);
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean saved = NicknameProcessor.getInstance()
                    .saveNickname(player.getUUID(), player.getGameProfile().name(), nickname.getNickname());
            if (saved) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    NickUtils.refreshDisplayName(player.getUUID());
                    sendToPlayer(player, LocaleMessage.SAVE_NICK, nickname);
                });
            } else {
                SimpleNicksCore.get().platform().runSync(() ->
                        sendToPlayer(player, LocaleMessage.ERROR_SAVE_FAILURE, nickname));
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    private int executeWithArgument(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;
        String raw = StringArgumentType.getString(ctx, "nickname");
        Nickname nickname = new Nickname(raw, NickUtils.normalizeNickname(raw));
        NickUtils.nicknameChecks(new FabricSenderContext(ctx.getSource()), nickname);
        checkSaveSlots(player);
        if (NicknameProcessor.getInstance().playerAlreadySavedThis(player.getUUID(), nickname.getNickname())) {
            throw Exceptions.alreadySaved();
        }
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean saved = NicknameProcessor.getInstance()
                    .saveNickname(player.getUUID(), player.getGameProfile().name(), nickname.getNickname());
            if (saved) {
                SimpleNicksCore.get().platform().runSync(() ->
                        sendToPlayer(player, LocaleMessage.SAVE_NICK, nickname));
            } else {
                SimpleNicksCore.get().platform().runSync(() ->
                        sendToPlayer(player, LocaleMessage.ERROR_SAVE_FAILURE, nickname));
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        if (source.getPlayer() == null) return false;
        return permissionNotRequired()
                || FabricPermissions.check(source, NickPermission.NICK_SAVE);
    }

    private void checkSaveSlots(@NotNull ServerPlayer player) throws CommandSyntaxException {
        int count = NicknameProcessor.getInstance().getCurrentSavedNickCount(player.getUUID(), true);
        if (count >= ConfigHandler.getInstance().getMaxSaves()) throw Exceptions.tooManySavedNames();
    }
}
