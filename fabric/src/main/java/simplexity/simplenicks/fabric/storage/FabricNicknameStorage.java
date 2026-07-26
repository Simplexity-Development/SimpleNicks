package simplexity.simplenicks.fabric.storage;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static storage for per-player display name components on the Fabric platform.
 * <p>
 * The {@code ServerPlayerMixin} reads from these maps when the server queries
 * {@code getDisplayName()} (chat) and {@code getTabListDisplayName()} (tab list).
 * {@link simplexity.simplenicks.fabric.platform.FabricPlatformAdapter} writes to
 * them after converting Adventure components to NMS components.
 * </p>
 */
public final class FabricNicknameStorage {

    private static final Map<UUID, Component> displayNames = new ConcurrentHashMap<>();
    private static final Map<UUID, Component> tabNames = new ConcurrentHashMap<>();

    private FabricNicknameStorage() {
    }

    public static void setDisplayName(@NotNull UUID uuid, @NotNull Component component) {
        displayNames.put(uuid, component);
    }

    public static void setTabName(@NotNull UUID uuid, @NotNull Component component) {
        tabNames.put(uuid, component);
    }

    public static void clearDisplayName(@NotNull UUID uuid) {
        displayNames.remove(uuid);
    }

    public static void clearTabName(@NotNull UUID uuid) {
        tabNames.remove(uuid);
    }

    /**
     * Returns the stored display name for the given player, or {@code null} if none is set.
     * A {@code null} return means the mixin should not intercept and vanilla behaviour applies.
     */
    @Nullable
    public static Component getDisplayName(@NotNull UUID uuid) {
        return displayNames.get(uuid);
    }

    /**
     * Returns the stored tab-list name for the given player, or {@code null} if none is set.
     * A {@code null} return means the mixin should not intercept and vanilla behaviour applies.
     */
    @Nullable
    public static Component getTabName(@NotNull UUID uuid) {
        return tabNames.get(uuid);
    }
}
