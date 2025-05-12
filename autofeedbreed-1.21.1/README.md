# AutoFeedBreed

AutoFeedBreed is a Fabric mod for Minecraft 1.21.1 that automatically handles animal feeding and breeding.

Animals will:
- Eat nearby fully grown crops (e.g., wheat, carrots, potatoes)
- Replant the crop they consumed
- Enter love mode and breed with others
- Prevent babies from eating until they grow
- Repeat this cycle automatically, without player input

## Features

- Works entirely server-side
- Compatible with vanilla and modded animals/crops
- Fully configurable via `autofeedbreed.json`
- Per-animal crop assignment and breeding cooldowns
- Baby animals excluded from eating
- Optional debug logging

## Configuration

Edit the file located at:

```
config/autofeedbreed.json
```

Example:
```json
{
  "cropEatCooldown": 30,
  "loveDuration": 600,
  "animalCropMap": {
    "minecraft:cow": "minecraft:wheat",
    "minecraft:pig": "minecraft:carrots",
    "minecraft:sheep": "minecraft:potatoes"
  },
  "breedCooldowns": {
    "minecraft:cow": 12000,
    "minecraft:sheep": 12000,
    "minecraft:pig": 12000
  }
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