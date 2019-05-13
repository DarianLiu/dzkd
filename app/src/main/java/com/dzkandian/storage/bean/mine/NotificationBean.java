package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/12/3.
 * 系统通知
 */

public class NotificationBean implements Serializable {

    /**
     * id : 7
     * uid : 75108
     * hideTime : 2018-06-06 16:54:39
     * title : 意见反馈回复通知
     * createTime : 2018-06-06 15:54:23
     * viewTime : 2018-06-06 15:54:39
     * msgFrom : FEEDBACK
     * objId : 6632
     * showTime : 3600000
     * content : 我们会优化好的
     */

    private int id;
    private int uid;
    private String hideTime;
    private String title;
    private String createTime;
    private String viewTime;
    private String msgFrom;
    private int objId;
    private int showTime;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getHideTime() {
        return hideTime;
    }

    public void setHideTime(String hideTime) {
        this.hideTime = hideTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getViewTime() {
        return viewTime;
    }

    public void setViewTime(String viewTime) {
        this.viewTime = viewTime;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public int getObjId() {
        return objId;
    }

    public void setObjId(int objId) {
        this.objId = objId;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
