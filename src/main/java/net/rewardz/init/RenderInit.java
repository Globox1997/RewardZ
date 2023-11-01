package net.rewardz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;
import net.rewardz.screen.RewardsScreen;
import net.rewardz.screen.RewardsScreenHandler;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final boolean isPatchouliButtonLoaded = FabricLoader.getInstance().isModLoaded("patchoulibutton");

    public static final Identifier REWARD_ICONS = new Identifier("rewardz", "textures/gui/rewards.png");

    public static void init() {
        HandledScreens.<RewardsScreenHandler, RewardsScreen>register(ScreenInit.REWARDS_SCREEN_HANDLER_TYPE, RewardsScreen::new);
    }
}
