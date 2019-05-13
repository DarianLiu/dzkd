package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 问题分类
 *
 * Created by Administrator on 2018/4/27 0027.
 */

public class QuestionTypeBean implements Serializable{

    /**
     * id : 3
     * title : 常见问题
     * sort : 1
     * relevantUrl : null
     * createUser : 1
     * createTime : 2017-12-20 18:31:54
     * modifyUser : null
     * modifyTime : 2017-12-20 18:31:54
     * delFlag : 0
     */

    private int id;
    private String title;
    private int sort;
    private String relevantUrl;
    private int createUser;
    private String createTime;
    private String modifyUser;
    private String modifyTime;
    private int delFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getRelevantUrl() {
        return relevantUrl;
    }

    public void setRelevantUrl(String relevantUrl) {
        this.relevantUrl = relevantUrl;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }
}
