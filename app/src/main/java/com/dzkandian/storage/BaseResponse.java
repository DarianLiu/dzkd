package com.dzkandian.storage;

import java.io.Serializable;

/**
 * 服务器返回结果基础实体
 * Created by 12 on 2018/4/10.
 */

public class BaseResponse<T> implements Serializable {
    private boolean status;
    private String msg;
    private int code;
    private T data;
    private Long timestamp;

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

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

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
