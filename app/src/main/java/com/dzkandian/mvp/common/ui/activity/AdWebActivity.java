package com.dzkandian.mvp.common.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.contract.AdWebContract;
import com.dzkandian.mvp.common.di.component.DaggerAdWebComponent;
import com.dzkandian.mvp.common.di.module.AdWebModule;
import com.dzkandian.mvp.common.presenter.AdWebPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 广告WebView：2019-01-18；
 */
public class AdWebActivity extends BaseActivity<AdWebPresenter> implements AdWebContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_progressbar)
    ProgressBar webProgressbar;
    @BindView(R.id.web_view)
    WebView webView;

    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerAdWebComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .adWebModule(new AdWebModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_ad_web;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        init();
        String url = getIntent().getStringExtra("AdUrl");
        String title = getIntent().getStringExtra("AdTitle");
        if (!TextUtils.isEmpty(title)) {
            tvToolbarTitle.setText(title);
        }
        webView.loadUrl(url);//加载需要显示的网页
        webView.setWebViewClient(new AdWebActivity.MyWebViewClient());//设置Web视图
    }

    private void init() {
        webView.setEnabled(true);
        webView.setVerticalScrollBarEnabled(false);//去掉垂直滚动条
        WebSettings webSettings = webView.getSettings();//设置WebView属性
        webSettings.setJavaScriptEnabled(true);//设置WebView属性，能够执行Javascript脚本
//        webSettings.setAllowFileAccess(false);//设置可以访问文件
        webSettings.setBuiltInZoomControls(false); //设置支持缩放
        /*设置自适应屏幕，两者合用*/
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式：默认加载方式
        webSettings.setDomStorageEnabled(true); //启用或禁用DOM缓存。

        /* 添加网页进度条事件：*/
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if (webProgressbar != null) {
                        webProgressbar.setVisibility(View.GONE);//加载完网页进度条消失
                        webView.evaluateJavascript("$('.ad-width-btn').remove();$('.page-info > .element:first').remove();$('.detail-bottom-ad').remove();$('#recommend-wrap').remove();$('.copyright').remove();$('.detail-source').remove();$('.jump-index').remove();", null);
                    }
                } else {
                    if (webProgressbar != null) {
                        webProgressbar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                        webProgressbar.setProgress(newProgress);//设置进度值
                    }
                }
            }
        });

        /*WebView文件下载监听事件：*/
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    //Web视图    刚进入Web时调用
    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        //销毁WebView
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        loadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.makeText(getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }
}
