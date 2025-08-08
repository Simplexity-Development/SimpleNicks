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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.config.LocaleMessage;
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
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            List<OfflinePlayer> playersWithNick = NickUtils.getInstance()
                    .getOfflinePlayersByNickname(nickname.getNormalizedNickname());

            if (playersWithNick == null) {
                sender.sendRichMessage(LocaleMessage.ERROR_NO_PLAYERS_FOUND_BY_THIS_NAME.getMessage());
                return;
            }

            Component message = buildWhoMessage(nickname, playersWithNick);
            sender.sendMessage(message);
        });

        return Command.SINGLE_SUCCESS;
    }

    private Component buildWhoMessage(Nickname nickname, List<OfflinePlayer> players) {
        Component header = miniMessage.deserialize(LocaleMessage.NICK_WHO_HEADER.getMessage(),
                Placeholder.parsed("value", nickname.getNormalizedNickname()));

        if (players.isEmpty()) {
            return header.append(miniMessage.deserialize(LocaleMessage.INSERT_NONE.getMessage()));
        }

        Component result = header;
        long now = System.currentTimeMillis();

        for (OfflinePlayer player : players) {
            String username = player.getName();
            if (username == null) continue;

            long timeDiffSeconds = (now - player.getLastSeen()) / 1000;
            result = result.append(miniMessage.deserialize(
                    LocaleMessage.NICK_WHO_USER.getMessage(),
                    Placeholder.parsed("name", username),
                    MessageUtils.getTimeFormat(timeDiffSeconds)
            ));
        }
        return result;
    }


    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(Constants.NICK_WHO);
    }
}
