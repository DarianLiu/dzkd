package com.dzkandian.storage.event;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 时段奖励（计时）事件
 * Created by Administrator on 2018/7/25.
 */

public class TimeRewardEvent implements Serializable {
    private long timeDifference;
    private boolean isTimeEnd;

//    private int second;//秒钟数
//    private int minute;//分钟数
//    //activity发给fragment时为false;
//    //fragment发给activity时为true;
//    private boolean isStartTime;//是否开始倒计时；


    public long getTimeDifference() {
        return timeDifference;
    }

    public boolean isTimeEnd() {
        return isTimeEnd;
    }

    private TimeRewardEvent(TimeRewardEvent.Builder builder) {
        this.timeDifference = builder.timeDifference;
        this.isTimeEnd = builder.isTimeEnd;
    }

    public static class Builder {
        private long timeDifference;
        private boolean isTimeEnd;

        public Builder timeDifference(long timeDifference) {
            this.timeDifference = timeDifference;
            return this;
        }

        public Builder isTimeEnd(boolean isTimeEnd) {
            this.isTimeEnd = isTimeEnd;
            return this;
        }

        public TimeRewardEvent build() {
            return new TimeRewardEvent(this);
        }
    }
}
