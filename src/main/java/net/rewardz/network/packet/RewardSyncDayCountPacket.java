package net.rewardz.network.packet;

import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RewardSyncDayCountPacket(int rewardDayCount, List<Integer> usedRewardDays) implements CustomPayload {

    public static final CustomPayload.Id<RewardSyncDayCountPacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("rewardz", "reward_sync_day_count_packet"));

    public static final PacketCodec<RegistryByteBuf, RewardSyncDayCountPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.rewardDayCount);
        buf.writeCollection(value.usedRewardDays, PacketByteBuf::writeInt);
    }, buf -> new RewardSyncDayCountPacket(buf.readInt(), buf.readList(PacketByteBuf::readInt)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
