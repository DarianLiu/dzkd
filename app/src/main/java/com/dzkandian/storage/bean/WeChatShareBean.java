package com.dzkandian.storage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 微信分享实体
 * Created by Administrator on 2018/3/26 0026.
 */

public class WeChatShareBean implements Serializable {
    private String inviteTitle;         //分享标题
    private String inviteContent;       //分享内容
    private String inviteImage;         //单图片地址
    private String inviteURL;           //分享链接
    private String inviteCode;          //邀请码，暂时不用理会
    private List<String> inviteImages;  //图片数组
    private String sharingQQ;           //QQ分享方式
    private String sharingQqZone;       //QQ空间分享方式
    private String sharingWechat;       //微信分享方式
    private String sharingWechatCircle; //朋友圈分享方式
    private String sharingWebo;         //新浪微博分享方式
    private String faceToFaceInviteImg; //面对面收徒二维码图片

    public String getWxAppInviteImage() {
        return wxAppInviteImage;
    }

    public void setWxAppInviteImage(String wxAppInviteImage) {
        this.wxAppInviteImage = wxAppInviteImage;
    }

    private String wxAppInviteImage;//微信小程序图片地址（新增）

    public String getFaceToFaceInviteImg() {
        return faceToFaceInviteImg;
    }

    public void setFaceToFaceInviteImg(String faceToFaceInviteImg) {
        this.faceToFaceInviteImg = faceToFaceInviteImg;
    }

    public void setSharingWebo(String sharingWebo) {
        this.sharingWebo = sharingWebo;
    }

    public String getSharingWebo() {
        return sharingWebo;
    }

    public String getInviteTitle() {
        return inviteTitle;
    }

    public void setInviteTitle(String inviteTitle) {
        this.inviteTitle = inviteTitle;
    }

    public String getInviteImage() {
        return inviteImage;
    }

    public void setInviteImage(String inviteImage) {
        this.inviteImage = inviteImage;
    }

    public String getSharingQQ() {
        return sharingQQ;
    }

    public void setSharingQQ(String sharingQQ) {
        this.sharingQQ = sharingQQ;
    }

    public String getSharingQqZone() {
        return sharingQqZone;
    }

    public void setSharingQqZone(String sharingQqZone) {
        this.sharingQqZone = sharingQqZone;
    }

    public String getSharingWechat() {
        return sharingWechat;
    }

    public void setSharingWechat(String sharingWechat) {
        this.sharingWechat = sharingWechat;
    }

    public String getSharingWechatCircle() {
        return sharingWechatCircle;
    }

    public void setSharingWechatCircle(String sharingWechatCircle) {
        this.sharingWechatCircle = sharingWechatCircle;
    }

    public void setInviteContent(String inviteContent) {
        this.inviteContent = inviteContent;
    }

    public String getInviteContent() {
        return inviteContent;
    }

    public void setInviteURL(String inviteURL) {
        this.inviteURL = inviteURL;
    }

    public String getInviteURL() {
        return inviteURL;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteImages(List<String> inviteImages) {
        this.inviteImages = inviteImages;
    }

    public List<String> getInviteImages() {
        return inviteImages;
    }

}
