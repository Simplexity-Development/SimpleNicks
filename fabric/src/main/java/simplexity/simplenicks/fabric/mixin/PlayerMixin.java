package simplexity.simplenicks.fabric.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import simplexity.simplenicks.fabric.storage.FabricNicknameStorage;

/**
 * Intercepts {@link Player#getDisplayName()} so that SimpleNicks nicknames appear
 * in chat-formatted messages. Only modifies the return value for {@link ServerPlayer}
 * instances; client-side players are unaffected.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "getDisplayName()Lnet/minecraft/network/chat/Component;",
            at = @At("HEAD"), cancellable = true)
    private void simplenicks$getDisplayName(CallbackInfoReturnable<Component> cir) {
        if (!((Object) this instanceof ServerPlayer sp)) return;
        Component stored = FabricNicknameStorage.getDisplayName(sp.getUUID());
        if (stored != null) {
            cir.setReturnValue(stored);
        }
    }
}
