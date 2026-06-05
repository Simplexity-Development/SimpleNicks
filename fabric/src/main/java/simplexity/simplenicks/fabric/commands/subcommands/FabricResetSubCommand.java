package simplexity.simplenicks.fabric.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.util.NickPermission;

public class FabricResetSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("reset").requires(this::canExecute)
                .executes(this::execute)
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;
        SimpleNicksCore.get().platform().runAsync(() -> {
            boolean success = NicknameProcessor.getInstance().resetNickname(player.getUUID());
            if (success) {
                SimpleNicksCore.get().platform().runSync(() -> {
                    NickUtils.refreshDisplayName(player.getUUID());
                    sendToPlayer(player, LocaleMessage.RESET_SELF, null);
                });
            } else {
                SimpleNicksCore.get().platform().runSync(() ->
                        sendToPlayer(player, LocaleMessage.ERROR_RESET_FAILURE, null));
            }
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        if (source.getPlayer() == null) return false;
        return permissionNotRequired()
                || FabricPermissions.check(source, NickPermission.NICK_SET);
    }
}
