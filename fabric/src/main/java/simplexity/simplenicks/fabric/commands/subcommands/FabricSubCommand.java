package simplexity.simplenicks.fabric.commands.subcommands;

import net.minecraft.server.players.NameAndId;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.fabric.platform.FabricPlatformAdapter;
import simplexity.simplenicks.logic.NickUtils;
import simplexity.simplenicks.saving.Cache;
import simplexity.simplenicks.saving.Nickname;

import java.util.UUID;

/**
 * Interface shared by all Fabric /nick subcommands, mirroring the Paper {@code SubCommand}
 * but typed to Fabric's {@link CommandSourceStack}.
 */
public interface FabricSubCommand {

    void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent);

    int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    boolean canExecute(@NotNull CommandSourceStack source);

    default boolean permissionNotRequired() {
        return !ConfigHandler.getInstance().isNickRequiresPermission();
    }

    default void refreshName(@NotNull UUID uuid, boolean isOnline) {
        if (!isOnline) return;
        SimpleNicksCore.get().platform().runSync(() -> NickUtils.refreshDisplayName(uuid));
    }

    /**
     * Sends a MiniMessage-rendered feedback message to the command source.
     */
    default void sendFeedback(@NotNull CommandSourceStack source,
                              @Nullable LocaleMessage localeMessage,
                              @Nullable Nickname nickname) {
        if (nickname == null) nickname = new Nickname("", "");
        if (localeMessage == null || localeMessage.getMessage().isEmpty()) return;
        Component message = SimpleNicksCore.get().miniMessage().deserialize(
                localeMessage.getMessage(), Placeholder.parsed("value", nickname.getNickname()));
        sendToSource(source, message);
    }

    /**
     * Sends a MiniMessage-rendered message to a specific {@link ServerPlayer}.
     */
    default void sendToPlayer(@NotNull ServerPlayer player, @Nullable LocaleMessage localeMessage,
                              @Nullable Nickname nickname) {
        if (nickname == null) nickname = new Nickname("", "");
        if (localeMessage == null || localeMessage.getMessage().isEmpty()) return;
        Component message = SimpleNicksCore.get().miniMessage().deserialize(
                localeMessage.getMessage(), Placeholder.parsed("value", nickname.getNickname()));
        FabricPlatformAdapter adapter = (FabricPlatformAdapter) SimpleNicksCore.get().platform();
        player.sendSystemMessage(adapter.getAudiences().asNative(message));
    }

    /**
     * Sends an Adventure component to the command source using NMS conversion.
     */
    default void sendToSource(@NotNull CommandSourceStack source, @NotNull Component message) {
        FabricPlatformAdapter adapter = (FabricPlatformAdapter) SimpleNicksCore.get().platform();
        net.minecraft.network.chat.Component nms = adapter.getAudiences().asNative(message);
        source.sendSuccess(() -> nms, false);
    }

    /**
     * Sends an already-built Adventure component directly to a {@link ServerPlayer}.
     */
    default void sendComponentToPlayer(@NotNull ServerPlayer player, @NotNull Component message) {
        FabricPlatformAdapter adapter = (FabricPlatformAdapter) SimpleNicksCore.get().platform();
        player.sendSystemMessage(adapter.getAudiences().asNative(message));
    }

    /**
     * Builds the admin feedback message that includes the initiator name, target name, and value.
     * Mirrors the Paper {@code parseAdminMessage} helper.
     */
    default @NotNull Component parseAdminMessage(@NotNull String template,
                                                 @NotNull String value,
                                                 @NotNull CommandSourceStack initiatorSource,
                                                 @NotNull NameAndId target) {
        Component initiatorName;
        ServerPlayer initiatorPlayer = initiatorSource.getPlayer();
        if (initiatorPlayer != null) {
            Nickname nick = Cache.getInstance().getActiveNickname(initiatorPlayer.getUUID());
            String nameStr = nick != null ? nick.getNickname() : initiatorPlayer.getGameProfile().name();
            initiatorName = SimpleNicksCore.get().miniMessage().deserialize(nameStr);
        } else {
            initiatorName = SimpleNicksCore.get().miniMessage()
                    .deserialize(LocaleMessage.SERVER_DISPLAY_NAME.getMessage());
        }
        return SimpleNicksCore.get().miniMessage().deserialize(template,
                Placeholder.parsed("value", value),
                Placeholder.component("initiator", initiatorName),
                Placeholder.parsed("target", target.name()));
    }
}
