package simplexity.simplenicks.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import simplexity.simplenicks.commands.subcommands.basic.DeleteSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.ResetSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.SaveSubCommand;
import simplexity.simplenicks.commands.subcommands.basic.SetSubCommand;

@SuppressWarnings("UnstableApiUsage")
public class NicknameCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("nick");
        new SetSubCommand().subcommandTo(builder);
        new SaveSubCommand().subcommandTo(builder);
        new ResetSubCommand().subcommandTo(builder);
        new DeleteSubCommand().subcommandTo(builder);
        return builder;
    }



}
