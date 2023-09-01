package net.rewardz.mixin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.util.RewardHelper;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements RewardPlayerAccess {

    @Unique
    private int currentMonth = 0;
    @Unique
    private int currentDay = 0;
    @Unique
    private int rewardDayCount = 0;
    @Unique
    private Set<Integer> usedRewardDaySet = new HashSet<Integer>();

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.currentMonth = nbt.getInt("RewardMonth");
        this.currentDay = nbt.getInt("RewardDay");
        this.rewardDayCount = nbt.getInt("RewardDayCount");

        this.usedRewardDaySet.clear();
        for (int value : nbt.getIntArray("UsedRewardDayList")) {
            this.usedRewardDaySet.add(value);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("RewardMonth", this.currentMonth);
        nbt.putInt("RewardDay", this.currentDay);
        nbt.putInt("RewardDayCount", this.rewardDayCount);
        nbt.putIntArray("UsedRewardDayList", new ArrayList<>(usedRewardDaySet));
    }

    @Override
    public void increaseRewardDay() {
        if (this.currentMonth != RewardHelper.getMonth()) {
            this.currentMonth = RewardHelper.getMonth();
            this.currentDay = RewardHelper.getDay();
            this.rewardDayCount = 1;
            this.usedRewardDaySet.clear();
        } else if (this.currentDay != RewardHelper.getDay()) {
            this.currentDay = RewardHelper.getDay();
            this.rewardDayCount++;
        }
    }

    @Override
    public int getRewardDayCount() {
        return this.rewardDayCount;
    }

    @Override
    public void setRewardDayCount(int count) {
        this.rewardDayCount = count;
    }

    @Override
    public void addUsedRewardDay(int day) {
        this.usedRewardDaySet.add(day);
    }

    @Override
    public Set<Integer> getUsedRewardDays() {
        return this.usedRewardDaySet;
    }

    @Override
    public void setUsedRewardDays(Set<Integer> set) {
        this.usedRewardDaySet.clear();
        this.usedRewardDaySet.addAll(set);
    }

}
