package net.rewardz.screen;

import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rewardz.RewardzMain;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.init.ScreenInit;
import net.rewardz.packet.RewardsServerPacket;
import net.rewardz.screen.widget.RewardSlot;
import net.rewardz.util.RewardHelper;

public class RewardsScreenHandler extends ScreenHandler {

    private final Inventory inventory = new SimpleInventory(RewardHelper.getDaysOfMonth()) {
        @Override
        public void markDirty() {
            super.markDirty();
            RewardsScreenHandler.this.onContentChanged(this);
        }
    };

    public RewardsScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ScreenInit.REWARDS_SCREEN_HANDLER_TYPE, syncId);
        int i;
        for (i = 0; i < 4; ++i) {
            for (int j = 0; j < 7; ++j) {
                this.addSlot(new RewardSlot(this.inventory, j + i * 7, 8 + j * 24, 18 + i * 29) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        if (this.getIndex() >= getRewardDayCount(playerEntity) || getUsedRewardSet(playerEntity).contains(this.getIndex() + 1)) {
                            return false;
                        }
                        if (RewardzMain.REWARD_MAP.containsKey(RewardHelper.getMonth()) && RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).containsKey(this.getIndex() + 1)
                                && (boolean) RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).get(this.getIndex() + 1).get(0) && RewardHelper.getDay() != this.getIndex() + 1) {
                            return false;
                        }
                        return super.canTakeItems(playerEntity);
                    }

                    @Override
                    public void onTakeItem(PlayerEntity player, ItemStack stack) {
                        ((RewardPlayerAccess) player).addUsedRewardDay(this.getIndex() + 1);
                        if (!this.getStack().isEmpty() && !player.getWorld().isClient()) {
                            playerInventory.offerOrDrop(this.getStack());
                        }
                        if (!player.getWorld().isClient()) {
                            RewardHelper.runRewardCommands((ServerPlayerEntity) player, this.getIndex());
                        }
                        this.setEnabled(false);
                        super.onTakeItem(player, stack);
                    }
                });
            }
        }
        if (RewardHelper.getDaysOfMonth() > 28) {
            for (i = 0; i < RewardHelper.getDaysOfMonth() - 28; ++i) {
                this.addSlot(new RewardSlot(this.inventory, 28 + i, 8 + i * 24, 134) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        if (this.getIndex() >= getRewardDayCount(playerEntity) || getUsedRewardSet(playerEntity).contains(this.getIndex() + 1)) {
                            return false;
                        }
                        if (RewardzMain.REWARD_MAP.containsKey(RewardHelper.getMonth()) && RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).containsKey(this.getIndex() + 1)
                                && (boolean) RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).get(this.getIndex() + 1).get(0) && RewardHelper.getDay() != this.getIndex() + 1) {
                            return false;
                        }
                        return super.canTakeItems(playerEntity);
                    }

                    @Override
                    public void onTakeItem(PlayerEntity player, ItemStack stack) {
                        ((RewardPlayerAccess) player).addUsedRewardDay(this.getIndex() + 1);
                        if (!this.getStack().isEmpty() && !player.getWorld().isClient()) {
                            playerInventory.offerOrDrop(this.getStack());
                        }
                        if (!player.getWorld().isClient()) {
                            RewardHelper.runRewardCommands((ServerPlayerEntity) player, this.getIndex());
                        }
                        this.setEnabled(false);
                        super.onTakeItem(player, stack);
                    }
                });
            }
        }
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 170 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 228));
        }

        if (RewardzMain.REWARD_MAP.containsKey(RewardHelper.getMonth())) {
            for (i = 1; i <= RewardHelper.getDaysOfMonth(); i++) {
                if (getUsedRewardSet(playerInventory.player).contains(i)) {
                    ((RewardSlot) this.getSlot(i - 1)).setEnabled(false);
                    continue;
                }
                if (RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).containsKey(i)) {
                    this.inventory.setStack(i - 1, ((ItemStack) RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).get(i).get(1)).copy());
                } else if (i <= getRewardDayCount(playerInventory.player)) { // might be not good if multiple days are empty in a row
                    ((RewardPlayerAccess) playerInventory.player).addUsedRewardDay(i);
                    RewardsServerPacket.writeS2CSyncRewardDayCountPacket((ServerPlayerEntity) playerInventory.player);
                }
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();

            if (index < RewardHelper.getDaysOfMonth()) {
                if (!this.insertItem(itemStack2, 0, RewardHelper.getDaysOfMonth(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }

    public int getRewardDayCount(PlayerEntity playerEntity) {
        return ((RewardPlayerAccess) playerEntity).getRewardDayCount();
    }

    public Set<Integer> getUsedRewardSet(PlayerEntity playerEntity) {
        return ((RewardPlayerAccess) playerEntity).getUsedRewardDays();
    }

}
