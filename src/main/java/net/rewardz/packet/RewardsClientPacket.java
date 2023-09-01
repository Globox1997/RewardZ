package net.rewardz.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.rewardz.RewardzMain;
import net.rewardz.access.RewardPlayerAccess;

@Environment(EnvType.CLIENT)
public class RewardsClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(RewardsServerPacket.SYNC_REWARDS_PACKET, (client, handler, buf, sender) -> {

            Map<Integer, Map<Integer, List<Object>>> rewardMap = new HashMap<Integer, Map<Integer, List<Object>>>();

            int mapSize = buf.readInt();
            for (int i = 0; i < mapSize; i++) {
                HashMap<Integer, List<Object>> monthMap = new HashMap<Integer, List<Object>>();
                int month = buf.readInt();
                int days = buf.readInt();
                for (int u = 0; u < days; u++) {
                    int day = buf.readInt();

                    List<Object> dayList = new ArrayList<Object>();
                    dayList.add(buf.readBoolean());
                    dayList.add(buf.readItemStack());
                    int tooltipSize = buf.readInt();
                    dayList.add(tooltipSize);
                    for (int k = 0; k < tooltipSize; k++) {
                        dayList.add(buf.readString());
                    }
                    int commandSize = buf.readInt();
                    dayList.add(commandSize);
                    for (int k = 0; k < commandSize; k++) {
                        dayList.add(buf.readString());
                    }
                    monthMap.put(day, dayList);
                }
                rewardMap.put(month, monthMap);
            }

            client.execute(() -> {
                RewardzMain.REWARD_MAP.clear();
                rewardMap.forEach((month, map) -> {
                    RewardzMain.REWARD_MAP.put(month, map);
                });
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RewardsServerPacket.SYNC_REWARD_DAY_COUNT_PACKET, (client, handler, buf, sender) -> {
            int rewardDayCount = buf.readInt();
            Set<Integer> usedRewardDaySet = new HashSet<Integer>();
            while (buf.isReadable()) {
                usedRewardDaySet.add(buf.readInt());
            }
            client.execute(() -> {
                ((RewardPlayerAccess) client.player).setRewardDayCount(rewardDayCount);
                ((RewardPlayerAccess) client.player).setUsedRewardDays(usedRewardDaySet);
            });
        });
    }

    public static void writeC2SRewardsScreenPacket(MinecraftClient client) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(RewardsServerPacket.REWARDS_SCREEN_PACKET, buf);
        client.getNetworkHandler().sendPacket(packet);
    }

}
