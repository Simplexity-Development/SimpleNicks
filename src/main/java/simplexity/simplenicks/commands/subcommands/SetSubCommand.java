package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.Message;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.Constants;

@SuppressWarnings("UnstableApiUsage")
public class SetSubCommand implements SubCommand{
    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    private final DynamicCommandExceptionType ERROR_LENGTH = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_INVALID_NICK_LENGTH.getMessage(),
                            Placeholder.unparsed("value", String.valueOf(ConfigHandler.getInstance().getMaxLength())),
                            Placeholder.unparsed("name", nickname.toString()),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    private final DynamicCommandExceptionType ERROR_REGEX = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_INVALID_NICK.getMessage(),
                            Placeholder.unparsed("regex", ConfigHandler.getInstance().getRegexString()),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );



    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {

        NicknameArgument argument = new NicknameArgument();

        root.then(Commands.literal("set").requires(this::canExecute)
                .then(Commands.argument("nickname", argument)
                        .suggests(argument::suggestOwnNicknames)
                        .executes(this::execute)
                )
        );

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Nickname nickname = ctx.getArgument("nickname", Nickname.class);
        Player player = (Player) ctx.getSource().getSender();
        if (!player.hasPermission(Constants.NICK_LENGTH_BYPASS) && nickname.normalizedNickname().length() > ConfigHandler.getInstance().getMaxLength()) throw ERROR_LENGTH.create(nickname.normalizedNickname());
        if (!player.hasPermission(Constants.NICK_REGEX_BYPASS) && !ConfigHandler.getInstance().getRegex().matcher(nickname.normalizedNickname()).matches()) throw ERROR_REGEX.create(nickname.normalizedNickname());
        NicknameProcessor.getInstance().setNickname((OfflinePlayer) ctx.getSource().getSender(), nickname.nickname());
        sendFeedback(player, Message.CHANGED_SELF, nickname);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return css.getSender() instanceof Player player && player.hasPermission(Constants.NICK_SET);
    }

}
