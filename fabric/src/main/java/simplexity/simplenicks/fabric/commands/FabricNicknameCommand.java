package simplexity.simplenicks.fabric.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.commands.subcommands.admin.AdminSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.DeleteSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.HelpSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.ReloadSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.ResetSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.SaveSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.SetSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.WhoSubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import simplexity.simplenicks.util.NickPermission;

/**
 * Builds the {@code /nick} command tree for the Fabric platform.
 * Registered via {@code CommandRegistrationCallback} in
 * {@link simplexity.simplenicks.fabric.SimpleNicksFabric}.
 */
public class FabricNicknameCommand {

    @NotNull
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        FabricBrigadierAdapter adapter = new FabricBrigadierAdapter();
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("nick")
                .requires(src -> !ConfigHandler.getInstance().isNickRequiresPermission()
                        || FabricPermissions.check(src, NickPermission.NICK_COMMAND));
        new HelpSubCommand<>(adapter).subcommandTo(builder);
        new SetSubCommand<>(adapter).subcommandTo(builder);
        new SaveSubCommand<>(adapter).subcommandTo(builder);
        new ResetSubCommand<>(adapter).subcommandTo(builder);
        new DeleteSubCommand<>(adapter).subcommandTo(builder);
        new AdminSubCommand<>(adapter).subcommandTo(builder);
        new ReloadSubCommand<>(adapter).subcommandTo(builder);
        new WhoSubCommand<>(adapter).subcommandTo(builder);
        return builder;
    }
}
