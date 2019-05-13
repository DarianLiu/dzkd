package com.dzkandian.storage.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/1/21.
 */

public class SearchBean implements Serializable {
    private String id;          //唯一标识
    private String webUrl;      //视频分享地址
    private String img;         //单图
    private String category;    //栏目名称
    private String title;       //文章标题
    private int canShare;       //能否分享
    private String sourceName;  //来源
    private String url;         //文章地址或视频播放地址
    private long beforeTime;    //仅用于翻页
    private String type;        //实体类型：news为资讯，video为横版视频，wuli为小视频
    private String createTime;  //创建时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCanShare() {
        return canShare;
    }

    public void setCanShare(int canShare) {
        this.canShare = canShare;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(long beforeTime) {
        this.beforeTime = beforeTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
