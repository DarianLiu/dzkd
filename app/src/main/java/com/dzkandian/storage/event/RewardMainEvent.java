package com.dzkandian.storage.event;

/**
 * Created by liuli on 2018/7/16.
 * 时段奖励，任务中心奖励发送到 MainActivity 进行金币爆裂；
 */

public class RewardMainEvent {
    private String rewardType;
    private int rewardGold;

    private RewardMainEvent(RewardMainEvent.Builder builder) {
        this.rewardType = builder.rewardType;
        this.rewardGold = builder.rewardGold;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public int getRewardGold() {
        return rewardGold;
    }

    public void setRewardGold(int rewardGold) {
        this.rewardGold = rewardGold;
    }

    public static class Builder {
        private String rewardType;
        private int rewardGold;

        public RewardMainEvent.Builder newRewardType(String rewardType) {
            this.rewardType = rewardType;
            return this;
        }

        public RewardMainEvent.Builder newRewardGoid(int rewardGold) {
            this.rewardGold = rewardGold;
            return this;
        }

        public RewardMainEvent build() {
            return new RewardMainEvent(this);
        }

    }
}
