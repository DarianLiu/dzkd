package com.dzkandian.storage.bean.news;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 评论
 * Created by Administrator on 2018/8/29.
 */

public class CommentRecordBean implements Serializable {

    /**
     * natural : [{"id":100003,"content":"活久见","userId":95,"userName":"小莫","userImg":"http://xxx.xxx.xx/zz.jpg","createTime":"2018-05-05 18:00:12"}]
     * total : 99+
     * lastId : 100003
     * commentType :
     */

    private int total;
    private Long lastId;
    @SerializedName("natural")
    private List<CommentBean> commentList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }


    public List<CommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentBean> natural) {
        this.commentList = natural;
    }
}
