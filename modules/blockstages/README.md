# blockstages

## Configuration

### Entries
```json5
{
  "entries": {
    "stage_name": {                      // will be parsed as 'block:stage_name'
      "target": "minecraft:diamond_ore", // target block that will be replaced
      "as"    : "minecraft:stone"        // the replacement block
    },
    "namespace:other_stage": {
      "target": "#minecraft:logs",       // also accepts block tag with '#' prefix
      "as"    : "minecraft:dirt"
    } 
  }
}
```
