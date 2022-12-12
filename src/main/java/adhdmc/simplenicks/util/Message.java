package adhdmc.simplenicks.util;

public enum Message {
    //errors
    INVALID_COMMAND("<prefix><red>Invalid Command."),
    NO_ARGUMENTS("<prefix><red>No arguments provided."),
    TOO_MANY_ARGUMENTS("<prefix><red>Too many arguments provided."),
    CANNOT_NICK_USERNAME("<prefix><red>You cannot name yourself <name>, as that is the username of another player on this server. Pick another name"),
    NO_PERMISSION("<prefix><red>You do not have permission to run this command"),
    CONSOLE_CANNOT_RUN("<prefix><red>This command cannot be run on the Console."),
    INVALID_PLAYER("<prefix><red>Invalid player specified"),
    INVALID_NICK_REGEX("<prefix><red>Not a valid nickname, must follow regex: <regex>"),
    INVALID_NICK_TOO_LONG("<prefix><red>Nickname is too long, must be <= <value>"),
    INVALID_TAGS("<prefix><red>You have used a color or formatting tag you do not have permission to use. Please try again"),
    BAD_REGEX("'nickname-regex' is null or malformed in file 'config.yml'. Please fix this"),
    NICK_NULL("<prefix><red>Something went wrong and the nickname is null, please check your formatting"),
    //plugin messages
    PREFIX("<aqua>SimpleNicks <white>» "),
    NICK_CHANGED_SELF("<prefix><green>Changed your own nickname to <nickname>!"),
    NICK_CHANGE_OTHER("<prefix><green>Changed <username>'s nickname to <nickname>"),
    NICK_CHANGED_BY_OTHER("<prefix><green><username> changed your nickname to <reset><nickname><green>!"),
    NICK_RESET_SELF("<prefix><green>Reset your own nickname!"),
    NICK_RESET_OTHER("<prefix><green>Reset <username>'s nickname."),
    NICK_RESET_BY_OTHER("<prefix><gray>Your nickname was reset by <username>"),
    //other
    VERSION("<prefix>yes"),
    HELP_BASE("<prefix><green>--------"),
    HELP_SET("<aqua>· <yellow>Setting a nickname: \n   <gray>/nick set <nickname>"),
    HELP_RESET("<aqua>· <yellow>removing a nickname: \n   <gray>/nick reset"),
    HELP_MINIMESSAGE("<aqua>· <yellow>Formatting: \n   <gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>"),
    CONFIG_RELOADED("<prefix><gold>SimpleNicks config and locale reloaded");

    String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
