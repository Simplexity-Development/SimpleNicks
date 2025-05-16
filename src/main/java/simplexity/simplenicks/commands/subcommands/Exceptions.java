package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.Message;

@SuppressWarnings("UnstableApiUsage")
public class Exceptions {

    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    public static final SimpleCommandExceptionType ERROR_CANNOT_DELETE = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_DELETE_FAILURE.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final SimpleCommandExceptionType ERROR_NICK_IS_NULL = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                   miniMessage.deserialize(
                           Message.ERROR_NICK_IS_NULL.getMessage(),
                           Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                   )
            )
    );

    public static final SimpleCommandExceptionType ERROR_EMPTY_NICK_AFTER_PARSE = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_INVALID_NICK_EMPTY.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final SimpleCommandExceptionType ERROR_CANNOT_SAVE = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_SAVE_FAILURE.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    @SuppressWarnings("unused")
    public static final SimpleCommandExceptionType ERROR_INVALID_COMMAND = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_INVALID_COMMAND.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final SimpleCommandExceptionType ERROR_CANNOT_ACCESS_PLAYERS_PERMISSIONS = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_CANNOT_ACCESS_PLAYERS_PERMISSIONS.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final SimpleCommandExceptionType ERROR_SET_FAILURE = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_SET_FAILURE.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final DynamicCommandExceptionType ERROR_LENGTH = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_INVALID_NICK_LENGTH.getMessage(),
                            Placeholder.unparsed("value", String.valueOf(ConfigHandler.getInstance().getMaxLength())),
                            Placeholder.unparsed("name", nickname.toString()),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final DynamicCommandExceptionType ERROR_REGEX = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_INVALID_NICK.getMessage(),
                            Placeholder.unparsed("regex", ConfigHandler.getInstance().getRegexString()),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final DynamicCommandExceptionType ERROR_PLAYER_NOT_FOUND = new DynamicCommandExceptionType(
            playerName -> MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_INVALID_PLAYER.getMessage(),
                            Placeholder.unparsed("player_name", playerName.toString()),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage())
                    )
            )
    );

    public static final DynamicCommandExceptionType ERROR_NICKNAME_IS_SOMEONES_USERNAME = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_OTHER_PLAYERS_USERNAME.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage()),
                            Placeholder.unparsed("value", nickname.toString())
                    )
            )
    );


    public static final DynamicCommandExceptionType ERROR_SOMEONE_USING_THAT_NICKNAME = new DynamicCommandExceptionType(
            nickname -> MessageComponentSerializer.message().serialize(
                    miniMessage.deserialize(
                            Message.ERROR_OTHER_PLAYERS_NICKNAME.getMessage(),
                            Placeholder.parsed("prefix", Message.PLUGIN_PREFIX.getMessage()),
                            Placeholder.unparsed("value", nickname.toString())
                    )
            )
    );

}
