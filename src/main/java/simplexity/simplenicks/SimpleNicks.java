package simplexity.simplenicks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simplenicks.commands.CommandHandler;
import simplexity.simplenicks.commands.Delete;
import simplexity.simplenicks.commands.Help;
import simplexity.simplenicks.commands.Reset;
import simplexity.simplenicks.commands.SNReload;
import simplexity.simplenicks.commands.Save;
import simplexity.simplenicks.commands.Set;
import simplexity.simplenicks.commands.SubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;
import simplexity.simplenicks.listener.LoginListener;
import simplexity.simplenicks.util.Constants;
import simplexity.simplenicks.util.NickHandler;
import simplexity.simplenicks.util.SNExpansion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    private static final HashMap<String, SubCommand> subCommands = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        registerSubCommands();
        this.saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.getCommand("nick").setExecutor(new CommandHandler());
        this.getCommand("snreload").setExecutor(new SNReload());
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

    public static Map<String, SubCommand> getSubCommands() {
        return Collections.unmodifiableMap(subCommands);
    }

    public static Logger getSimpleNicksLogger() {
        return instance.getLogger();
    }

    private void registerSubCommands() {
        subCommands.put("reset", new Reset("reset", Constants.NICK_RESET, Constants.NICK_RESET_OTHERS, false));
        subCommands.put("help", new Help("help", Constants.NICK_COMMAND, Constants.NICK_OTHERS_COMMAND, true));
        subCommands.put("set", new Set("set", Constants.NICK_COMMAND, Constants.NICK_OTHERS_RESTRICTIVE, false));
        subCommands.put("save", new Save("save", Constants.NICK_SAVE, Constants.NICK_OTHERS_SAVE, false));
        subCommands.put("delete", new Delete("delete", Constants.NICK_DELETE, Constants.NICK_OTHERS_DELETE, false));
    }

    public static void configReload() {
        LocaleHandler.getInstance().loadLocale();
        ConfigHandler.getInstance().reloadConfig();
        NickHandler.getInstance().loadSavingType();
    }

}
