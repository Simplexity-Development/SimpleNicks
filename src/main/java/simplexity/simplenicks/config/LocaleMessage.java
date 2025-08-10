package simplexity.simplenicks.config;

public enum LocaleMessage {
    HELP_MESSAGE("plugin.help-message", "\n========================\n<aqua>· <yellow>Setting a nickname:\n<gray>/nick set <nickname>\n<aqua>· <yellow>removing a nickname:\n<gray>/nick reset\n<aqua>· <yellow>Formatting:\n<gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>"),
    SHOWN_HELP("plugin.shown-help", "<target><reset><yellow> has been shown the help screen"),
    CONFIG_RELOADED("plugin.config-reloaded", "<gold>SimpleNicks config and locale reloaded"),
    SERVER_DISPLAY_NAME("plugin.server-display-name", "<gray>[Server]</gray>"),
    CHANGED_SELF("nick.changed.self", "<green>Changed your nickname to <value>!"),
    CHANGED_OTHER("nick.changed.other", "<green>Changed <target>'s nickname to <value>"),
    CHANGED_BY_OTHER("nick.changed.by-other", "<green><initiator> changed your nickname to <reset><value><green>!"),
    RESET_SELF("nick.reset.self", "<green>Reset your nickname!"),
    RESET_OTHER("nick.reset.other", "<green>Reset <target>'s nickname."),
    RESET_BY_OTHER("nick.reset.reset-by-other", "<gray>Your nickname was reset by <initiator>"),
    SAVE_NICK("nick.save.self", "<green>Success! The nickname <value><reset><green> has been saved for future use"),
    DELETE_NICK("nick.delete.self", "<gray>The nickname <value><reset><gray> has been successfully removed from your saved names"),
    NICK_DELETED_BY_OTHER("nick.delete.by-other", "<gray>The nickname <value><reset><gray> has been deleted from your saved nicknames by <initiator>"),
    NICK_DELETED_OTHER("nick.delete.other", "<gray>You have successfully deleted <value><reset><gray> from <target>'s saved nicknames"),
    NICK_WHO_HEADER("nick.who.header", "Users with the name <yellow><value></yellow>: "),
    NICK_WHO_USER("nick.who.user", "\n- <green><name></green> - Last Seen: <time>"),
    ADMIN_NICK_LOOKUP_HEADER("nick.admin.lookup.header", "<white><username>'s nickname info:"),
    ADMIN_NICK_LOOKUP_CURRENT_NICK("nick.admin.lookup.current-nick", "\n<yellow><bold>Current Nick:</bold></yellow> <name>"),
    ADMIN_NICK_LOOKUP_SAVED_NICKS_HEADER("nick.admin.lookup.saved-nicks", "\n<green>Saved Nicknames:</green><list>"),
    MIGRATION_STARTED("nick.admin.migration.started", "<gray>Save migration has been started, please check console for more details.</gray>"),
    MIGRATION_FINISHED("nick.admin.migration.finished", "<gray>Save migration has finished!</gray>"),
    INSERT_TIME_FORMAT_GROUP("insert.time-format.group", "<day><hour><min><sec>"),
    INSERT_TIME_FORMAT_DAY("insert.time-format.day", " <yellow><count></yellow> day"),
    INSERT_TIME_FORMAT_DAYS("insert.time-format.days", " <yellow><count></yellow> days"),
    INSERT_TIME_FORMAT_HOUR("insert.time-format.hour", " <yellow><count></yellow> hour"),
    INSERT_TIME_FORMAT_HOURS("insert.time-format.hours", " <yellow><count></yellow> hours"),
    INSERT_TIME_FORMAT_MINUTE("insert.time-format.minute", " <yellow><count></yellow> min"),
    INSERT_TIME_FORMAT_MINUTES("insert.time-format.minute", " <yellow><count></yellow> mins"),
    INSERT_TIME_FORMAT_SECOND("insert.time-format.second", " <yellow><count></yellow> sec"),
    INSERT_TIME_FORMAT_SECONDS("insert.time-format.second", " <yellow><count></yellow> secs"),
    INSERT_TIME_FORMAT_NOW("insert.time-format.now", "<yellow>Now</yellow>"),
    INSERT_TIME_FORMAT_AGO("insert.time-format.ago", "<yellow> ago</yellow>"),
    INSERT_NONE("insert.none", "<yellow>None</yellow>"),
    INSERT_SAVED_NICK("insert.saved-nick.format", "\n<white>-</white> <name><reset>"),
    INSERT_NO_SAVED_NICKS("insert.saved-nick.none", "\n<gray>(none)</gray>"),
    ERROR_UNABLE_TO_LOAD_FILE("error.migration.unable-to-load", "<red>Unable to load save file, please check console for more details</red>"),
    ERROR_INVALID_COMMAND("error.invalid.command", "<red>Invalid command."),
    ERROR_INVALID_PLAYER("error.invalid.player", "<red>Invalid player specified"),
    ERROR_INVALID_NICK("error.invalid.nick", "<red>Not a valid nickname, must follow regex: <regex>"),
    ERROR_INVALID_NICK_LENGTH("error.invalid.nick-length", "<red>Nickname is too long, must be <= <value> characters stripped of formatting. Your nickname stripped is: <name>"),
    ERROR_INVALID_NICK_EMPTY("error.invalid.nick-empty", "<red>Nickname after parsing must actually contain characters. The nickname you provided was blank after parsing."),
    ERROR_INVALID_TAGS("error.invalid.tags", "<red>You have used a color or formatting tag you do not have permission to use. Please try again"),
    ERROR_INVALID_CONFIG_REGEX("error.invalid.config-regex", "<red>nickname-regex is null or malformed in file 'config.yml'. Please fix this"),
    ERROR_NOT_ENOUGH_ARGS("error.arguments.not-enough", "<red>No arguments provided."),
    ERROR_TOO_MANY_ARGS("error.arguments.too-many", "<red>Too many arguments provided."),
    ERROR_NICK_IS_NULL("error.nickname.is-null", "<red>Something went wrong and the nickname is null, please check your formatting"),
    ERROR_CANNOT_ACCESS_PLAYERS_PERMISSIONS("error.nickname.cannot-access-permissions", "<red>Unable to process. If you are attempting to use a command with the 'restrictive' permission and the other user is offline, the server is unable to access the user's permissions while the user is offline."),
    ERROR_DELETE_FAILURE("error.nickname.delete-failure", "Failed to delete given nickname, it was likely already deleted."),
    ERROR_RESET_FAILURE("error.nickname.reset-failure", "<red>Failed to reset nickname</red>"),
    ERROR_SET_FAILURE("error.nickname.set-failure", "<gray>Failed to set the given username."),
    ERROR_SAVE_FAILURE("error.nickname.save-failure", "Failed to save current name, you likely do not have a nickname currently."),
    ERROR_TOO_MANY_TO_SAVE("error.nickname.too-many-to-save", "<gray>You have too many saved usernames, please remove some with /nick delete <value>"),
    ERROR_NO_PLAYERS_FOUND_BY_THIS_NAME("error.nickname.no-players-found-by-this-name", "<red>No players were found using this nickname</red>"),
    ERROR_OTHER_PLAYERS_USERNAME("error.nickname.other-players-username", "<red>You cannot name yourself <value><reset><red>, as that is the username of another player on this server. Pick another name"),
    ERROR_OTHER_PLAYERS_NICKNAME("error.nickname.other-players-nickname", "<gray>Sorry! Someone online is already using the nickname <value>! Try another one!"),
    ERROR_NAME_NONEXISTENT("error.nickname.name-nonexistent", "<gray>Cannot delete this name because it does not exist"),
    ERROR_NO_PERMISSION("error.no-permission", "<red>You do not have permission to run this command"),
    ERROR_MUST_BE_PLAYER("error.must-be-player", "<red>This command cannot be run on the Console. You must be a player to run this command"),
    ERROR_MULTIPLE_PLAYERS_BY_THAT_NAME("error.multiple-players-by-that-name", "<red>There are multiple online players by that name, please try using the actual username."),
    ERROR_CANNOT_REACH_DATABASE("error.cannot-reach-database", "<red>There was an issue reaching the database and your command was unable to be completed. Please let a staff member know about this issue if it continues</red>"),
    ERROR_USER_HAS_NO_NICKNAMES("error.user-has-no-nicknames", "<red>This user is not associated with any nicknames</red>");


    private final String path;
    private String message;

    LocaleMessage(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
