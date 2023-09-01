package net.rewardz.packet;

import java.util.Iterator;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rewardz.RewardzMain;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.screen.RewardsScreenHandler;

public class RewardsServerPacket {

    public static final Identifier REWARDS_SCREEN_PACKET = new Identifier("rewardz", "rewards_screen_packet");
    public static final Identifier SYNC_REWARDS_PACKET = new Identifier("rewardz", "sync_rewards_packet");
    public static final Identifier SYNC_REWARD_DAY_COUNT_PACKET = new Identifier("rewardz", "sync_reward_day_count_packet");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(REWARDS_SCREEN_PACKET, (server, player, handler, buffer, sender) -> {
            server.execute(() -> {
                writeS2CSyncRewardDayCountPacket(player);
                player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> {
                    return new RewardsScreenHandler(syncId, playerInventory, ScreenHandlerContext.EMPTY);
                }, Text.translatable("screen.rewardz")));
            });
        });
    }

    public static void writeS2CSyncRewardDayCountPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(((RewardPlayerAccess) serverPlayerEntity).getRewardDayCount());

        Iterator<Integer> iterator = ((RewardPlayerAccess) serverPlayerEntity).getUsedRewardDays().iterator();
        while (iterator.hasNext()) {
            buf.writeInt(iterator.next());
        }
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(SYNC_REWARD_DAY_COUNT_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CSyncRewardsPacket(ServerPlayNetworkHandler handler) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(RewardzMain.REWARD_MAP.size());
        RewardzMain.REWARD_MAP.forEach((month, map) -> {
            buf.writeInt(month);
            buf.writeInt(map.size());
            map.forEach((day, list) -> {
                buf.writeInt(day);
                buf.writeBoolean((boolean) list.get(0));
                buf.writeItemStack((ItemStack) list.get(1));

                int tooltipSize = (int) list.get(2);
                buf.writeInt(tooltipSize);
                for (int i = 0; i < tooltipSize; i++) {
                    buf.writeString((String) list.get(3 + i));
                }
                // Probably unnecessary to sync commands...
                int commandSize = (int) list.get(tooltipSize + 3);
                buf.writeInt(commandSize);
                for (int i = 0; i < commandSize; i++) {
                    buf.writeString((String) list.get(commandSize + tooltipSize + 3 + i));
                }
            });
        });

        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(SYNC_REWARDS_PACKET, buf);
        handler.sendPacket(packet);
    }

}
