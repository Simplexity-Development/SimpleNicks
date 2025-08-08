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
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            boolean success = NicknameProcessor.getInstance().setNickname(target, cleanedNick);
            if (success) {
                Bukkit.getScheduler().runTask(SimpleNicks.getInstance(), () -> {
                    if ((target instanceof Player onlineTarget)) {
                        NickUtils.getInstance().refreshNickname(target.getUniqueId());
                        onlineTarget.sendMessage(parseAdminMessage(LocaleMessage.CHANGED_BY_OTHER.getMessage(), cleanedNick, sender, target));
                    }
                    sender.sendMessage(parseAdminMessage(LocaleMessage.CHANGED_OTHER.getMessage(), cleanedNick, sender, target));
                });
            } else {
                sender.sendRichMessage(LocaleMessage.ERROR_SET_FAILURE.getMessage());
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(Constants.NICK_ADMIN_SET);
    }

}
