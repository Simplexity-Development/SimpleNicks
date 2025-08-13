package simplexity.simplenicks.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import simplexity.simplenicks.commands.subcommands.admin.AdminSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.DeleteSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.WhoSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.ReloadSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.ResetSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.SaveSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.SetSubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public class NicknameCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("nick")
                .requires(src -> !ConfigHandler.getInstance().isNickRequiresPermission() || src.getSender().hasPermission(NickPermission.NICK_COMMAND.getPermission()));
        new SetSubCommand().subcommandTo(builder);
        new SaveSubCommand().subcommandTo(builder);
        new ResetSubCommand().subcommandTo(builder);
        new DeleteSubCommand().subcommandTo(builder);
        new AdminSubCommand().subcommandTo(builder);
        new ReloadSubCommand().subcommandTo(builder);
        new WhoSubCommand().subcommandTo(builder);
        return builder;
    }



}
