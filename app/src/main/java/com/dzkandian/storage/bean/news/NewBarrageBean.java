package com.dzkandian.storage.bean.news;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 弹幕数据实体
 * Created by Administrator on 2018/8/28.
 */

public class NewBarrageBean implements Serializable {
    private String cmtCount;
    private int collection;

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    @SerializedName("list")
    private List<BarrageBean> barrageBeans;

    public String getCmtCount() {
        return cmtCount;
    }

    public void setCmtCount(String cmtCount) {
        this.cmtCount = cmtCount;
    }

    public List<BarrageBean> getBarrageBeans() {
        return barrageBeans;
    }

    public void setBarrageBeans(List<BarrageBean> barrageBeans) {
        this.barrageBeans = barrageBeans;
    }
}
