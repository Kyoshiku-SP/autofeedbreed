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
    private long eatingCooldownEnd = 0;
    private long breedingCooldownEnd = 0;
    // Tracks whether this animal has eaten a crop yet
    @Unique
    private boolean afbHasEaten = false;

    // Tracks whether this animal has bred since last eating
    @Unique
    private boolean afbHasBred = false;

    // Timestamp when this animal can next eat/breed again
    @Unique
    private int cooldownTicks = 0;

    // Injects after AnimalEntity constructor to assign the EatReplantBreedGoal
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructed(CallbackInfo ci) {
        // Delegate goal assignment to centralized registry class
        AutoBreedGoalRegistry.tryRegisterGoal(this);
    }

    @Override
    public boolean hasEaten() {
        return afbHasEaten;
    }

    @Override
    public void setHasEaten(boolean value) {
        afbHasEaten = value;
    }

    @Override
    public boolean hasBred() {
        return afbHasBred;
    }

    @Override
    public void setHasBred(boolean value) {
        afbHasBred = value;
    }

    @Override
    public long getEatingCooldownEnd() {
        return eatingCooldownEnd;
    }

    @Override
    public void setEatingCooldownEnd(long time) {
        this.eatingCooldownEnd = time;
    }

    @Override
    public long getBreedingCooldownEnd() {
        return breedingCooldownEnd;
    }

    @Override
    public void setBreedingCooldownEnd(long time) {
        this.breedingCooldownEnd = time;
    }
}
