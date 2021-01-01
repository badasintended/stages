# stages-base

### Commands

#### Player selector
```mcfunction
/give @a[stages={test:a, !test:b}] minecraft:enchanted_golden_apple 64
                  |       |
                  |       |---> must NOT have this stage
                  |
                  |---> must have this stage
```

#### Player predicate
[Predicate on Minecraft Wiki](https://minecraft.gamepedia.com/Predicate)
```json5
// ...
"player": {
  "stages": {
    "test:a": true, // player must have this stage
    "test:b": false // player must NOT have this stage
  }
}
```
