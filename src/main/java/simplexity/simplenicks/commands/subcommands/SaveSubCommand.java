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
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class SaveSubCommand implements SubCommand {

    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    private final SimpleCommandExceptionType ERROR_CANNOT_SAVE = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_SAVE_FAILURE.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {

        root.then(Commands.literal("save").requires(this::canExecute)
                .executes(this::execute)
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        OfflinePlayer player = (OfflinePlayer) ctx.getSource().getSender();
        Nickname nickname = NicknameProcessor.getInstance().getCurrentNickname(player);
        if (nickname == null) throw ERROR_CANNOT_SAVE.create();
        NicknameProcessor.getInstance().saveNickname(player, nickname.nickname());
        if (player instanceof Player onlinePlayer) sendFeedback(onlinePlayer, Message.SAVE_NICK, nickname);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_SAVE);
    }
}
