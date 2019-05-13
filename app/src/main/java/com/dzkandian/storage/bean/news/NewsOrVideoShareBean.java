package com.dzkandian.storage.bean.news;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/28.
 */

public class NewsOrVideoShareBean implements Serializable {
    private String url;
    private String type;
    private String content;
    private String title;
    private String bigPicUrl;

    public String getBigPicUrl() {
        return bigPicUrl;
    }

    public void setBigPicUrl(String bigPicUrl) {
        this.bigPicUrl = bigPicUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
