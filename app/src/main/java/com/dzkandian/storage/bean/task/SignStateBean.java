package com.dzkandian.storage.bean.task;

import java.io.Serializable;

/**
 * 签到状态实体
 * Created by liuli on 2018/4/24.
 */
public class SignStateBean implements Serializable{

    /**
     * days : 1
     * reward : 100
     * modifyTime : 2018-01-04 11:34:02
     * modifyUser : 1
     * receive : 2
     */

    private int days;//第几天
    private int reward;//奖励金币
    private String modifyTime;
    private int modifyUser;
    private int receive;//该天状态 2为已签到(按钮灰色)，否则为自然状态

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(int modifyUser) {
        this.modifyUser = modifyUser;
    }

    public int getReceive() {
        return receive;
    }

    public void setReceive(int receive) {
        this.receive = receive;
    }
}
