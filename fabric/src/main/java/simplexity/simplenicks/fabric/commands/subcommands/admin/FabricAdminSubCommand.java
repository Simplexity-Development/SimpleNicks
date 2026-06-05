package simplexity.simplenicks.fabric.commands.subcommands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.fabric.commands.subcommands.FabricSubCommand;
import simplexity.simplenicks.util.NickPermission;

public class FabricAdminSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        LiteralArgumentBuilder<CommandSourceStack> admin =
                Commands.literal("admin").requires(this::canExecute);
        new FabricAdminSetSubCommand().subcommandTo(admin);
        new FabricAdminResetSubCommand().subcommandTo(admin);
        new FabricAdminLookupSubCommand().subcommandTo(admin);
        new FabricAdminDeleteSubCommand().subcommandTo(admin);
        parent.then(admin);
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        throw new UnsupportedOperationException("FabricAdminSubCommand::execute should never be called directly.");
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        return FabricPermissions.check(source, NickPermission.NICK_ADMIN);
    }
}
