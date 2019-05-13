package com.dzkandian.storage.bean;

import java.io.Serializable;

/**
 * 版本信息
 * Created by LiuLi on 2018/4/20.
 */

public class VersionBean implements Serializable{

    /**
     * id : 4
     * appId : dzkandian
     * version : 1.1.0
     * apkUrl : https://watch-everyday.oss-cn-shenzhen.aliyuncs.com/custom/20180417/818eea66993b4322881c7d5a2d68fc7f.apk
     * force : 1
     * describe : 收徒赚钱功能全新上线。
     * 更新了很多不满意的地方，这次应该稳了。
     * 请原谅我以前所有的不是，更新新版继续使用，并卸载旧版。
     * 如有问题，可前往官网下载：dzkandian.com
     * openDown : 1
     * openDownTime : null
     * createTime : 2018-04-17 20:20:53
     * createUser : 2
     * modifyTime : 2018-04-17 21:23:43
     * modifyUser : 1
     * delFlag : 0
     */

    private String id;
    private String appId;
    private String version;
    private String apkUrl;
    private int force;//是否强制更新 0否1是
    private int versionCode;
    private String describe;
    private int openDown;
    private String openDownTime;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
    private int delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getOpenDown() {
        return openDown;
    }

    public void setOpenDown(int openDown) {
        this.openDown = openDown;
    }

    public Object getOpenDownTime() {
        return openDownTime;
    }

    public void setOpenDownTime(String openDownTime) {
        this.openDownTime = openDownTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(int modifyUser) {
        this.modifyUser = modifyUser;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }
}
