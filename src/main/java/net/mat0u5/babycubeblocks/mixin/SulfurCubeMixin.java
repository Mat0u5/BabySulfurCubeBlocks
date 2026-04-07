package net.mat0u5.babycubeblocks.mixin;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin extends AgeableMob {

	// Extending AgeableMob gives us access to protected methods/fields like this.age and this.usePlayerItem()
	protected SulfurCubeMixin(EntityType<? extends AgeableMob> entityType, Level level) {
		super(entityType, level);
	}

	/**
	 * 1. Manually run the baby feeding logic BEFORE the vanilla code executes.
	 */
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

	/**
	 * 2. Redirect the isBaby() check inside mobInteract to ALWAYS return false.
	 * Since we handled feeding above, this forces the method to fall through to
	 * the adult logic (Shearing, Swallowing, Bucketing) for everything else.
	 */
	@Redirect(
			method = "mobInteract",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;isBaby()Z")
	)
	private boolean bypassBabyCheckForInteract(SulfurCube instance) {
		return false;
	}

	/**
	 * 3. Allow babies to accept items into their body slot.
	 */
	@Inject(method = "canHoldItem", at = @At("HEAD"), cancellable = true)
	private void allowBabyToHoldItem(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (this.isBaby()) {
			ItemStack heldItemStack = this.getItemBySlot(EquipmentSlot.BODY);
			cir.setReturnValue(heldItemStack.isEmpty() && itemStack.is(ItemTags.SULFUR_CUBE_SWALLOWABLE));
		}
	}

	/**
	 * 4. Allow babies to be tempted by swallowable items.
	 */
	@Inject(method = "lambda$addBehaviourGoals$0", at = @At("HEAD"), cancellable = true)
	private void modifyTemptPredicate(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (itemStack.is(ItemTags.SULFUR_CUBE_SWALLOWABLE)) {
			cir.setReturnValue(true);
		}
	}
}