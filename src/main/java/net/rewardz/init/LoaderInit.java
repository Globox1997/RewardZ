package net.rewardz.init;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.rewardz.data.RewardsDataLoader;

public class LoaderInit {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RewardsDataLoader());
    }

}
