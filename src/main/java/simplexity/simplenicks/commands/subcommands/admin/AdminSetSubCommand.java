package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicks;
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
        Nickname inputNickname = ctx.getArgument("nickname", Nickname.class);

        String nickname = getPermissionProcessedNickname(inputNickname.nickname(), sender, target);
        if (nickname == null) throw Exceptions.ERROR_CANNOT_ACCESS_PLAYERS_PERMISSIONS.create();

        boolean setSuccessfully = NicknameProcessor.getInstance().setNickname(target, nickname);
        if (!setSuccessfully) throw Exceptions.ERROR_SET_FAILURE.create();

        sender.sendMessage(parseMessage(Message.CHANGED_OTHER.getMessage(), nickname, sender, target));
        if (target instanceof Player onlineTarget) onlineTarget.sendMessage(parseMessage(Message.CHANGED_BY_OTHER.getMessage(), nickname, sender, target));

        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        boolean canUse = sender.hasPermission(Constants.NICK_SET_OTHERS_BASIC) ||
                sender.hasPermission(Constants.NICK_SET_OTHERS_RESTRICTIVE) ||
                sender.hasPermission(Constants.NICK_SET_OTHERS_FULL);
        return sender.hasPermission(Constants.NICK_SET_OTHERS) && canUse;
    }

    @Nullable
    private String getPermissionProcessedNickname(String nickname, CommandSender sender, OfflinePlayer player) {
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_FULL)) {
            return nickname;
        }
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_BASIC)) {
            return NickUtils.getInstance().cleanNonPermittedTags(sender, nickname);
        }
        if (sender.hasPermission(Constants.NICK_SET_OTHERS_RESTRICTIVE)) {
            if (!(player instanceof Player onlinePlayer)) return null;
            return NickUtils.getInstance().cleanNonPermittedTags(onlinePlayer, nickname);
        }
        return null;
    }

    private Component parseMessage(String message, String value, CommandSender initiator, @NotNull OfflinePlayer target) {
        MiniMessage miniMessage = SimpleNicks.getMiniMessage();
        Component initiatorName;
        String targetUserName = target.getName();
        if (targetUserName == null) {
            targetUserName = "[Username not found, idk how but you got this error.]";
        }
        if (initiator instanceof Player playerInitiator) {
            initiatorName = playerInitiator.displayName();
        } else {
            initiatorName = miniMessage.deserialize(Message.SERVER_DISPLAY_NAME.getMessage());
        }
        return miniMessage.deserialize(message,
                Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage()),
                Placeholder.parsed("value", value),
                Placeholder.component("initiator", initiatorName),
                Placeholder.parsed("target", targetUserName)
        );
    }

}
