package net.rewardz.network.packet;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RewardSyncRewardsPacket(RewardMonthDatas rewardMonthDatas) implements CustomPayload {

    public static final CustomPayload.Id<RewardSyncRewardsPacket> PACKET_ID = new CustomPayload.Id<>(new Identifier("rewardz", "reward_sync_rewards_packet"));

    public static final PacketCodec<RegistryByteBuf, RewardSyncRewardsPacket> PACKET_CODEC = PacketCodec.of(RewardSyncRewardsPacket::write, RewardSyncRewardsPacket::new);

    private RewardSyncRewardsPacket(RegistryByteBuf buf) {
        this(new RewardMonthDatas(buf));
    }

    private void write(RegistryByteBuf buf) {
        this.rewardMonthDatas.write(buf);
    }

    public record RewardMonthDatas(int rewardMapSize, List<RewardMonthData> rewardMonthDatas) {
        private RewardMonthDatas(RegistryByteBuf buf) {
            this(0, Stream.generate(() -> {
                return new RewardMonthData(buf);
            }).limit(buf.readInt()).toList());
        }

        public void write(RegistryByteBuf buf) {
            buf.writeInt(this.rewardMapSize);
            for (int i = 0; i < this.rewardMonthDatas.size(); i++) {
                this.rewardMonthDatas.get(i).write(buf);
            }
        }
    }

    public record RewardMonthData(int month, int days, List<Integer> dayList, List<Boolean> exactDayList, List<ItemStack> itemStackList, List<Integer> tooltipSizeList, List<String> tooltipList) {

        private RewardMonthData(RegistryByteBuf buf) {
            this(buf.readInt(), buf.readInt(), buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readBoolean), ItemStack.LIST_PACKET_CODEC.decode(buf),
                    buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readString));
        }

        public void write(RegistryByteBuf buf) {
            buf.writeInt(this.month);
            buf.writeInt(this.days);
            buf.writeCollection(this.dayList, PacketByteBuf::writeInt);
            buf.writeCollection(this.exactDayList, PacketByteBuf::writeBoolean);
            ItemStack.LIST_PACKET_CODEC.encode(buf, this.itemStackList);
            buf.writeCollection(this.tooltipSizeList, PacketByteBuf::writeInt);
            buf.writeCollection(this.tooltipList, PacketByteBuf::writeString);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
