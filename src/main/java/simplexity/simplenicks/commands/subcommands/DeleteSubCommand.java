package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.NicknameArgument;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class DeleteSubCommand implements SubCommand {

    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    private final SimpleCommandExceptionType ERROR_CANNOT_DELETE = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_DELETE_FAILURE.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {

        NicknameArgument argument = new NicknameArgument();

        root.then(Commands.literal("delete").requires(this::canExecute)
                .then(Commands.argument("nickname", argument)
                        .suggests(argument::suggestOwnNicknames)
                        .executes(this::execute))
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        OfflinePlayer player = (OfflinePlayer) ctx.getSource().getSender();
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        boolean deleted = NicknameProcessor.getInstance().deleteNickname(player, nickname.nickname());
        if (deleted) {
            if (player instanceof Player onlinePlayer) sendFeedback(onlinePlayer, Message.DELETE_NICK, nickname);
            return Command.SINGLE_SUCCESS;
        }
        throw ERROR_CANNOT_DELETE.create();
    }



    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_DELETE);
    }
}
