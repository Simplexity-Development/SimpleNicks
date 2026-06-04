package simplexity.simplenicks.platform;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * {@link ConfigProvider} backed by Bukkit's {@link FileConfiguration}.
 * <p>
 * Use {@link #forMainConfig(JavaPlugin)} for {@code config.yml} (delegates to
 * {@link JavaPlugin#getConfig()}) and {@link #forFile(File)} for other YAML
 * files such as {@code locale.yml}.
 * </p>
 */
@SuppressWarnings("CallToPrintStackTrace")
public class BukkitConfigProvider implements ConfigProvider {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration yamlConfig;

    private BukkitConfigProvider(JavaPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    /**
     * Creates a provider backed by the plugin's main {@code config.yml}.
     *
     * @param plugin the plugin instance
     * @return a config provider for the main config
     */
    @NotNull
    public static BukkitConfigProvider forMainConfig(@NotNull JavaPlugin plugin) {
        return new BukkitConfigProvider(plugin, null);
    }

    /**
     * Creates a provider backed by the given YAML file. The file is created on
     * first {@link #reload()} if it does not exist.
     *
     * @param file the YAML file to read/write
     * @return a config provider for the given file
     */
    @NotNull
    public static BukkitConfigProvider forFile(@NotNull File file) {
        return new BukkitConfigProvider(null, file);
    }

    @Override
    public void reload() {
        if (isMainConfig()) {
            plugin.reloadConfig();
        } else {
            try {
                if (file.getParentFile() != null) file.getParentFile().mkdirs();
                file.createNewFile();
                yamlConfig = new YamlConfiguration();
                yamlConfig.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public @Nullable String getString(@NotNull String key, @Nullable String defaultValue) {
        return config().getString(key, defaultValue);
    }

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        return config().getInt(key, defaultValue);
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return config().getBoolean(key, defaultValue);
    }

    @Override
    public long getLong(@NotNull String key, long defaultValue) {
        return config().getLong(key, defaultValue);
    }

    @Override
    public void set(@NotNull String key, @Nullable Object value) {
        config().set(key, value);
    }

    @Override
    public void save() {
        if (isMainConfig()) {
            plugin.saveConfig();
        } else {
            try {
                yamlConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean contains(@NotNull String key) {
        return config().contains(key);
    }

    private boolean isMainConfig() {
        return plugin != null;
    }

    @NotNull
    private FileConfiguration config() {
        if (isMainConfig()) return plugin.getConfig();
        return yamlConfig;
    }
}
