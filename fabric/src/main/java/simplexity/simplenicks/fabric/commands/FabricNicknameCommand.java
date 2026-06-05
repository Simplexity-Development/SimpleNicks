package simplexity.simplenicks.fabric.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.fabric.commands.subcommands.FabricDeleteSubCommand;
import simplexity.simplenicks.fabric.commands.subcommands.FabricHelpSubCommand;
import simplexity.simplenicks.fabric.commands.subcommands.FabricReloadSubCommand;
import simplexity.simplenicks.fabric.commands.subcommands.FabricResetSubCommand;
import simplexity.simplenicks.fabric.commands.subcommands.FabricSaveSubCommand;
import simplexity.simplenicks.fabric.commands.subcommands.FabricSetSubCommand;
import simplexity.simplenicks.fabric.commands.subcommands.FabricWhoSubCommand;
import simplexity.simplenicks.fabric.commands.subcommands.admin.FabricAdminSubCommand;
import simplexity.simplenicks.util.NickPermission;

/**
 * Builds the {@code /nick} command tree for the Fabric platform.
 * Registered via {@code CommandRegistrationCallback} in
 * {@link simplexity.simplenicks.fabric.SimpleNicksFabric}.
 */
public class FabricNicknameCommand {

    @NotNull
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("nick")
                .requires(src -> !ConfigHandler.getInstance().isNickRequiresPermission()
                        || FabricPermissions.check(src, NickPermission.NICK_COMMAND));
        new FabricHelpSubCommand().subcommandTo(builder);
        new FabricSetSubCommand().subcommandTo(builder);
        new FabricSaveSubCommand().subcommandTo(builder);
        new FabricResetSubCommand().subcommandTo(builder);
        new FabricDeleteSubCommand().subcommandTo(builder);
        new FabricAdminSubCommand().subcommandTo(builder);
        new FabricReloadSubCommand().subcommandTo(builder);
        new FabricWhoSubCommand().subcommandTo(builder);
        return builder;
    }
}
