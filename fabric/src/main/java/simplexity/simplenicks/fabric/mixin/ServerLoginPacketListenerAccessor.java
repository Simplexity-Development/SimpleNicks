package simplexity.simplenicks.fabric.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLoginPacketListenerImpl.class)
public interface ServerLoginPacketListenerAccessor {

    @Accessor("authenticatedProfile")
    @Nullable GameProfile getAuthenticatedProfile();
}
