package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.config.MessageUtils;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.PlayerInfo;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class WhoSubCommand implements SubCommand {

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
        SimpleNicksCore.get().platform().runAsync(() -> {
            List<PlayerInfo> playersWithNick = NickUtils.getPlayersByNickname(nickname.getNormalizedNickname());
            if (playersWithNick.isEmpty()) {
                sender.sendRichMessage(LocaleMessage.ERROR_NO_PLAYERS_WITH_THIS_NAME.getMessage());
                return;
            }
            Component message = buildWhoMessage(nickname, playersWithNick);
            sender.sendMessage(message);
        });
        return Command.SINGLE_SUCCESS;
    }

    @NotNull
    private Component buildWhoMessage(@NotNull Nickname nickname, @NotNull List<PlayerInfo> players) {
        Component header = SimpleNicksCore.get().miniMessage().deserialize(LocaleMessage.WHO_HEADER.getMessage(),
                Placeholder.parsed("value", nickname.getNormalizedNickname()));

        if (players.isEmpty()) {
            return header.append(SimpleNicksCore.get().miniMessage().deserialize(LocaleMessage.INSERT_NONE.getMessage()));
        }

        Component result = header;
        long now = System.currentTimeMillis();

        for (PlayerInfo player : players) {
            long lastSeen = player.lastLoginMillis();
            long timeDiffSeconds = lastSeen > 0 ? (now - lastSeen) / 1000 : 0;
            result = result.append(SimpleNicksCore.get().miniMessage().deserialize(
                    LocaleMessage.WHO_INFO.getMessage(),
                    Placeholder.parsed("name", player.username()),
                    MessageUtils.getTimeFormat(timeDiffSeconds)
            ));
        }
        return result;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return !ConfigHandler.getInstance().isWhoRequiresPermission()
                || sender.hasPermission(NickPermission.NICK_WHO.getPermissionKey());
    }
}
