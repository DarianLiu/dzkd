package com.dzkandian.storage.bean;

/** 资讯bean
 * Created by sheng on 2018/10/31.
 */

public class CollectionNewsBean {
    private String id;//ID
    private String title;//标题
    private String images;//图片地址数组
    private String url;//文章地址
    private String source;//文章来源
    private String updateTime;//发布时间
    private String canShare;//是否可以分享
    private String createTime;//收藏时间
    private String type;//栏目

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCanShare() {
        return canShare;
    }

    public void setCanShare(String canShare) {
        this.canShare = canShare;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
