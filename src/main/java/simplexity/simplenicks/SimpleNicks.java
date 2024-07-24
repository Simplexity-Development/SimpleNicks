package simplexity.simplenicks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplenicks.command.CommandHandler;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.listener.LoginListener;
import simplexity.simplenicks.util.NickHandler;
import simplexity.simplenicks.util.SNExpansion;

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

public final class SimpleNicks extends JavaPlugin {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        CommandHandler.registerSubCommands();
        this.saveDefaultConfig();
        ConfigHandler.getInstance().setConfigDefaults();
        this.getCommand("nick").setExecutor(new CommandHandler());
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new SNExpansion().register();
        }
        instance.getServer().getPluginManager().registerEvents(new LoginListener(), this);
        configReload();
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
        LocaleHandler.getInstance().loadLocale();
        ConfigHandler.getInstance().reloadConfig();
        NickHandler.getInstance().loadSavingType();
    }
}
