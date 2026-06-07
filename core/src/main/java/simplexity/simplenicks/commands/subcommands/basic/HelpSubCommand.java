package simplexity.simplenicks.commands.subcommands.basic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.subcommands.SubCommand;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.platform.BrigadierAdapter;
import simplexity.simplenicks.platform.SenderContext;
import simplexity.simplenicks.util.NickPermission;

public class HelpSubCommand<S> implements SubCommand<S> {

    private final BrigadierAdapter<S> adapter;

    public HelpSubCommand(@NotNull BrigadierAdapter<S> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<S> parent) {
        parent.then(adapter.literal("help").requires(this::canExecute)
                .executes(this::execute));
    }

    @Override
    public int execute(@NotNull CommandContext<S> ctx) throws CommandSyntaxException {
        S source = ctx.getSource();
        SenderContext sender = adapter.senderContext(source);
        sender.sendMessage(buildHelpMessage(source));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull S source) {
        return adapter.hasPermission(source, NickPermission.NICK_HELP);
    }

    @NotNull
    private Component buildHelpMessage(@NotNull S source) {
        ConfigHandler config = ConfigHandler.getInstance();
        Component help = line(LocaleMessage.HELP_HEADER);

        if (adapter.isPlayer(source)) {
            boolean permNotRequired = !config.isNickRequiresPermission();

            if (permNotRequired || adapter.hasPermission(source, NickPermission.NICK_SET)) {
                help = help.appendNewline().append(line(LocaleMessage.HELP_SET));
                help = help.appendNewline().append(line(LocaleMessage.HELP_RESET));
            }

            if (permNotRequired || adapter.hasPermission(source, NickPermission.NICK_SAVE)) {
                help = help.appendNewline().append(line(LocaleMessage.HELP_SAVE));
                help = help.appendNewline().append(line(LocaleMessage.HELP_DELETE));
            }
        }

        if (!config.isWhoRequiresPermission() || adapter.hasPermission(source, NickPermission.NICK_WHO)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_WHO));
        }

        if (adapter.hasPermission(source, NickPermission.NICK_ADMIN_SET)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_SET));
        }
        if (adapter.hasPermission(source, NickPermission.NICK_ADMIN_RESET)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_RESET));
        }
        if (adapter.hasPermission(source, NickPermission.NICK_ADMIN_DELETE)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_DELETE));
        }
        if (adapter.hasPermission(source, NickPermission.NICK_ADMIN_LOOKUP)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_LOOKUP));
        }
        if (adapter.hasPermission(source, NickPermission.NICK_RELOAD)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_RELOAD));
        }

        return help;
    }

    @NotNull
    private static Component line(@NotNull LocaleMessage msg) {
        return SimpleNicksCore.get().miniMessage().deserialize(msg.getMessage());
    }
}
