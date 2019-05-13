package com.dzkandian.storage.bean.news;


import java.io.Serializable;
import java.util.List;

/**
 * 新闻资讯/视频标题实体
 * Created by 12 on 2018/4/9.
 */

public class NewsTitleBean implements Serializable{

    private List<String> list;

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

}
