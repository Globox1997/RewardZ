package net.rewardz.init;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.data.RewardsDataLoader;
import net.rewardz.network.RewardsServerPacket;

public class LoaderInit {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RewardsDataLoader());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            RewardsServerPacket.writeS2CSyncRewardsPacket(handler.player);
            ((RewardPlayerAccess) handler.player).increaseRewardDay();
            RewardsServerPacket.writeS2CSyncRewardDayCountPacket(handler.player);
        });
    }

}
