package com.dzkandian.storage.event;

public class ThumbsUpEvent {
    //根评论id
   private Long id;
    //点赞itemview的位置
   private int position;

    public ThumbsUpEvent(Long id, int position) {
        this.id = id;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
