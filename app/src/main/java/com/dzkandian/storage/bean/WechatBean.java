package com.dzkandian.storage.bean;

import java.io.Serializable;

/**
 * 微信相关实体
 * Created by Administrator on 2018/4/24.
 */

public class WechatBean implements Serializable{
    private String msg;
    private int code;
    private int bind;
    private String nickname;
    private String headimgurl;
    private boolean status;
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }
    public int getBind() {
        return bind;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getNickname() {
        return nickname;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public boolean getStatus() {
        return status;
    }
}
