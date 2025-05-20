package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class LookupSubCommand implements SubCommand {
    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        NicknameArgument argument = new NicknameArgument();
        parent.then(Commands.literal("lookup").requires(this::canExecute)
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
        sender.sendMessage("Users with name: " + nickname.getNormalizedNickname());
        if (playersWithNick.isEmpty()) {
            sender.sendMessage("none");
            return 1;
        }
        for (OfflinePlayer player : playersWithNick) {
            String username = player.getName();
            long lastSeen = player.getLastSeen();
            long currentTime = System.currentTimeMillis();
            long timeDiff = currentTime - lastSeen;
            timeDiff = timeDiff / 1000;
            long seconds = timeDiff % 60;
            long minutes = (timeDiff / 60) % 60;
            long hours = (timeDiff / (60 * 60)) % 24;
            long days = (timeDiff / (60 * 60 * 24)) % 365;
            sender.sendMessage("User: " + username);
            sender.sendMessage("Last Seen: " + days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds ago");
        }
        return 1;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(Constants.NICK_LOOKUP);
    }
}
