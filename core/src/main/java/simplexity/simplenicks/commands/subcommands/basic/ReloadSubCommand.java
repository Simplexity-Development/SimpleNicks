package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.NickPermission;

public class ReloadSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public ReloadSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("reload").requires(this::canExecute)
                .executes(this::execute));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        ConfigHandler.getInstance().reloadConfig();
        SqlHandler.getInstance().closeDatabase();
        SqlHandler.getInstance().init();
        SimpleNicksCore.get().platform().getOnlinePlayers()
                .forEach(NickUtils::refreshDisplayName);
        sendFeedback(adapter.senderContext(ctx.getSource()), LocaleMessage.CONFIG_RELOADED, null);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.hasPermission(source, NickPermission.NICK_RELOAD);
    }
}
