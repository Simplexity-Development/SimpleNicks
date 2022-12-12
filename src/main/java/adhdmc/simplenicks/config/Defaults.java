package adhdmc.simplenicks.config;

import adhdmc.simplenicks.SimpleNicks;
import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.comments.CommentType;

import java.util.Collections;
import java.util.List;

public class Defaults {

    public static void setConfigDefaults() {
        FileConfiguration config = SimpleNicks.getInstance().getConfig();
        config.addDefault("max-nickname-length", 30);
        config.addDefault("nickname-regex","[A-Za-z0-9_]+");
    }
}
