package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.saving.SaveMigrator;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class AdminMigrateCommand implements SubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("migrate")
                .requires(this::canExecute)
                .executes(this::execute));
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ctx.getSource().getSender().sendRichMessage(LocaleMessage.MIGRATION_STARTED.getMessage());
        SaveMigrator.migrateFromYml();
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender().hasPermission(Constants.NICK_MIGRATE);
    }
}
