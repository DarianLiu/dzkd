package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/31.
 */

public class ApprenticeMessageBean implements Serializable{
    private long userId;
    private long apprenticeId;
    private int msgType;

    public void setApprenticeId(long apprenticeId) {
        this.apprenticeId = apprenticeId;
    }

    public long getApprenticeId() {
        return apprenticeId;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
