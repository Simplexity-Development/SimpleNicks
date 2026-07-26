package simplexity.simplenicks.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
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
import simplexity.simplenicks.platform.PaperBrigadierAdapter;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public class PaperNicknameCommand {

    @NotNull
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        PaperBrigadierAdapter adapter = new PaperBrigadierAdapter();
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("nick")
                .requires(src -> !ConfigHandler.getInstance().isNickRequiresPermission()
                        || src.getSender().hasPermission(NickPermission.NICK_COMMAND.getPermissionKey()));
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
