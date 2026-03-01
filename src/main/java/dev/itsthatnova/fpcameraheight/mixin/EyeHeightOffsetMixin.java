package dev.itsthatnova.fpcameraheight.mixin;

import dev.itsthatnova.fpcameraheight.config.FPCameraHeightConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EyeHeightOffsetMixin {

    @Inject(
        method = "getCameraPosVec",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fpcameraheight$offsetCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (!shouldApplyOffset()) return;
        Vec3d original = cir.getReturnValue();
        cir.setReturnValue(new Vec3d(original.x, original.y + FPCameraHeightConfig.getOffset(), original.z));
    }

    @Inject(
        method = "getClientCameraPosVec",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fpcameraheight$offsetClientCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (!shouldApplyOffset()) return;
        Vec3d original = cir.getReturnValue();
        cir.setReturnValue(new Vec3d(original.x, original.y + FPCameraHeightConfig.getOffset(), original.z));
    }

    private boolean shouldApplyOffset() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isOnThread()) return false;
        if (client.player == null) return false;
        if ((Object) this != client.player) return false;
        if (!((Object) this instanceof PlayerEntity)) return false;
        if (!FPCameraHeightConfig.isEnabled()) return false;
        if (FPCameraHeightConfig.getOffset() == 0.0f) return false;
        if (((PlayerEntity)(Object)this).hasVehicle()) return false;
        return true;
    }
}
