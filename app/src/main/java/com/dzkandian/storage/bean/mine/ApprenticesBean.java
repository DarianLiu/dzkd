package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 当前用户的邀请的徒弟实体
 * Created by liuli on 2018/4/28.
 */

public class ApprenticesBean implements Serializable{

    /**
     * master : 75084
     * apprentice : 105
     * createTime : 2018-03-19 22:31:30
     * profit : 3500
     * gave : 3500
     * waitGive : 31500
     * title : 好友 159****9163
     * inviteCode : 10000001
     * rewardRule : 1
     * nextReward : 1
     * lastRewardDate : 2018-03-19 00:00:00
     * delFlag : 0
     */

    private int master;
    private int apprentice;
    private String createTime;//收徒时间
    private int profit;
    private int gave;//已发放奖励
    private int waitGive;//待发放奖励
    private String title;
    private int inviteCode;
    private int rewardRule;
    private int nextReward;
    private String lastRewardDate;
    private int delFlag;

    public int getMaster() {
        return master;
    }

    public void setMaster(int master) {
        this.master = master;
    }

    public int getApprentice() {
        return apprentice;
    }

    public void setApprentice(int apprentice) {
        this.apprentice = apprentice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public int getGave() {
        return gave;
    }

    public void setGave(int gave) {
        this.gave = gave;
    }

    public int getWaitGive() {
        return waitGive;
    }

    public void setWaitGive(int waitGive) {
        this.waitGive = waitGive;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(int inviteCode) {
        this.inviteCode = inviteCode;
    }

    public int getRewardRule() {
        return rewardRule;
    }

    public void setRewardRule(int rewardRule) {
        this.rewardRule = rewardRule;
    }

    public int getNextReward() {
        return nextReward;
    }

    public void setNextReward(int nextReward) {
        this.nextReward = nextReward;
    }

    public String getLastRewardDate() {
        return lastRewardDate;
    }

    public void setLastRewardDate(String lastRewardDate) {
        this.lastRewardDate = lastRewardDate;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }
}
