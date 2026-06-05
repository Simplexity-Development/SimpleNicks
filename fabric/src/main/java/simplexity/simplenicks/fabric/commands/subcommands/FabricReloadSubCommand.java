package simplexity.simplenicks.fabric.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.NickPermission;

public class FabricReloadSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("reload").requires(this::canExecute)
                .executes(this::execute)
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        ConfigHandler.getInstance().reloadConfig();
        SqlHandler.getInstance().closeDatabase();
        SqlHandler.getInstance().init();
        SimpleNicksCore.get().platform().getOnlinePlayers()
                .forEach(NickUtils::refreshDisplayName);
        sendFeedback(ctx.getSource(), LocaleMessage.CONFIG_RELOADED, null);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        return FabricPermissions.check(source, NickPermission.NICK_RELOAD);
    }
}
