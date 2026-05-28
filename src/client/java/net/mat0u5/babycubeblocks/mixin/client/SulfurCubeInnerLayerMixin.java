package net.mat0u5.babycubeblocks.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.SulfurCubeInnerLayer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SulfurCubeInnerLayer.class)
public class SulfurCubeInnerLayerMixin {

	@Inject(method = "submit", at = @At("HEAD"))
	private void scaleBabyBlockPre(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, SulfurCubeRenderState state, float yRot, float xRot, CallbackInfo ci) {
		if (state.isBaby && !state.containedBlock.isEmpty()) {
			poseStack.pushPose();
			poseStack.scale(0.5F, 0.5F, 0.5F);
		}
	}

	@Inject(method = "submit",at = @At("RETURN"))
	private void scaleBabyBlockPost(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, SulfurCubeRenderState state, float yRot, float xRot, CallbackInfo ci) {
		if (state.isBaby && !state.containedBlock.isEmpty()) {
			poseStack.popPose();
		}
	}
}