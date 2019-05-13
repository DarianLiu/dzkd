package com.dzkandian.storage.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/25.
 */

public class CoinBean implements Serializable {

    /**
     * id : 67
     * today : 460
     * surplus : 460
     * total : 460
     * lastExchange :
     * lastTask : 2018-01-02 14:47:38
     * modifyTime : 2018-01-02 14:47:39
     */

    private int id;
    private int today;
    private int surplus;
    private int total;
    private String lastExchange;
    private String lastTask;
    private String modifyTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getToday() {
        return today;
    }

    public void setToday(int today) {
        this.today = today;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getLastExchange() {
        return lastExchange;
    }

    public void setLastExchange(String lastExchange) {
        this.lastExchange = lastExchange;
    }

    public String getLastTask() {
        return lastTask;
    }

    public void setLastTask(String lastTask) {
        this.lastTask = lastTask;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}
