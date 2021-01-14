# blockstages

## Configuration

### JSON
`.minecraft/config/stages/block.json`
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

### KubeJS
`.minecraft/kubejs/server_scripts/<script_name>.js`
```js
onEvent("blockstages", event => {
    event.add("stage_name", "minecraft:diamond_ore", "minecraft:stone");
    event.add("namespace:other_stage", "#minecraft:logs", "minecraft:dirt");
    event.remove("something_else");
});
```
