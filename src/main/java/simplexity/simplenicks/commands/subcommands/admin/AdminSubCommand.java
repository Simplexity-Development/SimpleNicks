package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class AdminSubCommand implements SubCommand {
    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {

        LiteralArgumentBuilder<CommandSourceStack> admin =
                Commands.literal("admin")
                        .requires(this::canExecute);

        new AdminSetSubCommand().subcommandTo(admin);
        new AdminResetSubCommand().subcommandTo(admin);
        new AdminLookupSubCommand().subcommandTo(admin);
        new AdminDeleteSubCommand().subcommandTo(admin);

        parent.then(admin);

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        throw new NotImplementedException("AdminSubCommand::execute was used, this should be impossible.");
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender().hasPermission(Constants.NICK_ADMIN);
    }

}
