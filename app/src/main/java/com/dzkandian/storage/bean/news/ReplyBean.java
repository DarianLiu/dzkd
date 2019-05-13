package com.dzkandian.storage.bean.news;

import java.io.Serializable;

public class ReplyBean implements Serializable {
    private String id;                //评论id
    private String content;           //评论内容
    private String userId;            //用户ID
    private String userName;          //用户名
    private String userImg;           //用户头像
    private String createTime;        //评论时间
    private int thumbsUp;             //点赞数
    private int subReplyCount;        //回复总数
    private String parentId;          //根评论id
    private String replyId;           //回复id
    private String replyName;         //回复昵称
    private boolean status;           //是否可以点赞 true可以点赞，fasle不可以点赞
    private boolean isBogusData;      //这一条是否为假数据
    private boolean isAnimated;       //这一条是否有点赞 +1 动画效果
    private boolean isHasPraise;      //这一条是否有点赞成功过

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(int thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public int getSubReplyCount() {
        return subReplyCount;
    }

    public void setSubReplyCount(int subReplyCount) {
        this.subReplyCount = subReplyCount;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isBogusData() {
        return isBogusData;
    }

    public void setBogusData(boolean bogusData) {
        isBogusData = bogusData;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }

    public boolean isHasPraise() {
        return isHasPraise;
    }

    public void setHasPraise(boolean hasPraise) {
        isHasPraise = hasPraise;
    }
}
