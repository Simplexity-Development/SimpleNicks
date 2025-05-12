package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class DeleteSubCommand implements SubCommand {
    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {

        NicknameArgument argument = new NicknameArgument();

        root.then(Commands.literal("delete").requires(this::canExecute)
                .then(Commands.argument("nickname", argument)
                        .suggests(argument::suggestOwnNicknames)
                        .executes(this::execute))
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        OfflinePlayer player = (OfflinePlayer) ctx.getSource().getSender();
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        boolean deleted = NicknameProcessor.getInstance().deleteNickname(player, nickname.nickname());
        if (deleted) return Command.SINGLE_SUCCESS;
        return 0;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_DELETE);
    }
}
