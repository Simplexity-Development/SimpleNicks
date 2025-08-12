package simplexity.simplenicks;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplenicks.commands.NicknameCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.hooks.SNExpansion;
import simplexity.simplenicks.listener.LeaveListener;
import simplexity.simplenicks.listener.LoginListener;
import simplexity.simplenicks.saving.SaveMigrator;
import simplexity.simplenicks.saving.SqlHandler;
import simplexity.simplenicks.util.NickPermission;
import simplexity.simplenicks.util.TagPermission;

import java.util.logging.Logger;

/*command based
[/nick [nickname] or /nick <player> [nickname]]
need to check if it's a valid name
[Alphanumeric, tho a bypass permission would be nice]
need to check if the minimessage formats are allowed- i,e, not allowing hover/click event tag
[assuming I'd make a set of blacklisted tag types, check for those]
need to check length after parsing
PlaceholderAPI placeholder for before parsing, and after
*/

@SuppressWarnings("UnstableApiUsage")
public final class SimpleNicks extends JavaPlugin {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static Plugin instance;

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
        registerPermissions();
    }

    private void registerPermissions(){
        for (NickPermission perm : NickPermission.values()) {
            getServer().getPluginManager().addPermission(perm.getPermission());
        }
        for (TagPermission perm : TagPermission.values()) {
            getServer().getPluginManager().addPermission(perm.getPermission());
        }
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


    public static void configReload() {
        ConfigHandler.getInstance().reloadConfig();
    }

    @Override
    public void onDisable(){
        SqlHandler.getInstance().closeDatabase();
    }


}
