package com.dzkandian.storage.bean.news;

import com.baidu.mobad.feeds.NativeResponse;

import java.io.Serializable;
import java.util.List;

/**
 * 新闻资讯实体
 * Created by LiuLi on 2018/4/13.
 */

public class NewsBean implements Serializable {

    /**
     * id : 29935713
     * bigPicUrl : //publish-pic-cpu.baidu.com/8773f15f-846a-4bf4-8cb3-e0df13d27762.jpeg@w_1221,h_684
     * title : 超级芝顿一下子召唤了5只怪兽，幸亏老一辈奥特曼及时相救！
     * updateTime :  刚刚
     * images : ["//publish-pic-cpu.baidu.com/8773f15f-846a-4bf4-8cb3-e0df13d27762.jpeg@w_1221,h_684"]
     * url : http://cpu.baidu.com/1022/e37d0aff/detail/29935713/video?from=list&rts=1&scid=9881
     * click : 14329
     * source : 元气迷漫
     * type : video
     */

    private String id;
    private String bigPicUrl;
    private String title;
    private String updateTime;
    private String url;
    private String click;
    private String source;
    private String type;
    private List<String> images;
    private boolean isGDTAD;//是否广点通广告
    private String canShare; //看点列表  是否能分享
    private int top;//1为置顶，0或无该字段为不置顶
    private int adType;//广告类型：自营:0 /广点通:1 /百度:2
    private NativeResponse nativeResponse;

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public NativeResponse getNativeResponse() {
        return nativeResponse;
    }

    public void setNativeResponse(NativeResponse nativeResponse) {
        this.nativeResponse = nativeResponse;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public String getCanShare() {
        return canShare;
    }

    public void setCanShare(String canShare) {
        this.canShare = canShare;
    }

    public boolean isGDTAD() {
        return isGDTAD;
    }

    public void setGDTAD(boolean GDTAD) {
        isGDTAD = GDTAD;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBigPicUrl() {
        return bigPicUrl;
    }

    public void setBigPicUrl(String bigPicUrl) {
        this.bigPicUrl = bigPicUrl;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
