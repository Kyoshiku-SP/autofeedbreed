package com.kyoshiku.autofeedbreed;

import com.kyoshiku.autofeedbreed.goal.EatReplantBreedGoal;
import com.kyoshiku.autofeedbreed.mixin.MobEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

// Central goal injector that applies EatReplantBreedGoal to animals based on config
public class AutoBreedGoalRegistry {
    // Called from mixin constructor to attach crop-eating goal
    public static void tryRegisterGoal(Object self) {
        if (!(self instanceof AnimalEntity animal)) return;

        // Identify the entity to match config
        Identifier entityId = Registries.ENTITY_TYPE.getId(animal.getType());
        // Look up crop associated with this animal
        String cropId = AutoFeedBreed.CONFIG.animalCropMap.get(entityId.toString());

        if (cropId != null) {
            // Load block from registry based on string ID
            Block block = Registries.BLOCK.get(Identifier.of(cropId));
            if (block instanceof CropBlock crop) {
                // Debug message only
//                System.out.println("[AutoFeedBreed] Injected goal for " + entityId + " with crop: " + cropId);
                // Inject the EatReplantBreedGoal into the entity's goal selector
                ((MobEntityAccessor) self).getGoalSelector()
                        .add(1, new EatReplantBreedGoal(animal, crop));
            } else {
//                System.out.println("[AutoFeedBreed] Warning: Block " + cropId + " is not a CropBlock.");
            }
        } else {
//            System.out.println("[AutoFeedBreed] No crop configured for entity: " + entityId);
        }
    }
}
