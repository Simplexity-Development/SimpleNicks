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

        if (days == 0 && hours == 0 && minutes == 0 && seconds == 0) {
            Component nowComponent = miniMessage.deserialize(LocaleMessage.TIME_FORMAT_NOW.getMessage());
            return TagResolver.resolver(Placeholder.component("time", nowComponent));
        }

        Component finalComponent = Component.empty();

        if (days > 0) {
            finalComponent = finalComponent.append(
                    parseNumber(days == 1
                            ? LocaleMessage.TIME_FORMAT_DAY.getMessage()
                            : LocaleMessage.TIME_FORMAT_DAYS.getMessage(), days)
            );
        }
        if (hours > 0) {
            finalComponent = finalComponent.append(
                    parseNumber(hours == 1
                            ? LocaleMessage.TIME_FORMAT_HOUR.getMessage()
                            : LocaleMessage.TIME_FORMAT_HOURS.getMessage(), hours)
            );
        }
        if (minutes > 0) {
            finalComponent = finalComponent.append(
                    parseNumber(minutes == 1
                            ? LocaleMessage.TIME_FORMAT_MINUTE.getMessage()
                            : LocaleMessage.TIME_FORMAT_MINUTES.getMessage(), minutes)
            );
        }
        if (seconds > 0 && days == 0 && hours == 0) {
            finalComponent = finalComponent.append(
                    parseNumber(seconds == 1
                            ? LocaleMessage.TIME_FORMAT_SECOND.getMessage()
                            : LocaleMessage.TIME_FORMAT_SECONDS.getMessage(), seconds)
            );
        }
        finalComponent = finalComponent.append(
                miniMessage.deserialize(LocaleMessage.TIME_FORMAT_AGO.getMessage())
        );

        return TagResolver.resolver(Placeholder.component("time", finalComponent));
    }

    public static TagResolver savedNickListResolver(List<Nickname> nicknames) {
        if (nicknames == null || nicknames.isEmpty()) {
            return TagResolver.resolver(Placeholder.component("list",
                    miniMessage.deserialize(LocaleMessage.LOOKUP_NO_SAVED_NICKS.getMessage())));
        }
        Component finalComponent = Component.empty();
        for (Nickname nick : nicknames) {
            Component nickname = miniMessage.deserialize(nick.getNickname());
            finalComponent = finalComponent.append(
                    miniMessage.deserialize(
                            LocaleMessage.LOOKUP_SAVED_NICK.getMessage(),
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
