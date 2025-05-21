package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.OfflinePlayerArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class AdminResetSubCommand implements SubCommand {
    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {

        OfflinePlayerArgument offlinePlayerArgument = new OfflinePlayerArgument();

        parent.then(Commands.literal("reset")
                .requires(this::canExecute)
                .then(Commands.argument("player", offlinePlayerArgument)
                        .suggests(offlinePlayerArgument::suggestOnlinePlayers)
                        .executes(this::execute)));

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        OfflinePlayer target = ctx.getArgument("player", OfflinePlayer.class);
        boolean resetNick = NicknameProcessor.getInstance().resetNickname(target);
        if (!resetNick) throw Exceptions.ERROR_UNABLE_TO_RESET_NICK.create();
        sender.sendMessage(parseAdminMessage(Message.RESET_OTHER.getMessage(), "", sender, target));
        if (target instanceof Player onlineTarget)
            onlineTarget.sendMessage(parseAdminMessage(Message.RESET_BY_OTHER.getMessage(), "", sender, target));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(Constants.NICK_ADMIN_RESET);
    }

    @Override
    public Component parseAdminMessage(String message, String value, CommandSender initiator, @NotNull OfflinePlayer target) {
        return SubCommand.super.parseAdminMessage(message, value, initiator, target);
    }
}
