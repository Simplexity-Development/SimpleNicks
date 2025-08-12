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
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class DeleteSubCommand implements SubCommand {


    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {

        NicknameArgument argument = new NicknameArgument();

        parent.then(Commands.literal("delete").requires(this::canExecute)
                .then(Commands.argument("nickname", argument)
                        .suggests(argument::suggestOwnNicknames)
                        .executes(this::execute))
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = (Player) ctx.getSource().getSender();
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            boolean success = NicknameProcessor.getInstance().deleteNickname(player, nickname.getNickname());
            if (success) {
                Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> {
                    NickUtils.refreshDisplayName(player.getUniqueId());
                    sendFeedback(player, LocaleMessage.DELETE_NICK, nickname);
                });
            } else {
                sendFeedback(player, LocaleMessage.ERROR_DELETE_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }


    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_SAVE);
    }
}
