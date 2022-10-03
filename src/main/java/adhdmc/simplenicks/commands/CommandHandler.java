package adhdmc.simplenicks.commands;

import adhdmc.simplenicks.SimpleNicks;
import adhdmc.simplenicks.commands.subcommands.Reload;
import adhdmc.simplenicks.commands.subcommands.Set;
import adhdmc.simplenicks.config.ConfigValidator;
import adhdmc.simplenicks.config.Defaults;
import adhdmc.simplenicks.config.Permission;
import adhdmc.simplenicks.config.SimpleNickPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Map;


public class CommandHandler implements CommandExecutor {

    private static final Map<ConfigValidator.Message, String> messages = ConfigValidator.getMessages();
    private static final MiniMessage miniMessage = SimpleNicks.getMiniMessage();
    private static final Map<String, SubCommand> subcommandList = SimpleNicks.getSubCommands();


    private static final Plugin instance = SimpleNicks.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //Checking for arguments
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("PLACEHOLDER"));
            return true;
        }
        String input = args[0].toLowerCase();
        String action = args[1].toLowerCase();
        if (subcommandList.containsKey(input)) {
            subcommandList.get(input).execute(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
            }
        if(checkForPlayerName(input) &&
          (!(sender.hasPermission(SimpleNickPermission.NICK_COMMAND_OTHERS.getPermission())))
          || sender.hasPermission(SimpleNickPermission.NICK_COMMAND_RESET_OTHERS.getPermission())) {
            sender.sendMessage(miniMessage.deserialize(messages.get(ConfigValidator.Message.NO_PERMISSION)));
            return true;
        } else if (action.equals("set") && sender.hasPermission(SimpleNickPermission.NICK_COMMAND_OTHERS.getPermission()))){
            subcommandList.get(action).execute(input, );

        }

        return true;
    }

    private boolean checkForPlayerName(String input){
        return instance.getServer().getPlayer(input) != null;
    }
}

