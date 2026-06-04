package simplexity.simplenicks.platform;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * {@link SenderContext} backed by a Bukkit {@link CommandSender}.
 */
public class PaperSenderContext implements SenderContext {

    private final CommandSender sender;

    public PaperSenderContext(@NotNull CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public @NotNull Optional<UUID> getUuid() {
        if (sender instanceof Player player) return Optional.of(player.getUniqueId());
        return Optional.empty();
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public @NotNull String getDisplayName() {
        if (sender instanceof Player player) {
            return SimpleNicksCore.get().miniMessage().serialize(player.displayName());
        }
        return "[Console]";
    }
}
