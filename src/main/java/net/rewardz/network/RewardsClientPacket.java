package net.rewardz.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.rewardz.RewardzMain;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.network.packet.RewardSyncDayCountPacket;
import net.rewardz.network.packet.RewardSyncRewardsPacket;

@Environment(EnvType.CLIENT)
public class RewardsClientPacket {

    @SuppressWarnings("resource")
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(RewardSyncRewardsPacket.PACKET_ID, (payload, context) -> {

            Map<Integer, Map<Integer, List<Object>>> rewardMap = new HashMap<Integer, Map<Integer, List<Object>>>();
            for (int i = 0; i < payload.rewardMonthDatas().rewardMonthDatas().size(); i++) {
                HashMap<Integer, List<Object>> monthMap = new HashMap<Integer, List<Object>>();

                int month = payload.rewardMonthDatas().rewardMonthDatas().get(i).month();
                int days = payload.rewardMonthDatas().rewardMonthDatas().get(i).days();
                int count = 0;
                for (int u = 0; u < days; u++) {
                    int day = payload.rewardMonthDatas().rewardMonthDatas().get(i).dayList().get(u);

                    List<Object> dayList = new ArrayList<Object>();
                    dayList.add(payload.rewardMonthDatas().rewardMonthDatas().get(i).exactDayList().get(u));
                    dayList.add(payload.rewardMonthDatas().rewardMonthDatas().get(i).itemStackList().get(u));
                    int tooltipSize = payload.rewardMonthDatas().rewardMonthDatas().get(i).tooltipSizeList().get(u);
                    dayList.add(tooltipSize);

                    for (int k = 0; k < tooltipSize; k++) {
                        dayList.add(payload.rewardMonthDatas().rewardMonthDatas().get(i).tooltipList().get(count));
                        count++;
                    }
                    monthMap.put(day, dayList);
                }

                rewardMap.put(month, monthMap);
            }
            context.client().execute(() -> {
                RewardzMain.REWARD_MAP.clear();
                rewardMap.forEach((month, map) -> {
                    RewardzMain.REWARD_MAP.put(month, map);
                });
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RewardSyncDayCountPacket.PACKET_ID, (payload, context) -> {
            int rewardDayCount = payload.rewardDayCount();
            Set<Integer> usedRewardDaySet = new HashSet<Integer>();
            usedRewardDaySet.addAll(payload.usedRewardDays());

            context.client().execute(() -> {
                ((RewardPlayerAccess) context.client().player).setRewardDayCount(rewardDayCount);
                ((RewardPlayerAccess) context.client().player).setUsedRewardDays(usedRewardDaySet);
            });
        });
    }

}
