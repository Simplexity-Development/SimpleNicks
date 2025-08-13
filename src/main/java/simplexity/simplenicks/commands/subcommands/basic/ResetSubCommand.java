package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public class ResetSubCommand implements SubCommand {
    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {

        parent.then(Commands.literal("reset").requires(this::canExecute)
                .executes(this::execute)
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            boolean success = NicknameProcessor.getInstance().resetNickname(player);
            if (success) {
                Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> {
                    NickUtils.refreshDisplayName(player.getUniqueId());
                    sendFeedback(player, LocaleMessage.RESET_SELF, null);
                });
            } else {
                sendFeedback(player, LocaleMessage.ERROR_RESET_FAILURE, null);
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
