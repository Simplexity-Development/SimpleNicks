package simplexity.simplenicks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.platform.PlatformAdapter;

/**
 * Central singleton for the SimpleNicks core.
 * <p>
 * Initialized by the platform entry point (e.g. {@code SimpleNicks} on Paper,
 * {@code SimpleNicksFabric} on Fabric) before any core class is used.
 * All core classes access the platform through {@link #get()}.
 * </p>
 */
public final class SimpleNicksCore {

    private static SimpleNicksCore instance;

    private final PlatformAdapter platform;
    private final MiniMessage miniMessage;

    private SimpleNicksCore(@NotNull PlatformAdapter platform) {
        this.platform = platform;
        this.miniMessage = platform.getMiniMessage();
    }

    /**
     * Initializes the core with the given platform adapter.
     * Must be called before any core class accesses {@link #get()}.
     *
     * @param platform the platform-specific adapter
     */
    public static void initialize(@NotNull PlatformAdapter platform) {
        instance = new SimpleNicksCore(platform);
    }

    /**
     * Returns the active core instance.
     *
     * @return the core singleton
     * @throws IllegalStateException if {@link #initialize(PlatformAdapter)} has not been called
     */
    @NotNull
    public static SimpleNicksCore get() {
        if (instance == null) throw new IllegalStateException("SimpleNicksCore has not been initialized");
        return instance;
    }

    /**
     * Tears down the core instance. Called during plugin/mod shutdown.
     */
    public static void shutdown() {
        instance = null;
    }

    /**
     * Returns the platform adapter for this environment.
     *
     * @return the platform adapter
     */
    @NotNull
    public PlatformAdapter platform() {
        return platform;
    }

    /**
     * Returns the configured {@link MiniMessage} instance.
     *
     * @return the MiniMessage instance
     */
    @NotNull
    public MiniMessage miniMessage() {
        return miniMessage;
    }
}
