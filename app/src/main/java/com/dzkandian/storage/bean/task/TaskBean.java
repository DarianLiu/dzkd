package com.dzkandian.storage.bean.task;

import java.io.Serializable;

/**
 * 日常任务实体
 * Created by liuli on 2018/4/24.
 */

public class TaskBean implements Serializable{

    /**
     * id : 35
     * name : 阅读资讯
     * type : daily
     * activated : 0
     * receive : false
     * goldMax : 100
     * goldMini : 100
     * icon : gold
     * profit : +100
     * btnUrl : null
     * describe : 阅读新闻到一定时间，即可自动获得奖励
     * btnText : 阅读资讯
     * event : readNews
     * openTime : null
     * endTime : null
     * duration : 300
     * rersion : null
     * sort : 1000
     * createTime : 2017-12-29 17:48:55
     * createUser : 1
     * modifyTime : 2017-12-29 17:49:09
     * modifyUser : 1
     * delFlag : 0
     */

    private int id;//任务id，完成日常任务时需要出示
    private String name;//任务名称
    private String type;//任务类型
    private int activated;
    private int receive;//任务状态，0未完成，1待领取，2已领取
    private int goldMax;
    private int goldMini;
    private String icon;//图标：gold(金币)、red_envelopes(红包)
    private String profit;//收益
    private String btnUrl;//点击按钮后的跳转链接或被分享的链接，如果时innerJump或者outnerJump
    private String describe;//任务描述
    private String btnText;//按钮文字
    private String event;//事件：bindWeChat(绑定微信)bindAlipay(绑定支付宝)bindPhone(绑定手机)fullInfo(完善资料)readNews(阅读资讯)watchVideo(看视频)withdrawals(提现)invitation(邀请)innerJump(APP内跳转链接)outnerJump(第三方浏览器跳转链接)shareToGroup(分享到微信群)shareToCOF(分享到朋友圈)
    private String openTime;
    private String endTime;
    private int duration;//是否计时任务，-1为非计时任务 例如一个innerJump事件，点击后开始计时，当时间达到duration后，按钮亮起
    private String rersion;
    private int sort;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
    private int delFlag;
    private boolean isExpend;//是否展开

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

    public int getReceive() {
        return receive;
    }

    public void setReceive(int receive) {
        this.receive = receive;
    }

    public int getGoldMax() {
        return goldMax;
    }

    public void setGoldMax(int goldMax) {
        this.goldMax = goldMax;
    }

    public int getGoldMini() {
        return goldMini;
    }

    public void setGoldMini(int goldMini) {
        this.goldMini = goldMini;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getBtnUrl() {
        return btnUrl;
    }

    public void setBtnUrl(String btnUrl) {
        this.btnUrl = btnUrl;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getRersion() {
        return rersion;
    }

    public void setRersion(String rersion) {
        this.rersion = rersion;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(int modifyUser) {
        this.modifyUser = modifyUser;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public void setExpend(boolean expend) {
        isExpend = expend;
    }

    public boolean isExpend() {
        return isExpend;
    }
}
