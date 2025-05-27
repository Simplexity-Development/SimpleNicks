package simplexity.simplenicks.commands.subcommands.basic;

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
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.config.MessageUtils;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class WhoSubCommand implements SubCommand {

    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        NicknameArgument argument = new NicknameArgument();
        parent.then(Commands.literal("who").requires(this::canExecute)
                .then(Commands.argument("nickname", argument)
                        .suggests(argument::suggestAllOnlineNicknames)
                        .executes(this::execute)));

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        List<OfflinePlayer> playersWithNick = NickUtils.getInstance().getOfflinePlayersByNickname(nickname.getNormalizedNickname());
        if (playersWithNick == null) throw Exceptions.ERROR_NICK_IS_NULL.create();
        Component messageComponent = miniMessage.deserialize(Message.NICK_WHO_HEADER.getMessage(),
                Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage()),
                Placeholder.parsed("value", nickname.getNormalizedNickname()));
        if (playersWithNick.isEmpty()) {
            messageComponent = messageComponent.append(miniMessage.deserialize(
                    Message.INSERT_NONE.getMessage()));
            sender.sendMessage(messageComponent);
            return Command.SINGLE_SUCCESS;
        }
        for (OfflinePlayer player : playersWithNick) {
            String username = player.getName();
            if (username == null) continue;
            long lastSeen = player.getLastSeen();
            long currentTime = System.currentTimeMillis();
            long timeDiff = currentTime - lastSeen;
            timeDiff = timeDiff / 1000;
            messageComponent = messageComponent.append(miniMessage.deserialize(
                    Message.NICK_WHO_USER.getMessage() + Message.INSERT_TIME_FORMAT_AGO.getMessage(),
                    Placeholder.parsed("name", username),
                    MessageUtils.getTimeFormat(timeDiff)));

        }
        sender.sendMessage(messageComponent);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(Constants.NICK_WHO);
    }
}
