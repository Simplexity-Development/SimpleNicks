package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigValidator {
    public enum Message {
        //errors
        INVALID_COMMAND, NO_PERMISSION, CONSOLE_CANNOT_RUN, INVALID_PLAYER, INVALID_NICK,
        //plugin messages
        PREFIX, NICK_CHANGED_SELF, NICK_CHANGED_OTHER, NICK_RESET_SELF, NICK_RESET_OTHER,
        //other
        VERSION
    }
    private static final HashMap<Message, String> messages = new HashMap<>();

    public static void loadLocaleMessages(){
        FileConfiguration locale = SimpleNicks.getLocale().getlocaleConfig();
        messages.clear();
        messages.put(Message.INVALID_COMMAND, locale.getString("invalid-command"));
        messages.put(Message.NO_PERMISSION, locale.getString("no-permission"));
        messages.put(Message.CONSOLE_CANNOT_RUN, locale.getString("console-cannot-run"));
        messages.put(Message.INVALID_PLAYER, locale.getString("invalid-player"));
        messages.put(Message.INVALID_NICK, locale.getString("invalid-nick"));
        messages.put(Message.PREFIX, locale.getString("prefix"));
        messages.put(Message.NICK_CHANGED_SELF, locale.getString("nick-changed-self"));
        messages.put(Message.NICK_CHANGED_OTHER, locale.getString("nick-changed-other"));
        messages.put(Message.NICK_RESET_SELF, locale.getString("nick-reset-self"));
        messages.put(Message.NICK_RESET_OTHER, locale.getString("nick-reset-other"));
        messages.put(Message.VERSION, "PLACEHOLDER");
    }

    public static Map<Message, String> getMessages(){
        return Collections.unmodifiableMap(messages);
    }
}
