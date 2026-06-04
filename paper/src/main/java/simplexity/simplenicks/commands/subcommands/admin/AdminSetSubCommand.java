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
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.arguments.OfflinePlayerArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.PaperSenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public class AdminSetSubCommand implements SubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        OfflinePlayerArgument offlinePlayerArgument = new OfflinePlayerArgument();
        NicknameArgument nicknameArgument = new NicknameArgument();

        parent.then(Commands.literal("set").requires(this::canExecute)
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
        String targetUsername = target.getName();
        if (targetUsername == null) throw Exceptions.invalidPlayerSpecified(target);
        PaperSenderContext senderCtx = new PaperSenderContext(sender);
        if (!NickUtils.isValidTags(senderCtx, nickname.getNickname())) throw Exceptions.tagsNotPermitted();
        NickUtils.nicknameChecks(senderCtx, nickname);
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance().setNickname(target.getUniqueId(), targetUsername, nickname.getNickname());
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());
                    if (onlineTarget != null) {
                        NickUtils.refreshDisplayName(target.getUniqueId());
                        onlineTarget.sendMessage(parseAdminMessage(LocaleMessage.SET_BY_INITIATOR.getMessage(), nickname.getNickname(), sender, target));
                    }
                    sender.sendMessage(parseAdminMessage(LocaleMessage.SET_TARGET.getMessage(), nickname.getNickname(), sender, target));
                });
            } else {
                sender.sendRichMessage(LocaleMessage.ERROR_SET_FAILURE.getMessage());
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender().hasPermission(NickPermission.NICK_ADMIN_SET.getPermissionKey());
    }
}
