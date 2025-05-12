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
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class ResetSubCommand implements SubCommand {
    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {

        root.then(Commands.literal("reset").requires(this::canExecute)
                .executes(this::execute)
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        OfflinePlayer player = (OfflinePlayer) ctx.getSource().getSender();
        NicknameProcessor.getInstance().resetNickname(player);
        if (player instanceof Player onlinePlayer) sendFeedback(onlinePlayer, Message.RESET_SELF, null);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_RESET);
    }
}
