package com.dzkandian.storage.bean.video;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 奖励记录（视频）
 * Created by LiuLi on 2018/10/30.
 */
@Entity(nameInDb = "RECORD_VIDEO")
public class VideoRecordBean {

    @Id(autoincrement = true)//自增长
    private Long id;
    private String vId;    //是	string	文章ID
    private String vType;    //是	string	文章类型
    private Integer vScale;    //是	int	观看奖励刻度（刻度，范围0-100）
    @Generated(hash = 1702855346)
    public VideoRecordBean(Long id, String vId, String vType, Integer vScale) {
        this.id = id;
        this.vId = vId;
        this.vType = vType;
        this.vScale = vScale;
    }
    @Generated(hash = 1411832166)
    public VideoRecordBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getVId() {
        return this.vId;
    }
    public void setVId(String vId) {
        this.vId = vId;
    }
    public String getVType() {
        return this.vType;
    }
    public void setVType(String vType) {
        this.vType = vType;
    }
    public Integer getVScale() {
        return this.vScale;
    }
    public void setVScale(Integer vScale) {
        this.vScale = vScale;
    }

}