package simplexity.simplenicks.fabric.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

public class FabricDeleteSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("delete").requires(this::canExecute)
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            ServerPlayer player = ctx.getSource().getPlayer();
                            if (player == null) return builder.buildFuture();
                            NicknameProcessor.getInstance()
                                    .getSavedNicknames(player.getUUID(), true)
                                    .forEach(n -> builder.suggest(n.getNickname()));
                            return builder.buildFuture();
                        })
                        .executes(this::execute)
                )
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;
        String raw = StringArgumentType.getString(ctx, "nickname");
        Nickname nickname = new Nickname(raw, NickUtils.normalizeNickname(raw));
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance()
                    .deleteNickname(player.getUUID(), nickname.getNickname());
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    NickUtils.refreshDisplayName(player.getUUID());
                    sendToPlayer(player, LocaleMessage.DELETE_SELF, nickname);
                });
            } else {
                SimpleNicksCore.get().platform().runSync(() ->
                        sendToPlayer(player, LocaleMessage.ERROR_DELETE_FAILURE, nickname));
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        if (source.getPlayer() == null) return false;
        return permissionNotRequired()
                || FabricPermissions.check(source, NickPermission.NICK_SAVE);
    }
}
