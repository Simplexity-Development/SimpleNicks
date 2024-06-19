package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LocaleHandler {
    private static LocaleHandler instance;
    private final String fileName = "locale.yml";
    private final File localeFile = new File(SimpleNicks.getInstance().getDataFolder(), fileName);
    private final FileConfiguration localeConfig = new YamlConfiguration();
    private final Logger logger = SimpleNicks.getInstance().getLogger();
    private String invalidCommand, noArguments, tooManyArguments, cantNickUsername, noPermission, consoleCannotRun,
            invalidPlayer, invalidNickRegex, invalidNickTooLong, invalidTags, prefix, helpBase, helpSet, helpReset,
            helpMinimessage, configReload, nickChangedSelf, nickChangeOther, nickChangedByOther, nickResetSelf,
            nickResetOther, nickResetByOther, noRegex, nickIsNull, nickSaveSuccess, nickSaveFailure, nickSaveFailureTooMany,
            nickDeleteSuccess, nickDeleteFailure;



    private LocaleHandler() {
        if (!localeFile.exists()) {
            SimpleNicks.getInstance().saveResource(fileName, false);
        }
    }

    public static LocaleHandler getInstance() {
        if (instance == null) instance = new LocaleHandler();
        return instance;
    }

    public FileConfiguration getLocaleConfig() {
        return localeConfig;
    }



    public void loadLocale() {
        try {
            localeConfig.load(localeFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("Issue loading locale.yml");
            e.printStackTrace();
        }
        invalidCommand = localeConfig.getString("invalid-command", "<prefix><red>Invalid command.");
        noArguments = localeConfig.getString("no-arguments", "<prefix><red>No arguments provided.");
        tooManyArguments = localeConfig.getString("too-many-arguments", "<prefix><red>Too many arguments provided.");
        cantNickUsername = localeConfig.getString("cant-nick-username", "<prefix><red>You cannot name yourself <name>, as that is the username of another player on this server. Pick another name");
        noPermission = localeConfig.getString("no-permission", "<prefix><red>You do not have permission to run this command");
        consoleCannotRun = localeConfig.getString("console-cannot-run", "<prefix><red>This command cannot be run on the Console.");
        invalidPlayer = localeConfig.getString("invalid-player", "<prefix><red>Invalid player specified");
        invalidNickRegex = localeConfig.getString("invalid-nick-regex", "<prefix><red>Not a valid nickname, must follow regex: <regex>");
        invalidNickTooLong = localeConfig.getString("invalid-nick-too-long", "<prefix><red>Nickname is too long, must be <= <value>");
        invalidTags = localeConfig.getString("invalid-tags", "<prefix><red>You have used a color or formatting tag you do not have permission to use. Please try again");
        prefix = localeConfig.getString("prefix", "<aqua>SimpleNicks <white>» ");
        helpBase = localeConfig.getString("help-base", "<prefix><green>--------");
        helpSet = localeConfig.getString("help-set","<aqua>· <yellow>Setting a nickname: \n   <gray>/nick set <nickname>");
        helpReset = localeConfig.getString("help-reset","<aqua>· <yellow>removing a nickname: \n   <gray>/nick reset");
        helpMinimessage = localeConfig.getString("help-minimessage", "<aqua>· <yellow>Formatting: \n   <gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>");
        configReload = localeConfig.getString("config-reload", "<prefix><gold>SimpleNicks config and locale reloaded");
        nickChangedSelf = localeConfig.getString("nick-changed-self", "<prefix><green>Changed your own nickname to <nickname>!");
        nickChangeOther = localeConfig.getString("nick-change-other", "<prefix><green>Changed <username>'s nickname to <nickname>");
        nickChangedByOther = localeConfig.getString("nick-changed-by-other", "<prefix><green><username> changed your nickname to <reset><nickname><green>!");
        nickResetSelf = localeConfig.getString("nick-reset-self", "<prefix><green>Reset your own nickname!");
        nickResetOther = localeConfig.getString("nick-reset-other", "<prefix><green>Reset <username>'s nickname.");
        nickResetByOther = localeConfig.getString("nick-reset-by-other", "<prefix><gray>Your nickname was reset by <username>");
        noRegex = localeConfig.getString("no-regex", "nickname-regex is null or malformed in file 'config.yml'. Please fix this");
        nickIsNull = localeConfig.getString("nick-is-null", "<prefix><red>Something went wrong and the nickname is null, please check your formatting");
        nickSaveSuccess = localeConfig.getString("nick-save-success", "<prefix><green>Success! The nickname <nickname><reset><green> has been saved for future use");
        nickSaveFailure = localeConfig.getString("nick-save-failure", "<prefix><gray>Failed to save current username.");
        nickSaveFailureTooMany = localeConfig.getString("nick-save-failure-too-many", "<prefix><gray>You have too many saved usernames, please remove some with /nick delete <nickname>");
        nickDeleteSuccess = localeConfig.getString("nick-delete-success", "<prefix><gray>The nickname <nickname><reset><gray> has been successfully removed from your saved names");
        nickDeleteFailure = localeConfig.getString("nick-delete-failure", "<prefix><gray>Failed to delete given username.");
    }

    public String getInvalidCommand() {
        return invalidCommand;
    }

    public String getNoArguments() {
        return noArguments;
    }

    public String getTooManyArguments() {
        return tooManyArguments;
    }

    public String getCantNickUsername() {
        return cantNickUsername;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getConsoleCannotRun() {
        return consoleCannotRun;
    }

    public String getInvalidPlayer() {
        return invalidPlayer;
    }

    public String getInvalidNickRegex() {
        return invalidNickRegex;
    }

    public String getInvalidNickTooLong() {
        return invalidNickTooLong;
    }

    public String getInvalidTags() {
        return invalidTags;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getHelpBase() {
        return helpBase;
    }

    public String getHelpSet() {
        return helpSet;
    }

    public String getHelpReset() {
        return helpReset;
    }

    public String getHelpMinimessage() {
        return helpMinimessage;
    }

    public String getConfigReload() {
        return configReload;
    }

    public String getNickChangedSelf() {
        return nickChangedSelf;
    }

    public String getNickChangeOther() {
        return nickChangeOther;
    }

    public String getNickChangedByOther() {
        return nickChangedByOther;
    }

    public String getNickResetSelf() {
        return nickResetSelf;
    }

    public String getNickResetOther() {
        return nickResetOther;
    }

    public String getNickResetByOther() {
        return nickResetByOther;
    }

    public String getNoRegex() {
        return noRegex;
    }

    public String getNickIsNull() {
        return nickIsNull;
    }

    public String getNickSaveSuccess() {
        return nickSaveSuccess;
    }

    public String getNickSaveFailure() {
        return nickSaveFailure;
    }

    public String getNickSaveFailureTooMany() {
        return nickSaveFailureTooMany;
    }

    public String getNickDeleteSuccess() {
        return nickDeleteSuccess;
    }

    public String getNickDeleteFailure() {
        return nickDeleteFailure;
    }
}

