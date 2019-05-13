package com.dzkandian.common.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/** RecyclerView使用GridLayoutManager间距设置：使用RecyclerView设置间距，需要重写RecyclerView.ItemDecoration这个类。
 * Created by Administrator on 2018/11/2.
 */

public class RecyclerItemDecoration extends RecyclerView.ItemDecoration{
    private int itemSpace;

    /**
     * @param itemSpace item间隔
     */
    public RecyclerItemDecoration(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = itemSpace;
        outRect.bottom = itemSpace;
        //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) %2==0) {
            outRect.left = 0;
        }
    }
}
