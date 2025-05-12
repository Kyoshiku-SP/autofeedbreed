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
import java.util.List;

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
        // Prevent baby animals from interacting with crops
        if (animal.isBaby()) return false; // Prevent baby animals from eating crops

        if (!(animal instanceof AnimalFeedBreedState state)) return false;

        long now = System.currentTimeMillis();

        if (now < state.getCooldownEnd()) return false;

        // Reset state after cooldown
        // Reset per-animal custom state after cooldown ends
        state.setHasEaten(false);
        state.setHasBred(false);

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
        // Replace the fully grown crop with a replanted one
        world.setBlockState(targetPos, cropBlock.getDefaultState(), 3);
        // Set this animal into love mode
        // Put this animal into love mode
        animal.setLoveTicks(600);

        // Try to find a mate
        if (animal instanceof AnimalFeedBreedState state) {
            // Also put nearby animals into love mode if ready
            List<AnimalEntity> potentialMates = world.getEntitiesByClass(
                    AnimalEntity.class,
                    animal.getBoundingBox().expand(8.0),
                    other -> other != animal &&
                            other.getClass() == animal.getClass()
            );

            for (AnimalEntity other : potentialMates) {
                if (other instanceof AnimalFeedBreedState state2 &&
                        System.currentTimeMillis() >= state2.getCooldownEnd()) {
                    // Put mates into love mode if they're ready
                    other.setLoveTicks(600);
                }
            }

            // Try to breed
            List<AnimalEntity> mates = world.getEntitiesByClass(
                    AnimalEntity.class,
                    animal.getBoundingBox().expand(8.0),
                    other -> other != animal &&
                            other.getClass() == animal.getClass() &&
                            other.isInLove()
            );

            if (!mates.isEmpty()) {
                AnimalEntity mate = mates.get(0);

                AnimalEntity baby = (AnimalEntity) animal.createChild((ServerWorld) world, mate);
                if (baby != null) {
                    baby.setBaby(true);
                    baby.refreshPositionAndAngles(animal.getX(), animal.getY(), animal.getZ(), 0.0F, 0.0F);
                    world.spawnEntity(baby);
                }

                Identifier entityId = Registries.ENTITY_TYPE.getId(animal.getType());
                int breedCooldown = AutoFeedBreed.CONFIG.breedCooldowns.getOrDefault(
                        entityId.toString(),
                        AutoFeedBreed.CONFIG.defaultBreedCooldown
                );
                // Apply per-animal cooldown after successful breeding
                animal.setBreedingAge(breedCooldown);
                mate.setBreedingAge(breedCooldown);
                animal.resetLoveTicks();
                mate.resetLoveTicks();

                state.setHasBred(true);
                // Reset per-animal custom state after cooldown ends
                state.setHasEaten(false);
            } else {
                state.setHasBred(false);
            }
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
