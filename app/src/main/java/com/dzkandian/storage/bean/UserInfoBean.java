package com.dzkandian.storage.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 用户详细信息
 * Created by Administrator on 2018/4/25.
 */
@Entity
public class UserInfoBean implements Serializable {

    private static final long serialVersionUID = -3236254180529580513L;
    /**
     * username	string	用户名
     * phone	string	手机号，使用*保护中间4位
     * inviteCode	string	邀请码
     * avatar	string	头像
     * gender	int	性别：1为男，2为女，0为未设置
     * birthday	Date	生日yyyy-MM-dd格式
     * status	string	账号状态：NORMAL(正常)
     * alipayAccount	string	支付宝-账户
     * alipayName	string	支付宝-姓名
     * weixinPayName	string	微信钱包-姓名
     * weixinPayPhone	string	微信钱包-手机号
     * weixinPayNickname	string	微信钱包-昵称
     * weixinPayOpenid	string	微信钱包-绑定微信
     * 如果该值为null则表示未绑定
     * weixinPayAvatar	string	微信钱包-绑定用户头像
     * 如果已绑定则显示头像
     * createTime	string	用户注册时间
     * modifyTime	string	资料最后更新时间
     * disableFlag	string	用户是否被禁用，1为禁用
     * disableMsg	string	被禁用时的提示
     * disableTime	string	被禁时间
     * autoEnable	string	是否自动解禁
     * autoEnableTime	string	自动解禁时间
     * weixinBindOpenid	string	微信绑定
     * alipayBindOpenid	string	支付宝绑定
     * deviceId	string	用户注册时使用的设备id
     * weixinPayAppid	string	微信支付绑定appid
     */

    @Id(autoincrement = true)
    private Long userId;
    private String username;
    private String phone;
    private String password;
    private String salt;
    private String inviteCode;
    private String avatar;
    private Long gender;
    private String birthday;
    private String status;
    private String alipayAccount;
    private String alipayName;
    private String weixinPayName;
    private String weixinPayPhone;
    private String weixinPayNickname;
    private String weixinPayOpenid;
    private String weixinPayAvatar;
    private String createTime;
    private String modifyTime;
    private Long disableFlag;
    private String disableMsg;
    private String disableTime;
    private Long autoEnable;
    private String autoEnableTime;
    private String weixinBindOpenid;
    private String alipayBindOpenid;
    private String deviceId;
    private Long delFlag;
    private String weixinPayAppid;

    @Generated(hash = 1962948773)
    public UserInfoBean(Long userId, String username, String phone, String password, String salt,
            String inviteCode, String avatar, Long gender, String birthday, String status,
            String alipayAccount, String alipayName, String weixinPayName, String weixinPayPhone,
            String weixinPayNickname, String weixinPayOpenid, String weixinPayAvatar, String createTime,
            String modifyTime, Long disableFlag, String disableMsg, String disableTime, Long autoEnable,
            String autoEnableTime, String weixinBindOpenid, String alipayBindOpenid, String deviceId,
            Long delFlag, String weixinPayAppid) {
        this.userId = userId;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.salt = salt;
        this.inviteCode = inviteCode;
        this.avatar = avatar;
        this.gender = gender;
        this.birthday = birthday;
        this.status = status;
        this.alipayAccount = alipayAccount;
        this.alipayName = alipayName;
        this.weixinPayName = weixinPayName;
        this.weixinPayPhone = weixinPayPhone;
        this.weixinPayNickname = weixinPayNickname;
        this.weixinPayOpenid = weixinPayOpenid;
        this.weixinPayAvatar = weixinPayAvatar;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.disableFlag = disableFlag;
        this.disableMsg = disableMsg;
        this.disableTime = disableTime;
        this.autoEnable = autoEnable;
        this.autoEnableTime = autoEnableTime;
        this.weixinBindOpenid = weixinBindOpenid;
        this.alipayBindOpenid = alipayBindOpenid;
        this.deviceId = deviceId;
        this.delFlag = delFlag;
        this.weixinPayAppid = weixinPayAppid;
    }

    @Generated(hash = 1818808915)
    public UserInfoBean() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getInviteCode() {
        return this.inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getGender() {
        return this.gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlipayAccount() {
        return this.alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public String getAlipayName() {
        return this.alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getWeixinPayName() {
        return this.weixinPayName;
    }

    public void setWeixinPayName(String weixinPayName) {
        this.weixinPayName = weixinPayName;
    }

    public String getWeixinPayPhone() {
        return this.weixinPayPhone;
    }

    public void setWeixinPayPhone(String weixinPayPhone) {
        this.weixinPayPhone = weixinPayPhone;
    }

    public String getWeixinPayNickname() {
        return this.weixinPayNickname;
    }

    public void setWeixinPayNickname(String weixinPayNickname) {
        this.weixinPayNickname = weixinPayNickname;
    }

    public String getWeixinPayOpenid() {
        return this.weixinPayOpenid;
    }

    public void setWeixinPayOpenid(String weixinPayOpenid) {
        this.weixinPayOpenid = weixinPayOpenid;
    }

    public String getWeixinPayAvatar() {
        return this.weixinPayAvatar;
    }

    public void setWeixinPayAvatar(String weixinPayAvatar) {
        this.weixinPayAvatar = weixinPayAvatar;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getDisableFlag() {
        return this.disableFlag;
    }

    public void setDisableFlag(Long disableFlag) {
        this.disableFlag = disableFlag;
    }

    public String getDisableMsg() {
        return this.disableMsg;
    }

    public void setDisableMsg(String disableMsg) {
        this.disableMsg = disableMsg;
    }

    public String getDisableTime() {
        return this.disableTime;
    }

    public void setDisableTime(String disableTime) {
        this.disableTime = disableTime;
    }

    public Long getAutoEnable() {
        return this.autoEnable;
    }

    public void setAutoEnable(Long autoEnable) {
        this.autoEnable = autoEnable;
    }

    public String getAutoEnableTime() {
        return this.autoEnableTime;
    }

    public void setAutoEnableTime(String autoEnableTime) {
        this.autoEnableTime = autoEnableTime;
    }

    public String getWeixinBindOpenid() {
        return this.weixinBindOpenid;
    }

    public void setWeixinBindOpenid(String weixinBindOpenid) {
        this.weixinBindOpenid = weixinBindOpenid;
    }

    public String getAlipayBindOpenid() {
        return this.alipayBindOpenid;
    }

    public void setAlipayBindOpenid(String alipayBindOpenid) {
        this.alipayBindOpenid = alipayBindOpenid;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDelFlag() {
        return this.delFlag;
    }

    public void setDelFlag(Long delFlag) {
        this.delFlag = delFlag;
    }

    public String getWeixinPayAppid() {
        return this.weixinPayAppid;
    }

    public void setWeixinPayAppid(String weixinPayAppid) {
        this.weixinPayAppid = weixinPayAppid;
    }
}
