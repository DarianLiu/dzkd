package com.dzkandian.storage.event;

import java.io.Serializable;

/**
 * 栏目点击事件
 * Created by Administrator on 2018/5/15.
 */

public class ColumnEvent implements Serializable {

    private String type;
    private int position;

    public ColumnEvent(int position, String type) {
        this.position = position;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }
}
