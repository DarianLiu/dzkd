package com.dzkandian.storage.bean.mine;

import java.io.Serializable;
import java.util.List;

/**
 * 一次性获取接口问题分类Bean
 * Created by Administrator on 2018/5/2 0002.
 */

public class QuestionAllBean implements Serializable{

    private String typeName;
    private  List<QuestionBean> list;

    public void setList(List<QuestionBean> list) {
        this.list = list;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<QuestionBean> getList() {
        return list;
    }

    public String getTypeName() {
        return typeName;
    }
}
