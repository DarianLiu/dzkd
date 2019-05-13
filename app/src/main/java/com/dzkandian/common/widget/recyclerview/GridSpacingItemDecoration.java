package com.dzkandian.common.widget.recyclerview;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import timber.log.Timber;

/**
 * 设置RecyclerView GridLayoutManager or StaggeredGridLayoutManager spacing
 * Created by john on 17-1-5.
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;
    private int spanCount;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, View view, @NonNull RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position

        GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = manager.getSpanSizeLookup();

        int spanSize = spanSizeLookup.getSpanSize(position);
        int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);

        if (spanSize == spanCount) {
//            if (position < spanCount) { // top edge
//                outRect.top = spacing;
//            }
            outRect.bottom = spacing; // item bottom
        } else if (includeEdge) {
            outRect.left = spacing - spanIndex * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (spanIndex + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount && position == spanIndex) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
//            Timber.d("========position " + position + "spanIndex " + spanIndex + "----spanSize" + spanSize);
        } else {
            outRect.left = spanIndex * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (spanIndex + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }

    }

}