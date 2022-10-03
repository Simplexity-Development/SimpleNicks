package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.subcommands.Set;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class ConfigValidator {
    public enum Message {
        //errors
        INVALID_COMMAND("<red>Invalid Command."),
        NO_ARGUMENTS("<red>No arguments provided."),
        TOO_MANY_ARGUMENTS("<red>Too many arguments provided."),
        NO_PERMISSION("<red>No Permission."),
        CONSOLE_CANNOT_RUN("<red>Cannot be run on the Console."),
        INVALID_PLAYER("<red>Not a valid player."),
        INVALID_NICK_REGEX("<red>Not a valid nickname, must follow regex: " + Set.NICKNAME_REGEX),
        INVALID_NICK_TOO_LONG("<red>Nickname is too long, must be <=" + Set.MAX_NICKNAME_LENGTH),
        //plugin messages
        PREFIX("<aqua>SimpleNicks <white>» "),
        NICK_CHANGED_SELF("<green>Changed your own nickname!"),
        NICK_CHANGED_OTHER("<green>Changed user's nickname."),
        NICK_RESET_SELF("<green>Reset your own nickname!"),
        NICK_RESET_OTHER("<green>Reset user's nickname."),
        //other
        VERSION("yes");

        String message;
        Message(String message) {
            this.message = message;
        }

        public String getMessage() { return this.message; }
        private void setMessage(String message) { this.message = message; }
    }

    public static void loadLocaleMessages(){
        FileConfiguration locale = SimpleNicks.getLocale().getLocaleConfig();
        for (Message m : Message.values()) {
            if (m == Message.VERSION) { continue; }
            String localeOption = m.toString().toLowerCase(Locale.ENGLISH).replace('_','-');
            String message = locale.getString(localeOption, null);
            if (message == null) { continue; }
            m.setMessage(message);
        }
    }
}
