package simplexity.simplenicks.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.saving.Nickname;

import java.util.List;

public class MessageUtils {


    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();

    public static TagResolver getTimeFormat(long timeSeconds) {
        long seconds = timeSeconds % 60;
        long minutes = (timeSeconds / 60) % 60;
        long hours = (timeSeconds / (60 * 60)) % 24;
        long days = (timeSeconds / (60 * 60 * 24)) % 365;
        Component dayComponent = Component.empty();
        Component hourComponent = Component.empty();
        Component minuteComponent = Component.empty();
        Component secondComponent = Component.empty();
        if (days > 0) {
            if (days == 1) {
                dayComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_DAY.getMessage(), days);
            } else {
                dayComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_DAYS.getMessage(), days);
            }
        }
        if (hours > 0) {
            if (hours == 1) {
                hourComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_HOUR.getMessage(), hours);
            } else {
                hourComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_HOURS.getMessage(), hours);
            }
        }
        if (minutes > 0) {
            if (minutes == 1) {
                minuteComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_MINUTE.getMessage(), minutes);
            } else {
                minuteComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_MINUTES.getMessage(), minutes);
            }
        }
        if (seconds > 0) {
            if (seconds == 1) {
                secondComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_SECOND.getMessage(), seconds);
            } else {
                secondComponent = parseNumber(LocaleMessage.INSERT_TIME_FORMAT_SECONDS.getMessage(), seconds);
            }
        }
        Component finalComponent = miniMessage.deserialize(LocaleMessage.INSERT_TIME_FORMAT_GROUP.getMessage(),
                Placeholder.component("day", dayComponent),
                Placeholder.component("hour", hourComponent),
                Placeholder.component("min", minuteComponent),
                Placeholder.component("sec", secondComponent));
        return TagResolver.resolver(Placeholder.component("time", finalComponent));
    }

    public static TagResolver savedNickListResolver(List<Nickname> nicknames) {
        if (nicknames == null || nicknames.isEmpty()) {
            return TagResolver.resolver(Placeholder.component("list",
                    miniMessage.deserialize(LocaleMessage.INSERT_NO_SAVED_NICKS.getMessage())));
        }
        Component finalComponent = Component.empty();
        for (Nickname nick : nicknames) {
            Component nickname = miniMessage.deserialize(nick.getNickname());
            finalComponent = finalComponent.append(
                    miniMessage.deserialize(
                            LocaleMessage.INSERT_SAVED_NICK.getMessage(),
                            Placeholder.component("name", nickname)
                    ));
        }
        return TagResolver.resolver(Placeholder.component("list", finalComponent));
    }

    private static Component parseNumber(String message, long number) {
        return miniMessage.deserialize(message,
                Placeholder.parsed("count", String.valueOf(number)));
    }


}
