package simplexity.simplenicks;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplenicks.commands.NicknameCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.hooks.SNExpansion;
import simplexity.simplenicks.hooks.SNMiniExpansion;
import simplexity.simplenicks.listener.LeaveListener;
import simplexity.simplenicks.listener.LoginListener;
import simplexity.simplenicks.platform.PaperPlatformAdapter;
import simplexity.simplenicks.saving.SaveMigrator;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.ColorTag;
import simplexity.simplenicks.util.FormatTag;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public final class SimpleNicks extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        PaperPlatformAdapter adapter = new PaperPlatformAdapter(this);
        SimpleNicksCore.initialize(adapter);

        ConfigHandler.getInstance().reloadConfig();
        SqlHandler.getInstance().init();
        SaveMigrator.migrateFromYml();

        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new SNExpansion().register();
        }
        if (this.getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            SNMiniExpansion.register();
        }

        getServer().getPluginManager().registerEvents(new LoginListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(NicknameCommand.createCommand().build());
        });

        registerPermissions();
    }

    @Override
    public void onDisable() {
        SqlHandler.getInstance().closeDatabase();
        SimpleNicksCore.shutdown();
    }

    private void registerPermissions() {
        for (NickPermission perm : NickPermission.values()) {
            getServer().getPluginManager().addPermission(
                    new Permission(perm.getPermissionKey(), toDefault(perm.getPermissionDefault()))
            );
        }
        for (ColorTag tag : ColorTag.values()) {
            getServer().getPluginManager().addPermission(
                    new Permission(tag.getPermissionKey(), toDefault(tag.getPermissionDefault()))
            );
        }
        for (FormatTag tag : FormatTag.values()) {
            getServer().getPluginManager().addPermission(
                    new Permission(tag.getPermissionKey(), toDefault(tag.getPermissionDefault()))
            );
        }
    }

    @SuppressWarnings("deprecation")
    private static PermissionDefault toDefault(String value) {
        return switch (value.toLowerCase()) {
            case "true" -> PermissionDefault.TRUE;
            case "false" -> PermissionDefault.FALSE;
            default -> PermissionDefault.OP;
        };
    }
}
