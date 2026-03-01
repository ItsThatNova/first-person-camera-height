package dev.itsthatnova.fpcameraheight.mixin;

import dev.itsthatnova.fpcameraheight.config.FPCameraHeightConfig;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraOffsetMixin {

    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Inject(
        method = "update",
        at = @At("TAIL")
    )
    private void fpcameraheight$applyHeightOffset(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        // Only apply in first person
        if (thirdPerson) return;

        // Only apply to players
        if (!(focusedEntity instanceof PlayerEntity player)) return;

        // Skip when mounted
        if (player.hasVehicle()) return;

        // Skip if mod is disabled or offset is zero
        if (!FPCameraHeightConfig.isEnabled()) return;
        float offset = FPCameraHeightConfig.getOffset();
        if (offset == 0.0f) return;

        // Get current camera position and shift Y by offset
        Camera camera = (Camera) (Object) this;
        double newX = camera.getPos().x;
        double newY = camera.getPos().y + offset;
        double newZ = camera.getPos().z;
        setPos(newX, newY, newZ);
    }
}
