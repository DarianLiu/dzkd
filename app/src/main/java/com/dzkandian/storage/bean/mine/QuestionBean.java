package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * 常见问题
 * Created by Administrator on 2018/4/27 0027.
 */

public class QuestionBean implements Serializable{

    /**
     * id : 1
     * type : 1
     * question : 问题1
     * answer : 我是答案啦啦啦我是答案啦啦啦我是答案啦啦啦
     * sort : 1
     * relevantUrl : null
     * relevantFaq : null
     * version : null
     * createUser : null
     * createTime : null
     * modifyUser : null
     * modifyTime : null
     * delFlag : 0
     */

    private int id;
    private int type;
    private String question;
    private String answer;
    private int sort;
    private String relevantUrl;
    private String relevantFaq;
    private String version;
    private String createUser;
    private String createTime;
    private String modifyUser;
    private String modifyTime;
    private int delFlag;
    private boolean isOpen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public String getRelevantFaq() {
        return relevantFaq;
    }

    public void setRelevantFaq(String relevantFaq) {
        this.relevantFaq = relevantFaq;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
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

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean open) {
        isOpen = open;
    }
}
