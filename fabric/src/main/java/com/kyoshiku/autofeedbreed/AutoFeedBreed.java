package com.kyoshiku.autofeedbreed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kyoshiku.autofeedbreed.config.AutoBreedConfig;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class AutoFeedBreed implements ModInitializer {
	public static final String MOD_ID = "autofeedbreed";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static AutoBreedConfig CONFIG = new AutoBreedConfig();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		loadConfig();
		LOGGER.info("AutoFeedBreed initialized");
	}

	// Load the config file to show the JSON file
	private void loadConfig() {
		File file = new File("config/" + MOD_ID + ".json");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		try {
			if (file.exists()) {
				CONFIG = gson.fromJson(new FileReader(file), AutoBreedConfig.class);
			} else {
				file.getParentFile().mkdirs();
				try (FileWriter writer = new FileWriter(file)) {
					gson.toJson(CONFIG, writer);
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to load config: " + e.getMessage());
		}
	}
}