package simplexity.simplenicks.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.saving.Nickname;

import java.util.UUID;

public interface SubCommand<S> {

    void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent);

    int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException;

    boolean canExecute(@NotNull S source);

    default boolean permissionNotRequired() {
        return !ConfigHandler.getInstance().isNickRequiresPermission();
    }

    default void refreshName(@NotNull UUID uuid, boolean isOnline) {
        if (!isOnline) return;
        SimpleNicksCore.get().platform().runSync(() -> NickUtils.refreshDisplayName(uuid));
    }

    default void sendFeedback(@NotNull SenderContext sender, @Nullable LocaleMessage msg, @Nullable Nickname nick) {
        if (nick == null) nick = new Nickname("", "");
        if (msg == null || msg.getMessage().isEmpty()) return;
        Component component = SimpleNicksCore.get().miniMessage().deserialize(
                msg.getMessage(), Placeholder.parsed("value", nick.getNickname()));
        sender.sendMessage(component);
    }

    default @NotNull Component parseAdminMessage(@NotNull String template, @NotNull String value,
                                                  @NotNull SenderContext initiator, @NotNull String targetName) {
        String initiatorDisplayName = initiator.isPlayer()
                ? initiator.getDisplayName()
                : LocaleMessage.SERVER_DISPLAY_NAME.getMessage();
        Component initiatorComponent = SimpleNicksCore.get().miniMessage().deserialize(initiatorDisplayName);
        return SimpleNicksCore.get().miniMessage().deserialize(template,
                Placeholder.parsed("value", value),
                Placeholder.component("initiator", initiatorComponent),
                Placeholder.parsed("target", targetName));
    }
}
