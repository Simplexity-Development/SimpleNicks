package simplexity.simplenicks.config;

public enum Message {
    PLUGIN_PREFIX("plugin.prefix", "<aqua>SimpleNicks <white>» "),
    HELP_MESSAGE("plugin.help-message", "\n<prefix>========================\n<aqua>· <yellow>Setting a nickname:\n<gray>/nick set <nickname>\n<aqua>· <yellow>removing a nickname:\n<gray>/nick reset\n<aqua>· <yellow>Formatting:\n<gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>"),
    SHOWN_HELP("plugin.shown-help", "<prefix><target><reset><yellow> has been shown the help screen"),
    CONFIG_RELOADED("plugin.config-reloaded", "<prefix><gold>SimpleNicks config and locale reloaded"),
    INVALID_COMMAND("error.invalid.command", "<prefix><red>Invalid command."),
    INVALID_PLAYER("error.invalid.player", "<prefix><red>Invalid player specified"),
    INVALID_NICK("error.invalid.nick", "<prefix><red>Not a valid nickname, must follow regex: <regex>"),
    INVALID_NICK_LENGTH("error.invalid.nick-length", "<prefix><red>Nickname is too long, must be <= <value>"),
    INVALID_TAGS("error.invalid.tags", "<prefix><red>You have used a color or formatting tag you do not have permission to use. Please try again"),
    INVALID_CONFIG_REGEX("error.invalid.config-regex", "nickname-regex is null or malformed in file 'config.yml'. Please fix this"),
    NOT_ENOUGH_ARGS("error.arguments.not-enough", "<prefix><red>No arguments provided."),
    TOO_MANY_ARGS("error.arguments.too-many", "<prefix><red>Too many arguments provided."),
    NICK_IS_NULL("error.nickname.is-null", "<prefix><red>Something went wrong and the nickname is null, please check your formatting"),
    DELETE_FAILURE("error.nickname.delete-failure", "<prefix><gray>Failed to delete given username."),
    SAVE_FAILURE("error.nickname.save-failure", "<prefix><gray>Failed to save current username."),
    TOO_MANY_TO_SAVE("error.nickname.too-many-to-save", "<prefix><gray>You have too many saved usernames, please remove some with /nick delete <value>"),
    OTHER_PLAYERS_USERNAME("error.nickname.other-players-username", "<prefix><red>You cannot name yourself <value><reset><red>, as that is the username of another player on this server. Pick another name"),
    NAME_NONEXISTENT("error.nickname.name-nonexistent", "<prefix><gray>Cannot delete this name because it does not exist"),
    NO_PERMISSION("error.no-permission", "<prefix><red>You do not have permission to run this command"),
    MUST_BE_PLAYER("error.must-be-player", "<prefix><red>This command cannot be run on the Console. You must be a player to run this command"),
    MULTIPLE_PLAYERS_BY_THAT_NAME("error.multiple-players-by-that-name", "<prefix><red>There are multiple online players by that name, please try using the actual username."),
    CHANGED_SELF("nick.changed.self", "<prefix><green>Changed your nickname to <value>!"),
    CHANGED_OTHER("nick.changed.other", "<prefix><green>Changed <target>'s nickname to <value>"),
    CHANGED_BY_OTHER("nick.changed.by-other", "<prefix><green><initiator> changed your nickname to <reset><value><green>!"),
    RESET_SELF("nick.reset.self", "<prefix><green>Reset your nickname!"),
    RESET_OTHER("nick.reset.other", "<prefix><green>Reset <target>'s nickname."),
    RESET_BY_OTHER("nick.reset.reset-by-other", "<prefix><gray>Your nickname was reset by <initiator>"),
    SAVE_NICK("nick.save", "<prefix><green>Success! The nickname <value><reset><green> has been saved for future use"),
    DELETE_NICK("nick.delete", "<prefix><gray>The nickname <value><reset><gray> has been successfully removed from your saved names");


    private final String path;
    private String message;

    Message(String path, String message) {
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
