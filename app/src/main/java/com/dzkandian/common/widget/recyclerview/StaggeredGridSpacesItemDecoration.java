package com.dzkandian.common.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 瀑布流布局间距
 * Created by liuli on 2018/7/27.
 */

public class StaggeredGridSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public StaggeredGridSpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

        outRect.top = space / 2;
        outRect.bottom = space / 2;
        if (params.getSpanIndex() % 2 == 0) {
//            outRect.left = space;
            outRect.right = space / 2;
        } else {
            outRect.left = space / 2;
//            outRect.right = space;
        }

    }
}
