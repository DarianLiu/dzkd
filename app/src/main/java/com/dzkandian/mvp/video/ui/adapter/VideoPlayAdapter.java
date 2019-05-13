package com.dzkandian.mvp.video.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.baidu.mobad.feeds.NativeResponse;
import com.bdtt.sdk.wmsdk.TTFeedAd;
import com.bdtt.sdk.wmsdk.TTNativeAd;
import com.dzkandian.R;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.AdWebActivity;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

/**
 * 视频播放详情页列表适配器
 * Created by Administrator on 2018/5/6 0006.
 */

public class VideoPlayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private WeakReference<Context> softReference;      // 上下文Context
    private List<VideoBean> mVideoList;
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<>();//广告插入position/view 列表

    private final int TYPE_HEADER = 0;  //头部类型
    private final int TYPE_NORMAL = 1;  // 正常类型
    private final int TYPE_ERROR = 2; //异常类型
    private final int TYPE_AD_BAIDU = 3; //百度广告类型
    private final int TYPE_AD_GDT = 4; //广点通广告类型
    private final int TYPE_AD_CSJ = 5; //穿山甲广告类型
    private final int TYPE_AD_OWN = 6; //自营广告类型
    private String mTextSize;
    private Random random;

    private ImageLoader imageLoader;
    private boolean isShowError = false;//是否显示异常布局，默认为false
    private OnItemClickListener mOnItemClickListener;
    private String mTitle;

    public VideoPlayAdapter(Context context, String title, String textSize, List<VideoBean> videoBean) {
        this.softReference = new WeakReference<>(context);
        this.mTitle = title;
        this.mTextSize = textSize;
        this.mVideoList = videoBean;
        this.imageLoader = ArmsUtils.obtainAppComponentFromContext(context).imageLoader();
    }

    public void removeAllItem() {
        mVideoList.clear();
        isShowError = false;
        destroyADView();
        mAdViewPositionMap.clear();
        notifyDataSetChanged();
    }

    /**
     * 插入广点通广告
     *
     * @param map 广告（key: 广告视图；value：position）
     */
    public void insertGDTAD(HashMap<NativeExpressADView, Integer> map) {
        if (map != null && map.size() != 0) {
            mAdViewPositionMap.putAll(map);
        }
    }

    /**
     * 移除指定位置广点通广告
     *
     * @param nativeExpressADView 广告视图
     */
    public void removeGDTAD(NativeExpressADView nativeExpressADView) {
        int position = mAdViewPositionMap.get(nativeExpressADView);
        //移除该项资讯广告数据
        mVideoList.remove(position);
        notifyItemRemoved(position + 1);
        //广点通原生广告视图回收
        nativeExpressADView.destroy();
        mAdViewPositionMap.remove(nativeExpressADView);
    }

    /**
     * 广点通原生广告视图回收(注意：广告视图用完需要回收)
     */
    public void destroyADView() {
        for (NativeExpressADView adView : mAdViewPositionMap.keySet()) {
            adView.destroy();
        }
        mAdViewPositionMap.clear();
    }

    @Override
    public int getItemCount() {
        if (mVideoList == null || mVideoList.size() == 0) {
            return 2;
        } else {
            return mVideoList.size() + 1;
        }
    }

    /**
     * 获取指定位置数据
     *
     * @param position 当前位置
     */
    public VideoBean getDataIndex(int position) {
//        Timber.d("=========getDataIndex" + position);
        if (position < mVideoList.size()) {
            return mVideoList.get(position);
        } else {
            return null;
        }
    }

    public int getVideoListSize() {
        return mVideoList.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (mVideoList.size() == 0) {
            return position == 0 ? TYPE_HEADER : TYPE_ERROR;
        } else if (position == 0) {
            return TYPE_HEADER;
        } else if (mVideoList.get(position - 1).getType().equals("ad")) {
            if (mVideoList.get(position - 1).getAdNewType() != null) {
                if (mVideoList.get(position - 1).getAdNewType().equals("BaiDu")) {
                    return TYPE_AD_BAIDU;
                } else if (mVideoList.get(position - 1).getAdNewType().equals("GDT")) {
                    return TYPE_AD_GDT;
                } else if (mVideoList.get(position - 1).getAdNewType().equals("CSJ")) {
                    return TYPE_AD_CSJ;
                } else {
                    return TYPE_AD_OWN;
                }
            } else {
                return TYPE_NORMAL;
            }
        } else {
            return TYPE_NORMAL;
        }
    }


    /**
     * 显示空布局
     *
     * @param isShow //是否显示
     */
    public void showEmptyView(boolean isShow) {
        mVideoList.clear();
        this.isShowError = isShow;
        notifyDataSetChanged();
    }

    /**
     * 刷新视频列表
     *
     * @param videoList 资讯列表
     */
    public void refreshVideo(List<VideoBean> videoList) {
        mVideoList.addAll(videoList);
        notifyDataSetChanged();
    }

    /**
     * 加载更多视频
     *
     * @param videoList 视频列表
     */
    public void loadMoreVideo(List<VideoBean> videoList) {
        int size = mVideoList.size();
        mVideoList.addAll(videoList);
        notifyItemRangeInserted(size + 1, videoList.size());
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MyViewPlayHolder) {
            imageLoader.clear(softReference.get(), CustomImageConfig.builder().imageView(((MyViewPlayHolder) holder).imageView).build());
        }
    }

    public void recycle() {
        imageLoader = null;
        if (softReference != null) {
            softReference.clear();
            softReference = null;
        }
        if (mVideoList != null) {
            mVideoList.clear();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeadHolder(LayoutInflater.from(softReference.get()).inflate(R.layout.include_video_head, parent, false));
            case TYPE_ERROR:
                return new FootHolder(LayoutInflater.from(softReference.get()).inflate(R.layout.view_empty, parent, false));
            case TYPE_AD_BAIDU:
            case TYPE_AD_OWN:
            case TYPE_AD_CSJ:
                return new AdNormalHolder(LayoutInflater.from(softReference.get()).inflate(R.layout.item_ads_videodetail, parent, false));
            case TYPE_AD_GDT:
                return new AdGDTHolder(LayoutInflater.from(softReference.get()).inflate(R.layout.item_ads_gdt_videodetail, parent, false));
            case TYPE_NORMAL:
                return new MyViewPlayHolder(LayoutInflater.from(softReference.get()).inflate(R.layout.item_video_play, parent, false));
            default:
                return new MyViewPlayHolder(LayoutInflater.from(softReference.get()).inflate(R.layout.item_video_play, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 如果是正常的imte，直接设置TextView的值
        mTextSize = DataHelper.getStringSF(softReference.get().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        if (holder instanceof MyViewPlayHolder) {
            if (!TextUtils.isEmpty(mTextSize)) {
                switch (mTextSize) {
                    case "small":
                        ((MyViewPlayHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        break;
                    case "medium":
                        ((MyViewPlayHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        break;
                    case "big":
                        ((MyViewPlayHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        break;
                }
            } else {
                ((MyViewPlayHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }

            VideoBean videoBean = mVideoList.get(position - 1);
            ((MyViewPlayHolder) holder).tvTitle.setText(videoBean.getTitle());
            ((MyViewPlayHolder) holder).tvWriter.setText(videoBean.getSource());
            String time = videoBean.getUpdateTime();
            if (!TextUtils.equals("刚刚", time)) {
                time = time.substring(0, 10);
            }
            ((MyViewPlayHolder) holder).tvTime.setText(time);
            String url = videoBean.getThumbUrl();
            if (!url.startsWith("http")) {
                url = "http:" + url;
            }
            if (random == null) {
                random = new Random();
            }
            if (!TextUtils.isEmpty(url)) {
                imageLoader.loadImage(softReference.get(),
                        CustomImageConfig.builder().url(url)
                                .isCenterCrop(true)
                                .isClearMemory(true)
                                .isClearDiskCache(true)
                                .isSkipMemoryCache(true)
                                .cacheStrategy(1)
                                .errorPic(getRandomPic(random.nextInt(6)))
                                .placeholder(getRandomPic(random.nextInt(6)))
                                .fallback(getRandomPic(random.nextInt(6)))
                                .imageView(((MyViewPlayHolder) holder).imageView)
                                .build());
            } else {
                ((MyViewPlayHolder) holder).imageView.setImageResource(new Random().nextInt(6));
            }
            ((MyViewPlayHolder) holder).linearLayout.setOnLongClickListener(view -> {
//                    Timber.d("=======================长按了");
                return true;
            });
            ((MyViewPlayHolder) holder).linearLayout.setOnClickListener(view -> {
                if (!TextUtils.isEmpty(videoBean.getWebUrl())) {
                    mOnItemClickListener.onClick1(position - 1);
                }
            });
        } else if (holder instanceof FootHolder) {
            if (isShowError) {
                ((FootHolder) holder).tvError.setText(R.string.error_network);
            } else {
                ((FootHolder) holder).tvError.setText("");
            }
        } else if (holder instanceof AdNormalHolder) {
            VideoBean videoBean = mVideoList.get(position - 1);
            if (!TextUtils.isEmpty(mTextSize)) {//字体设置
                if (mTextSize.equals("small")) {
                    ((AdNormalHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                } else if (mTextSize.equals("medium")) {
                    ((AdNormalHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else if (mTextSize.equals("big")) {
                    ((AdNormalHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                }
            } else {
                ((AdNormalHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }

            if (videoBean.getAdNewType().equals("BaiDu") && videoBean.getNativeResponse() != null) {
                NativeResponse nativeResponse = videoBean.getNativeResponse();
                AQuery aQuery = new AQuery(((AdNormalHolder) holder).itemView);
                aQuery.id(((AdNormalHolder) holder).adsIamge).image(nativeResponse.getImageUrl(), false, false);
                aQuery.id(((AdNormalHolder) holder).adsTitle).text(nativeResponse.getTitle());
                aQuery.id(((AdNormalHolder) holder).adsWriter).text(nativeResponse.getBrandName());
                nativeResponse.recordImpression(((AdNormalHolder) holder).itemView);
                ((AdNormalHolder) holder).itemView.setOnClickListener(nativeResponse::handleClick);
            } else if (videoBean.getAdNewType().equals("CSJ") && videoBean.getTtFeedAd() != null) {
                TTFeedAd ttFeedAd = videoBean.getTtFeedAd();
                AQuery aQuery = new AQuery(((AdNormalHolder) holder).itemView);
                aQuery.id(((AdNormalHolder) holder).adsIamge).image(ttFeedAd.getImageList().get(0).getImageUrl(), false, false);
                aQuery.id(((AdNormalHolder) holder).adsTitle).text(ttFeedAd.getTitle());
                aQuery.id(((AdNormalHolder) holder).adsWriter).text(ttFeedAd.getSource());
                ttFeedAd.registerViewForInteraction((ViewGroup) ((AdNormalHolder) holder).itemView, ((AdNormalHolder) holder).itemView, new TTNativeAd.AdInteractionListener() {
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

            } else {
                String url = videoBean.getAdImage().get(0);//获取图片路径
                if (!url.startsWith("http")) {
                    url = "http:" + url;
                }
                try {
                    url = URLDecoder.decode(url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String URL = videoBean.getAdClickUrl();
                if (!TextUtils.isEmpty(url)) {
                    imageLoader.loadImage(softReference.get(),
                            CustomImageConfig.builder().url(url)
                                    .isCenterCrop(true)
                                    .isClearMemory(true)
                                    .isClearDiskCache(true)
                                    .isSkipMemoryCache(true)
                                    .cacheStrategy(1)
                                    .errorPic(getRandomPic(random.nextInt(6)))
                                    .placeholder(getRandomPic(random.nextInt(6)))
                                    .fallback(getRandomPic(random.nextInt(6)))
                                    .imageView(((AdNormalHolder) holder).adsIamge)
                                    .build());
                } else {
                    ((AdNormalHolder) holder).adsIamge.setImageResource(new Random().nextInt(6));
                }
                ((AdNormalHolder) holder).adsTitle.setText(videoBean.getAdTitle());
                ((AdNormalHolder) holder).adsWriter.setText("");
                ((AdNormalHolder) holder).itemView.setOnClickListener(view -> {
                    if (position < mVideoList.size()) {
                        Intent intent = new Intent(softReference.get(), AdWebActivity.class);
                        String adClickUrl = mVideoList.get(position - 1).getAdClickUrl();
                        String adTitle = mVideoList.get(position - 1).getAdTitle();
                        intent.putExtra("AdUrl", adClickUrl);
                        intent.putExtra("AdTitle", adTitle);
                        ArmsUtils.startActivity(intent);
                    }
                });
            }
        } else if (holder instanceof AdGDTHolder) {
            Timber.d("=========Ad-广告渲染：" + mVideoList.get(position - 1).getIsGDTAD()
                    + " " + mAdViewPositionMap.containsValue(position - 1) + "\n广告位置：" + (position - 1));
            if (mVideoList.get(position - 1).getIsGDTAD() && mAdViewPositionMap.containsValue(position - 1)) {
                ((AdGDTHolder) holder).lineView.setVisibility(View.VISIBLE);
                //如果广告视图存在则加载GDT广告视图，否则加载默认广告数据
                for (NativeExpressADView adView : mAdViewPositionMap.keySet()) {
                    if (mAdViewPositionMap.get(adView) == (position - 1) && adView != null) {
                        if (((AdGDTHolder) holder).linearLayout.getChildCount() > 0) {
                            ((AdGDTHolder) holder).linearLayout.removeAllViews();
                        }

                        if (adView.getParent() != null) {
                            ((ViewGroup) adView.getParent()).removeView(adView);
                        }
                        ((AdGDTHolder) holder).linearLayout.addView(adView);
                        adView.render();
                    }
                }

            } else {
                if (((AdGDTHolder) holder).linearLayout.getChildCount() > 0) {
                    ((AdGDTHolder) holder).linearLayout.removeAllViews();
                }

                View adView = LayoutInflater.from(softReference.get()).inflate(R.layout.item_ads_videodetail, ((AdGDTHolder) holder).linearLayout, false);
                TextView tvTitle = adView.findViewById(R.id.ads_videodetail_title);
                ImageView image = adView.findViewById(R.id.ads_videodetail_image);

                tvTitle.setText(mVideoList.get(position - 1).getAdTitle());
                try {
                    String url = mVideoList.get(position - 1).getAdImage().get(0);//获取图片路径
                    if (url.startsWith("http")) {
                    } else {
                        url = "http:" + url;
                    }
                    String url1 = URLDecoder.decode(url, "utf-8");
                    imageLoader.loadImage(softReference.get(), CustomImageConfig.builder().url(url1)
                            .isCenterCrop(true)
                            .cacheStrategy(1)
                            .errorPic(R.drawable.icon_dzkd_place)
                            .placeholder(R.drawable.icon_dzkd_place)
                            .imageView(image)
                            .build());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ((AdGDTHolder) holder).linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ((AdGDTHolder) holder).linearLayout.addView(adView);
                adView.setOnClickListener(v -> {
                    if (position < mVideoList.size()) {
                        String adClickUrl = mVideoList.get(position - 1).getAdClickUrl();
                        String adTitle = mVideoList.get(position - 1).getAdTitle();
                        Intent intent = new Intent(softReference.get(), AdWebActivity.class);
                        intent.putExtra("AdUrl", adClickUrl);
                        intent.putExtra("AdTitle", adTitle);
                        ArmsUtils.startActivity(intent);
                    }
                });
            }
        } else if (holder instanceof HeadHolder) {
            if (!TextUtils.isEmpty(mTextSize)) {
                switch (mTextSize) {
                    case "small":
                        ((HeadHolder) holder).tv_video_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        break;
                    case "medium":
                        ((HeadHolder) holder).tv_video_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        break;
                    case "big":
                        ((HeadHolder) holder).tv_video_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        break;
                }
            } else {
                ((HeadHolder) holder).tv_video_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
            ((HeadHolder) holder).tv_video_Title.setText(mTitle);
        }
    }

    public void setVideoTitle(String title) {
        this.mTitle = title;
        notifyItemChanged(0);
    }

    /**
     * 获取随机图片
     */
    private int getRandomPic(int random) {
        switch (random) {
            case 0:
                return R.drawable.icon_dzkd_place_1;
            case 1:
                return R.drawable.icon_dzkd_place_2;
            case 2:
                return R.drawable.icon_dzkd_place_3;
            case 3:
                return R.drawable.icon_dzkd_place_4;
            case 4:
                return R.drawable.icon_dzkd_place_5;
            case 5:
                return R.drawable.icon_dzkd_place_6;
            default:
                return R.drawable.icon_dzkd_place_6;
        }
    }

    //点击事件
    public interface OnItemClickListener {
        void onClick1(int position);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    // 正常的ViewHolder，
    class MyViewPlayHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        ImageView imageView;
        TextView tvTitle;
        TextView tvWriter;
        TextView tvTime;

        public MyViewPlayHolder(@NonNull View view) {
            super(view);
            linearLayout = view.findViewById(R.id.videoplay_ll);
            imageView = view.findViewById(R.id.videoplay_image);
            tvTitle = view.findViewById(R.id.videoplay_title);
            tvWriter = view.findViewById(R.id.videoplay_writer);
            tvTime = view.findViewById(R.id.videoplay_time);
        }
    }

    // 顶部headView的ViewHolder，用以缓存findView操作
    class HeadHolder extends RecyclerView.ViewHolder {
        public TextView tv_video_Title;

        public HeadHolder(@NonNull View view) {
            super(view);
            tv_video_Title = view.findViewById(R.id.tv_video_Title);
        }
    }

    // 底部footView的ViewHolder，用以缓存findView操作
    class FootHolder extends RecyclerView.ViewHolder {
        public TextView tvError;

        public FootHolder(@NonNull View view) {
            super(view);
            tvError = view.findViewById(R.id.tv_error);
        }
    }

    //视频详情页的 百度广告 ViewHolder
    class AdNormalHolder extends RecyclerView.ViewHolder {
        public LinearLayout adsLayout;
        public ImageView adsIamge;
        public TextView adsTitle;
        public TextView adsWriter;

        public AdNormalHolder(@NonNull View itemView) {
            super(itemView);
            adsLayout = itemView.findViewById(R.id.ads_videodetail_layout);
            adsIamge = itemView.findViewById(R.id.ads_videodetail_image);
            adsTitle = itemView.findViewById(R.id.ads_videodetail_title);
            adsWriter = itemView.findViewById(R.id.ads_videodetail_writer);
        }
    }

    // 视频详情页广告布局的ViewHolder
    class AdGDTHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private View lineView;

        public AdGDTHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ads_gdt_videodetail_layout);
            lineView = itemView.findViewById(R.id.ads_native_videoplay_lineview);
        }
    }
}
