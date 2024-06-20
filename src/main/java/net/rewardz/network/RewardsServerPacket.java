package net.rewardz.network;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rewardz.RewardzMain;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.network.packet.RewardScreenPacket;
import net.rewardz.network.packet.RewardSyncDayCountPacket;
import net.rewardz.network.packet.RewardSyncRewardsPacket;
import net.rewardz.screen.RewardsScreenHandler;

public class RewardsServerPacket {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(RewardScreenPacket.PACKET_ID, RewardScreenPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RewardSyncDayCountPacket.PACKET_ID, RewardSyncDayCountPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RewardSyncRewardsPacket.PACKET_ID, RewardSyncRewardsPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RewardScreenPacket.PACKET_ID, (payload, context) -> {
            context.player().server.execute(() -> {
                writeS2CSyncRewardDayCountPacket(context.player());

                context.player().openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> {
                    return new RewardsScreenHandler(syncId, playerInventory, ScreenHandlerContext.EMPTY);
                }, Text.translatable("screen.rewardz")));
            });
        });

    }

    public static void writeS2CSyncRewardDayCountPacket(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity,
                new RewardSyncDayCountPacket(((RewardPlayerAccess) serverPlayerEntity).getRewardDayCount(), ((RewardPlayerAccess) serverPlayerEntity).getUsedRewardDays().stream().toList()));
    }

    public static void writeS2CSyncRewardsPacket(ServerPlayerEntity serverPlayerEntity) {
        List<RewardSyncRewardsPacket.RewardMonthData> rewardMonthDatas = new ArrayList<>();

        RewardzMain.REWARD_MAP.forEach((month, map) -> {
            List<Integer> dayList = new ArrayList<Integer>();
            List<Boolean> exactDayList = new ArrayList<Boolean>();
            List<ItemStack> itemStackList = new ArrayList<ItemStack>();
            List<Integer> tooltipSizeList = new ArrayList<Integer>();
            List<String> tooltipList = new ArrayList<String>();

            map.forEach((day, list) -> {
                dayList.add(day);
                exactDayList.add((boolean) list.get(0));
                itemStackList.add((ItemStack) list.get(1));

                int tooltipSize = (int) list.get(2);
                tooltipSizeList.add(tooltipSize);
                for (int i = 0; i < tooltipSize; i++) {
                    tooltipList.add((String) list.get(3 + i));
                }
            });
            rewardMonthDatas.add(new RewardSyncRewardsPacket.RewardMonthData(month, map.size(), dayList, exactDayList, itemStackList, tooltipSizeList, tooltipList));
        });
        ServerPlayNetworking.send(serverPlayerEntity, new RewardSyncRewardsPacket(new RewardSyncRewardsPacket.RewardMonthDatas(RewardzMain.REWARD_MAP.size(), rewardMonthDatas)));
    }

}
