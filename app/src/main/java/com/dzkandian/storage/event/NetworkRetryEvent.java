package com.dzkandian.storage.event;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 网络异常后重试事件
 * Created by Administrator on 2018/5/8 0008.
 */

public class NetworkRetryEvent implements Serializable {

    /**
     * 0:资讯栏目重试；1：视频栏目重试
     */
    private int event;

    public int getEvent() {
        return event;
    }

    private NetworkRetryEvent(@NonNull Builder builder) {
        this.event = builder.event;
    }

    public static class Builder {
        private int event;

        public void setEvent(int event) {
            this.event = event;
        }

        public int getEvent() {
            return event;
        }

        @NonNull
        public Builder event(int event) {
            this.event = event;
            return this;
        }

        @NonNull
        public NetworkRetryEvent build() {
            return new NetworkRetryEvent(this);
        }

    }
}
