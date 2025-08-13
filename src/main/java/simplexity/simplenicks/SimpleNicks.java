package simplexity.simplenicks;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplenicks.commands.NicknameCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.hooks.SNExpansion;
import simplexity.simplenicks.listener.LeaveListener;
import simplexity.simplenicks.listener.LoginListener;
import simplexity.simplenicks.saving.SaveMigrator;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.ColorTag;
import simplexity.simplenicks.util.FormatTag;
import simplexity.simplenicks.util.NickPermission;

import java.util.logging.Logger;


@SuppressWarnings("UnstableApiUsage")
public final class SimpleNicks extends JavaPlugin {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static Plugin instance;
    private static MiniMessage defaultResolver;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new SNExpansion().register();
        }
        getServer().getPluginManager().registerEvents(new LoginListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);
        configReload();
        SqlHandler.getInstance().init();
        SaveMigrator.migrateFromYml();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(NicknameCommand.createCommand().build());
        });
        setUpResolver();
        registerPermissions();
    }

    private void registerPermissions() {
        for (NickPermission perm : NickPermission.values()) {
            getServer().getPluginManager().addPermission(perm.getPermission());
        }
        for (ColorTag perm : ColorTag.values()) {
            getServer().getPluginManager().addPermission(perm.getPermission());
        }
    }

    private void setUpResolver() {
        TagResolver.Builder tagResolver = TagResolver.builder();
        for (ColorTag colorTag : ColorTag.values()) {
            tagResolver.resolver(colorTag.getTagResolver());
        }
        for (FormatTag formatTag : FormatTag.values()) {
            tagResolver.resolver(formatTag.getTagResolver());
        }
        TagResolver resolver = tagResolver.build();
        defaultResolver = MiniMessage.builder()
                .strict(false)
                .tags(resolver)
                .build();
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static Logger getSimpleNicksLogger() {
        return instance.getLogger();
    }

    public static MiniMessage getDefaultParser() {
        return defaultResolver;

    }


    public static void configReload() {
        ConfigHandler.getInstance().reloadConfig();
    }

    @Override
    public void onDisable() {
        SqlHandler.getInstance().closeDatabase();
    }


}
