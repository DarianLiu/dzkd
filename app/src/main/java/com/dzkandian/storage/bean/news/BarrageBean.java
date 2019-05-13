package com.dzkandian.storage.bean.news;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/28.
 */

public class BarrageBean implements Serializable {
    private String id;
    private String content;
    private String userImg;
    private String thumbsUp; //点赞数
    private boolean canThumbsUp;   //当前弹幕是否可以点赞

    public boolean getCanThumbsUp() {
        return canThumbsUp;
    }

    public void setCanThumbsUp(boolean canThumbsUp) {
        this.canThumbsUp = canThumbsUp;
    }

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

    public String getThumbsUpCount() {
        return thumbsUp;
    }

    public void setThumbsUpCount(String thumbsUpCount) {
        this.thumbsUp = thumbsUpCount;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
