package com.dzkandian.mvp.common.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dzkandian.R;
import com.dzkandian.mvp.common.contract.PlayWebViewContract;
import com.dzkandian.mvp.common.di.component.DaggerPlayWebViewComponent;
import com.dzkandian.mvp.common.di.module.PlayWebViewModule;
import com.dzkandian.mvp.common.presenter.PlayWebViewPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class PlayWebViewActivity extends BaseActivity<PlayWebViewPresenter> implements PlayWebViewContract.View {

    //    @Nullable
//    @BindView(R.id.tv_toolbar_title)
//    TextView tvToolbarTitle;
//    @Nullable
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    @Nullable
    @BindView(R.id.web_play)
    WebView webView;

    private ProgressDialog progressDialog;//APK 下载进度条

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerPlayWebViewComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .playWebViewModule(new PlayWebViewModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_play_web_view; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        initWebView();


        String url = getIntent().getStringExtra("URL");
        webView.loadUrl(url);//加载需要显示的网页
        webView.setWebViewClient(new PlayWebViewActivity.MWebViewClient());//设置Web视图

    }

    private void initWebView() {
        webView.setEnabled(true);
        webView.setVerticalScrollBarEnabled(false);//去掉垂直滚动条
        WebSettings webSettings = webView.getSettings();//设置WebView属性
        webSettings.setJavaScriptEnabled(true);//设置WebView属性，能够执行Javascript脚本
        webSettings.setAllowFileAccess(false);//设置可以访问文件
        webSettings.setBuiltInZoomControls(false); //设置支持缩放
        webSettings.setBlockNetworkImage(true);//先加载文字，再加载图片
//        webView.setWebChromeClient(new WebChromeClient()); //设置允许JS交互弹窗

        /*设置自适应屏幕，两者合用*/
//        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式：默认加载方式
        webSettings.setDomStorageEnabled(true); //启用或禁用DOM缓存。

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    webSettings.setBlockNetworkImage(false);
                }
            }
        });
        webView.addJavascriptInterface(new PlayWebViewActivity.JavaScripObject(), "android");

//        webView.evaluateJavascript("window.CheckInstall_Return("+String.valueOf(1)+");", null);
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();

        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


    public class JavaScripObject {

        //H5触发APP方法-检验包名是否安装
        @JavascriptInterface
        public void CheckInstall(String packageName) {
            if (isAvilible(PlayWebViewActivity.this, packageName)) {

//                Timber.d("===================是否安装" + isAvilible(PlayWebViewActivity.this, packageName));
//                Timber.d("===================检验包名安装" + packageName);

                runOnUiThread(() -> {
                    if (webView != null) {
                        webView.evaluateJavascript("window.CheckInstall_Return(" + 1 + ");", null);
                    }
                });

            } else {
                runOnUiThread(() -> {
                    if (webView != null) {
                        webView.evaluateJavascript("window.CheckInstall_Return(" + 0 + ");", null);
                    }
                });

//                Timber.d("===================是否安装" + isAvilible(PlayWebViewActivity.this, packageName));
//                Timber.d("===================包名没有安装" + packageName);
            }

        }

        //H5触发APP方法-触发APP内下载      下载完成后会自动进行安装 【必须】
        @JavascriptInterface
        public void InstallAPP(String apkUrl) {
            Timber.d("===================触发APP内下载" + apkUrl);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(apkUrl))
                        launchActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)));
                }
            });

        }


        //、H5触发APP方法-触发APP打开指定包名
        @JavascriptInterface
        public void OpenAPP(String openPackage) {
            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent();
            intent = packageManager.getLaunchIntentForPackage(openPackage);
            if (intent == null) {
                Timber.d("===================触发APP打开指定包名没有安装" + openPackage);
            } else {
                startActivity(intent);
            }

            Timber.d("===================触发APP打开指定包名" + openPackage);

        }

        //H5触发APP方法-触发APP打开用户默认浏览器
        @JavascriptInterface
        public void Browser(String url) {
            Timber.d("===================触发APP打开用户默认浏览器" + url);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    launchActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });
        }


    }


    //Web视图    刚进入Web时调用
    private class MWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        //销毁WebView
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            //清理Webview缓存数据库
            try {
                deleteDatabase("webview.db");
                deleteDatabase("webviewCache.db");
            } catch (Exception e) {
                e.printStackTrace();
            }

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
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
