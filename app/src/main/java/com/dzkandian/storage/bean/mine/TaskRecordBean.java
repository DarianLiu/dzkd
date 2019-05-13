package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 收益明细
 * Created by Administrator on 2018/4/28.
 */

public class TaskRecordBean implements Serializable {

    /**
     * id : 2018-01-03 15:05:35
     * userId : 67
     * type : 39
     * reward : 1000
     * describe : 完成新手任务获得奖励收益1000金币
     */

    private String id;
    private int userId;
    private int type;
    private int reward;
    private String describe;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
