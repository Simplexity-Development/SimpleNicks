package simplexity.simplenicks.fabric.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.config.MessageUtils;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.PlayerInfo;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.List;

public class FabricWhoSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("who").requires(this::canExecute)
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            SimpleNicksCore.get().platform().getOnlinePlayers().forEach(uuid -> {
                                Nickname nick = simplexity.simplenicks.saving.Cache.getInstance().getActiveNickname(uuid);
                                if (nick != null) builder.suggest(nick.getNormalizedNickname());
                            });
                            return builder.buildFuture();
                        })
                        .executes(this::execute)
                )
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String raw = StringArgumentType.getString(ctx, "nickname");
        String normalized = NickUtils.normalizeNickname(raw);
        SimpleNicksCore.get().platform().runAsync(() -> {
            List<PlayerInfo> players = NickUtils.getPlayersByNickname(normalized);
            if (players.isEmpty()) {
                sendFeedback(source, LocaleMessage.ERROR_NO_PLAYERS_WITH_THIS_NAME, null);
                return;
            }
            Nickname nickname = new Nickname(raw, normalized);
            sendToSource(source, buildWhoMessage(nickname, players));
        });
        return Command.SINGLE_SUCCESS;
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
            long timeDiffSeconds = player.lastLoginMillis() > 0
                    ? (now - player.lastLoginMillis()) / 1000
                    : 0;
            result = result.append(SimpleNicksCore.get().miniMessage().deserialize(
                    LocaleMessage.WHO_INFO.getMessage(),
                    Placeholder.parsed("name", player.username()),
                    MessageUtils.getTimeFormat(timeDiffSeconds)));
        }
        return result;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        return !ConfigHandler.getInstance().isWhoRequiresPermission()
                || FabricPermissions.check(source, NickPermission.NICK_WHO);
    }
}
