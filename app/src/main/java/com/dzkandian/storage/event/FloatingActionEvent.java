package com.dzkandian.storage.event;

import java.io.Serializable;

/**
 * 资讯、视频列表悬乎窗   显示|隐藏|回到顶部
 * Created by Administrator on 2018/6/1.
 */

public class FloatingActionEvent implements Serializable {

    // 0：回到顶部，1：显示；2：隐藏;
    private int state;

    private String type;//类型

    private FloatingActionEvent(Builder builder) {
        this.state = builder.state;
        this.type = builder.type;
    }

    public int getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public static class Builder {
        private int state;
        private String type;

        public Builder state(int state) {
            this.state = state;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public FloatingActionEvent build() {
            return new FloatingActionEvent(this);
        }
    }
}
