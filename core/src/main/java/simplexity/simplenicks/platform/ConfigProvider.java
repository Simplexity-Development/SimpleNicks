package simplexity.simplenicks.platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstracts YAML file reading and writing so that core config/locale handlers
 * do not depend on platform-specific config APIs (e.g. Bukkit {@code FileConfiguration}).
 */
public interface ConfigProvider {

    /**
     * Reloads values from the backing file.
     */
    void reload();

    /**
     * Returns the string value at {@code key}, or {@code defaultValue} if absent.
     *
     * @param key          the config key
     * @param defaultValue fallback value
     * @return the string value, or {@code defaultValue}
     */
    @Nullable
    String getString(@NotNull String key, @Nullable String defaultValue);

    /**
     * Returns the int value at {@code key}, or {@code defaultValue} if absent or not an int.
     *
     * @param key          the config key
     * @param defaultValue fallback value
     * @return the int value, or {@code defaultValue}
     */
    int getInt(@NotNull String key, int defaultValue);

    /**
     * Returns the boolean value at {@code key}, or {@code defaultValue} if absent.
     *
     * @param key          the config key
     * @param defaultValue fallback value
     * @return the boolean value, or {@code defaultValue}
     */
    boolean getBoolean(@NotNull String key, boolean defaultValue);

    /**
     * Returns the long value at {@code key}, or {@code defaultValue} if absent.
     *
     * @param key          the config key
     * @param defaultValue fallback value
     * @return the long value, or {@code defaultValue}
     */
    long getLong(@NotNull String key, long defaultValue);

    /**
     * Sets a value in the backing store. Does not persist until {@link #save()} is called.
     *
     * @param key   the config key
     * @param value the value to set
     */
    void set(@NotNull String key, @Nullable Object value);

    /**
     * Persists the current state of the backing store to disk.
     */
    void save();

    /**
     * Returns whether the given key exists in the backing store.
     *
     * @param key the config key
     * @return {@code true} if the key is present
     */
    boolean contains(@NotNull String key);
}
