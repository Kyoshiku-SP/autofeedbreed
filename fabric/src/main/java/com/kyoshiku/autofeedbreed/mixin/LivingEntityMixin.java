package com.kyoshiku.autofeedbreed.mixin;

import com.kyoshiku.autofeedbreed.state.AnimalFeedBreedState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void autoFeedBreedTick(CallbackInfo ci) {
        if (!((Object) this instanceof AnimalFeedBreedState animal)) return;

        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity.isBaby()) return;

        long now = System.currentTimeMillis();
        if (now >= animal.getEatingCooldownEnd()) {
            animal.setHasEaten(false);
        }
        if (now >= animal.getBreedingCooldownEnd()) {
            animal.setHasBred(false);
        }
    }
}

