package com.dzkandian.storage.event;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 主页Fragment切换事件
 * Created by Administrator on 2018/5/4.
 */

public class ChangeTabEvent implements Serializable {
    private int indexTab;

    private ChangeTabEvent(@NonNull Builder builder) {
        this.indexTab = builder.indexTab;
    }

    public void setIndexTab(int indexTab) {
        this.indexTab = indexTab;
    }

    public int getIndexTab() {
        return indexTab;
    }

   public static class Builder {
        private int indexTab;

        @NonNull
        public Builder indexTab(int indexTab) {
            this.indexTab = indexTab;
            return this;
        }

        @NonNull
        public ChangeTabEvent build() {
           return new ChangeTabEvent(this);
        }

    }
}
