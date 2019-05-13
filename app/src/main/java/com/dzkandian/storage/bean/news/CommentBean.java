package com.dzkandian.storage.bean.news;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 评论列表
 * Created by Administrator on 2018/8/28.
 */

public class CommentBean implements Serializable {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private String userImg;
    private String createTime;
    private int thumbsUp;
    private int subReplyCount;
    private boolean status;
    @SerializedName("replyList")
    private List<ReplyBean> replyList;
    //是否还有点赞机会，默认为true
    private boolean hasThumbs = true;
    //是否做动画，默认为false
    private boolean hasAnimate = false;
    //是否已经通过机审的真数据，默认为true
    private boolean hasReply = true;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public List<ReplyBean> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<ReplyBean> replyList) {
        this.replyList = replyList;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isHasThumbs() {
        return hasThumbs;
    }

    public void setHasThumbs(boolean hasThumbs) {
        this.hasThumbs = hasThumbs;
    }

    public boolean isHasAnimate() {
        return hasAnimate;
    }

    public void setHasAnimate(boolean hasAnimate) {
        this.hasAnimate = hasAnimate;
    }

    public boolean isHasReply() {
        return hasReply;
    }

    public void setHasReply(boolean hasReply) {
        this.hasReply = hasReply;
    }
}

