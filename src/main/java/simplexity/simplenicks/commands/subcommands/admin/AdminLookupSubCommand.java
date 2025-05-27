package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.OfflinePlayerArgument;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AdminLookupSubCommand implements SubCommand {
    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        OfflinePlayerArgument offlinePlayerArg = new OfflinePlayerArgument();
        parent.then(Commands.literal("lookup")
                .requires(this::canExecute)
                .then(Commands.argument("player", offlinePlayerArg)
                        .suggests(offlinePlayerArg::suggestOnlinePlayers)
                        .executes(this::execute)));

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        OfflinePlayer lookupTarget = ctx.getArgument("player", OfflinePlayer.class);
        Nickname currentNickname = NicknameProcessor.getInstance().getCurrentNickname(lookupTarget);
        List<Nickname> savedNicknames = NicknameProcessor.getInstance().getSavedNicknames(lookupTarget);
        return 0;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(Constants.NICK_ADMIN_LOOKUP);
    }
}
