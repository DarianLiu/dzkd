package com.dzkandian.storage.bean.mine;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/13.
 */

public class AlipayInfoBean implements Serializable {
    private String target_id;
    private String rsaSign;

    public String getTargetId() {
        return target_id;
    }

    public void setTargetId(String target_id) {
        this.target_id = target_id;
    }

    public String getRsasign() {
        return rsaSign;
    }

    public void setRsasign(String rsaSign) {
        this.rsaSign = rsaSign;
    }
}
