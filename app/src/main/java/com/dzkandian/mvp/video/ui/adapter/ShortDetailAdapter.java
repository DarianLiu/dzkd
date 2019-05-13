package com.dzkandian.mvp.video.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.dzkandian.R;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.player.play.ListPlayLogic;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 小视频详情页适配器
 * Created by LiuLi on 2018/8/18.
 */

public class ShortDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SoftReference<Activity> mWeakReference;
    private List<VideoBean> mItems;

    private ListPlayLogic mListPlayLogic;
    private String mTextSize;
    private final static int normalType = 0;
    private final static int adShortType = 1;
    private List<NativeResponse> mNrAdListBig;//百度广告数据列表
    private List<NativeResponse> mNrAdListSmall;//百度广告数据列表
    private ImageLoader imageLoader;
    private BaiduNative mBaiduNativeBig;//百度广告
    private BaiduNative mBaiduNativeSmall;//百度广告
    private RequestParameters mRequestParametersBig;//百度广告参数设置
    private RequestParameters mRequestParametersSmall;//百度广告参数设置
    private boolean isGetBaiduNativeBig = true;
    private boolean isGetBaiduNativeSmall = true;

    public ShortDetailAdapter(Activity context, RecyclerView recyclerView, List<VideoBean> list, String textSize, OnVideoSwitchListener onVideoSwitchListener) {
        this.mWeakReference = new SoftReference<>(context);
        this.mItems = list;
        this.mTextSize = textSize;
        this.mNrAdListBig = new ArrayList<>();
        this.mNrAdListSmall = new ArrayList<>();
        this.imageLoader = ArmsUtils.obtainAppComponentFromContext(context).imageLoader();
        this.mListPlayLogic = new ListPlayLogic(recyclerView, this, onVideoSwitchListener);
    }

    public ListPlayLogic getListPlayLogic() {
        return mListPlayLogic;
    }

    public void recycle() {
        if (mWeakReference != null) {
            mWeakReference.clear();
            mWeakReference = null;
        }

        if (mNrAdListBig != null) {
            mNrAdListBig.clear();
            mNrAdListBig = null;
        }
        if (mRequestParametersBig != null) {
            mRequestParametersBig = null;
        }
        if (mBaiduNativeBig != null) {
            mBaiduNativeBig.destroy();
            mBaiduNativeBig = null;
        }

        if (mNrAdListSmall != null) {
            mNrAdListSmall.clear();
            mNrAdListSmall = null;
        }
        if (mRequestParametersSmall != null) {
            mRequestParametersSmall = null;
        }
        if (mBaiduNativeSmall != null) {
            mBaiduNativeSmall.destroy();
            mBaiduNativeSmall = null;
        }

        if (mItems != null) {
            mItems.clear();
        }

        if (mListPlayLogic != null) {
            mListPlayLogic.destroy();
            mListPlayLogic = null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (indexIsAD(position)) {
            return adShortType;
        } else {
            return normalType;
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ShortAdItemHolder) {
            imageLoader.clear(mWeakReference.get(), CustomImageConfig.builder().imageView(((ShortAdItemHolder) holder).adsBigImage).build());
            imageLoader.clear(mWeakReference.get(), CustomImageConfig.builder().imageView(((ShortAdItemHolder) holder).adsIamge1).build());
            imageLoader.clear(mWeakReference.get(), CustomImageConfig.builder().imageView(((ShortAdItemHolder) holder).adsIamge2).build());
            imageLoader.clear(mWeakReference.get(), CustomImageConfig.builder().imageView(((ShortAdItemHolder) holder).adsIamge3).build());
            imageLoader.clear(mWeakReference.get(), CustomImageConfig.builder().imageView(((ShortAdItemHolder) holder).adsIamge4).build());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == adShortType) {
            return new ShortAdItemHolder(LayoutInflater.from(mWeakReference.get()).inflate(R.layout.item_short_ad, null));
        } else {
            return new VideoItemHolder(LayoutInflater.from(mWeakReference.get()).inflate(R.layout.item_short_detail, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mBaiduNativeBig == null) {
//            Timber.d("======Ad  第一次onBindViewHolder");
            fetchBaiDuAdBig();
        }
        if (mBaiduNativeSmall == null) {
            fetchBaiDuAdSmall();
        }
        if (holder instanceof VideoItemHolder) {
            VideoItemHolder videoItemHolder = (VideoItemHolder) holder;
            final VideoBean item = getItem(position);
            videoItemHolder.tvVideoTitle.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            mTextSize = DataHelper.getStringSF(mWeakReference.get().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
            if (!TextUtils.isEmpty(mTextSize)) {
                switch (mTextSize) {
                    case "small":
                        videoItemHolder.tvVideoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        break;
                    case "medium":
                        videoItemHolder.tvVideoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        break;
                    case "big":
                        videoItemHolder.tvVideoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        break;
                }
            } else {
                videoItemHolder.tvVideoTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }

            if (!item.getThumbUrl().startsWith("http")) {
                item.setThumbUrl("http:" + item.getThumbUrl());
            }

            videoItemHolder.layoutContainer.removeAllViews();
        } else if (holder instanceof ShortAdItemHolder) {
//            Timber.d("======Ad  holder  是广告位");
            ShortAdItemHolder shortAdItemHolder = (ShortAdItemHolder) holder;
            if (mNrAdListBig != null && mNrAdListBig.size() > 0 && mNrAdListSmall != null && mNrAdListSmall.size() > 4) {
                setAdView(mNrAdListBig, mNrAdListSmall, shortAdItemHolder);
                isGetBaiduNativeBig = true;
                isGetBaiduNativeSmall = true;
            }
        }
    }

    public VideoBean getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    public boolean indexIsAD(int position) {
        if (position < 0) {
            return false;
        }

        if (position < getItemCount() && TextUtils.equals(mItems.get(position).getType(), "ad")) {
            Timber.d("===========插屏广告位：" + position);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public static class VideoItemHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        public FrameLayout layoutContainer;
        TextView tvVideoTitle;

        public VideoItemHolder(View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            tvVideoTitle = itemView.findViewById(R.id.tv_video_title);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            ivCover.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }

    public static class ShortAdItemHolder extends RecyclerView.ViewHolder {
        public ImageView adsBigImage;
        public LinearLayout adsLayout1;
        public ImageView adsIamge1;
        public TextView adsTitle1;
        public TextView adsDesc1;
        public TextView adsWriter1;
        public LinearLayout adsLayout2;
        public ImageView adsIamge2;
        public TextView adsTitle2;
        public TextView adsDesc2;
        public TextView adsWriter2;
        public LinearLayout adsLayout3;
        public ImageView adsIamge3;
        public TextView adsTitle3;
        public TextView adsDesc3;
        public TextView adsWriter3;
        public LinearLayout adsLayout4;
        public ImageView adsIamge4;
        public TextView adsTitle4;
        public TextView adsDesc4;
        public TextView adsWriter4;

        public ShortAdItemHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            adsBigImage = itemView.findViewById(R.id.item_shortad_bigImage);
            adsLayout1 = itemView.findViewById(R.id.item_shortad1_layout);
            adsIamge1 = itemView.findViewById(R.id.item_shortad1_image);
            adsTitle1 = itemView.findViewById(R.id.item_shortad1_title);
            adsDesc1 = itemView.findViewById(R.id.item_shortad1_desc);
            adsWriter1 = itemView.findViewById(R.id.item_shortad1_writer);
            adsLayout2 = itemView.findViewById(R.id.item_shortad2_layout);
            adsIamge2 = itemView.findViewById(R.id.item_shortad2_image);
            adsTitle2 = itemView.findViewById(R.id.item_shortad2_title);
            adsDesc2 = itemView.findViewById(R.id.item_shortad2_desc);
            adsWriter2 = itemView.findViewById(R.id.item_shortad2_writer);
            adsLayout3 = itemView.findViewById(R.id.item_shortad3_layout);
            adsIamge3 = itemView.findViewById(R.id.item_shortad3_image);
            adsTitle3 = itemView.findViewById(R.id.item_shortad3_title);
            adsDesc3 = itemView.findViewById(R.id.item_shortad3_desc);
            adsWriter3 = itemView.findViewById(R.id.item_shortad3_writer);
            adsLayout4 = itemView.findViewById(R.id.item_shortad4_layout);
            adsIamge4 = itemView.findViewById(R.id.item_shortad4_image);
            adsTitle4 = itemView.findViewById(R.id.item_shortad4_title);
            adsDesc4 = itemView.findViewById(R.id.item_shortad4_desc);
            adsWriter4 = itemView.findViewById(R.id.item_shortad4_writer);
        }
    }

    public boolean isGetAdBig() {
        return isGetBaiduNativeBig;
    }

    public boolean isGetAdSmall() {
        return isGetBaiduNativeSmall;
    }

    /**
     * 加载大图广告数据
     */
    public void fetchBaiDuAdBig() {
//        Timber.d("======Ad  进入fetchBaiDuAd方法");
        if (mBaiduNativeBig == null) {
//            Timber.d("======Ad  进入  if (mBaiduNative == null)");
            mBaiduNativeBig = new BaiduNative(
                    mWeakReference.get(),
                    Constant.BAIDU_AD_ID_SHORT_BIG,
                    new BaiduNative.BaiduNativeNetworkListener() {
                        @Override
                        public void onNativeLoad(List<NativeResponse> list) {
//                            Timber.d("======Ad  onNativeLoad  加载成功" + list.size());
                            if (list.size() > 0) {
//                                Timber.d("======Ad  onNativeLoad  加载成功 五条以上");
                                Timber.d("======Ad  onNativeLoad  getImageUrl(): 0." + list.get(0).getImageUrl());
                                mNrAdListBig = list;
                                isGetBaiduNativeBig = false;
                            }
                        }

                        @Override
                        public void onNativeFail(NativeErrorCode nativeErrorCode) {
//                            Timber.e("======Ad  onNativeFail  加载失败");
                        }
                    });
        }

        if (mRequestParametersBig == null) {
//            Timber.d("======Ad  进入  if (mRequestParameters == null)");
            mRequestParametersBig = new RequestParameters.Builder()
                    .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
        }

        mBaiduNativeBig.makeRequest(mRequestParametersBig);

    }

    /**
     * 加载小图广告数据
     */
    public void fetchBaiDuAdSmall() {
        if (mBaiduNativeSmall == null) {
            mBaiduNativeSmall = new BaiduNative(
                    mWeakReference.get(),
                    Constant.BAIDU_AD_ID_SHORT_SMALL,
                    new BaiduNative.BaiduNativeNetworkListener() {
                        @Override
                        public void onNativeLoad(List<NativeResponse> list) {
                            if (list.size() > 4) {
                                Timber.d("======Ad  onNativeLoad  getImageUrl(): 1." + list.get(1).getImageUrl() + "  2." + list.get(2).getImageUrl() + "  3." + list.get(3).getImageUrl() + "  4." + list.get(3).getImageUrl());
                                mNrAdListSmall = list;
                                isGetBaiduNativeSmall = false;
                            }
                        }

                        @Override
                        public void onNativeFail(NativeErrorCode nativeErrorCode) {
                        }
                    });
        }

        if (mRequestParametersSmall == null) {
            mRequestParametersSmall = new RequestParameters.Builder()
                    .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
        }

        mBaiduNativeSmall.makeRequest(mRequestParametersSmall);

    }

    /**
     * 获得广告数据后进行填充数据
     *
     * @param listBig
     * @param listSmall
     * @param shortAdItemHolder
     */
    private void setAdView(List<NativeResponse> listBig, List<NativeResponse> listSmall, ShortAdItemHolder shortAdItemHolder) {
//        Timber.d("======Ad  进入  setAdView方法");
        int bigWidth = ArmsUtils.getScreenWidth(mWeakReference.get().getApplicationContext())
                - ArmsUtils.dip2px(mWeakReference.get(), 30);
        int bigHeight = bigWidth * 9 / 16;
        int smallWidth = bigWidth / 3;
        int smallHeight = smallWidth * 3 / 4;
        shortAdItemHolder.adsBigImage.setLayoutParams(new LinearLayout.LayoutParams(bigWidth, bigHeight));
        shortAdItemHolder.adsIamge1.setLayoutParams(new LinearLayout.LayoutParams(smallWidth, smallHeight));
        shortAdItemHolder.adsIamge2.setLayoutParams(new LinearLayout.LayoutParams(smallWidth, smallHeight));
        shortAdItemHolder.adsIamge3.setLayoutParams(new LinearLayout.LayoutParams(smallWidth, smallHeight));
        shortAdItemHolder.adsIamge4.setLayoutParams(new LinearLayout.LayoutParams(smallWidth, smallHeight));

//        Timber.d("======Ad  进入  setAdView方法  getTitle():1." + list.get(0).getTitle() + "  2." + list.get(1).getTitle() + "  3." + list.get(2).getTitle() + "  4." + list.get(3).getTitle() + "  5." + list.get(4).getTitle());
        if (!TextUtils.isEmpty(listBig.get(0).getImageUrl()))
            imageLoader.loadImage(mWeakReference.get(), CustomImageConfig.builder()
                    .url(listBig.get(0).getImageUrl())
                    .isCenterCrop(true)
                    .isClearMemory(true)
                    .isClearDiskCache(true)
                    .isSkipMemoryCache(true)
                    .cacheStrategy(1)
                    .imageView(shortAdItemHolder.adsBigImage)
                    .build());
        listBig.get(0).recordImpression(shortAdItemHolder.adsBigImage);
        shortAdItemHolder.adsBigImage.setOnClickListener(view -> {
            listBig.get(0).handleClick(view);
        });

        if (!TextUtils.isEmpty(listSmall.get(1).getImageUrl())){
            imageLoader.loadImage(mWeakReference.get(), CustomImageConfig.builder()
                    .url(listSmall.get(1).getImageUrl())
                    .isCenterCrop(true)
                    .isClearMemory(true)
                    .isClearDiskCache(true)
                    .isSkipMemoryCache(true)
                    .cacheStrategy(1)
                    .imageView(shortAdItemHolder.adsIamge1)
                    .build());
        }else {
            shortAdItemHolder.adsIamge1.setImageResource(R.drawable.icon_dzkd_news_2);
        }
        shortAdItemHolder.adsTitle1.setText(listSmall.get(1).getTitle());
        shortAdItemHolder.adsDesc1.setText(listSmall.get(1).getDesc());
        shortAdItemHolder.adsWriter1.setText(listSmall.get(1).getBrandName());
        listSmall.get(1).recordImpression(shortAdItemHolder.adsLayout1);
        shortAdItemHolder.adsLayout1.setOnClickListener(view -> {
            listSmall.get(1).handleClick(view);
        });

        if (!TextUtils.isEmpty(listSmall.get(2).getImageUrl())){
            imageLoader.loadImage(mWeakReference.get(), CustomImageConfig.builder()
                    .url(listSmall.get(2).getImageUrl())
                    .isCenterCrop(true)
                    .isClearMemory(true)
                    .isClearDiskCache(true)
                    .isSkipMemoryCache(true)
                    .cacheStrategy(1)
                    .imageView(shortAdItemHolder.adsIamge2)
                    .build());
        }else {
            shortAdItemHolder.adsIamge2.setImageResource(R.drawable.icon_dzkd_news_3);
        }
        shortAdItemHolder.adsTitle2.setText(listSmall.get(2).getTitle());
        shortAdItemHolder.adsDesc2.setText(listSmall.get(2).getDesc());
        shortAdItemHolder.adsWriter2.setText(listSmall.get(2).getBrandName());
        listSmall.get(2).recordImpression(shortAdItemHolder.adsLayout2);
        shortAdItemHolder.adsLayout2.setOnClickListener(view -> {
            listSmall.get(2).handleClick(view);
        });

        if (!TextUtils.isEmpty(listSmall.get(3).getImageUrl())){
            imageLoader.loadImage(mWeakReference.get(), CustomImageConfig.builder()
                    .url(listSmall.get(3).getImageUrl())
                    .isCenterCrop(true)
                    .isClearMemory(true)
                    .isClearDiskCache(true)
                    .isSkipMemoryCache(true)
                    .cacheStrategy(1)
                    .imageView(shortAdItemHolder.adsIamge3)
                    .build());
        }else {
            shortAdItemHolder.adsIamge3.setImageResource(R.drawable.icon_dzkd_news_4);
        }
        shortAdItemHolder.adsTitle3.setText(listSmall.get(3).getTitle());
        shortAdItemHolder.adsDesc3.setText(listSmall.get(3).getDesc());
        shortAdItemHolder.adsWriter3.setText(listSmall.get(3).getBrandName());
        listSmall.get(3).recordImpression(shortAdItemHolder.adsLayout3);
        shortAdItemHolder.adsLayout3.setOnClickListener(view -> {
            listSmall.get(3).handleClick(view);
        });

        if (!TextUtils.isEmpty(listSmall.get(4).getImageUrl())){
            imageLoader.loadImage(mWeakReference.get(), CustomImageConfig.builder()
                    .url(listSmall.get(4).getImageUrl())
                    .isCenterCrop(true)
                    .isClearMemory(true)
                    .isClearDiskCache(true)
                    .isSkipMemoryCache(true)
                    .cacheStrategy(1)
                    .imageView(shortAdItemHolder.adsIamge4)
                    .build());
        }else {
            shortAdItemHolder.adsIamge4.setImageResource(R.drawable.icon_dzkd_news_5);
        }
        shortAdItemHolder.adsTitle4.setText(listSmall.get(4).getTitle());
        shortAdItemHolder.adsDesc4.setText(listSmall.get(4).getDesc());
        shortAdItemHolder.adsWriter4.setText(listSmall.get(4).getBrandName());
        listSmall.get(4).recordImpression(shortAdItemHolder.adsLayout4);
        shortAdItemHolder.adsLayout4.setOnClickListener(view -> {
            listSmall.get(4).handleClick(view);
        });
    }

    public interface OnVideoSwitchListener {
        void showAd(boolean showInsertAd);
        void showComment();
    }
}
