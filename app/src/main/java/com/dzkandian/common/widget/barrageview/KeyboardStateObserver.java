package com.dzkandian.common.widget.barrageview;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2018/11/21.
 */

public class KeyboardStateObserver {

    private static final String TAG = KeyboardStateObserver.class.getSimpleName();

    public static KeyboardStateObserver getKeyboardStateObserver(Activity activity) {
        return new KeyboardStateObserver(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private OnKeyboardVisibilityListener listener;

    public void setKeyboardVisibilityListener(OnKeyboardVisibilityListener listener) {
        this.listener = listener;
    }

    private KeyboardStateObserver(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                if (listener != null) {
                    listener.onKeyboardShow();
                }
            } else {
                if (listener != null) {
                    listener.onKeyboardHide();
                }
            }
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);

        return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

    public interface OnKeyboardVisibilityListener {
        void onKeyboardShow();

        void onKeyboardHide();
    }
}