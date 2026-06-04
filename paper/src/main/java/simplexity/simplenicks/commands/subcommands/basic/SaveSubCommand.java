package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.PaperSenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public class SaveSubCommand implements SubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        NicknameArgument argument = new NicknameArgument();
        parent.then(Commands.literal("save").requires(this::canExecute)
                .executes(this::execute)
                .then(Commands.argument("nickname", argument)
                        .suggests(argument::suggestOwnNicknames)
                        .executes(this::executeWithArgument))
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = (Player) ctx.getSource().getSender();
        Nickname nickname = NicknameProcessor.getInstance().getCurrentNickname(player.getUniqueId(), true);
        if (nickname == null) throw Exceptions.cannotSave();
        checkSaveSlots(player);
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean saved = NicknameProcessor.getInstance().saveNickname(player.getUniqueId(), player.getName(), nickname.getNickname());
            if (saved) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    NickUtils.refreshDisplayName(player.getUniqueId());
                    sendFeedback(player, LocaleMessage.SAVE_NICK, nickname);
                });
            } else {
                sendFeedback(player, LocaleMessage.ERROR_SAVE_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    public int executeWithArgument(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = (Player) ctx.getSource().getSender();
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        NickUtils.nicknameChecks(new PaperSenderContext(player), nickname);
        checkSaveSlots(player);
        if (NicknameProcessor.getInstance().playerAlreadySavedThis(player.getUniqueId(), nickname.getNickname())) {
            throw Exceptions.alreadySaved();
        }
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean saved = NicknameProcessor.getInstance().saveNickname(player.getUniqueId(), player.getName(), nickname.getNickname());
            if (saved) {
                sendFeedback(player, LocaleMessage.SAVE_NICK, nickname);
            } else {
                sendFeedback(player, LocaleMessage.ERROR_SAVE_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        if (!(css.getSender() instanceof Player player)) return false;
        return permissionNotRequired() || player.hasPermission(NickPermission.NICK_SAVE.getPermissionKey());
    }

    public void checkSaveSlots(@NotNull Player player) throws CommandSyntaxException {
        int currentUsed = NicknameProcessor.getInstance().getCurrentSavedNickCount(player.getUniqueId(), true);
        if (currentUsed >= ConfigHandler.getInstance().getMaxSaves()) throw Exceptions.tooManySavedNames();
    }
}
