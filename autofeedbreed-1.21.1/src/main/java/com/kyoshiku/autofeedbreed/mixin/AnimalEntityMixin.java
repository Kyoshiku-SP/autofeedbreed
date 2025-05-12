package com.kyoshiku.autofeedbreed.mixin;

import com.kyoshiku.autofeedbreed.AutoBreedGoalRegistry;
import com.kyoshiku.autofeedbreed.state.AnimalFeedBreedState;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
// Mixin that injects goal registration into all AnimalEntity types
public class AnimalEntityMixin implements AnimalFeedBreedState {
    // Tracks whether this animal has eaten a crop yet
    @Unique
    private boolean autofeed_hasEaten = false;

    // Tracks whether this animal has bred since last eating
    @Unique
    private boolean autofeed_hasBred = false;

    // Timestamp when this animal can next eat/breed again
    @Unique
    private long autofeed_cooldownEnd = 0;

    // Injects after AnimalEntity constructor to assign the EatReplantBreedGoal
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructed(CallbackInfo ci) {
        // Delegate goal assignment to centralized registry class
        AutoBreedGoalRegistry.tryRegisterGoal(this);
    }

    @Override
    public boolean hasEaten() {
        return autofeed_hasEaten;
    }

    @Override
    public void setHasEaten(boolean value) {
        autofeed_hasEaten = value;
    }

    @Override
    public boolean hasBred() {
        return autofeed_hasBred;
    }

    @Override
    public void setHasBred(boolean value) {
        autofeed_hasBred = value;
    }

    @Override
    public long getCooldownEnd() {
        return autofeed_cooldownEnd;
    }

    @Override
    public void setCooldownEnd(long time) {
        autofeed_cooldownEnd = time;
    }
}
