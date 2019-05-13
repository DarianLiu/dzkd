package com.dzkandian.storage.event;

import java.io.Serializable;

/**
 * 更改绑定手机号事件
 * Created by liuli on 2018/4/27.
 */

public class UpdatePhoneEvent implements Serializable {
    private String code;
    private int type;

    public UpdatePhoneEvent(int type, String code) {
        this.code = code;
        this.type = type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }
}
