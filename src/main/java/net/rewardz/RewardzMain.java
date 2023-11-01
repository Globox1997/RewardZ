package net.rewardz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.ModInitializer;
import net.rewardz.init.CommandInit;
import net.rewardz.init.ConfigInit;
import net.rewardz.init.LoaderInit;
import net.rewardz.init.ScreenInit;
import net.rewardz.packet.RewardsServerPacket;

public class RewardzMain implements ModInitializer {

    public static final Map<Integer, Map<Integer, List<Object>>> REWARD_MAP = new HashMap<Integer, Map<Integer, List<Object>>>();

    @Override
    public void onInitialize() {
        ConfigInit.init();
        LoaderInit.init();
        ScreenInit.init();
        CommandInit.init();
        RewardsServerPacket.init();
    }

}
