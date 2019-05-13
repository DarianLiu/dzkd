package com.dzkandian.storage.event;


import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 微信绑定事件
 * Created by Administrator on 2018/5/4.
 */
public class WeChatBindEvent implements Serializable {

    private String type;//微信事件类型
    private String code;//微信授权字符串
    private int position;//任务位置（任务中心使用）

    private WeChatBindEvent(@NonNull Builder builder) {
        this.type = builder.type;
        this.code = builder.code;
        this.position = builder.position;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getCode() {
        return code;
    }

    public static class Builder {
        private String type;
        private String code;
        private int position;

        @NonNull
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        @NonNull
        public Builder code(String code) {
            this.code = code;
            return this;
        }

        @NonNull
        public Builder position(int position) {
            this.position = position;
            return this;
        }

        @NonNull
        public WeChatBindEvent build() {
            return new WeChatBindEvent(this);
        }
    }
}
