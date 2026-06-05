package simplexity.simplenicks.fabric.platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import simplexity.simplenicks.platform.ConfigProvider;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link ConfigProvider} backed by a plain YAML file via SnakeYAML.
 * Used for both {@code config.yml} and {@code locale.yml} on Fabric.
 */
@SuppressWarnings("unchecked")
public class FabricConfigProvider implements ConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FabricConfigProvider.class);

    private final Path file;
    private Map<String, Object> data = new LinkedHashMap<>();
    private final Yaml yaml;

    public FabricConfigProvider(@NotNull Path file) {
        this.file = file;
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        opts.setPrettyFlow(true);
        this.yaml = new Yaml(opts);
    }

    @Override
    public void reload() {
        try {
            Files.createDirectories(file.getParent());
            if (!Files.exists(file) || Files.size(file) == 0) {
                copyDefaultResource();
            }
            try (FileReader reader = new FileReader(file.toFile())) {
                Map<String, Object> loaded = yaml.load(reader);
                data = loaded != null ? loaded : new LinkedHashMap<>();
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to load config file '{}': {}", file, e.getMessage(), e);
        }
    }

    /**
     * Copies the bundled default resource matching this file's name into the config directory.
     * Only called when the destination file is absent or empty.
     */
    private void copyDefaultResource() throws IOException {
        String resourceName = file.getFileName().toString();
        try (InputStream in = FabricConfigProvider.class.getResourceAsStream("/" + resourceName)) {
            if (in != null) {
                Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.createFile(file);
            }
        }
    }

    @Override
    public @Nullable String getString(@NotNull String key, @Nullable String defaultValue) {
        Object value = resolve(key);
        if (value == null) return defaultValue;
        return value.toString();
    }

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        Object value = resolve(key);
        if (value instanceof Number n) return n.intValue();
        return defaultValue;
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        Object value = resolve(key);
        if (value instanceof Boolean b) return b;
        return defaultValue;
    }

    @Override
    public long getLong(@NotNull String key, long defaultValue) {
        Object value = resolve(key);
        if (value instanceof Number n) return n.longValue();
        return defaultValue;
    }

    @Override
    public void set(@NotNull String key, @Nullable Object value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new LinkedHashMap<>());
        }
        if (value == null) {
            current.remove(parts[parts.length - 1]);
        } else {
            current.put(parts[parts.length - 1], value);
        }
    }

    @Override
    public void save() {
        try (FileWriter writer = new FileWriter(file.toFile())) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            LOGGER.warn("Failed to save config file '{}': {}", file, e.getMessage(), e);
        }
    }

    @Override
    public boolean contains(@NotNull String key) {
        return resolve(key) != null;
    }

    @Nullable
    private Object resolve(@NotNull String key) {
        String[] parts = key.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(part);
        }
        return current;
    }
}
