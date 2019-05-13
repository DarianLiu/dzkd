package com.dzkandian.storage.bean;

import java.io.Serializable;

/**
 * 用户信息
 * Created by LiuLi on 2018/4/18.
 */

public class UserBean implements Serializable{
    private String token;
    private String expire;
    private String message;//"只在绑定手机号code为524时使用"

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getExpire() {
        return expire;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
