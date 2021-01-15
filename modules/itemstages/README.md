# itemstages

## Configuration
Config file can be located in `.minecraft/config/stages/item.json`

Settings decide how staged items effect player ability

|        Option        |                                                 Description                                                 |
|:--------------------:|:-----------------------------------------------------------------------------------------------------------:|
|  `dropWhenOnCursor`  |                                  Drop item when clicked in inventory screen                                 |
|   `dropWhenOnHand`   |                                      Drop item when selected on hotbar                                      |
|     `changeModel`    |                         Change item model and texture to `itemstages:unknown_item`'s                        |
|     `hideFromRei`    | Hide item from RoughlyEnoughItems search. It won't hide it if it somehow ended on player's boorkmark though |
|     `hideTooltip`    |                                          Hide item name and tooltip                                         |
| `preventToInventory` |  Try to prevent item to be inserted to player inventory, this includes picking thrown item, dispenser, etc. |

### JSON
`.minecraft/config/stages/item.json`
```json5
{
  "settings": {
    "option": true,
    "other": false
  },

  "entries": {
    "stage_name": "minecraft:diamond",
    "namespace:other": "#minecraft:beds",
    "with_nbt": { // untested, probably doesn't work :P
      "target": "minecraft:enchanted_book",
      "nbt": "{StoredEnchantments: [{id: 'minecraft:mending', lvl: 1}]}"
    }
  }
}
```

### KubeJS
`.minecraft/kubejs/server_scripts/<script_name>.js`
```js
onEvent("itemstages", event => {
    event.settings({
        option: true,
        other: false
    });

    event.add("stage_name", "minecraft:diamond");
    event.add("namespace:other", "#minecraft:beds");
    event.add("with_nbt", "minecraft:enchanted_book", {
        StoredEnchantments: [{id: "minecraft:mending", lvl: 1}]
    });

    event.remove("something_else");
});
```
