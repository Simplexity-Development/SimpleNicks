package simplexity.simplenicks.commands.subcommands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicks;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.arguments.OfflinePlayerArgument;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.commands.subcommands.basic.SubCommand;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.config.MessageUtils;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AdminLookupSubCommand implements SubCommand {

    private final MiniMessage miniMessage = SimpleNicks.getMiniMessage();


    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        OfflinePlayerArgument offlinePlayerArg = new OfflinePlayerArgument();
        parent.then(Commands.literal("lookup")
                .requires(this::canExecute)
                .then(Commands.argument("player", offlinePlayerArg)
                        .suggests(offlinePlayerArg::suggestOnlinePlayers)
                        .executes(this::execute)));

    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        OfflinePlayer lookupTarget = ctx.getArgument("player", OfflinePlayer.class);
        String username = lookupTarget.getName();
        if (username == null) throw Exceptions.INVALID_PLAYER_SPECIFIED.create(lookupTarget);
        Bukkit.getScheduler().runTaskAsynchronously(SimpleNicks.getInstance(), () -> {
            Nickname currentNickname = NicknameProcessor.getInstance().getCurrentNickname(lookupTarget);
            List<Nickname> savedNicknames = NicknameProcessor.getInstance().getSavedNicknames(lookupTarget);
            sender.sendMessage(lookupInfoComponent(username, currentNickname, savedNicknames));
        });
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack css) {
        CommandSender sender = css.getSender();
        return sender.hasPermission(NickPermission.NICK_ADMIN_LOOKUP.getPermission());
    }

    @NotNull
    public Component lookupInfoComponent(@NotNull String username, @Nullable Nickname currentNick, @Nullable List<Nickname> savedNames) {
        String nickname;
        if (currentNick == null) {
            if (savedNames == null || savedNames.isEmpty()) return miniMessage.deserialize(LocaleMessage.ERROR_NO_PLAYERS_WITH_THIS_NAME.getMessage());
            nickname = LocaleMessage.INSERT_NONE.getMessage();
        } else {
            nickname = currentNick.getNickname();
        }
        String infoString = LocaleMessage.LOOKUP_HEADER.getMessage() +
                                   LocaleMessage.LOOKUP_CURRENT.getMessage() +
                                   LocaleMessage.LOOKUP_SAVED.getMessage();
        return miniMessage.deserialize(infoString,
                Placeholder.unparsed("username", username),
                Placeholder.parsed("name", nickname),
                MessageUtils.savedNickListResolver(savedNames));
    }
}
