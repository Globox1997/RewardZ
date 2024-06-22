package net.rewardz.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RewardScreenPacket() implements CustomPayload {

    public static final CustomPayload.Id<RewardScreenPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("rewardz", "reward_screen_packet"));

    public static final PacketCodec<RegistryByteBuf, RewardScreenPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    }, buf -> new RewardScreenPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
