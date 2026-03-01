package dev.itsthatnova.fpcameraheight.mixin;

import dev.itsthatnova.fpcameraheight.server.PlayerOffsetStore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ServerEyeYOffsetMixin {

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
