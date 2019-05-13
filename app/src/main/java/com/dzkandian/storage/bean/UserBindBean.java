package com.dzkandian.storage.bean;

import java.io.Serializable;

/**
 * 用户第三方绑定信息
 * Created by liuli on 2018/4/24.
 */

public class UserBindBean implements Serializable{

    private String nickname;
    private String headimgurl;
    private int bind;//区别绑定与登录操作

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

    public void setBind(int bind) {
        this.bind = bind;
    }

    public int getBind() {
        return bind;
    }
}
