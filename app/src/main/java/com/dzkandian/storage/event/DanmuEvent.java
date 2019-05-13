package com.dzkandian.storage.event;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 主页Fragment切换事件
 * Created by Administrator on 2018/5/4.
 */

public class DanmuEvent implements Serializable {
    private int viewX;
    private int viewY;
    private String position;

    private DanmuEvent(@NonNull Builder builder) {
        this.viewX = builder.viewX;
        this.viewY = builder.viewY;
        this.position = builder.position;
    }

    public void setViewY(int viewY) {
        this.viewY = viewY;
    }

    public int getViewY() {
        return viewY;
    }

    public void setViewX(int viewX) {
        this.viewX = viewX;
    }

    public int getViewX() {
        return viewX;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public static class Builder {
        private int viewX;
        private int viewY;
        private String position;

        public Builder viewPosition(String position){
            this.position = position;
            return this;
        }

        public Builder viewX(int viewX) {
            this.viewX = viewX;
            return this;
        }

        public Builder viewY(int viewY){
            this.viewY = viewY;
            return this;
        }

        public DanmuEvent build() {
           return new DanmuEvent(this);
        }

    }
}
