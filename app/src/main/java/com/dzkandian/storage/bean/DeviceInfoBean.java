package com.dzkandian.storage.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

/**
 * 设备信息
 * Created by Administrator on 2018/6/13.
 */

@Entity
public class DeviceInfoBean implements Serializable {

    private static final long serialVersionUID = 8743576699820823153L;

    @Id(autoincrement = true)
    @Property(nameInDb = "_id")
    private Long id;
    private Integer level;//移动端安全级别，为1时放行虚拟机和ROOT用户、为2时不放行虚拟机用户、为3时两者都不放行
    private Integer newsActivity;//未读活动公告
    private Integer newsMessage;//未读反馈回复消息
    private Integer newsArticle;//未读我的评论回复
    private Integer readGoldCircles;//资讯金币转圈数
    private Integer videoGoldCircles;//视频金币转圈数
    private Integer maxSameTouchAreaCount;//最大相同触摸面积次数
    private Integer indexPop; //首页弹窗类型：//0：什么也不做//1：显示新手红包//2：成功领取新手红包//3：显示活动页面//4：成功领取召回红包
    private String indexPopAttachData; //弹窗附加数据：    indexPop=2或4时，此值为红包金额如8.88    indexPop=3时，此值为活动url链接
    private String indexPopActivityPic;//首页弹窗活动图片地址
    private String indexPopActivityEvent;//首页弹窗活动事件：内部跳转：innerJump  外部跳转：outnerJump  好友邀请页：invitation  快速提现：withdrawals  活动公告页：activityPage  我的消息页面：myMsgPage
    private Integer indexPopActivityHour;//首页弹窗活动时频;
    private String indexActivityIconUrl;//首页右下图标URL;
    private String indexActivityIconPic;//首页右下角图片地址;
    private String riskDdeviceNname; //脚本程序APP包名
    private Integer hasApprentice;//是否有收徒;
    private Integer finishNoviceTask;//是否完成新手任务;
    private Integer todaySign;//今日是否签到;
    private String gotoInvitationPic;//收徒引导弹窗图片URL
    private String gotoSignPic;//签到引导弹窗图片URL
    private String gotoTaskCenterPic;//新手任务引导弹窗图片URL
    private String weixinPayAppid;//微信支付绑定appid
    private Integer readGoldPercent;//资讯金币转圈数完后的百分比数值，填的范围：0-99
    private Integer videoGoldPercent;//视频金币转圈数完后的百分比数值，填的范围：0-99

    @Generated(hash = 191784066)
    public DeviceInfoBean(Long id, Integer level, Integer newsActivity, Integer newsMessage, Integer newsArticle, Integer readGoldCircles, Integer videoGoldCircles,
            Integer maxSameTouchAreaCount, Integer indexPop, String indexPopAttachData, String indexPopActivityPic, String indexPopActivityEvent, Integer indexPopActivityHour,
            String indexActivityIconUrl, String indexActivityIconPic, String riskDdeviceNname, Integer hasApprentice, Integer finishNoviceTask, Integer todaySign, String gotoInvitationPic,
            String gotoSignPic, String gotoTaskCenterPic, String weixinPayAppid, Integer readGoldPercent, Integer videoGoldPercent) {
        this.id = id;
        this.level = level;
        this.newsActivity = newsActivity;
        this.newsMessage = newsMessage;
        this.newsArticle = newsArticle;
        this.readGoldCircles = readGoldCircles;
        this.videoGoldCircles = videoGoldCircles;
        this.maxSameTouchAreaCount = maxSameTouchAreaCount;
        this.indexPop = indexPop;
        this.indexPopAttachData = indexPopAttachData;
        this.indexPopActivityPic = indexPopActivityPic;
        this.indexPopActivityEvent = indexPopActivityEvent;
        this.indexPopActivityHour = indexPopActivityHour;
        this.indexActivityIconUrl = indexActivityIconUrl;
        this.indexActivityIconPic = indexActivityIconPic;
        this.riskDdeviceNname = riskDdeviceNname;
        this.hasApprentice = hasApprentice;
        this.finishNoviceTask = finishNoviceTask;
        this.todaySign = todaySign;
        this.gotoInvitationPic = gotoInvitationPic;
        this.gotoSignPic = gotoSignPic;
        this.gotoTaskCenterPic = gotoTaskCenterPic;
        this.weixinPayAppid = weixinPayAppid;
        this.readGoldPercent = readGoldPercent;
        this.videoGoldPercent = videoGoldPercent;
    }

    @Generated(hash = 784809703)
    public DeviceInfoBean() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getRiskDdeviceNname() {
        return riskDdeviceNname;
    }

    public void setRiskDdeviceNname(String riskDdeviceNname) {
        this.riskDdeviceNname = riskDdeviceNname;
    }

    public int getIndexPopActivityHour() {
        return indexPopActivityHour;
    }

    public void setIndexPopActivityHour(int indexPopActivityHour) {
        this.indexPopActivityHour = indexPopActivityHour;
    }

    public String getIndexActivityIconUrl() {
        return indexActivityIconUrl;
    }

    public void setIndexActivityIconUrl(String indexActivityIconUrl) {
        this.indexActivityIconUrl = indexActivityIconUrl;
    }

    public String getIndexPopActivityPic() {
        return indexPopActivityPic;
    }

    public void setIndexPopActivityPic(String indexPopActivityPic) {
        this.indexPopActivityPic = indexPopActivityPic;
    }

    public String getIndexPopActivityEvent() {
        return indexPopActivityEvent;
    }

    public void setIndexPopActivityEvent(String indexPopActivityEvent) {
        this.indexPopActivityEvent = indexPopActivityEvent;
    }

    public Integer getIndexPop() {
        return indexPop;
    }

    public void setIndexPop(Integer indexPop) {
        this.indexPop = indexPop;
    }

    public String getIndexPopAttachData() {
        return indexPopAttachData;
    }

    public void setIndexPopAttachData(String indexPopAttachData) {
        this.indexPopAttachData = indexPopAttachData;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getNewsActivity() {
        return newsActivity;
    }

    public void setNewsActivity(Integer newsActivity) {
        this.newsActivity = newsActivity;
    }

    public Integer getNewsMessage() {
        return newsMessage;
    }

    public void setNewsMessage(int newsMessage) {
        this.newsMessage = newsMessage;
    }

    public void setReadGoldCircles(int readGoldCircles) {
        this.readGoldCircles = readGoldCircles;
    }

    public int getReadGoldCircles() {
        return readGoldCircles;
    }

    public void setVideoGoldCircles(int videoGoldCircles) {
        this.videoGoldCircles = videoGoldCircles;
    }

    public Integer getVideoGoldCircles() {
        return videoGoldCircles;
    }

    public void setMaxSameTouchAreaCount(Integer maxSameTouchAreaCount) {
        this.maxSameTouchAreaCount = maxSameTouchAreaCount;
    }

    public Integer getMaxSameTouchAreaCount() {
        return maxSameTouchAreaCount;
    }

    public Integer getHasApprentice() {
        return this.hasApprentice;
    }

    public void setHasApprentice(Integer hasApprentice) {
        this.hasApprentice = hasApprentice;
    }

    public Integer getFinishNoviceTask() {
        return this.finishNoviceTask;
    }

    public void setFinishNoviceTask(Integer finishNoviceTask) {
        this.finishNoviceTask = finishNoviceTask;
    }

    public Integer getTodaySign() {
        return this.todaySign;
    }

    public void setTodaySign(Integer todaySign) {
        this.todaySign = todaySign;
    }

    public String getGotoInvitationPic() {
        return this.gotoInvitationPic;
    }

    public void setGotoInvitationPic(String gotoInvitationPic) {
        this.gotoInvitationPic = gotoInvitationPic;
    }

    public String getGotoSignPic() {
        return this.gotoSignPic;
    }

    public void setGotoSignPic(String gotoSignPic) {
        this.gotoSignPic = gotoSignPic;
    }

    public String getGotoTaskCenterPic() {
        return this.gotoTaskCenterPic;
    }

    public void setGotoTaskCenterPic(String gotoTaskCenterPic) {
        this.gotoTaskCenterPic = gotoTaskCenterPic;
    }

    public String getIndexActivityIconPic() {
        return this.indexActivityIconPic;
    }

    public void setIndexActivityIconPic(String indexActivityIconPic) {
        this.indexActivityIconPic = indexActivityIconPic;
    }

    public String getWeixinPayAppid() {
        return this.weixinPayAppid;
    }

    public void setWeixinPayAppid(String weixinPayAppid) {
        this.weixinPayAppid = weixinPayAppid;
    }

    public void setNewsMessage(Integer newsMessage) {
        this.newsMessage = newsMessage;
    }

    public void setReadGoldCircles(Integer readGoldCircles) {
        this.readGoldCircles = readGoldCircles;
    }

    public void setVideoGoldCircles(Integer videoGoldCircles) {
        this.videoGoldCircles = videoGoldCircles;
    }

    public void setIndexPopActivityHour(Integer indexPopActivityHour) {
        this.indexPopActivityHour = indexPopActivityHour;
    }

    public Integer getReadGoldPercent() {
        return this.readGoldPercent;
    }

    public void setReadGoldPercent(Integer readGoldPercent) {
        this.readGoldPercent = readGoldPercent;
    }

    public Integer getVideoGoldPercent() {
        return this.videoGoldPercent;
    }

    public void setVideoGoldPercent(Integer videoGoldPercent) {
        this.videoGoldPercent = videoGoldPercent;
    }

    public Integer getNewsArticle() {
        return this.newsArticle;
    }

    public void setNewsArticle(Integer newsArticle) {
        this.newsArticle = newsArticle;
    }
}
