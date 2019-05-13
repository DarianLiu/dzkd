package com.dzkandian.common.uitls;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AQuery2;
import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.bdtt.sdk.wmsdk.AdSlot;
import com.bdtt.sdk.wmsdk.TTAdConstant;
import com.bdtt.sdk.wmsdk.TTAdManager;
import com.bdtt.sdk.wmsdk.TTAdNative;
import com.bdtt.sdk.wmsdk.TTFeedAd;
import com.bdtt.sdk.wmsdk.TTImage;
import com.bdtt.sdk.wmsdk.TTNativeAd;
import com.dzkandian.R;
import com.dzkandian.common.uitls.ttAd.TTAdManagerHolder;
import com.dzkandian.mvp.common.ui.activity.AdWebActivity;
import com.dzkandian.storage.bean.RandomAdBean;
import com.jess.arms.utils.ArmsUtils;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 我的页面底部的大图广告
 */
public class MinAdUtils {

    private static List<NativeResponse> baiDuAdList;//百度广告列表

    private static List<TTFeedAd> csjAdList;//穿山甲

    private static List<NativeExpressADView> gdtAdList;//广点通广告列表
    private static Context mContext;
    private static RelativeLayout mAdLayout;
    private NativeExpressAD nativeExpressAD;
    private TTAdNative adNative;
    private AdSlot adSlot;
    private BaiduNative baiduAd;
    private RequestParameters requestParameters;

    private boolean isAdRefreshFail;  // 廣告刷新失敗
    private boolean showGDT = false;//初始化不显示
    private boolean showBD = false;
    private AQuery2 mAQuery;
    private RandomAdBean mRandomAdBean;
    private final LayoutInflater inflater;
    private View csjAdview;
    private ImageView csjImage;
    private TextView csjAdDes;
    private View baiDuAdview;
    private ImageView baiDuImage;
    private TextView baiDuAdDes;

    private static class InitAdClass {
        private static final MinAdUtils INSTANCE = new MinAdUtils();
    }

    private MinAdUtils() {
        inflater = LayoutInflater.from(mContext);

        initBaiDuAd();

        initGdtAd();

        initCsjAd();


    }

    public static MinAdUtils getInstance(Context context, RelativeLayout adLayout) {
        mContext = context;
        mAdLayout = adLayout;
        return InitAdClass.INSTANCE;
    }

    public void updateShowAdType(RandomAdBean randomAdBean) {
        mRandomAdBean = randomAdBean;
        String adType = randomAdBean.getAd_type();
        Timber.d("adset2=" + adType);
        if (!TextUtils.isEmpty(adType)) {
            mAdLayout.setEnabled(true);
            switch (adType) {
                case Constant.AD_SUPPORT_SDK_BAIDU://百度广告
                    loadShowBaiDuAd(randomAdBean);
                    break;
                case Constant.AD_SUPPORT_SDK_CSJ://穿山甲
                    loadShowCsjAd(randomAdBean);
                    break;
                case Constant.AD_SUPPORT_SDK_GDT://广点通
                    loadShowGdtAd(randomAdBean);
                    break;
                case Constant.AD_SUPPORT_SDK_OWN://自营广告
                    loadShowSelfAd(randomAdBean);
                    break;
                default:
                    break;
            }
        } else {
            mAdLayout.setEnabled(false);
        }
    }

    /**
     * 加载显示自营广告
     *
     * @param randomAdBean
     */
    private void loadShowSelfAd(RandomAdBean randomAdBean) {
        if (randomAdBean != null) {
            //广告样式
            View SelfAdview = inflater.inflate(R.layout.feed_native_ad_itme, null);
            //广告图片
            ImageView SelfAdImage = (ImageView) SelfAdview.findViewById(R.id.native_main_image);
            //广告描述
            TextView SelfAdDes = SelfAdview.findViewById(R.id.native_ad_title);

            mAdLayout.removeAllViews();
            AQuery aq = new AQuery(SelfAdview);
            String imgUrl = randomAdBean.getAd_info_images().get(0);
            String title = randomAdBean.getAd_info_title();
            String clickUrl = randomAdBean.getAd_info_click_url();
            String urlType = randomAdBean.getAd_info_click_action();
            aq.id(SelfAdImage).image(imgUrl, false, true);
            if (!TextUtils.isEmpty(title)) {
                aq.id(SelfAdDes).visible();
                aq.id(SelfAdDes).text(title);
            } else {
                aq.id(SelfAdDes).invisible();
            }
            mAdLayout.addView(SelfAdview);
            mAdLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(clickUrl) && !TextUtils.isEmpty(urlType)) {
                        if (urlType.equals(Constant.AD_JUMP_INTERNAL_URL)) {//内部跳转
                            Intent innerIntent = new Intent(mContext, AdWebActivity.class);
                            innerIntent.putExtra("AdUrl", clickUrl);
                            innerIntent.putExtra("AdTitle", title);
                            ArmsUtils.startActivity(innerIntent);
                        } else if (urlType.equals(Constant.AD_JUMP_EXTERNAL_URL)) {//外部跳转
                            ArmsUtils.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl)));
                        } else if (urlType.equals(Constant.AD_JUMP_DOWNLOAD_APK)) {//下载SDK
                        }
                    }
                }
            });
        }
    }

    /**
     * 加载显示广点通广告
     *
     * @param randomAdBean
     */
    private void loadShowGdtAd(RandomAdBean randomAdBean) {
        mAdLayout.removeAllViews();
        if (isAdRefreshFail) {
            nativeExpressAD.loadAD(10);   //上次廣告刷新失敗 重新加載
            isAdRefreshFail = false;
            showGDT = true;
        }
        if (gdtAdList.size() > 0) {  //获取到的广告数量大于0
            gdtAdList.remove(0);
            if (gdtAdList.size() > 0) {
                NativeExpressADView nativeExpressADView = gdtAdList.get(0);
                // 广告可见才会产生曝光，否则将无法产生收益。
                mAdLayout.addView(nativeExpressADView);
                nativeExpressADView.render();
            } else {
                showGDT = true;
                nativeExpressAD.loadAD(10);
            }
        } else {
            showGDT = true;
            nativeExpressAD.loadAD(10);
        }
    }

    /**
     * 加载显示穿山甲广告
     *
     * @param randomAdBean
     */
    private void loadShowCsjAd(RandomAdBean randomAdBean) {
        adNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {
                if (mRandomAdBean != null) {
                    loadShowSelfAd(randomAdBean);
                } else {
                    invisibleView(mAdLayout);
                }
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                //加载成功的回调 请确保您的代码足够健壮，可以处理异常情况；
                if (mAdLayout != null && mAdLayout.getVisibility() != View.VISIBLE) {
                    mAdLayout.setVisibility(View.VISIBLE);
                }
                if (mAdLayout != null && mAdLayout.getChildCount() > 0) {
                    mAdLayout.removeAllViews();
                }
                if (csjAdList.size() > 0) {
                    csjAdList.clear();
                }
                if (list != null && list.size() > 0) {
                    csjAdList.addAll(list);
                    fillingCsjAd();
                }
            }
        });
    }


    /**
     * 加载显示百度广告
     *
     * @param randomAdBean
     */
    private void loadShowBaiDuAd(RandomAdBean randomAdBean) {
        if (baiDuAdList.size() > 0) {
            baiDuAdList.remove(0);
            if (baiDuAdList.size() > 0) {
                if (mAdLayout.getChildCount() > 0) {
                    mAdLayout.removeAllViews();
                }
                fillingBaiduAd();
            } else {
                showBD = true;
                mAdLayout.removeAllViews();
                baiduAd.makeRequest(requestParameters);
            }
        } else {
            showBD = true;
            mAdLayout.removeAllViews();
            baiduAd.makeRequest(requestParameters);
        }
    }

    /**
     * 初始化广点通
     */
    private void initGdtAd() {
        gdtAdList = new ArrayList<>();
        nativeExpressAD = new NativeExpressAD(
                mContext,
                new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT),
                Constant.GDT_APP_ID,
                Constant.GDT_AD_ID_MINE_BOTTOM,
                new NativeExpressAD.NativeExpressADListener() {
                    @Override
                    public void onNoAD(AdError adError) {
                        Timber.d("==ads_native_video_view" + "广告加载失败:  " + adError.getErrorMsg());
                        if (mRandomAdBean != null) {
                            loadShowSelfAd(mRandomAdBean);
                        } else {
                            isAdRefreshFail = true;
                        }
                    }

                    @Override
                    public void onADLoaded(List<NativeExpressADView> list) {
                        mAdLayout.setVisibility(View.VISIBLE);
                        if (gdtAdList.size() > 0) {
                            gdtAdList.clear();
                        }
                        if (list != null && list.size() > 0) { //广告集合不为空时
                            gdtAdList.addAll(list);
                            if (showGDT) {
                                NativeExpressADView nativeExpressADView = gdtAdList.get(0);
                                // 广告可见才会产生曝光，否则将无法产生收益。
                                if (mAdLayout != null) {
                                    mAdLayout.addView(nativeExpressADView);
                                }
                                nativeExpressADView.render();
                            }
                        }

                    }

                    @Override
                    public void onRenderFail(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADExposure(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADClicked(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADClosed(NativeExpressADView nativeExpressADView) {
//                        Timber.d("==ads_native_video_view" + "onADClosed:  ");
                        // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，释放资源，不可以再用来展示。
                        invisibleView(mAdLayout);
                    }

                    @Override
                    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

                    }
                }); // 传入Activity
        // 注意：如果您在联盟平台上新建原生模板广告位时，选择了“是”支持视频，那么可以进行个性化设置（可选）
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); //
        nativeExpressAD.loadAD(10);//加载广告
    }

    /**
     * 初始化穿山甲广告
     */
    private void initCsjAd() {
        //广告样式
        csjAdview = inflater.inflate(R.layout.feed_native_ad_itme, null);
        //广告图片
        csjImage = (ImageView) csjAdview.findViewById(R.id.native_main_image);
        //广告描述
        csjAdDes = csjAdview.findViewById(R.id.native_ad_title);
        csjAdList = new ArrayList<>();
        TTAdManager ttAdManager = TTAdManagerHolder.getInstance(mContext);//穿山甲

        mAQuery = new AQuery2(mContext);
        adNative = ttAdManager.createAdNative(mContext);
        adSlot = new AdSlot.Builder()
                // 必选参数 设置您的CodeId
                .setCodeId("905316087")
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(1) //请求广告数量
                .build();
    }

    /**
     * 初始化百度广告
     */
    private void initBaiDuAd() {
        //广告样式
        baiDuAdview = inflater.inflate(R.layout.feed_native_ad_itme, null);
        //广告图片
        baiDuImage = (ImageView) baiDuAdview.findViewById(R.id.native_main_image);
        //广告描述
        baiDuAdDes = baiDuAdview.findViewById(R.id.native_ad_title);

        baiDuAdList = new ArrayList<NativeResponse>();
        /**
         * Step 1. 创建BaiduNative对象，参数分别为： 上下文context，广告位ID, BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
         * 注意：请将YOUR_AD_PALCE_ID替换为自己的广告位ID
         */
        baiduAd = new BaiduNative(mContext, Constant.BAIDU_AD_ID_MINE_BOTTOM, new BaiduNative.BaiduNativeNetworkListener() {

            @Override
            public void onNativeFail(NativeErrorCode arg0) {
                Timber.d("onNativeFail reason:" + arg0.name());
                if (mRandomAdBean != null) {
                    loadShowSelfAd(mRandomAdBean);
                } else {
                    invisibleView(mAdLayout);
                }

            }

            @Override
            public void onNativeLoad(List<NativeResponse> arg0) {
                mAdLayout.setVisibility(View.VISIBLE);
                if (baiDuAdList.size() > 0) {
                    baiDuAdList.clear();
                }
                if (arg0 != null && arg0.size() > 0) { //广告集合不为空时
                    baiDuAdList.addAll(arg0);
                    // 广告可见才会产生曝光，否则将无法产生收益。
//                    showBaiduAd(context, adsNativeMineView);
                    if (showBD) {
                        fillingBaiduAd();
                    }
                }
            }
        });

        /**
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
        requestParameters = new RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
        baiduAd.makeRequest(requestParameters);
    }

    /**
     * 加载失败隐藏广告位
     *
     * @param relativeLayout
     */

    private void invisibleView(RelativeLayout relativeLayout) {
        if (relativeLayout != null && relativeLayout.getChildCount() > 0) {
            relativeLayout.removeAllViews();
            relativeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 填充百度广告样式
     */
    private void fillingBaiduAd() {
        NativeResponse nativeResponse = baiDuAdList.get(0);
        if (mAdLayout != null) {
            mAdLayout.removeAllViews();
            AQuery aq = new AQuery(baiDuAdview);
            aq.id(baiDuImage).image(nativeResponse.getImageUrl(), false, true);
            String trim = nativeResponse.getDesc().trim();
            if (!TextUtils.isEmpty(trim)) {
                aq.id(baiDuAdDes).visible();
                aq.id(baiDuAdDes).text(trim);
            } else {
                aq.id(baiDuAdDes).invisible();
            }
            mAdLayout.addView(baiDuAdview);
            nativeResponse.recordImpression(baiDuAdview);
            mAdLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nativeResponse.handleClick(baiDuAdview);
                }
            });
        }

    }

    /**
     * 填充穿山甲广告样式
     */
    private void fillingCsjAd() {
        mAdLayout.removeAllViews();
        TTFeedAd ttFeedAd = csjAdList.get(0);
        int imageMode = ttFeedAd.getImageMode();
        if (imageMode == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
            String adDescription = ttFeedAd.getDescription().trim();
            if (!TextUtils.isEmpty(adDescription)) {
                csjAdDes.setVisibility(View.VISIBLE);
                csjAdDes.setText(adDescription);
            } else {
                csjAdDes.setVisibility(View.GONE);
            }
            ttFeedAd.registerViewForInteraction(mAdLayout, csjImage, new TTNativeAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                    if (ttNativeAd != null) {
                        Timber.d("广告" + ttNativeAd.getTitle() + "被点击");
                    }
                }

                @Override
                public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                    if (ttNativeAd != null) {
                        Timber.d("广告" + ttNativeAd.getTitle() + "被创意按钮被点击");
                    }
                }

                @Override
                public void onAdShow(TTNativeAd ttNativeAd) {
                    if (ttNativeAd != null) {
                        Timber.d("广告" + ttNativeAd.getTitle() + "展示");
                    }
                }
            });
            if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                TTImage image = ttFeedAd.getImageList().get(0);
                if (image != null && image.isValid()) {
                    mAQuery.id(csjImage).image(image.getImageUrl());
                    Timber.d("穿山甲图片：" + image.getImageUrl());
                }
            }
            mAdLayout.addView(csjAdview);
        }
    }

    /**
     * 回收广告
     */
    public void destroyNativeAds() {
        if (gdtAdList != null) {
            for (NativeExpressADView nativeExpressADView : gdtAdList) {
                nativeExpressADView.destroy();
            }
        }
        if (baiduAd != null) {
            baiduAd.destroy();
            baiduAd = null;
        }
        if (csjAdList != null) {
            csjAdList.clear();
            csjAdList = null;
        }
        if (baiDuAdList != null) {
            baiDuAdList.clear();
            baiDuAdList = null;
        }
        if (csjAdList != null) {
            csjAdList.clear();
            csjAdList = null;
        }
        if (mRandomAdBean != null) {
            mRandomAdBean = null;
        }
    }
}
