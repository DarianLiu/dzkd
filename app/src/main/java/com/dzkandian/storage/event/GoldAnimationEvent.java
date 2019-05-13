package com.dzkandian.storage.event;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 时段奖励的金币箱动画
 * Created by Administrator on 2018/5/10 0010.
 */

public class GoldAnimationEvent implements Serializable {
    private int module;// 1 为看点  2 为视频

    public int getModule(){
        return module;
    }

    private GoldAnimationEvent(@NonNull Builder builder){
        this.module=builder.module;
    }

    public static class Builder {
        private int module;

        @NonNull
        public Builder module(int module){
            this.module = module;
            return this;
        }

        @NonNull
        public GoldAnimationEvent build() {
            return new GoldAnimationEvent(this);
        }
    }
}
