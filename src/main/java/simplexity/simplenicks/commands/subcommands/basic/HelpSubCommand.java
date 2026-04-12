package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.util.NickPermission;

@SuppressWarnings("UnstableApiUsage")
public class HelpSubCommand implements SubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("help").requires(this::canExecute)
                .executes(this::execute));
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ctx.getSource().getSender().sendMessage(buildHelpMessage(ctx.getSource().getSender()));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Builds a help message containing only the commands the given sender has access to.
     *
     * @param sender the command sender
     * @return formatted help component
     */
    @NotNull
    private Component buildHelpMessage(@NotNull CommandSender sender) {
        ConfigHandler config = ConfigHandler.getInstance();
        Component help = line(LocaleMessage.HELP_HEADER);

        // Player-only commands — only shown to players who have the relevant permission
        if (sender instanceof Player player) {
            boolean permNotRequired = !config.isNickRequiresPermission();

            if (permNotRequired || player.hasPermission(NickPermission.NICK_SET.getPermission())) {
                help = help.appendNewline().append(line(LocaleMessage.HELP_SET));
                help = help.appendNewline().append(line(LocaleMessage.HELP_RESET));
            }

            if (permNotRequired || player.hasPermission(NickPermission.NICK_SAVE.getPermission())) {
                help = help.appendNewline().append(line(LocaleMessage.HELP_SAVE));
                help = help.appendNewline().append(line(LocaleMessage.HELP_DELETE));
            }
        }

        // /nick who — usable by any sender type
        if (!config.isWhoRequiresPermission() || sender.hasPermission(NickPermission.NICK_WHO.getPermission())) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_WHO));
        }

        // Admin commands — shown per individual permission, not just the parent node
        if (sender.hasPermission(NickPermission.NICK_ADMIN_SET.getPermission())) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_SET));
        }
        if (sender.hasPermission(NickPermission.NICK_ADMIN_RESET.getPermission())) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_RESET));
        }
        if (sender.hasPermission(NickPermission.NICK_ADMIN_DELETE.getPermission())) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_DELETE));
        }
        if (sender.hasPermission(NickPermission.NICK_ADMIN_LOOKUP.getPermission())) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_LOOKUP));
        }

        // /nick reload
        if (sender.hasPermission(NickPermission.NICK_RELOAD.getPermission())) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_RELOAD));
        }

        return help;
    }

    @NotNull
    private static Component line(@NotNull LocaleMessage msg) {
        return SimpleNicks.getMiniMessage().deserialize(msg.getMessage());
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        return true;
    }
}
