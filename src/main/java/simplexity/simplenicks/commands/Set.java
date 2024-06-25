package simplexity.simplenicks.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import simplexity.simplenicks.util.NickHandler;
import simplexity.simplenicks.util.TagPermission;

import java.util.ArrayList;

public class Set extends SubCommand {

    public Set(String commandName, Permission basicPermission, Permission adminPermission, boolean consoleRunNoPlayer) {
        super(commandName, basicPermission, adminPermission, consoleRunNoPlayer);
    }

    @Override
    public void executeOnOther(CommandSender sender, Player player, String[] args) {


    }

    @Override
    public void executeOnSelf(CommandSender sender, Player player, String[] args) {
        String nickname = args[1];
        Component nickComponent = getNickComponent(player, nickname);
        String nickToSave = miniMessage.serialize(nickComponent);
        NickHandler.getInstance().setNickname(player, nickToSave);
        player.sendMessage(miniMessage.deserialize("<green>Nickname set to: " + nickComponent));
        player.sendMessage(miniMessage.deserialize("<green>Nickname saved as: " + nickToSave));
    }

    private void setPlayerNick(Player player, String nick, TagResolver resolver) {
        Component nameComponent = miniMessage.deserialize(nick, resolver);
        NickHandler.getInstance().setNickname(player, nick);
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender) {
        return null;
    }

    private Component getNickComponent(Player player, String nick) {
        int i = 0;
        TagResolver.Builder resolverBuilder = TagResolver.builder();
        for (TagPermission tagPermission : TagPermission.values()) {
            if (!player.hasPermission(tagPermission.getPermission())) {
                continue;
            }
            i++;
            resolverBuilder.resolver(tagPermission.getTagResolver());
        }
        if (i == 0) {
            String strippedMessage = miniMessage.stripTags(nick);
            return Component.text(strippedMessage);
        }
        return miniMessage.deserialize(nick, resolverBuilder.build());
    }


}
