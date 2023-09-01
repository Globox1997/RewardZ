package net.rewardz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "rewardz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class RewardzConfig implements ConfigData {

    public int posX = 127;
    public int posY = 61;
    @Comment("Use keybind instead - by default unset")
    public boolean inventoryButton = true;
}
