package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class SaveSubCommand implements SubCommand {


    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {

        NicknameArgument argument = new NicknameArgument();

        root.then(Commands.literal("save").requires(this::canExecute)
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
        boolean saved = NicknameProcessor.getInstance().saveNickname(player, nickname.nickname());
        if (!saved) {
            throw Exceptions.ERROR_CANNOT_SAVE.create();
        }
        sendFeedback(player, Message.SAVE_NICK, nickname);
        return Command.SINGLE_SUCCESS;
    }

    public int executeWithArgument(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = (Player) ctx.getSource().getSender();
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        if (nickname == null) {
            throw Exceptions.ERROR_NICK_IS_NULL.create();
        }
        if (!player.hasPermission(Constants.NICK_LENGTH_BYPASS) && nickname.normalizedNickname().length() > ConfigHandler.getInstance().getMaxLength()) {
            throw Exceptions.ERROR_LENGTH.create(nickname.normalizedNickname());
        }
        if (!player.hasPermission(Constants.NICK_REGEX_BYPASS) && !ConfigHandler.getInstance().getRegex().matcher(nickname.normalizedNickname()).matches()) {
            throw Exceptions.ERROR_REGEX.create(nickname.normalizedNickname());
        }
        boolean saved = NicknameProcessor.getInstance().saveNickname(player, nickname.nickname());
        if (!saved) {
            throw Exceptions.ERROR_CANNOT_SAVE.create();
        }
        sendFeedback(player, Message.SAVE_NICK, nickname);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_SAVE);
    }
}
