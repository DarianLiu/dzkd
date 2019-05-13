package com.dzkandian.storage.bean;

import java.io.Serializable;

/**
 * 活动中心活动子项实体
 * Created by liuli on 2018/5/2.
 */

public class ActiveBean implements Serializable{

    /**
     * id : 4
     * classification : 活动
     * title : 新年好
     * imgUrl : http://page_id/60879738258507
     * desc : 新年到了....
     * startTime : 2018-01-23 09:27:36
     * endTime : 2018-02-08 09:27:41
     * link : https://www.showdoc.cc/
     * btnText : 马上开始
     * tag : 1
     */

    private int id;
    private String classification;
    private String title;
    private String imgUrl;
    private String desc;
    private String createTime;//创建时间
    private String startTime;
    private String endTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    private String link;//跳转链接
    private String btnText;//按钮文字
    private String tag;//打开方式的标记  1：app内打开，2：内部浏览器打开 3：外部浏览器打开

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
