package com.dzkandian.storage.bean.video;

import com.baidu.mobad.feeds.NativeResponse;
import com.bdtt.sdk.wmsdk.TTFeedAd;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

/**
 * 视频实体
 * Created by LiuLi on 2018/4/13.
 */
@Entity
public class VideoBean implements Serializable {

    private static final long serialVersionUID = 4945384271347183034L;
    /**
     * id : f4b856bc31c742c6934ce569e7f7bb48
     * webUrl : http://mtv.mop.com/video/180413000109290886107.html
     * source : ♡风一样的女子
     * title : 蒲公英丰收啦，原来是这样采集的
     * updateTime : 2018-04-13 09:29
     * avatar : //00.imgmini.eastday.com/touxiang/ae67ab928544217f07502aeb0ae9c3a4.jpg
     * url : http://mv.eastday.com/vpaike/20180413/20180413000109290886107_1_06400360.mp4
     * duration : 11
     * playbackCount : 577
     * thumbUrl : //08.imgmini.eastday.com/video/vpaike/20180413/20180413000109290886107_1_mwpm_03201609.jpeg
     */
    @Id(autoincrement = true)
    private Long ID;
    @SerializedName("id")
    private String videoId;           //视频ID
    private String webUrl;       //视频网页地址
    private String source;       //来源/作者
    private String title;        //标题
    private String updateTime;   //更新时间
    private String avatar;       //头像
    private String url;          //视频地址
    private String duration;     //视频时长（秒）
    private String playbackCount;//播放量
    private String thumbUrl;     //视频缩略图
    private String type;         //类型：ad或video
    private int adType;       //广告类型：自营:0 /广点通:1 /百度:2
    @Transient
    @SerializedName("ad_type")
    private String adNewType;       //广告类型：AD_OWN:自营 BaiDu：百度 CSJ:穿山甲 GDT:广点通
    @Transient
    @SerializedName("ad_info_click_url")
    private String adClickUrl;   //打底广告点击后要跳转的地址
    @Transient
    @SerializedName("ad_info_click_action")
    private String adClickAction; //打底广告的点击后的操作
    @Transient
    @SerializedName("ad_info_images")
    private List<String> adImage; //打底广告图片集合
    @Transient
    @SerializedName("ad_info_title")
    private String adTitle;       //打底广告标题
    private String canShare;     //是否可以分享
    private String describe;     //分享的描述内容
    private boolean isGDTAD;     //是否广点通广告
    private String width;        //图片宽度
    private String height;       //图片高度

    @Transient
    private NativeResponse nativeResponse;

    @Transient
    private TTFeedAd ttFeedAd;

    @Generated(hash = 1585042252)
    public VideoBean(Long ID, String videoId, String webUrl, String source, String title, String updateTime, String avatar,
            String url, String duration, String playbackCount, String thumbUrl, String type, int adType, String canShare,
            String describe, boolean isGDTAD, String width, String height) {
        this.ID = ID;
        this.videoId = videoId;
        this.webUrl = webUrl;
        this.source = source;
        this.title = title;
        this.updateTime = updateTime;
        this.avatar = avatar;
        this.url = url;
        this.duration = duration;
        this.playbackCount = playbackCount;
        this.thumbUrl = thumbUrl;
        this.type = type;
        this.adType = adType;
        this.canShare = canShare;
        this.describe = describe;
        this.isGDTAD = isGDTAD;
        this.width = width;
        this.height = height;
    }

    @Generated(hash = 2024490299)
    public VideoBean() {
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeight() {
        return height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth() {
        return width;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getCanShare() {
        return canShare;
    }

    public void setCanShare(String canShare) {
        this.canShare = canShare;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdNewType() {
        return adNewType;
    }

    public void setAdNewType(String adNewType) {
        this.adNewType = adNewType;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String id) {
        this.videoId = id;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPlaybackCount() {
        return playbackCount;
    }

    public void setPlaybackCount(String playbackCount) {
        this.playbackCount = playbackCount;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public void setNativeResponse(NativeResponse nativeResponse) {
        this.nativeResponse = nativeResponse;
    }

    public NativeResponse getNativeResponse() {
        return nativeResponse;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public boolean getIsGDTAD() {
        return this.isGDTAD;
    }

    public void setIsGDTAD(boolean isGDTAD) {
        this.isGDTAD = isGDTAD;
    }

    public String getAdClickUrl() {
        return adClickUrl;
    }

    public void setAdClickUrl(String adClickUrl) {
        this.adClickUrl = adClickUrl;
    }

    public String getAdClickAction() {
        return adClickAction;
    }

    public void setAdClickAction(String adClickAction) {
        this.adClickAction = adClickAction;
    }

    public List<String> getAdImage() {
        return adImage;
    }

    public void setAdImage(List<String> adImage) {
        this.adImage = adImage;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public TTFeedAd getTtFeedAd() {
        return ttFeedAd;
    }

    public void setTtFeedAd(TTFeedAd ttFeedAd) {
        this.ttFeedAd = ttFeedAd;
    }
}
