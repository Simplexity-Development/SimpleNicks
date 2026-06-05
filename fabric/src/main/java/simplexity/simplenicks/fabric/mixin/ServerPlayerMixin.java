package simplexity.simplenicks.fabric.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import simplexity.simplenicks.fabric.storage.FabricNicknameStorage;

/**
 * Intercepts {@link ServerPlayer#getTabListDisplayName()} so that SimpleNicks
 * nicknames appear in the tab list without manual packet construction.
 * <p>
 * Chat display name is handled in {@link PlayerMixin} since {@code getDisplayName()}
 * is defined on {@link net.minecraft.world.entity.player.Player}, not {@link ServerPlayer}.
 * </p>
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "getTabListDisplayName()Lnet/minecraft/network/chat/Component;",
            at = @At("HEAD"), cancellable = true)
    private void simplenicks$getTabListDisplayName(CallbackInfoReturnable<@Nullable Component> cir) {
        Component stored = FabricNicknameStorage.getTabName(((ServerPlayer) (Object) this).getUUID());
        if (stored != null) {
            cir.setReturnValue(stored);
        }
    }
}
