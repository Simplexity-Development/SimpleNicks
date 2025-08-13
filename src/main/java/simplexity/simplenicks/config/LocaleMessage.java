package simplexity.simplenicks.config;

public enum LocaleMessage {
    HELP_MESSAGE("plugin.help-message",
            """
                    Nickname Help
                    ========================
                    <aqua>· <yellow>Setting a nickname:
                    <gray>/nick set <nickname>
                    <aqua>· <yellow>removing a nickname:
                    <gray>/nick reset
                    <aqua>· <yellow>Formatting:
                    <gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>"""),
    SHOWN_HELP("plugin.user-shown-help", "<target><reset><yellow> has been shown the help screen"),
    CONFIG_RELOADED("plugin.config-reloaded", "<gold>SimpleNicks config and locale reloaded"),
    SERVER_DISPLAY_NAME("plugin.server-display-name", "<gray>[Server]</gray>"),

    // Basic Functionality
    SET_SELF("nick.set.self", "<green>Changed your nickname to <value><green>!"),
    SET_TARGET("nick.set.target", "<green>Changed <target>'s nickname to <value>"),
    SET_BY_INITIATOR("nick.set.by-initiator", "<green><initiator> changed your nickname to <reset><value><green>!"),
    RESET_SELF("nick.reset.self", "<green>Reset your nickname!"),
    RESET_TARGET("nick.reset.target", "<green>Reset <target>'s nickname."),
    RESET_BY_INITIATOR("nick.reset.by-initiator", "<gray>Your nickname was reset by <initiator>"),
    SAVE_NICK("nick.save.self", "<green>Success! The nickname <value><reset><green> has been saved for future use"),
    DELETE_SELF("nick.delete.self", "<gray>The nickname <value><reset><gray> has been successfully removed from your saved names"),
    DELETED_TARGET("nick.delete.target", "<gray>You have successfully deleted <value><reset><gray> from <target>'s saved nicknames"),
    DELETED_BY_INITIATOR("nick.delete.by-initiator", "<gray>The nickname <value><reset><gray> has been deleted from your saved nicknames by <initiator>"),
    WHO_HEADER("nick.who.header", "Users with the name <yellow><value></yellow>: "),
    WHO_INFO("nick.who.info", "\n- <green><name></green> - Last Seen: <time>"),

    // Admin Messages
    LOOKUP_HEADER("nick.admin.lookup.header", "<white><username>'s nickname info:"),
    LOOKUP_CURRENT("nick.admin.lookup.current", "\n<yellow><bold>Current Nick:</bold></yellow> <name>"),
    LOOKUP_SAVED("nick.admin.lookup.saved.header", "\n<green>Saved Nicknames:</green><list>"),
    LOOKUP_SAVED_NICK("nick.admin.lookup.saved.nick-format", "\n<white>-</white> <name><reset>"),
    LOOKUP_NO_SAVED_NICKS("nick.admin.lookup.saved.none", "\n<gray>(none)</gray>"),

    // Formats
    TIME_FORMAT_DAY("time-format.day", " <yellow><count></yellow> day"),
    TIME_FORMAT_DAYS("time-format.days", " <yellow><count></yellow> days"),
    TIME_FORMAT_HOUR("time-format.hour", " <yellow><count></yellow> hour"),
    TIME_FORMAT_HOURS("time-format.hours", " <yellow><count></yellow> hours"),
    TIME_FORMAT_MINUTE("time-format.minute", " <yellow><count></yellow> min"),
    TIME_FORMAT_MINUTES("time-format.minutes", " <yellow><count></yellow> mins"),
    TIME_FORMAT_SECOND("time-format.second", " <yellow><count></yellow> sec"),
    TIME_FORMAT_SECONDS("time-format.seconds", " <yellow><count></yellow> secs"),
    TIME_FORMAT_NOW("time-format.now", "<yellow>Now</yellow>"),
    TIME_FORMAT_AGO("time-format.ago", "<yellow> ago</yellow>"),
    INSERT_NONE("insert.none", "<yellow>None</yellow>"),

    // Nickname checks
    ERROR_INVALID_NICK("error.invalid.regex-fail", "<red>Not a valid nickname, must follow regex: <regex>"),
    ERROR_INVALID_NICK_LENGTH("error.invalid.too-long", "<red>Nickname is too long, must be <= <value> characters stripped of formatting. Your nickname stripped is: <name>"),
    ERROR_INVALID_NICK_EMPTY("error.invalid.nick-empty", "<red>Nickname after parsing must actually contain characters. The nickname you provided was blank after parsing."),
    ERROR_INVALID_TAGS("error.invalid.tags", "<red>You have used a color or formatting tag you do not have permission to use. Please try again"),
    ERROR_INVALID_OTHER_PLAYERS_USERNAME("error.invalid.other-players-username", "<red>You cannot name yourself <value><reset><red>, as that is the username of another player on this server. Pick another name"),
    ERROR_INVALID_OTHER_PLAYERS_NICKNAME("error.invalid.other-players-nickname", "<gray>Sorry! Someone online is already using the nickname <value>! Try another one!"),
    ERROR_NICK_IS_NULL("error.nickname.set.nick-is-null", "<red>Something went wrong and the nickname is null, please check your formatting"),

    // Player issues
    ERROR_NO_PLAYERS_WITH_THIS_NAME("error.player.none-found", "<red>No players were found using this nickname</red>"),
    ERROR_PLAYER_HAS_NO_NICKNAMES("error.player.no-nicknames", "<red>This user is not associated with any nicknames</red>"),
    ERROR_INVALID_PLAYER("error.player.invalid", "<red>Invalid player specified"),

    // Database Issues
    ERROR_CANNOT_REACH_DATABASE("error.cannot-reach-database", "<red>There was an issue reaching the database and your command was unable to be completed. Please let a staff member know about this issue if it continues</red>"),

    // Config Issues
    ERROR_INVALID_CONFIG_REGEX("error.config.invalid-regex", "<red>nickname-regex is null or malformed in file 'config.yml'. Please fix this"),

    // Basic Command Errors
    ERROR_DELETE_FAILURE("error.nickname.delete.failure", "Failed to delete given nickname, it was likely already deleted."),
    ERROR_RESET_FAILURE("error.nickname.reset.failure", "<red>Failed to reset nickname</red>"),
    ERROR_SET_FAILURE("error.nickname.set.failure", "<gray>Failed to set the given username."),
    ERROR_SAVE_FAILURE("error.nickname.save.failure", "<gray>Failed to save current name, you likely do not have a nickname currently."),
    ERROR_ALREADY_SAVED("error.nickname.save.already-saved", "<gray>You have already saved this nickname!</gray>"),
    ERROR_TOO_MANY_TO_SAVE("error.nickname.save.too-many", "<gray>You have too many saved usernames, please remove some with /nick delete <value>");



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
