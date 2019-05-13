package com.dzkandian.storage.event;

/**
 * 推送消息事件
 * Created by Administrator on 2018/7/6.
 */

public class PushEvent {
    private int newActive;//活动公告
    private int newNotification;//系统通知（旧版的我的消息）
    private int newMessage;//我的消息

    private PushEvent(Builder builder) {
        this.newActive = builder.newActive;
        this.newNotification = builder.newNotification;
        this.newMessage = builder.newMessage;
    }

    public void setNewMessage(int newMessage) {
        this.newMessage = newMessage;
    }

    public void setNewActive(int newActive) {
        this.newActive = newActive;
    }

    public int getNewNotification() {
        return newNotification;
    }

    public void setNewNotification(int newNotification) {
        this.newNotification = newNotification;
    }

    public int getNewMessage() {
        return newMessage;
    }

    public int getNewActive() {
        return newActive;
    }

    /**
     * 是否有未读消息
     */
    public boolean isPush() {
        return (newActive > 0 || newMessage > 0 || newNotification > 0);
    }

    public static class Builder {
        private int newActive;//活动公告
        private int newNotification;//系统通知（旧版的我的消息）
        private int newMessage;//我的消息

        public Builder newActive(int newActive) {
            this.newActive = newActive;
            return this;
        }

        public Builder newNotification(int newNotification) {
            this.newNotification = newNotification;
            return this;
        }

        public Builder newMessage(int newMessage) {
            this.newMessage = newMessage;
            return this;
        }

        public PushEvent build() {
            return new PushEvent(this);
        }

    }

}
