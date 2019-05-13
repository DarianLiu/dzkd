package com.dzkandian.app.exception;

/**
 * 业务逻辑处理
 * Created by LiuLi on 2018/4/10.
 */

public class ApiException extends Exception{
    private int code;

    public ApiException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
