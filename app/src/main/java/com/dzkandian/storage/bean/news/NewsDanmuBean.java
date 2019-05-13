package com.dzkandian.storage.bean.news;



/**
 * Created by Administrator on 2017/3/30.
 */

public class NewsDanmuBean {
    public String content;
    public int textColor;
    public String time;
    private String headImg;  //弹幕头像
    private String thumbsUpCount; //评论点赞数量
    private String position;
    private boolean canThumbsUp; //是否可以点赞 true：可以；false：不可以
    int type ;
    long showTime;

    public boolean getCanThumbsUp() {
        return canThumbsUp;
    }

    public void setCanThumbsUp(boolean canthumbsUpCount) {
        this.canThumbsUp = canthumbsUpCount;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getThumbsUpCount() {
        return thumbsUpCount;
    }

    public void setThumbsUpCount(String thumbsUpCount) {
        this.thumbsUpCount = thumbsUpCount;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
