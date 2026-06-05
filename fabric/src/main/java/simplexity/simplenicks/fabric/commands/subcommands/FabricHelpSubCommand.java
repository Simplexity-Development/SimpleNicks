package simplexity.simplenicks.fabric.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.util.NickPermission;

public class FabricHelpSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("help").requires(this::canExecute)
                .executes(this::execute)
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) {
        sendToSource(ctx.getSource(), buildHelpMessage(ctx.getSource()));
        return Command.SINGLE_SUCCESS;
    }

    @NotNull
    private Component buildHelpMessage(@NotNull CommandSourceStack source) {
        ConfigHandler config = ConfigHandler.getInstance();
        Component help = line(LocaleMessage.HELP_HEADER);

        ServerPlayer player = source.getPlayer();
        if (player != null) {
            boolean permNotRequired = !config.isNickRequiresPermission();

            if (permNotRequired || FabricPermissions.check(source, NickPermission.NICK_SET)) {
                help = help.appendNewline().append(line(LocaleMessage.HELP_SET));
                help = help.appendNewline().append(line(LocaleMessage.HELP_RESET));
            }

            if (permNotRequired || FabricPermissions.check(source, NickPermission.NICK_SAVE)) {
                help = help.appendNewline().append(line(LocaleMessage.HELP_SAVE));
                help = help.appendNewline().append(line(LocaleMessage.HELP_DELETE));
            }
        }

        if (!config.isWhoRequiresPermission()
                || FabricPermissions.check(source, NickPermission.NICK_WHO)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_WHO));
        }

        if (FabricPermissions.check(source, NickPermission.NICK_ADMIN_SET)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_SET));
        }
        if (FabricPermissions.check(source, NickPermission.NICK_ADMIN_RESET)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_RESET));
        }
        if (FabricPermissions.check(source, NickPermission.NICK_ADMIN_DELETE)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_DELETE));
        }
        if (FabricPermissions.check(source, NickPermission.NICK_ADMIN_LOOKUP)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_ADMIN_LOOKUP));
        }
        if (FabricPermissions.check(source, NickPermission.NICK_RELOAD)) {
            help = help.appendNewline().append(line(LocaleMessage.HELP_RELOAD));
        }

        return help;
    }

    @NotNull
    private static Component line(@NotNull LocaleMessage msg) {
        return SimpleNicksCore.get().miniMessage().deserialize(msg.getMessage());
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        return FabricPermissions.check(source, NickPermission.NICK_HELP);
    }
}
