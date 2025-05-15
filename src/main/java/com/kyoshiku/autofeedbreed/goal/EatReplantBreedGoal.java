package com.kyoshiku.autofeedbreed.goal;

import com.kyoshiku.autofeedbreed.state.AnimalFeedBreedState;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import com.kyoshiku.autofeedbreed.AutoFeedBreed;
import net.minecraft.world.World;

import java.util.EnumSet;

public class EatReplantBreedGoal extends Goal {
    private final AnimalEntity animal;
    private final CropBlock cropBlock;
    private BlockPos targetPos;

    public EatReplantBreedGoal(AnimalEntity animal, CropBlock cropBlock) {
        this.animal = animal;
        this.cropBlock = cropBlock;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    // Determines whether the goal can start
    // Only adults, off cooldown, and if a mature crop is nearby
    public boolean canStart() {
        if (animal.isBaby()) return false;
        if (!(animal instanceof AnimalFeedBreedState state)) return false;

        long now = System.currentTimeMillis();

        // Reset breed/eat state if breeding cooldown is over
        if (state.hasBred() && now >= state.getBreedingCooldownEnd()) {
            state.setHasBred(false);
            state.setHasEaten(false);
        }

        // Still on eating cooldown
        if (now < state.getEatingCooldownEnd()) return false;

        // Already ate, waiting for partner
        if (state.hasEaten()) return false;

        // Search for crop
        BlockPos center = animal.getBlockPos();
        for (BlockPos pos : BlockPos.iterate(center.add(-4, -1, -4), center.add(4, 1, 4))) {
            BlockState cropState = animal.getWorld().getBlockState(pos);
            if (cropState.getBlock() == cropBlock && cropState.get(CropBlock.AGE) == cropBlock.getMaxAge()) {
                targetPos = pos;
                return true;
            }
        }

        return false;
    }

    @Override
    // Begins pathfinding toward the target crop block
    public void start() {
        if (targetPos != null) {
            animal.getNavigation().startMovingTo(
                    targetPos.getX() + 0.5,
                    targetPos.getY(),
                    targetPos.getZ() + 0.5,
                    1.0
            );
        }
    }

    @Override
    // Continue pathing as long as they are not close enough to the target
    public boolean shouldContinue() {
        return targetPos != null &&
                animal.getNavigation().isFollowingPath() &&
                animal.squaredDistanceTo(
                        targetPos.getX() + 0.5,
                        targetPos.getY(),
                        targetPos.getZ() + 0.5) > 2.25;
    }

    @Override
    // The crop eating, replanting, and breeding logic each tick
    public void tick() {
        if (targetPos == null) return;
        if (animal.squaredDistanceTo(
                targetPos.getX() + 0.5,
                targetPos.getY(),
                targetPos.getZ() + 0.5) > 2.25) return;

        animal.getLookControl().lookAt(
                targetPos.getX() + 0.5,
                targetPos.getY(),
                targetPos.getZ() + 0.5
        );

        World world = animal.getWorld();

        // Safely get custom animal state
        if (!(animal instanceof AnimalFeedBreedState state)) return;

        // Replace the fully grown crop with a replanted one
        world.setBlockState(targetPos, cropBlock.getDefaultState(), 3);

        // Mark this animal has eaten
        state.setHasEaten(true);

        long now = System.currentTimeMillis();

        // Apply eating cooldown after eating
        state.setEatingCooldownEnd(now + AutoFeedBreed.CONFIG.cropEatCooldown * 1000L);

        // Only enter love mode and breed if eligible
        if (!state.hasBred() && now >= state.getBreedingCooldownEnd()) {
            // Set this animal into love mode
            animal.setLoveTicks(AutoFeedBreed.CONFIG.loveDuration);

            // Breed by self (automatically create baby)
            AnimalEntity mate = animal; // simulate a mate
            AnimalEntity baby = (AnimalEntity) animal.createChild((ServerWorld) world, mate);
            if (baby != null) {
                baby.setBreedingAge(-24000); // Ensure baby doesn't instantly grow
                baby.refreshPositionAndAngles(animal.getX(), animal.getY(), animal.getZ(), 0.0F, 0.0F);
                world.spawnEntity(baby);
            }

            // Apply vanilla Minecraft breeding age cooldown
            Identifier entityId = Registries.ENTITY_TYPE.getId(animal.getType());
            int breedCooldown = AutoFeedBreed.CONFIG.breedCooldowns.getOrDefault(
                    entityId.toString(),
                    AutoFeedBreed.CONFIG.defaultBreedCooldown
            );
            animal.setBreedingAge(breedCooldown);
            animal.resetLoveTicks();

            // Reset state
            state.setHasBred(true);
            state.setBreedingCooldownEnd(now + AutoFeedBreed.CONFIG.breedCooldown * 1000L);
            state.setHasEaten(false);
        }

        targetPos = null;
        animal.getNavigation().stop();
    }

    @Override
    public void stop() {
        targetPos = null;
        animal.getNavigation().stop();
    }
}
