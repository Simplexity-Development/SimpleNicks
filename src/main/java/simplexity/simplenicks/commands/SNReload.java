package simplexity.simplenicks.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleHandler;

public class SNReload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ConfigHandler.getInstance().reloadConfig();
        LocaleHandler.getInstance().loadLocale();
        sender.sendMessage(SimpleNicks.getMiniMessage().deserialize(LocaleHandler.getInstance().getConfigReloaded(),
                Placeholder.parsed("prefix", LocaleHandler.getInstance().getPluginPrefix())));
        return false;
    }
}
