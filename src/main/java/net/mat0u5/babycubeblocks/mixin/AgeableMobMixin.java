package net.mat0u5.babycubeblocks.mixin;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AgeableMob.class, priority = 1)
public class AgeableMobMixin {

    @Inject(method = "isAgeLocked", at = @At("HEAD"), cancellable = true)
    public void isAgeLocked(CallbackInfoReturnable<Boolean> cir) {
        AgeableMob self = (AgeableMob) (Object) this;
        if (!(self instanceof SulfurCube cube)) return;
        if (!cube.isBaby()) return;
        if (!cube.hasBodyItem()) return;
        cir.setReturnValue(true);
    }
}
