package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
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
public class SetSubCommand implements SubCommand {


    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {

        NicknameArgument argument = new NicknameArgument();

        parent.then(Commands.literal("set").requires(this::canExecute)
                .then(Commands.argument("nickname", argument)
                        .suggests(argument::suggestOwnNicknames)
                        .executes(this::execute)
                )
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        if (nickname == null) {
            throw Exceptions.ERROR_NICK_IS_NULL.create();
        }
        Player player = (Player) ctx.getSource().getSender();
        if (!player.hasPermission(Constants.NICK_LENGTH_BYPASS) && nickname.normalizedNickname().length() > ConfigHandler.getInstance().getMaxLength()) {
            throw Exceptions.ERROR_LENGTH.create(nickname.normalizedNickname());
        }
        if (!player.hasPermission(Constants.NICK_REGEX_BYPASS) && !ConfigHandler.getInstance().getRegex().matcher(nickname.normalizedNickname()).matches()) {
            throw Exceptions.ERROR_REGEX.create(nickname.normalizedNickname());
        }
        NicknameProcessor.getInstance().setNickname((OfflinePlayer) ctx.getSource().getSender(), nickname.nickname());
        sendFeedback(player, Message.CHANGED_SELF, nickname);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_SET);
    }

}
