package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/7 0007.
 */

public class MessageBean implements Serializable {

    /**
     * "id": 2,
     * "objId": 46176,
     * "userId": 46176,
     * "username": "大看点",
     * "avatar": "https://file.dzkandian.com/default-head-icon.png?x-oss-process=image/resize,m_fill,h_132,w_132",
     * "content": "赞了我的评论",
     * "flag": "1",
     * "type": "news",
     * "columnType": "健身",
     * "title": "每天都是新的一天，瑜伽只需小小的空间，效果不比上健身房差哦！",
     * "url": "http://lua.frp.dzkandian.com/a/news.html?type=å¥èº«&id=6ba635e2d5102d4093de28621b9b514a",
     * "images": "//05imgmini.eastday.com/mobile/20180905/20180905082130_a486c93e736f34b0c52afb44d9b96d6f_1_mwpm_03200403.jpg",
     * "source": "练瑜伽更美丽",
     * "describle": null,
     * "webUrl": null,
     * "canShare": "1",
     * "delFlag": "0",
     * "createTime": "2018-12-03 20:17:23",
     * "updateTime": "2018-12-03 20:17:23",
     * "aid": "6ba635e2d5102d4093de28621b9b514a"
     */

    private String objId;                         //当前用户ID
    private String objname;                     //当前用户名
    private String objImage;                    //当前用户头像
    private String userId;                        //点赞用户ID
    private String username;                    //点赞用户名
    private String avatar;                      //点赞用户名
    private int flag;                           //标识(0：评论；1：点赞)
    private String id;                          //资讯/视频ID
    private String commentId;                     //评论根ID
    private String replyId;                       //回复  ID
    private String title;                       //标题
    private String url;                         //资讯/视频链接
    private String type;                        //类型（news,video,wuli）
    private String columnType;                  //栏目
    private String source;                      //来源
    private String updateTime;                  //资讯/视频更新时间
    private String canShare;                    //是否可以分享
    private String content;                     //点赞/评论内容
    private String webUrl;                      //视频链接
    private String images;                      //资讯/视频图片
    private String describle;                   //分享内容
    private String createTime;                  //创建时间

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getObjname() {
        return objname;
    }

    public void setObjname(String objname) {
        this.objname = objname;
    }

    public String getObjImage() {
        return objImage;
    }

    public void setObjImage(String objImage) {
        this.objImage = objImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
