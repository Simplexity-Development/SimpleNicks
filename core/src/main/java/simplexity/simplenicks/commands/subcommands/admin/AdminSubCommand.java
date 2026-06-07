package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.util.NickPermission;

public class AdminSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public AdminSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        LiteralArgumentBuilder<S> admin = adapter.literal("admin").requires(this::canExecute);
        new AdminSetSubCommand<>(adapter).subcommandTo(admin);
        new AdminResetSubCommand<>(adapter).subcommandTo(admin);
        new AdminLookupSubCommand<>(adapter).subcommandTo(admin);
        new AdminDeleteSubCommand<>(adapter).subcommandTo(admin);
        parent.then(admin);
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) {
        throw new UnsupportedOperationException("AdminSubCommand::execute was used, this should be impossible.");
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.hasPermission(source, NickPermission.NICK_ADMIN);
    }
}
