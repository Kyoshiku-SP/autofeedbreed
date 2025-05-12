package com.kyoshiku.autofeedbreed.config;

import java.util.HashMap;
import java.util.Map;

public class AutoBreedConfig {
    public int cropEatCooldown = 30; // seconds
    public int loveDuration = 600;   // ticks
    public int defaultBreedCooldown = 10000; // fallback

    // Per-animal crop config
    public Map<String, String> animalCropMap = new HashMap<>();

    // Optional per-animal breeding cooldown
    public Map<String, Integer> breedCooldowns = new HashMap<>();

    public AutoBreedConfig() {
        // Per animal and crop
        animalCropMap.put("minecraft:cow", "minecraft:wheat");
        animalCropMap.put("minecraft:pig", "minecraft:carrots");
        animalCropMap.put("minecraft:sheep", "minecraft:potatoes");

        // Per animal breed cooldowns
        breedCooldowns.put("minecraft:cow", 12000);   // 10 min
        breedCooldowns.put("minecraft:sheep", 12000);
        breedCooldowns.put("minecraft:pig", 12000);
    }
}
