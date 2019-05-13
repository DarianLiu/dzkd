package com.dzkandian.common.uitls.ttAd;

import android.content.Context;

import com.bdtt.sdk.wmsdk.TTAdConstant;
import com.bdtt.sdk.wmsdk.TTAdManager;
import com.bdtt.sdk.wmsdk.TTAdManagerFactory;
import com.dzkandian.common.uitls.Constant;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static boolean sInit;

    public static TTAdManager getInstance(Context context) {
        TTAdManager ttAdManager = TTAdManagerFactory.getInstance(context);
        if (!sInit) {
            synchronized (TTAdManagerHolder.class) {
                if (!sInit) {
                    doInit(ttAdManager, context);
                    sInit = true;
                }
            }
        }
        return ttAdManager;
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(TTAdManager ttAdManager, Context context) {
        ttAdManager.setAppId(Constant.CSJ_APP_ID)
                .isUseTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .setName("大众看点_android")
                .setTitleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .setAllowShowNotifiFromSDK(true) //是否允许sdk展示通知栏提示
                .setAllowLandingPageShowWhenScreenLock(false) //是否在锁屏场景支持展示广告落地页
                .openDebugMode() //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .setGlobalAppDownloadListener(new AppDownloadStatusListener(context)) //下载任务的全局监听
                .setDirectDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G); //允许直接下载的网络状态集合
    }
}
