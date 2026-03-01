package dev.itsthatnova.fpcameraheight.mixin;

import dev.itsthatnova.fpcameraheight.server.PlayerOffsetStore;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ServerEyePosOffsetMixin {

    @Inject(
        method = "getEyePos",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fpcameraheight$serverOffsetEyePos(CallbackInfoReturnable<Vec3d> cir) {
        if (!((Object) this instanceof ServerPlayerEntity player)) return;
        float offset = PlayerOffsetStore.getOffset(player.getUuid());
        if (offset == 0.0f) return;
        if (player.hasVehicle()) return;
        Vec3d original = cir.getReturnValue();
        cir.setReturnValue(new Vec3d(original.x, original.y + offset, original.z));
    }

    @Inject(
        method = "getEyeY",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fpcameraheight$serverOffsetEyeY(CallbackInfoReturnable<Double> cir) {
        if (!((Object) this instanceof ServerPlayerEntity player)) return;
        float offset = PlayerOffsetStore.getOffset(player.getUuid());
        if (offset == 0.0f) return;
        if (player.hasVehicle()) return;
        cir.setReturnValue(cir.getReturnValue() + offset);
    }
}
