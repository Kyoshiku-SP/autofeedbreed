package com.kyoshiku.autofeedbreed.mixin;

import org.spongepowered.asm.mixin.Mixin;                       // Mixin annotation
import org.spongepowered.asm.mixin.gen.Accessor;                // @Accessor
import net.minecraft.entity.mob.MobEntity;                     // Target class
import net.minecraft.entity.ai.goal.GoalSelector;              // Field we need

@Mixin(MobEntity.class)
public interface MobEntityAccessor {
    @Accessor(value = "goalSelector", remap = false)
    GoalSelector getGoalSelector();
}
