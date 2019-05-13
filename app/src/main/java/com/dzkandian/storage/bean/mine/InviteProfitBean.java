package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 徒弟邀请收益实体
 * Created by liuli on 2018/4/28.
 */

public class InviteProfitBean implements Serializable{

    /**
     * master : 75084
     * apprentice : 105
     * day : -1
     * gold : 30
     * rewardRule : 1
     * createTime : 2018-03-21 10:53:23
     * title : 好友159****1465
     * describe : 提成收益：<font color="red">+30</font>金币
     */

    private int master;
    private int apprentice;
    private int day;
    private int gold;
    private int rewardRule;
    private String createTime;
    private String title;
    private String describe;

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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getRewardRule() {
        return rewardRule;
    }

    public void setRewardRule(int rewardRule) {
        this.rewardRule = rewardRule;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
