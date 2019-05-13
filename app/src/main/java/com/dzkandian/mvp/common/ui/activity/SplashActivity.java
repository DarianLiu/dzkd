package com.dzkandian.mvp.common.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.uitls.TelephoneUtils;
import com.dzkandian.common.uitls.emulator.EmuCheckUtil;
import com.dzkandian.common.uitls.root.CheckRoot;
import com.dzkandian.common.uitls.websockets.WebSocketServer;
import com.dzkandian.common.uitls.xposed.CheckXposed;
import com.dzkandian.common.widget.ringtextview.RingTextView;
import com.dzkandian.mvp.common.contract.SplashContract;
import com.dzkandian.mvp.common.di.component.DaggerSplashComponent;
import com.dzkandian.mvp.common.di.module.SplashModule;
import com.dzkandian.mvp.common.presenter.SplashPresenter;
import com.dzkandian.mvp.mine.ui.activity.QuickCashActivity;
import com.dzkandian.mvp.news.ui.activity.NewsDetailActivity;
import com.dzkandian.mvp.video.ui.activity.VideoDetailActivity;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 引导页
 */
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.splash_container)
    RelativeLayout splashContainer;
    @BindView(R.id.start_page_logo)
    ImageView startPageLogo;
    @BindView(R.id.ring)
    RingTextView ring;
    @BindView(R.id.ll_splash)
    RelativeLayout llSplash;

    public boolean flags = true;
    @BindView(R.id.iv_splash_bg)
    ImageView ivBackground;
    private boolean emulator;
    private boolean xposed;
    private boolean root;
    private boolean isPermissions; //电话权限是否请求成功

    private boolean canJump;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerSplashComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .splashModule(new SplashModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        return R.layout.activity_splash; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_APP_STATUS, Constant.APP_STATUS_NORMAL);
//        Timber.d("----------- SplashActivity: " + DataHelper.getIntergerSF(getApplicationContext(), Constant.SP_KEY_APP_STATUS));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        int APP_STATUS = getIntent().getIntExtra("app_status", 2);
        if (APP_STATUS == Constant.APP_STATUS_RESTART) {
            DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_APP_STATUS, Constant.APP_STATUS_RESTART);
        }

        //清理资讯阅读奖励进度
        clearRewardRecord();

        ObjectAnimator animator = ObjectAnimator.ofFloat(startPageLogo, "alpha", 0f, 0.8f, 1.0f);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

        if (TextUtils.isEmpty(DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_FIRST))) {
            DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_FIRST, "no");//设置打开标识
            bcatChannel();//变现猫渠道下载统计
        }
        removeOldSP();
        startWebScoket();//启动WebScoket端口监听
        requestPermission();//获取权限

    //    是否WEB打开
//        Uri uri = getIntent().getData();
//        if (uri != null) {
//            //uri不为空时为web唤醒APP，web传值跳转对应页面
//            webOpenActivity(uri);
//        }

        if (getIntent().getBooleanExtra("jPushData", false)) {
            Timber.d("==JPush  启动了APP:  ");
            jPushOpenActivity(getIntent().getExtras());
        }
    }

    /**
     * 清空进度奖励记录
     */
    private void clearRewardRecord() {
        //清理资讯阅读奖励进度
        DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_NEWS_PROGRESS_SCALE, 0);

        //清理视频阅读奖励进度
        DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_VIDEO_PROGRESS_SCALE, 0);

        try {
            //清空数据库资讯阅读记录
            MyApplication.get().getDaoSession().getNewsRecordBeanDao().deleteAll();
            //清空数据库视频观看记录
            MyApplication.get().getDaoSession().getVideoRecordBeanDao().deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 引导页删除多余的信息
     */
    private void removeOldSP() {
        DataHelper.removeSF(getApplicationContext(), "userInfo");
        DataHelper.removeSF(getApplicationContext(), "imageUserPath");
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        //运行时权限处理
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.READ_PHONE_STATE);
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        assert mPresenter != null;
        mPresenter.requestPermission(permissionList);
    }

    /**
     * 极光推送打开对应页面
     */
    private void jPushOpenActivity(Bundle bundle) {
        JSONObject json = null;
        try {
            json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String pushType;                  //  pushType  区分跳转类型;pushNews  pushActivity  pushQuickCash  pushTaskCenter
        String pushNewsUrl;               //  pushNewsUrl  资讯网页url;
        String pushNewsId;                //  pushNewsId  资讯ID;
        String pushNewsTab;               //  pushNewsTab  资讯栏目;
        String pushActivityUrl;           //  pushActivityUrl  活动网页 url;
        String pushActivityTitle;         //  pushActivityTitle  活动网页 title;
        if (json != null) {
            pushType = json.optString("pushType", "");
            pushNewsUrl = json.optString("pushNewsUrl", "");
            pushNewsId = json.optString("pushNewsId", "");
            pushNewsTab = json.optString("pushNewsTab", "");
            pushActivityUrl = json.optString("pushActivityUrl", "");
            pushActivityTitle = json.optString("pushActivityTitle", "");
            Timber.d("==JPush  信息:  "
                    + "\n" + "pushType= " + pushType
                    + "\n" + "pushNewsUrl= " + pushNewsUrl
                    + "\n" + "pushNewsId= " + pushNewsId
                    + "\n" + "pushNewsTab= " + pushNewsTab
                    + "\n" + "pushActivityUrl= " + pushActivityUrl
                    + "\n" + "pushActivityTitle= " + pushActivityTitle
            );

            Intent intent = new Intent();
            intent.putExtra("mPushUpApp", "mPushUpApp");//用于区分是否推送打开唤醒打开

            if (TextUtils.equals(pushType, "pushNews")) {
                intent.putExtra("id", pushNewsId);//资讯、视频ID;
                intent.putExtra("web_url", pushNewsUrl);//资讯、网页url;
                intent.putExtra("tab", pushNewsTab);//资讯、视频频道类型;
                intent.setClass(this, NewsDetailActivity.class);
            } else if (TextUtils.equals(pushType, "pushActivity")) {
                intent.putExtra("URL", pushActivityUrl);//活动网页url;
                intent.putExtra("title", pushActivityTitle);//活动网页url;
                intent.setClass(this, WebViewActivity.class);
            } else if (TextUtils.equals(pushType, "pushQuickCash")) {
                intent.setClass(this, QuickCashActivity.class);//快速提现页
            } else if (TextUtils.equals(pushType, "pushTaskCenter")) {
                intent.setClass(this, MainActivity.class);//主界面的任务中心
                EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(2).build(), EventBusTags.TAG_CHANGE_TAB);
            }
            startActivity(intent);
            killMyself();
        }
    }

    /**
     * Web打开对应页面
     *
     * @param uri web传值
     */
    private void webOpenActivity(Uri uri) {
        String scheme = uri.getScheme();//用于区分是否web唤醒打开
        String url = uri.getQueryParameter("url");//视频url;
        String web_url = uri.getQueryParameter("webUrl");//资讯、视频网页url;
        String id = uri.getQueryParameter("id");//资讯、视频网页ID;
        String image_url = uri.getQueryParameter("img");//资讯、视频分享图片url;
        String title = uri.getQueryParameter("title");//资讯、视频网页标题;
        String content = uri.getQueryParameter("content");//资讯、视频描述文本;
        String tab = uri.getQueryParameter("tab");//资讯、视频频道类型;
        String type = uri.getQueryParameter("type");//区分跳转类型;
        String textSize = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        if (!TextUtils.isEmpty(image_url) && !image_url.startsWith("http")) {
            image_url = "http:" + image_url;
        }

        String is_share = uri.getQueryParameter("canShare");//能否分享;
//        Timber.d("===========SplashActivity: " + is_share);
        if (TextUtils.equals(is_share, "ture") || TextUtils.equals(is_share, "true")) {
            is_share = "1";
//            Timber.d("===========SplashActivity: " + is_share);
        }

//        Timber.d("===========SplashActivity: "
//                + "\n" + "url= " + url
//                + "\n" + "web_url= " + web_url
//                + "\n" + "id= " + id
//                + "\n" + "image_url= " + image_url
//                + "\n" + "title= " + title
//                + "\n" + "content= " + content
//                + "\n" + "tab= " + tab
//                + "\n" + "type= " + type
//                + "\n" + "is_share= " + is_share
//                + "\n" + "scheme= " + scheme
//        );

        Intent intent = new Intent();
        intent.putExtra("id", id);//资讯、视频ID;
        intent.putExtra("url", url);//视频url
        intent.putExtra("web_url", web_url);//资讯、视频网页url;
        intent.putExtra("title", title);//资讯、视频标题;
        intent.putExtra("content", content);//资讯、视频描述文本
        intent.putExtra("tab", tab);//资讯、视频频道类型;
        intent.putExtra("is_share", is_share);//该资讯、视频能否分享
        intent.putExtra("image_url", image_url);//资讯、视频分享图片url;
        intent.putExtra("scheme", scheme);//用于区分是否web唤醒打开
        intent.putExtra("textSize", textSize);//字体大小

        if (TextUtils.equals(type, "video")) {
            intent.setClass(this, VideoDetailActivity.class);
        } else if (TextUtils.equals(type, "news")) {
            intent.setClass(this, NewsDetailActivity.class);
        } else {
            intent.setClass(this, MainActivity.class);
        }
        startActivity(intent);
        killMyself();
    }

    /**
     * 变现猫渠道统计（网页端复制变现猫ID到粘贴板，APP从粘贴板获取并上传）
     */
    private void bcatChannel() {
        String channel = TelephoneUtils.getAppMetaData(getApplicationContext(), "UMENG_CHANNEL");//获取渠道名
        //只有渠道为变现猫时才上传统计数据
        if (!TextUtils.isEmpty(channel) && TextUtils.equals(channel, "Bcat")) {
            //获取粘贴板数据
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = clipboardManager == null ? null : clipboardManager.getPrimaryClip();
            if (clipData != null) {
                int itemCount = clipData.getItemCount();
                String bxm_id = "";
                if (itemCount > 0 && clipData.getItemAt(itemCount - 1).getText() != null)
                    bxm_id = clipData.getItemAt(itemCount - 1).getText().toString();
                if (bxm_id.length() > 9 && bxm_id.substring(0, 9).equals("dzkd_bxm=") && mPresenter != null) {
                    mPresenter.appActivate(bxm_id);//上传变现猫ID
                }
            }
        }

    }

    /**
     * 有米渠道统计
     */
    private void youMiChannel() {
        String isFirst = DataHelper.getStringSF(this.getApplicationContext(), Constant.SP_YOU_MI_KEY_FIRST);//是否第一次打开(为空说明为第一次打开应用)
        if (TextUtils.isEmpty(isFirst)) {
            DataHelper.setStringSF(this.getApplicationContext(), Constant.SP_YOU_MI_KEY_FIRST, "no");//设置打开标识
            String channel = TelephoneUtils.getAppMetaData(this.getApplicationContext(), "UMENG_CHANNEL");//获取渠道名
            //只有渠道为变现猫时才上传统计数据
            if (!TextUtils.isEmpty(channel) && TextUtils.equals(channel, "M_youmi") && mPresenter != null) {
                mPresenter.appYouMi();
            }
        }
    }

    /**
     * 启动WebScoket端口监听
     */
    private void startWebScoket() {
        try {
            new WebSocketServer(Constant.SP_KEY_WEB_SOCKET).start();
            Timber.i("启动WebScoket成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    /**
     * 倒计时  毫秒
     *
     * @param time 时间
     */
    @Override
    public void updateCountDown(long time) {
        if (llSplash.getVisibility() == View.GONE) {
            llSplash.setVisibility(View.VISIBLE);
        }
        if (ivBackground != null && ivBackground.getVisibility() == View.GONE) {
            ivBackground.setVisibility(View.VISIBLE);
        }
        int mTime = 360;
        ring.setProgess(mTime, (int) time);
        ring.setClickListener(text -> {
            if (flags) {
                launchActivity(new Intent(SplashActivity.this, MainActivity.class));
                killMyself();
            }
        });
        if (time == mTime) {
            if (flags) {
                launchActivity(new Intent(SplashActivity.this, MainActivity.class));
                killMyself();
            }
        }
    }

    /**
     * 加载广告
     */
    @Override
    public void adsNativeSplash() {
//        if (isInternet()) {
//            llSplash.setVisibility(View.GONE);
//            requestAds();
//        } else {
//            llSplash.setVisibility(View.VISIBLE);
//            if (mPresenter != null) {
//                mPresenter.countDown();
//            }
//        }
//        llSplash.setVisibility(View.GONE);
        requestAds();
    }

    /**
     * 检测 上传信息
     */
    @Override
    public void getPhoneInfo() {
        if (EmuCheckUtil.mayOnEmulator(getApplicationContext())) {
            emulator = true;
            DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_MOBILE_EMULATOR, Constant.VALUE_EMULATOR);
        }
        if (CheckXposed.isXposed(getApplicationContext())) {
            xposed = true;
            DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_MOBILE_XPOSED, Constant.VALUE_XPOSED);
        }
        if (CheckRoot.isDeviceRooted()) {
            root = true;
            DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_MOBILE_ROOT, Constant.VALUE_ROOT);
        }
        assert mPresenter != null;
        if (new RxPermissions(SplashActivity.this)
                .isGranted(Manifest.permission.READ_PHONE_STATE)) {
            isPermissions = true;
        }
        String uploasdInfors = "emulator=" + emulator + "&xposed=" + xposed + "&root=" + root + TelephoneUtils.uploadInfomation(this.getApplicationContext(), isPermissions);
        Timber.d("=========root" + emulator + "  xposed" + xposed + "root" + root);
        youMiChannel();//有米渠道下载统计
        DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_MOBILE_INFO, uploasdInfors);
        int haveTouchHardware = DataHelper.getIntergerSF(getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);
        mPresenter.uploadDeviceInfo(uploasdInfors, haveTouchHardware != 1 ? 0 : 1);
        mPresenter.getEssentialParameter(root, xposed, emulator);
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    @Override
    public void showNormalDialog() {
        flags = false;
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle(getResources().getString(R.string.dialog_prompt));
        normalDialog.setMessage(getResources().getString(R.string.dialog_root_reminder));
        normalDialog.setNegativeButton(getResources().getString(R.string.dialog_i_know),
                (dialog, which) -> {
                    dialog.dismiss();
                    ArmsUtils.exitApp();
                });

        normalDialog.setCancelable(false);
        // 显示
        normalDialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            forward();
        }
        canJump = true;
    }

    /**
     * 请求开屏广告
     */
    private void requestAds() {
//        String appId = "1106851121";//这个地方放你自己的appID
//        String adId = "4040539405048870";//这个地方放自己的adID
        new SplashAD(this, splashContainer, Constant.GDT_APP_ID, Constant.GDT_AD_ID_SPLASH, new SplashADListener() {
            @Override
            public void onADDismissed() {
                Timber.d("==ads_native_splash" + "广告显示完毕:  ");
                forward();
            }

            @Override
            public void onNoAD(AdError adError) {
                Timber.d("==ads_native_splash" + "广告加载失败:  " + adError.getErrorMsg());
//                forward();
                if (llSplash != null)
                    llSplash.setVisibility(View.VISIBLE);
                if (ivBackground != null) {
                    ivBackground.setVisibility(View.VISIBLE);
                }
                if (mPresenter != null)
                    mPresenter.countDown();
            }

            @Override
            public void onADPresent() {
                Timber.d("==ads_native_splash" + "广告加载成功:  ");
            }

            @Override
            public void onADClicked() {
                Timber.d("==ads_native_splash" + "广告被点击:  ");
            }

            @Override
            public void onADTick(long l) {
                Timber.d("==ads_native_splash" + "onADTick:  ");
            }

            @Override
            public void onADExposure() {
                Timber.d("==ads_native_splash" + "广告曝光:  ");
            }
        }, 5000);
    }

    private void forward() {
        if (canJump) {
            //跳转到MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            canJump = true;
        }
    }
}
