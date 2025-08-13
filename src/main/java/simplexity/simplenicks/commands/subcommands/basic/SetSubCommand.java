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
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

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
        if (nickname.getNormalizedNickname().isEmpty()) {
            throw Exceptions.ERROR_NICK_IS_NULL.create();
        }
        Player player = (Player) ctx.getSource().getSender();
        if (!NickUtils.isValidTags(player, nickname.getNickname())) throw Exceptions.ERROR_TAGS_NOT_PERMITTED.create();
        NickUtils.nicknameChecks(player, nickname);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            boolean succeeded = NicknameProcessor.getInstance().setNickname(player, nickname.getNickname());
            if (succeeded) {
                Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> {
                    refreshName(player);
                    sendFeedback(player, LocaleMessage.SET_SELF, nickname);
                });
            } else {
                sendFeedback(player, LocaleMessage.ERROR_SET_FAILURE, nickname);
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        if (!(css.getSender() instanceof Player player)) return false;
        return permissionNotRequired() || player.hasPermission(NickPermission.NICK_SET.getPermission());
    }

}
