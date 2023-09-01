package net.rewardz.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.rewardz.screen.RewardsScreenHandler;

public class ScreenInit {

    public static ScreenHandlerType<RewardsScreenHandler> REWARDS_SCREEN_HANDLER_TYPE;

    public static void init() {
        REWARDS_SCREEN_HANDLER_TYPE = Registry.register(Registries.SCREEN_HANDLER, "rewardz:rewards",
                new ScreenHandlerType<>((syncId, inventory) -> new RewardsScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY), FeatureFlags.VANILLA_FEATURES));
    }

}
