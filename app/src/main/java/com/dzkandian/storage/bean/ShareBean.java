package com.dzkandian.storage.bean;

/**
 * 分享所需数据集合实体
 */
public class ShareBean {
    public String title;//分享标题
    public String content;//分享内容
    public String imageUrl;//分享图Url
    public String imagePath;//分享图本地地址
    public String pageUrl;//分享网页地址
    public String[] imgUrls;//多图分享图片Url
    public String inviteCode; // （邀请码）分享小程序携带

    public String qqShareType;//QQ分享方式
    public String qZoneShareType;//QQ空间分享方式
    public String weChatShareType;//微信好友分享方式
    public String weChatMomentsShareType;//微信朋友圈分享方式
    public String sinaShareType;//新浪微博分享方式
    public boolean isUserHead;//是否使用用户的头像；（任务中心，好友邀请，web活动使用用户头像）
    public String wxAppInviteImage;//微信小程序图片地址（新增）

    private ShareBean(Builder builder) {
        this.title = builder.title;
        this.content = builder.content;
        this.imageUrl = builder.imageUrl;
        this.imagePath = builder.imagePath;
        this.pageUrl = builder.pageUrl;
        this.imgUrls = builder.imgUrls;
        this.inviteCode = builder.inviteCode;

        this.qqShareType = builder.qqShareType;
        this.qZoneShareType = builder.qZoneShareType;
        this.weChatShareType = builder.weChatShareType;
        this.weChatMomentsShareType = builder.weChatMomentsShareType;
        this.sinaShareType = builder.sinaShareType;
        this.isUserHead = builder.isUserHead;
        this.wxAppInviteImage = builder.wxAppInviteImage;
    }

    public static class Builder {
        private String title;//分享标题
        private String content;//分享内容
        private String imageUrl;//分享图Url
        private String imagePath;//分享图本地地址
        private String pageUrl;//分享网页地址
        private String[] imgUrls;//多图分享图片Url
        public String inviteCode; // （邀请码）分享小程序携带

        private String qqShareType;//QQ分享方式
        private String qZoneShareType;//QQ空间分享方式
        private String weChatShareType;//微信好友分享方式
        private String weChatMomentsShareType;//微信朋友圈分享方式
        private String sinaShareType;//新浪微博分享方式
        private boolean isUserHead;//是否使用用户的头像；
        private String wxAppInviteImage;//微信小程序图片地址（新增）

        public Builder wxAppInviteImage(String wxAppInviteImage) {
            this.wxAppInviteImage = wxAppInviteImage;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder inviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder pageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
            return this;
        }

        public Builder imgUrls(String[] imgUrls) {
            this.imgUrls = imgUrls;
            return this;
        }

        public Builder qqShareType(String qqShareType) {
            this.qqShareType = qqShareType;
            return this;
        }

        public Builder qZoneShareType(String qZoneShareType) {
            this.qZoneShareType = qZoneShareType;
            return this;
        }

        public Builder weChatShareType(String weChatShareType) {
            this.weChatShareType = weChatShareType;
            return this;
        }

        public Builder weChatMomentsShareType(String weChatMomentsShareType) {
            this.weChatMomentsShareType = weChatMomentsShareType;
            return this;
        }

        public Builder sinaShareType(String sinaShareType) {
            this.sinaShareType = sinaShareType;
            return this;
        }

        public Builder isUserHead(boolean isUserHead) {
            this.isUserHead = isUserHead;
            return this;
        }

        public ShareBean create() {
            return new ShareBean(this);
        }

    }

}