package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class SaveSubCommand {

    public static void subcommandTo(LiteralArgumentBuilder<CommandSourceStack> root) {

        root.then(Commands.literal("save").requires(SaveSubCommand::canExecute)
                .executes(SaveSubCommand::execute)
        );

    }

    public static int execute(CommandContext<CommandSourceStack> ctx) {
        OfflinePlayer player = (OfflinePlayer) ctx.getSource().getSender();
        Nickname nickname = NicknameProcessor.getInstance().getCurrentNickname(player);
        NicknameProcessor.getInstance().saveNickname(player, nickname.nickname());
        return Command.SINGLE_SUCCESS;
    }

    public static boolean canExecute(CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_SAVE);
    }
}
