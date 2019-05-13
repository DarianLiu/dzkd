package com.dzkandian.storage.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 搜索历史Bean
 * Created by LiuLi on 2019/1/8.
 */
@Entity
public class SearchHistoryBean {
    @Id(autoincrement = true)
    private Long id;
    private String searchKey;//搜索Key

    @Generated(hash = 1133207071)
    public SearchHistoryBean(Long id, String searchKey) {
        this.id = id;
        this.searchKey = searchKey;
    }

    @Generated(hash = 1570282321)
    public SearchHistoryBean() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchKey() {
        return searchKey;
    }
}
