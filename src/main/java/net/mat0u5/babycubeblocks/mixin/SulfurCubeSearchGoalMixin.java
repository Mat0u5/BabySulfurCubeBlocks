package net.mat0u5.babycubeblocks.mixin;

import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.cubemob.SulfurCube$SulfurCubeSearchForItemsGoal")
public class SulfurCubeSearchGoalMixin {

    @Redirect(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;isBaby()Z"))
    private boolean bypassBabyCheckForSearch(SulfurCube instance) {
        return false;
    }
}