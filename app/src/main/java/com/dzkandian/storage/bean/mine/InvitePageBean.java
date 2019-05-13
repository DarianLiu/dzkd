package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 收徒页面展示的必需数据
 * Created by liuli on 2018/4/27.
 */

public class InvitePageBean implements Serializable{
    private int totalIncome;//徒弟提供的总收益
    private String addedProfitText;//	提成收益
    private String rewardRuleText;//奖励发放规则描述
    private String inviteProfitText;//邀请收益
    private int inviteCount;//成功邀请的徒弟
    private String bannerImg;//头部横幅图标地址

    public void setAddedProfitText(String addedProfitText) {
        this.addedProfitText = addedProfitText;
    }

    public int getInviteCount() {
        return inviteCount;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public void setInviteCount(int inviteCount) {
        this.inviteCount = inviteCount;
    }

    public String getAddedProfitText() {
        return addedProfitText;
    }

    public void setInviteProfitText(String inviteProfitText) {
        this.inviteProfitText = inviteProfitText;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public String getInviteProfitText() {
        return inviteProfitText;
    }

    public String getRewardRuleText() {
        return rewardRuleText;
    }

    public void setRewardRuleText(String rewardRuleText) {
        this.rewardRuleText = rewardRuleText;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    private RewardRuleBean rewardRule;//奖励发放规则

    public RewardRuleBean getRewardRule() {
        return rewardRule;
    }

    public void setRewardRule(RewardRuleBean rewardRule) {
        this.rewardRule = rewardRule;
    }

    public static class RewardRuleBean {

        private int id;
        private int firstReward;
        private int day1;
        private int day2;
        private int day3;
        private int day4;
        private int day5;
        private int day6;
        private int day7;
        private int threshold;
        private String createTime;
        private int createUser;
        private Object effectiveTime;
        private Object expiryTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFirstReward() {
            return firstReward;
        }

        public void setFirstReward(int firstReward) {
            this.firstReward = firstReward;
        }

        public int getDay1() {
            return day1;
        }

        public void setDay1(int day1) {
            this.day1 = day1;
        }

        public int getDay2() {
            return day2;
        }

        public void setDay2(int day2) {
            this.day2 = day2;
        }

        public int getDay3() {
            return day3;
        }

        public void setDay3(int day3) {
            this.day3 = day3;
        }

        public int getDay4() {
            return day4;
        }

        public void setDay4(int day4) {
            this.day4 = day4;
        }

        public int getDay5() {
            return day5;
        }

        public void setDay5(int day5) {
            this.day5 = day5;
        }

        public int getDay6() {
            return day6;
        }

        public void setDay6(int day6) {
            this.day6 = day6;
        }

        public int getDay7() {
            return day7;
        }

        public void setDay7(int day7) {
            this.day7 = day7;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getCreateUser() {
            return createUser;
        }

        public void setCreateUser(int createUser) {
            this.createUser = createUser;
        }

        public Object getEffectiveTime() {
            return effectiveTime;
        }

        public void setEffectiveTime(Object effectiveTime) {
            this.effectiveTime = effectiveTime;
        }

        public Object getExpiryTime() {
            return expiryTime;
        }

        public void setExpiryTime(Object expiryTime) {
            this.expiryTime = expiryTime;
        }
    }
}
