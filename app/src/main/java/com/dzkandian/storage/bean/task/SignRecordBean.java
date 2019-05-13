package com.dzkandian.storage.bean.task;

import java.io.Serializable;
import java.util.List;

/**
 * 签到记录实体
 * Created by liuli on 2018/4/24.
 */

public class SignRecordBean implements Serializable{

    private boolean today;//今日是否已签到

    private List<SignStateBean> list;//签到列表

    public void setToday(boolean today) {
        this.today = today;
    }

    public boolean isToday() {
        return today;
    }

    public void setList(List<SignStateBean> list) {
        this.list = list;
    }

    public List<SignStateBean> getList() {
        return list;
    }
}
