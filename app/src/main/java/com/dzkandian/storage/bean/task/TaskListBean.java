package com.dzkandian.storage.bean.task;

import java.io.Serializable;
import java.util.List;

/**
 * 任务中心实体（任务列表：日常任务列表，新手任务列表）
 * Created by LiuLi on 2018/4/24.
 */

public class TaskListBean implements Serializable{
    private List<TaskBean> daily;//日常任务列表
    private List<TaskBean> novice;//新手任务列表

    public void setDaily(List<TaskBean> daily) {
        this.daily = daily;
    }

    public List<TaskBean> getDaily() {
        return daily;
    }

    public void setNovice(List<TaskBean> novice) {
        this.novice = novice;
    }

    public List<TaskBean> getNovice() {
        return novice;
    }
}
