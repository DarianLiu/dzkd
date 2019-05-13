package com.dzkandian.mvp.video.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.baidu.mobad.feeds.NativeResponse;
import com.dzkandian.R;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.TimesUtils;
import com.dzkandian.mvp.common.ui.activity.AdWebActivity;
import com.dzkandian.mvp.video.ui.activity.VideoDetailActivity;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;


/**
 * 视频列表适配器
 * Created by XiaoJianjun on 2017/5/21.
 */

public class VideoViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SoftReference<Fragment> softReference;

    private List<VideoBean> mVideoList = new ArrayList<>();
    private final int normalType = 0;  // 第一种ViewType，正常的item
    private final int errType = 2;     // 第三种ViewType，网络错误
    private final int adsNativeVideoType = 5; //视频广告布局；

    private String type;

    private boolean isShowError = false;//是否显示异常布局，默认为false
    private Random random;
    private String mTextSize;
    private ImageLoader imageLoader;

    public VideoViewAdapter(Fragment context, String type, String textSize) {
        this.softReference = new SoftReference<>(context);
        this.type = type;
        this.mTextSize = textSize;
        this.imageLoader = ArmsUtils.obtainAppComponentFromContext(context.getActivity()).imageLoader();
    }

    @Override
    public int getItemCount() {
        if (mVideoList.size() == 0)
            return 1;
        return mVideoList.size();
    }

    public int getNewsSize() {
        return mVideoList.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (mVideoList.size() == 0) {
            return errType;
        } else if (TextUtils.equals(mVideoList.get(position).getType(), "ad")) {
            return adsNativeVideoType;
        } else {
            return normalType;
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
     * 刷新视频
     *
     * @param videoList 视频列表
     */
    public void refreshNews(List<VideoBean> videoList) {
        int size = mVideoList.size();
        mVideoList.clear();
        notifyItemRangeRemoved(0, size);
//        destroyADView();
//        mAdViewPositionMap.clear();

        mVideoList.addAll(videoList);
        notifyItemRangeChanged(0, videoList.size());
    }

    /**
     * 加载更多资讯
     *
     * @param videoList 资讯列表
     */
    public void loadMoreNews(List<VideoBean> videoList) {
        int size = mVideoList.size();
        mVideoList.addAll(videoList);
        notifyItemRangeInserted(size, mVideoList.size());
    }

    public void recycle() {
        imageLoader = null;
        softReference.clear();
        softReference = null;
        mVideoList.clear();
        mVideoList = null;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MyViewHolder) {
            imageLoader.clear(softReference.get().getContext(), CustomImageConfig.builder().imageView(((MyViewHolder) holder).videoPlayerImage).build());
        } else if (holder instanceof AdsVideoHolder) {
            imageLoader.clear(softReference.get().getContext(), CustomImageConfig.builder().imageView(((AdsVideoHolder) holder).ivNativeAdCover).build());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case normalType:
                return new MyViewHolder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.item_video, null));
            case adsNativeVideoType:
                return new AdsVideoHolder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.item_video_native_ad, null));
            case errType:
                return new EmptyViewHolder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.view_empty, null));
            default:
                return new MyViewHolder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.item_video, null));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        mTextSize = DataHelper.getStringSF(softReference.get().getContext().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        VideoBean videoBean = null;
        String videoId = "", videoUrl = "", thumbUrl = "", title = "", duration = "", describe = "",
                canShare = "", webShareUrl = "", source = "", playbackCount = "";
        if (mVideoList.size() > 0) {
            videoBean = mVideoList.get(position);

            if (videoBean.getType().equals("ad")) {
                videoId = mVideoList.get(position + 1).getVideoId();
            } else {
                videoId = mVideoList.get(position).getVideoId();
            }
            videoUrl = videoBean.getUrl();

            thumbUrl = videoBean.getThumbUrl();
            if (!thumbUrl.startsWith("http")) {
                thumbUrl = "http:" + thumbUrl;
            }

            title = videoBean.getTitle();
            duration = videoBean.getDuration();
            describe = videoBean.getDescribe();
            canShare = videoBean.getCanShare();
            webShareUrl = videoBean.getWebUrl();
            source = videoBean.getSource();
            playbackCount = videoBean.getPlaybackCount();
        }

        if (random == null) {
            random = new Random();
        }

        if (holder instanceof MyViewHolder) {
            MyViewHolder videoHolder = (MyViewHolder) holder;

            int screenWidth = ArmsUtils.getScreenWidth(softReference.get().getContext().getApplicationContext());
            int height = screenWidth * 9 / 16;
            videoHolder.relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, height));

            if (!TextUtils.isEmpty(mTextSize)) {
                if (mTextSize.equals("small")) {
                    videoHolder.videoPlayTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                } else if (mTextSize.equals("medium")) {
                    videoHolder.videoPlayTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else if (mTextSize.equals("big")) {
                    videoHolder.videoPlayTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                }
            } else {
                videoHolder.videoPlayTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }

            if (!TextUtils.isEmpty(thumbUrl)) {
                imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder().url(thumbUrl)
                        .isCenterCrop(true)
                        .isClearMemory(true)
                        .isClearDiskCache(true)
                        .isSkipMemoryCache(true)
                        .cacheStrategy(1)
                        .errorPic(getRandomPic(random.nextInt(6)))
                        .placeholder(getRandomPic(random.nextInt(6)))
                        .fallback(getRandomPic(random.nextInt(6)))
                        .override(screenWidth, height)
                        .imageView(videoHolder.videoPlayerImage)
                        .build());
            } else {
                videoHolder.videoPlayerImage.setImageResource(random.nextInt(6));
            }

            videoHolder.videoPlayTitle.setText(title);
            videoHolder.videoPlayTime.setText(TimesUtils.getVideoTime(Integer.parseInt(duration)));
            videoHolder.tvName.setText(source);
            videoHolder.tvLiang.setText(playbackCount);

            String finalThumbUrl = thumbUrl;
            String finalVideoUrl = videoUrl;
            String finalTitle = title;
            String finalVideoId = videoId;
            String finalCanShare = canShare;
            String finalWebShareUrl = webShareUrl;
            String finalDescribe = describe;
            videoHolder.videoPlayLL.setOnClickListener(v -> {
//                Timber.d("====================mVideoList.get(position).getWebUrl()" + mVideoList.get(position).getWebUrl());
                Intent intent = new Intent(softReference.get().getActivity(), VideoDetailActivity.class);

                if (!TextUtils.isEmpty(finalVideoUrl)) {
                    intent.putExtra("url", finalVideoUrl);
                    intent.putExtra("image_url", finalThumbUrl);
                    intent.putExtra("title", finalTitle);
                    intent.putExtra("tab", type);
                    intent.putExtra("id", finalVideoId);
                    intent.putExtra("is_share", finalCanShare);
                    intent.putExtra("web_url", finalWebShareUrl);
                    intent.putExtra("content", finalDescribe);
                    intent.putExtra("textSize", mTextSize);
                }
                ArmsUtils.startActivity(intent);
            });
        } else if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;

            emptyViewHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (DeviceUtils.getScreenHeight(softReference.get().getContext().getApplicationContext()) * 0.6)));
            if (isShowError) {
                emptyViewHolder.tvError.setText(R.string.error_network);
                emptyViewHolder.tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                        softReference.get().getResources().getDrawable(R.drawable.icon_error_network),
                        null, null);
            }
        } else if (holder instanceof AdsVideoHolder) {
            AdsVideoHolder adsVideoHolder = (AdsVideoHolder) holder;
            int screenWidth = ArmsUtils.getScreenWidth(softReference.get().getContext().getApplicationContext());
            int height = screenWidth * 9 / 16;

            if (!TextUtils.isEmpty(mTextSize)) {
                if (mTextSize.equals("small")) {
                    adsVideoHolder.tvNativeAdTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                } else if (mTextSize.equals("medium")) {
                    adsVideoHolder.tvNativeAdTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else if (mTextSize.equals("big")) {
                    adsVideoHolder.tvNativeAdTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                }
            } else {
                adsVideoHolder.tvNativeAdTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
            adsVideoHolder.ivNativeAdCover.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, height));
            if (videoBean.getAdNewType() == "BaiDu" && videoBean.getNativeResponse() != null) {
                adsVideoHolder.ivNativeAdCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                NativeResponse nrAd = videoBean.getNativeResponse();
                AQuery aQuery = new AQuery(adsVideoHolder.itemView);
                aQuery.id(adsVideoHolder.ivNativeAdCover).image(nrAd.getImageUrl(), false, true);
                aQuery.id(adsVideoHolder.tvNativeAdTitle).text(nrAd.getTitle());
                aQuery.id(adsVideoHolder.tvNativeAdSource).text(nrAd.getBrandName());
                nrAd.recordImpression(adsVideoHolder.itemView);
                adsVideoHolder.itemView.setOnClickListener(nrAd::handleClick);
            } else {
                if (!TextUtils.isEmpty(thumbUrl)) {
                    imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder()
                            .url(thumbUrl)
                            .isCenterCrop(true)
                            .isClearMemory(true)
                            .isClearDiskCache(true)
                            .isSkipMemoryCache(true)
                            .cacheStrategy(1)
                            .errorPic(getRandomPic(random.nextInt(6)))
                            .placeholder(getRandomPic(random.nextInt(6)))
                            .fallback(getRandomPic(random.nextInt(6)))
                            .imageView(adsVideoHolder.ivNativeAdCover)
                            .build());
                } else {
                    adsVideoHolder.ivNativeAdCover.setImageResource(random.nextInt(6));
                }
                adsVideoHolder.tvNativeAdTitle.setText(title);
                adsVideoHolder.tvNativeAdSource.setText(source);

                String finalWebShareUrl1 = webShareUrl;
                String finalTitle1 = title;
                adsVideoHolder.itemView.setOnClickListener(v -> {
//                    Timber.d("====================mVideoList.get(position).getWebUrl()" + mVideoList.get(position).getWebUrl());
                    if (!TextUtils.isEmpty(finalWebShareUrl1)) {
                        Intent intent = new Intent(softReference.get().getActivity(), AdWebActivity.class);
                        intent.putExtra("AdUrl", finalWebShareUrl1);
                        intent.putExtra("AdTitle", finalTitle1);
                        ArmsUtils.startActivity(intent);
                    }
                });
            }
        }
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout videoPlayLL;
        RelativeLayout relativeLayout;
        ImageView videoPlayerImage;
        TextView videoPlayTitle;
        TextView videoPlayTime;
        LinearLayout ll;
        TextView tvName;
        TextView tvLiang;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            videoPlayLL = itemView.findViewById(R.id.ll_video_play);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            videoPlayerImage = itemView.findViewById(R.id.image_video_item);
            videoPlayTitle = itemView.findViewById(R.id.tv_video_title);
            videoPlayTime = itemView.findViewById(R.id.tv_video_time);
            ll = itemView.findViewById(R.id.ll_video_item);
            tvName = itemView.findViewById(R.id.tv_video_name);
            tvLiang = itemView.findViewById(R.id.tv_video_fang);

        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvError;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvError = itemView.findViewById(R.id.tv_error);
        }
    }


    // 视频广告布局的ViewHolder
    class AdsVideoHolder extends RecyclerView.ViewHolder {
        ImageView ivNativeAdCover;
        TextView tvNativeAdTitle;
        TextView tvNativeAdSource;

        public AdsVideoHolder(@NonNull View itemView) {
            super(itemView);
            ivNativeAdCover = itemView.findViewById(R.id.iv_native_ad_cover);
            tvNativeAdTitle = itemView.findViewById(R.id.tv_native_ad_title);
            tvNativeAdSource = itemView.findViewById(R.id.tv_native_ad_source);
        }
    }

}
