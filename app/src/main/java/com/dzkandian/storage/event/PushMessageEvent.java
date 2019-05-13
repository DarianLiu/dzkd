package com.dzkandian.storage.event;

import java.io.Serializable;

/**
 * 后台推送消息
 * Created by Administrator on 2018/6/3 0003.
 */

public class PushMessageEvent implements Serializable {
    private String message;
    private String param;


    public String getMessage() {
        return message;
    }

    public String getParam() {
        return param;
    }

    private PushMessageEvent(Builder builder) {
        this.message = builder.message;
        this.param = builder.param;
    }

    public static class Builder {
        private String message;
        private String param;

        public String getMessage() {
            return message;
        }

        public String getParam() {
            return param;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder param(String param) {
            this.param = param;
            return this;
        }

        public PushMessageEvent build() {
            return new PushMessageEvent(this);
        }

    }
}
