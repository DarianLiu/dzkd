package com.dzkandian.storage;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * 栏目保存到本地
 * Created by Administrator on 2018/5/14.
 */

public class ColumnBean implements Serializable {

    private int viewSize;
    private List<String> allColumn;

    public void setAllColumn(List<String> allColumn) {
        this.allColumn = allColumn;
    }

    public int getViewSize() {
        return viewSize;
    }

    public void setViewSize(int viewSize) {
        this.viewSize = viewSize;
    }

    public List<String> getAllColumn() {
        return allColumn;
    }

    @NonNull
    public List<String> getViewColumn() {
        return allColumn.subList(0, viewSize);
    }
}
