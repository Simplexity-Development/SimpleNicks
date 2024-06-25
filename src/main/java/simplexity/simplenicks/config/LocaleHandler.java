package simplexity.simplenicks.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import simplexity.simplenicks.SimpleNicks;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LocaleHandler {
    private static LocaleHandler instance;
    private final String fileName = "locale.yml";
    private final File localeFile = new File(SimpleNicks.getInstance().getDataFolder(), fileName);
    private final FileConfiguration localeConfig = new YamlConfiguration();
    private final Logger logger = SimpleNicks.getInstance().getLogger();
    //Plugin
    private String pluginPrefix, helpMessage, configReloaded;
    //Errors
    private String invalidCommand, invalidPlayer, invalidNick, invalidNickLength, invalidTags, invalidConfigRegex,
            tooManyArgs, notEnoughArgs, nickIsNull, deleteFailure, saveFailure, tooManyToSave, otherPlayersUsername,
            noPermission, mustBePlayer;
    //Nick
    private String changedSelf, changedOther, changedByOther, resetSelf, resetOther, resetByOther, saveNick, deleteNick;


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
        pluginPrefix = localeConfig.getString("plugin.prefix", "<aqua>SimpleNicks <white>» ");
        helpMessage = localeConfig.getString("plugin.help-message",
                "    <prefix>========================\n" +
                        "    <aqua>· <yellow>Setting a nickname: \n" +
                        "       <gray>/nick set <nickname>\n" +
                        "    <aqua>· <yellow>removing a nickname: \n" +
                        "       <gray>/nick reset\"\n" +
                        "    <aqua>· <yellow>Formatting: \n" +
                        "        <gray>This plugin uses minimessage formatting. You can find a format viewer <aqua><u><click:open_url:'https://webui.adventure.kyori.net/'>here</click></u></aqua>\"\n" +
                        "  ");
        configReloaded = localeConfig.getString("plugin.config-reloaded", "<prefix><gold>SimpleNicks config and locale reloaded");
        invalidCommand = localeConfig.getString("error.invalid.command", "<prefix><red>Invalid command.");
        invalidPlayer = localeConfig.getString("error.invalid.player", "<prefix><red>Invalid player specified");
        invalidNick = localeConfig.getString("error.invalid.nick", "<prefix><red>Not a valid nickname, must follow regex: <regex>");
        invalidNickLength = localeConfig.getString("error.invalid.nick-length", "<prefix><red>Nickname is too long, must be <= <value>");
        invalidTags = localeConfig.getString("error.invalid.tags", "<prefix><red>You have used a color or formatting tag you do not have permission to use. Please try again");
        invalidConfigRegex = localeConfig.getString("error.invalid.config-regex", "nickname-regex is null or malformed in file 'config.yml'. Please fix this");
        notEnoughArgs = localeConfig.getString("error.arguments.not-enough", "<prefix><red>No arguments provided.");
        tooManyArgs = localeConfig.getString("error.arguments.too-many", "<prefix><red>Too many arguments provided.");
        nickIsNull = localeConfig.getString("error.nickname.is-null", "<prefix><red>Something went wrong and the nickname is null, please check your formatting");
        deleteFailure = localeConfig.getString("error.nickname.delete-failure", "<prefix><gray>Failed to delete given username.");
        saveFailure = localeConfig.getString("error.nickname.save-failure", "<prefix><gray>Failed to save current username.");
        tooManyToSave = localeConfig.getString("error.nickname.too-many-to-save", "<prefix><gray>You have too many saved usernames, please remove some with /nick delete <value>");
        otherPlayersUsername = localeConfig.getString("error.nickname.other-players-username", "<prefix><red>You cannot name yourself <value>, as that is the username of another player on this server. Pick another name");
        noPermission = localeConfig.getString("error.no-permission", "<prefix><red>You do not have permission to run this command");
        mustBePlayer = localeConfig.getString("error.must-be-player", "<prefix><red>This command cannot be run on the Console. You must be a player to run this command");
        changedSelf = localeConfig.getString("nick.changed.self", "<prefix><green>Changed your nickname to <value>!");
        changedOther = localeConfig.getString("nick.changed.other", "<prefix><green>Changed <target>'s nickname to <value>");
        changedByOther = localeConfig.getString("nick.changed.by-other", "<prefix><green><initiator> changed your nickname to <reset><value><green>!");
        resetSelf = localeConfig.getString("nick.reset.self", "<prefix><green>Reset your nickname!");
        resetOther = localeConfig.getString("nick.reset.other", "<prefix><green>Reset <target>'s nickname.");
        resetByOther = localeConfig.getString("nick.reset.reset-by-other", "<prefix><gray>Your nickname was reset by <initiator>");
        saveNick = localeConfig.getString("nick.save", "<prefix><green>Success! The nickname <value><reset><green> has been saved for future use");
        deleteNick = localeConfig.getString("nick.delete", "<prefix><gray>The nickname <value><reset><gray> has been successfully removed from your saved names");
    }


    public String getPluginPrefix() {
        return pluginPrefix;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public String getConfigReloaded() {
        return configReloaded;
    }

    public String getInvalidCommand() {
        return invalidCommand;
    }

    public String getInvalidPlayer() {
        return invalidPlayer;
    }

    public String getInvalidNick() {
        return invalidNick;
    }

    public String getInvalidNickLength() {
        return invalidNickLength;
    }

    public String getInvalidTags() {
        return invalidTags;
    }

    public String getInvalidConfigRegex() {
        return invalidConfigRegex;
    }

    public String getTooManyArgs() {
        return tooManyArgs;
    }

    public String getNotEnoughArgs() {
        return notEnoughArgs;
    }

    public String getNickIsNull() {
        return nickIsNull;
    }

    public String getDeleteFailure() {
        return deleteFailure;
    }

    public String getSaveFailure() {
        return saveFailure;
    }

    public String getTooManyToSave() {
        return tooManyToSave;
    }

    public String getOtherPlayersUsername() {
        return otherPlayersUsername;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getMustBePlayer() {
        return mustBePlayer;
    }

    public String getChangedSelf() {
        return changedSelf;
    }

    public String getChangedOther() {
        return changedOther;
    }

    public String getChangedByOther() {
        return changedByOther;
    }

    public String getResetSelf() {
        return resetSelf;
    }

    public String getResetOther() {
        return resetOther;
    }

    public String getResetByOther() {
        return resetByOther;
    }

    public String getSaveNick() {
        return saveNick;
    }

    public String getDeleteNick() {
        return deleteNick;
    }
}

