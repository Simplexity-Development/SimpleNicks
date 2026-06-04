package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Nickname;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public interface SubCommand {

    void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent);

    int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    boolean canExecute(@NotNull CommandSourceStack css);

    default void sendFeedback(@NotNull Player player, @Nullable LocaleMessage localeMessage, @Nullable Nickname nickname) {
        if (nickname == null) nickname = new Nickname("", "");
        if (localeMessage == null || localeMessage.getMessage().isEmpty()) return;
        player.sendRichMessage(
                localeMessage.getMessage(),
                Placeholder.parsed("value", nickname.getNickname())
        );
    }

    default Component parseAdminMessage(@NotNull String message, @NotNull String value,
                                        @NotNull CommandSender initiator, @NotNull OfflinePlayer target) {
        Component initiatorName;
        String targetUserName = target.getName();
        if (targetUserName == null) targetUserName = "[Username not found]";
        if (initiator instanceof Player playerInitiator) {
            initiatorName = playerInitiator.displayName();
        } else {
            initiatorName = SimpleNicksCore.get().miniMessage().deserialize(LocaleMessage.SERVER_DISPLAY_NAME.getMessage());
        }
        return SimpleNicksCore.get().miniMessage().deserialize(message,
                Placeholder.parsed("value", value),
                Placeholder.component("initiator", initiatorName),
                Placeholder.parsed("target", targetUserName)
        );
    }

    @SuppressWarnings("unused")
    default <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        throw new NotImplementedException("listSuggestions was used, but not implemented.");
    }

    default void refreshName(@NotNull UUID uuid, boolean isOnline) {
        if (!isOnline) return;
        SimpleNicksCore.get().platform().runSync(() -> NickUtils.refreshDisplayName(uuid));
    }

    default boolean permissionNotRequired() {
        return !ConfigHandler.getInstance().isNickRequiresPermission();
    }
}
