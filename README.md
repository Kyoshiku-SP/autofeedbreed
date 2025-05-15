# AutoFeedBreed

AutoFeedBreed is a Fabric mod for Minecraft 1.21.1 that automatically handles animal feeding and breeding.

Animals will:
- Eat nearby fully grown crops (wheat, carrots, potatoes, etc.)
- Replant the crop after eating
- Breed with another animal of the same type
- Prevent baby animals from eating crops until they grow up
- Repeat this cycle automatically without player interaction

## Features

- Fully server-side
- Supports both vanilla and modded animals and crops
- Configurable via `autofeedbreed.json`
- Per-animal crop types and breeding cooldowns

## Configuration

Edit the file located at:

```
config/autofeedbreed.json
```

Example:
```json
{
  "cropEatCooldown": 300,
  "loveDuration": 600,
  "breedCooldown": 300,
  "animalCropMap": {
    "minecraft:cow": "minecraft:wheat",
    "minecraft:pig": "minecraft:carrots",
    "minecraft:sheep": "minecraft:potatoes"
  },
  "breedCooldowns": {
    "minecraft:cow": 6000,
    "minecraft:pig": 6000,
    "minecraft:sheep": 6000
  },
  "defaultBreedCooldown": 6000
}
```

## Build Instructions

To build the mod:

```bash
./gradlew build
```

The compiled `.jar` will be located in:

```
build/libs/
```

## License

This project is licensed under [CC0 1.0 Universal (Public Domain Dedication)](https://creativecommons.org/publicdomain/zero/1.0/)

Made by Kyoshiku
