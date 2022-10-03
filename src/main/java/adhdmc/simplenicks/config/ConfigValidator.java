package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.subcommands.Set;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfigValidator {
    public enum Message {
        //errors
        INVALID_COMMAND("<red>Invalid Command."),
        NO_PERMISSION("<red>No Permission."),
        CONSOLE_CANNOT_RUN("<red>Cannot be run on the Console."),
        INVALID_PLAYER("<red>Not a valid player."),
        INVALID_NICK("<red>Not a valid nickname, must follow regex: " + Set.NICKNAME_REGEX),
        //plugin messages
        PREFIX("<aqua>SimpleNicks <white>»"),
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
        FileConfiguration locale = SimpleNicks.getLocale().getlocaleConfig();
        for (Message m : Message.values()) {
            if (m == Message.VERSION) { continue; }
            String localeOption = m.toString().toLowerCase(Locale.ENGLISH).replace('_','-');
            m.setMessage(locale.getString(localeOption));
        }
    }
}
