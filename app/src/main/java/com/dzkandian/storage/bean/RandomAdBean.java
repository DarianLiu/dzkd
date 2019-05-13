package com.dzkandian.storage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */

public class RandomAdBean implements Serializable {
    private String ad_type;//广告的类型①BaiDu，CSJ，GDT：联盟广告；    AD_OWN：自营广告；
    private String ad_info_click_url;//广告点击后要跳转的地址
    private String ad_info_click_action;//广告的点击后的操作①JUMP_DOWNLOAD_PDK：下载SDK      ②JUMP_EXTERNAL_URL：跳转外部URL   ③JUMP_INTERNAL_URL:跳转内部的URL
    private String ad_info_title;//广告的标题
    private List<String> ad_info_images;//图片集合，list类型以json格式进行返回

    public String getAd_type() {
        return ad_type;
    }

    public void setAd_type(String ad_type) {
        this.ad_type = ad_type;
    }

    public String getAd_info_click_url() {
        return ad_info_click_url;
    }

    public void setAd_info_click_url(String ad_info_click_url) {
        this.ad_info_click_url = ad_info_click_url;
    }

    public String getAd_info_click_action() {
        return ad_info_click_action;
    }

    public void setAd_info_click_action(String ad_info_click_action) {
        this.ad_info_click_action = ad_info_click_action;
    }

    public String getAd_info_title() {
        return ad_info_title;
    }

    public void setAd_info_title(String ad_info_title) {
        this.ad_info_title = ad_info_title;
    }

    public List<String> getAd_info_images() {
        return ad_info_images;
    }

    public void setAd_info_images(List<String> ad_info_images) {
        this.ad_info_images = ad_info_images;
    }
}
