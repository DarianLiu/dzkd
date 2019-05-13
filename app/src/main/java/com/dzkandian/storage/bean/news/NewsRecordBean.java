package com.dzkandian.storage.bean.news;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 奖励记录（资讯）
 * Created by LiuLi on 2018/10/30.
 */
@Entity(nameInDb = "RECORD_NEWS")
public class NewsRecordBean {

    @Id(autoincrement = true)//自增长
    private Long id;
    private String aId;        //是 string	文章ID
    private String aType;      //是	string	文章类型
    private Integer aScale;       //是	int	观看奖励刻度（刻度，范围0-100）
    private String ad; //广告(第三方url或其他数据)
    private Integer no;//文章列表的顺序
    private Integer action;//文章行为标识：默认0 进入详情页入口

    @Generated(hash = 1277745366)
    public NewsRecordBean(Long id, String aId, String aType, Integer aScale,
            String ad, Integer no, Integer action) {
        this.id = id;
        this.aId = aId;
        this.aType = aType;
        this.aScale = aScale;
        this.ad = ad;
        this.no = no;
        this.action = action;
    }

    @Generated(hash = 1825439919)
    public NewsRecordBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAId() {
        return this.aId;
    }

    public void setAId(String aId) {
        this.aId = aId;
    }

    public String getAType() {
        return this.aType;
    }

    public void setAType(String aType) {
        this.aType = aType;
    }

    public Integer getAScale() {
        return this.aScale;
    }

    public void setAScale(Integer aScale) {
        this.aScale = aScale;
    }

    public String getAd() {
        return this.ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getNo() {
        return no;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Integer getAction() {
        return action;
    }
}
