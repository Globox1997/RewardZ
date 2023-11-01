# RewardZ
RewardZ is a mod which adds a datapack system to add daily rewards.

### Installation
RewardZ is a mod built for the [Fabric Loader](https://fabricmc.net/). It requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately; all other dependencies are installed with the mod.

### License
RewardZ is licensed under MIT.

### Datapacks
RewardZ does not provide a default datapack, so for functionality you have to write your own.  
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.fandom.com/wiki/Data_Pack) website and try to create your first one for the vanilla game.  
If you know how to create one, the folder path has to be ```data\modid\rewards\YOURFILE.json```.  
For each day of each month you can set the tooltip, item (including nbt), exact day of rewarding and commands (which get executed when the player takes the reward).  
I hope the example is self explanatory. ``month_1`` means january, ``month_2`` means february!  
An example for a recipe can be found below:

```json
{
    "month_1": {
        "day_1": {
            "tooltip": [
                "This is an example tooltip",
                "Ouh this is another line for the example tooltip"
            ],
            "item": {
                "item": "minecraft:apple",
                "count": 5
            },
            "commands": [
                "give @s minecraft:stick"
            ],
            "exact": false
        },
        "day_2": {
            "tooltip": [
                "This is day 2",
                "whut whut",
                "okeee"
            ],
            "item": {
                "item": "minecraft:diamond_sword",
                "count": 3,
                "nbt": "{Damage:0}"
            },
            "commands": [
                "give @s minecraft:apple"
            ],
            "exact": false
        }
    }
}
```

### Commands
`/rewards add playername integer`
- Add reward days of the player

`/rewards remove playername integer`
- Remove reward days of the player

`/rewards set playername integer`
- Set reward days of the player

`/rewards reset playername integer`
- Reset reward days and used reward days oh the player

`/rewards info playername`
- Print the reward days and used reward days of the player
  