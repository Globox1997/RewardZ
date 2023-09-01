package net.rewardz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rewardz.init.KeyInit;
import net.rewardz.init.RenderInit;
import net.rewardz.packet.RewardsClientPacket;

@Environment(EnvType.CLIENT)
public class RewardzClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RenderInit.init();
        RewardsClientPacket.init();
        KeyInit.init();
    }

}
