package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.subcommands.NickSuggestionProviders;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.config.MessageUtils;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.PlayerInfo;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.List;

public class WhoSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public WhoSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("who").requires(this::canExecute)
                .then(adapter.nicknameArgument("nickname",
                        NickSuggestionProviders.activeOnlineNicks(),
                        this::execute)));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        SenderContext sender = adapter.senderContext(source);
        Nickname nickname = adapter.getNickname(ctx, "nickname");
        SimpleNicksCore.get().platform().runAsync(() -> {
            List<PlayerInfo> playersWithNick = NickUtils.getPlayersByNickname(nickname.getNormalizedNickname());
            if (playersWithNick.isEmpty()) {
                sendFeedback(sender, LocaleMessage.ERROR_NO_PLAYERS_WITH_THIS_NAME, null);
                return;
            }
            sender.sendMessage(buildWhoMessage(nickname, playersWithNick));
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return !ConfigHandler.getInstance().isWhoRequiresPermission()
                || adapter.hasPermission(source, NickPermission.NICK_WHO);
    }

    @NotNull
    private Component buildWhoMessage(@NotNull Nickname nickname, @NotNull List<PlayerInfo> players) {
        Component header = SimpleNicksCore.get().miniMessage().deserialize(
                LocaleMessage.WHO_HEADER.getMessage(),
                Placeholder.parsed("value", nickname.getNormalizedNickname()));

        if (players.isEmpty()) {
            return header.append(SimpleNicksCore.get().miniMessage()
                    .deserialize(LocaleMessage.INSERT_NONE.getMessage()));
        }

        Component result = header;
        long now = System.currentTimeMillis();
        for (PlayerInfo player : players) {
            long lastSeen = player.lastLoginMillis();
            long timeDiffSeconds = lastSeen > 0 ? (now - lastSeen) / 1000 : 0;
            result = result.append(SimpleNicksCore.get().miniMessage().deserialize(
                    LocaleMessage.WHO_INFO.getMessage(),
                    Placeholder.parsed("name", player.username()),
                    MessageUtils.getTimeFormat(timeDiffSeconds)));
        }
        return result;
    }
}
