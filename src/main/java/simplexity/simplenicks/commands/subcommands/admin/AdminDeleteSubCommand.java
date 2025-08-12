package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.arguments.OfflinePlayerArgument;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public class AdminDeleteSubCommand implements SubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        OfflinePlayerArgument offlinePlayerArg = new OfflinePlayerArgument();
        NicknameArgument nicknameArg = new NicknameArgument();

        parent.then(Commands.literal("delete")
                .requires(this::canExecute)
                .then(Commands.argument("player", offlinePlayerArg)
                        .suggests(offlinePlayerArg::suggestOnlinePlayers)
                        .then(Commands.argument("nickname", nicknameArg)
                                .suggests(nicknameArg::suggestOtherNicknames)
                                .executes(this::execute))));
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        OfflinePlayer target = ctx.getArgument("player", OfflinePlayer.class);
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            boolean success = NicknameProcessor.getInstance().deleteNickname(target, nickname.getNickname());
            if (success) {
                Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> {
                    if ((target instanceof Player onlineTarget)) {
                        NickUtils.refreshDisplayName(target.getUniqueId());
                        onlineTarget.sendMessage(parseAdminMessage(LocaleMessage.NICK_DELETED_BY_OTHER.getMessage(), nickname.getNickname(), sender, target));
                    }
                    sender.sendMessage(parseAdminMessage(LocaleMessage.NICK_DELETED_OTHER.getMessage(), nickname.getNickname(), sender, target));
                });
            } else {
                sender.sendRichMessage(LocaleMessage.ERROR_DELETE_FAILURE.getMessage());
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(NickPermission.NICK_ADMIN_DELETE.getPermission());
    }

}
