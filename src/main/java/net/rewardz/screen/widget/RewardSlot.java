package net.rewardz.screen.widget;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class RewardSlot extends Slot {

    private boolean isEnabled = true;

    public RewardSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}
