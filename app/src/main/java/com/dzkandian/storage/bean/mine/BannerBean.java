package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 轮播图图片
 * Created by Administrator on 2018/7/27.
 */
public class BannerBean implements Serializable {

    /**
     * event	String	点击响应事件
     * 内部跳转：innerJump
     * 外部跳转：outnerJump
     * 好友邀请页：invitation
     * url	String	跳转链接
     * img	String	图片地址
     */

    private int id;
    private String title;
    private String event;
    private String url;
    private String img;
    private int sort;
    private String createTime;
    private String page;
    private int delFlag;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public int getDelFlag() {
        return delFlag;
    }

}
