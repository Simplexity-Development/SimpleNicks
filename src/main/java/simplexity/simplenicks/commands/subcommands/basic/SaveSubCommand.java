package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

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
        Nickname nickname = NicknameProcessor.getInstance().getCurrentNickname(player);
        if (nickname == null) {
            throw Exceptions.ERROR_CANNOT_SAVE.create();
        }
        checkSaveSlots(player);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            boolean saved = NicknameProcessor.getInstance().saveNickname(player, nickname.getNickname());
            if (saved) {
                Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> {
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
        NickUtils.nicknameChecks(player, nickname);
        checkSaveSlots(player);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            boolean saved = NicknameProcessor.getInstance().saveNickname(player, nickname.getNickname());
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
        return player.hasPermission(Constants.NICK_SAVE);
    }

    public void checkSaveSlots(Player player) throws CommandSyntaxException {
        int currentUsed = NicknameProcessor.getInstance().getCurrentSavedNickCount(player);
        if (currentUsed >= ConfigHandler.getInstance().getMaxSaves())
            throw Exceptions.ERROR_TOO_MANY_SAVED_NAMES.create();
    }
}
