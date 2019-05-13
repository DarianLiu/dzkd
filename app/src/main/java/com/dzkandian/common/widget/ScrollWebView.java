package com.dzkandian.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ScrollWebView extends WebView {
    public OnScrollChangeListener listener;

    public ScrollWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollWebView(Context context) {
        super(context);
        init();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"InlinedApi", "SetJavaScriptEnabled"})
    private void init() {
        setClickable(true);
        WebSettings settings = getSettings();
        // basic
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setNeedInitialFocus(false);
        settings.setAllowFileAccess(true);


//        try {
//
//            if (getContext() != null) {
//                String dbPath = getContext().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//                // API 7, LocalStorage/SessionStorage
//                settings.setDomStorageEnabled(true);
//                settings.setDatabaseEnabled(true);
//                settings.setDatabasePath(dbPath);
//                // API 7， Web SQL Database, 需要重载方法（WebChromeClient）才能生效，无法只通过反射实现
//
//                // API 7， Application Storage
//                settings.setAppCacheEnabled(true);
//                settings.setAppCachePath(dbPath);
//                settings.setAppCacheMaxSize(5 * 1024 * 1024);
//
//                // API 5， Geolocation
//                settings.setGeolocationEnabled(true);
//                settings.setGeolocationDatabasePath(dbPath);
//            }
//        } catch (Exception e) {
//
//        }

        setWebChromeClient(new WebChromeClient());


        try {
            // API 19, open debug
            if (Build.VERSION.SDK_INT >= 19) {
                // WebView.setWebContentsDebuggingEnabled(true);

            }
        } catch (Exception e) {

        }
        // setWebViewClient(new XMyWebViewClient());
        // 关闭硬件加速，否则webview设置为透明，但是实际却是黑色背景
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }
//        } catch (Exception e) {
//            ;
//        }

//        ViewGroup.LayoutParams p =
//                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//        this.setLayoutParams(p);
//        this.setScrollContainer(false);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float webcontent = getContentHeight() * getScale();// webview的高度
        float webnow = getHeight() + getScrollY();// 当前webview的高度
//        Log.i("TAG1", "webview.getScrollY()====>>" + getScrollY());
        if (Math.abs(webcontent - webnow) < 1) {
            // 已经处于底端
            // Log.i("TAG1", "已经处于底端");
            listener.onPageEnd(l, t, oldl, oldt);
        } else if (getScrollY() == 0) {
            // Log.i("TAG1", "已经处于顶端");
            listener.onPageTop(l, t, oldl, oldt);
        } else {
            listener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.listener = listener;
    }

    public interface OnScrollChangeListener {
        public void onPageEnd(int l, int t, int oldl, int oldt);

        public void onPageTop(int l, int t, int oldl, int oldt);

        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}