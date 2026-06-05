package simplexity.simplenicks.fabric.commands.subcommands.admin;

import net.minecraft.server.players.NameAndId;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import simplexity.simplenicks.fabric.util.FabricPermissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.commands.NicknameProcessor;
import simplexity.simplenicks.commands.subcommands.Exceptions;
import simplexity.simplenicks.config.LocaleMessage;
import simplexity.simplenicks.config.MessageUtils;
import simplexity.simplenicks.fabric.commands.subcommands.FabricSubCommand;
import simplexity.simplenicks.saving.Nickname;
import simplexity.simplenicks.util.NickPermission;

import java.util.Collection;
import java.util.List;

public class FabricAdminLookupSubCommand implements FabricSubCommand {

    @Override
    public void subcommandTo(@NotNull LiteralArgumentBuilder<CommandSourceStack> parent) {
        parent.then(Commands.literal("lookup").requires(this::canExecute)
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .suggests((ctx, builder) -> {
                            ctx.getSource().getServer().getPlayerList().getPlayers()
                                    .forEach(p -> builder.suggest(p.getGameProfile().name()));
                            return builder.buildFuture();
                        })
                        .executes(this::execute)
                )
        );
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<NameAndId> profiles = GameProfileArgument.getGameProfiles(ctx, "player");
        NameAndId target = profiles.stream().findFirst().orElseThrow(
                () -> Exceptions.invalidPlayerSpecified(null));
        CommandSourceStack source = ctx.getSource();
        boolean isOnline = source.getServer().getPlayerList().getPlayer(target.id()) != null;
        SimpleNicksCore.get().platform().runAsync(() -> {
            Nickname current = NicknameProcessor.getInstance().getCurrentNickname(target.id(), isOnline);
            List<Nickname> saved = NicknameProcessor.getInstance().getSavedNicknames(target.id(), isOnline);
            sendToSource(source, buildLookupMessage(target.name(), current, saved));
        });
        return Command.SINGLE_SUCCESS;
    }

    @NotNull
    private Component buildLookupMessage(@NotNull String username,
                                          @Nullable Nickname current,
                                          @Nullable List<Nickname> saved) {
        String nickname;
        if (current == null) {
            if (saved == null || saved.isEmpty()) {
                return SimpleNicksCore.get().miniMessage()
                        .deserialize(LocaleMessage.ERROR_NO_PLAYERS_WITH_THIS_NAME.getMessage());
            }
            nickname = LocaleMessage.INSERT_NONE.getMessage();
        } else {
            nickname = current.getNickname();
        }
        String template = LocaleMessage.LOOKUP_HEADER.getMessage()
                + LocaleMessage.LOOKUP_CURRENT.getMessage()
                + LocaleMessage.LOOKUP_SAVED.getMessage();
        return SimpleNicksCore.get().miniMessage().deserialize(template,
                Placeholder.unparsed("username", username),
                Placeholder.parsed("name", nickname),
                MessageUtils.savedNickListResolver(saved));
    }

    @Override
    public boolean canExecute(@NotNull CommandSourceStack source) {
        return FabricPermissions.check(source, NickPermission.NICK_ADMIN_LOOKUP);
    }
}
