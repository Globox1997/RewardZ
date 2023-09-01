package net.rewardz.access;

import java.util.Set;

public interface RewardPlayerAccess {

    public void increaseRewardDay();

    public int getRewardDayCount();

    public void setRewardDayCount(int count);

    public void addUsedRewardDay(int day);

    public Set<Integer> getUsedRewardDays();

    public void setUsedRewardDays(Set<Integer> set);

}
