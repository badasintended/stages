# stages

## Commands

### Player selector
```
/give @a[stages={test:a, !test:b}] minecraft:enchanted_golden_apple 64
                  |       |
                  |       |---> must NOT have this stage
                  |
                  |---> must have this stage
```


## Datapack

### Player predicate
[Predicate on Minecraft Wiki](https://minecraft.gamepedia.com/Predicate)
```json5
{
  "condition": "minecraft:entity_properties",
  "entity": "this",
  "predicate": {
    "player": {
      "stages": {
        "test:a": true, // player must have this stage
        "test:b": false // player must NOT have this stage
      }
    }
  }
}
```

### Advancement Criterion
[Advancement on Minecraft Wiki](https://minecraft.gamepedia.com/Advancement/JSON_format)
```json5
{
  "criteria": {
    "test": {
      "trigger": "stages:changed",
      "conditions": {
        "stages": {
          "test:a": true, // player must have this stage
          "test:b": false // player must NOT have this stage
        }
      }
    }
  }
}
```

### Advancement Rewards
[Advancement on Minecraft Wiki](https://minecraft.gamepedia.com/Advancement/JSON_format)
```json5
{
  "rewards": {
    "stages": ["test:a", "test:b"]
  }
}
```


## KubeJS

### `stages.changed` Event
Fired after player stage is changed.

```js
onEvent("stages.changed", event => {
    var player = event.getPlayer();   // get stage owner
    var stages = event.getStages();   // get list containing all unlocked stages
    for (i in stages) {
        console.info(stages[i]);
    }
});
```
