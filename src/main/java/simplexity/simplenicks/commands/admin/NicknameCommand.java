package simplexity.simplenicks.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import simplexity.simplenicks.commands.subcommands.SaveSubCommand;
import simplexity.simplenicks.commands.subcommands.SetSubCommand;

@SuppressWarnings("UnstableApiUsage")
public class NicknameCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("nick");

        SetSubCommand.subcommandTo(builder);
        SaveSubCommand.subcommandTo(builder);

        return builder;
    }



}
