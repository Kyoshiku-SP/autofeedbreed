package com.kyoshiku.autofeedbreed.mixin;

// Mixin target
import com.kyoshiku.autofeedbreed.MobEntityGoalAccess;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;

// Mixin annotations
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements MobEntityGoalAccess {
    @Shadow @Final
    protected GoalSelector goalSelector;

    @Override
    public GoalSelector getGoalSelector() {
        return this.goalSelector;
    }
}
