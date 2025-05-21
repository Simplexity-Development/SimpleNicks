package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.arguments.OfflinePlayerArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class AdminSetSubCommand implements SubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {

        OfflinePlayerArgument offlinePlayerArgument = new OfflinePlayerArgument();
        NicknameArgument nicknameArgument = new NicknameArgument();

        parent.then(Commands.literal("set")
                .requires(this::canExecute)
                .then(Commands.argument("player", offlinePlayerArgument)
                        .suggests(offlinePlayerArgument::suggestOnlinePlayers)
                        .then(Commands.argument("nickname", nicknameArgument)
                                .suggests(nicknameArgument::suggestOwnAndOtherNicknames)
                                .executes(this::execute)
                        )
                )
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        OfflinePlayer target = ctx.getArgument("player", OfflinePlayer.class);
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        String cleanedNick = NickUtils.getInstance().cleanNonPermittedTags(sender, nickname.getNickname());
        nickname.setNickname(cleanedNick);
        NickUtils.getInstance().nicknameChecks(sender, nickname);
        boolean setSuccessfully = NicknameProcessor.getInstance().setNickname(target, cleanedNick);
        if (!setSuccessfully) throw Exceptions.ERROR_SET_FAILURE.create();
        sender.sendMessage(parseAdminMessage(Message.CHANGED_OTHER.getMessage(), cleanedNick, sender, target));
        if (target instanceof Player onlineTarget)
            onlineTarget.sendMessage(parseAdminMessage(Message.CHANGED_BY_OTHER.getMessage(), cleanedNick, sender, target));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(Constants.NICK_ADMIN_SET);
    }

}
