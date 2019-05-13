package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 我的订单bean
 * Created by Administrator on 2018/5/2.
 */

public class MyOrderBean implements Serializable {

    /**
     * id : 20180108193329541866726
     * userId : 68
     * gold : 10000
     * rmb : 100
     * status : 1
     * channel : alipay
     * phone : null
     * name : 小莫
     * userAccount : 1149989463@qq.com
     * banlance : 20000
     * payNo : null
     * createTime : 2018-01-08 19:33:29
     * finishTime : null
     * closeTime : null
     * closeMsg : null
     */

    private String id;
    private int userId;
    private int gold;
    private int rmb;
    private int status;
    private String channel;
    private String phone;
    private String name;
    private String userAccount;
    private int banlance;
    private String payNo;
    private String createTime;
    private String finishTime;
    private String closeTime;
    private String closeMsg;

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

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getRmb() {
        return rmb;
    }

    public void setRmb(int rmb) {
        this.rmb = rmb;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public int getBanlance() {
        return banlance;
    }

    public void setBanlance(int banlance) {
        this.banlance = banlance;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCloseMsg() {
        return closeMsg;
    }

    public void setCloseMsg(String closeMsg) {
        this.closeMsg = closeMsg;
    }
}
