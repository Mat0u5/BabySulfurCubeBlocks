package net.mat0u5.babycubeblocks.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin extends AgeableMob {

	protected SulfurCubeMixin(EntityType<? extends AgeableMob> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
	private void manualBabyFoodInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (this.isBaby()) {
			// Reproduce the exact vanilla baby feeding logic
			if (heldItem.is(ItemTags.SULFUR_CUBE_FOOD) && this.canAgeUp()) {
				this.usePlayerItem(player, hand, heldItem);
				this.ageUp(getSpeedUpSecondsWhenFeeding(-this.age), true);
				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}

	@Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;isBaby()Z"))
	private boolean bypassBabyCheckForInteract(SulfurCube instance) {
		return false;
	}

	@Inject(method = "canHoldItem", at = @At("HEAD"), cancellable = true)
	private void allowBabyToHoldItem(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (this.isBaby()) {
			ItemStack heldItemStack = this.getItemBySlot(EquipmentSlot.BODY);
			cir.setReturnValue(heldItemStack.isEmpty() && itemStack.is(ItemTags.SULFUR_CUBE_SWALLOWABLE));
		}
	}

	@Inject(method = "lambda$addBehaviourGoals$0", at = @At("HEAD"), cancellable = true)
	private void modifyTemptPredicate(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (itemStack.is(ItemTags.SULFUR_CUBE_SWALLOWABLE)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "saveToBucketTag", at = @At("RETURN"))
	private void extraBucketData(ItemStack bucket, CallbackInfo ci) {
		SulfurCube self = (SulfurCube) (Object) this;
		CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
			tag.putInt("Size", self.getSize());
		});
	}

	@Inject(method = "loadFromBucketTag", at = @At("RETURN"))
	private void applySizeOnLoad(CompoundTag tag, CallbackInfo ci) {
		SulfurCube self = (SulfurCube) (Object) this;
		self.setSize(tag.getIntOr("Size", 1), true);
	}
}