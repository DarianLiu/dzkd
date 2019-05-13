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
import com.dzkandian.mvp.common.ui.activity.AdWebActivity;
import com.dzkandian.mvp.video.ui.activity.ShortDetailActivity;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 小视频列表（瀑布流）
 * Created by liuli on 2018/7/26.
 */

public class VideoWaterfallAdapter extends RecyclerView.Adapter<VideoWaterfallAdapter.MyViewHolder> {

    private List<VideoBean> mVideoList = new ArrayList<>();
    private final int normalType = 1;  // 第一种ViewType，正常的item
    private final int adsNativeVideoType = 2; //视频原生广告；
    private final int adsGDTVideoType = 3; //视频广点通广告；
    private final int adsBDVideoType = 4; //视频百度广告；

    private String type;//栏目类型
    private String mTextSize;

    private SoftReference<Fragment> context;

    private long clickShortVideoItemTimes;//小视频列表 点击item 的上一次时间；
    private ImageLoader imageLoader;

    public VideoWaterfallAdapter(Fragment context, String type, String textSize) {
        this.context = new SoftReference<>(context);
        this.type = type;
        this.mTextSize = textSize;
        this.imageLoader = ArmsUtils.obtainAppComponentFromContext(context.getActivity()).imageLoader();
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public int getVideoSize() {
        return mVideoList.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mVideoList.get(position).getType(), "video")) {
            return normalType;
        } else if (mVideoList.get(position).getAdType() == 1) {
            return adsGDTVideoType;
        } else if (mVideoList.get(position).getAdType() == 2
                && mVideoList.get(position).getNativeResponse() != null) {
            return adsBDVideoType;
        } else {
            return adsNativeVideoType;
        }
    }

    /**
     * 刷新资讯
     *
     * @param videoList 资讯列表
     */
    public void refreshNews(List<VideoBean> videoList) {
        int size = mVideoList.size();
        mVideoList.clear();
        notifyItemRangeRemoved(0, size);

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

    /**
     * 另类刷新  加载数据库数据
     */
    public void refreshDBNews(List<VideoBean> videoList) {
        videoList.addAll(mVideoList);
        mVideoList.clear();
        mVideoList = videoList;
        notifyItemRangeChanged(0, mVideoList.size());
    }

    public void recycle() {
        imageLoader = null;
        context.clear();
        context = null;
        mVideoList.clear();
        mVideoList = null;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context.get().getContext()).inflate(R.layout.item_video_waterfall, null));
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        imageLoader.clear(context.get().getContext(), CustomImageConfig.builder().imageView(holder.ivVideo).build());
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // 如果是正常的imte，直接设置TextView的值
//        if (holder instanceof MyViewHolder) {
        mTextSize = DataHelper.getStringSF(context.get().getContext().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        if (!TextUtils.isEmpty(mTextSize)) {
            switch (mTextSize) {
                case "small":
                    holder.tvVideoName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    break;
                case "medium":
                    holder.tvVideoName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    break;
                case "big":
                    holder.tvVideoName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    break;
            }
        } else {
            holder.tvVideoName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        VideoBean videoBean = mVideoList.get(position);

        String videoUrl = videoBean.getUrl();
        String imageUrl = videoBean.getThumbUrl();
        String title = videoBean.getTitle();
        String source = videoBean.getSource();
        String playCount = videoBean.getPlaybackCount();

        if (!imageUrl.startsWith("http")) {
            imageUrl = "http:" + imageUrl;
        }

        int width = (ArmsUtils.getScreenWidth(context.get().getContext().getApplicationContext()) - 2) / 2;
        int height = width * Integer.parseInt(videoBean.getHeight()) / Integer.parseInt(videoBean.getWidth());
        int viewType = getItemViewType(position);
        holder.ivVideo.setBackgroundResource(getRandomPic(new Random().nextInt(6)));
        switch (viewType) {
            case normalType://小视频类型
                holder.ivVideo.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
                if (!TextUtils.isEmpty(imageUrl)) {
                    imageLoader.loadImage(context.get().getContext(), CustomImageConfig.builder().url(imageUrl)
                            .isCenterCrop(true)
                            .isClearMemory(true)
                            .isClearDiskCache(true)
                            .isSkipMemoryCache(true)
                            .cacheStrategy(1)
                            .errorPic(getRandomPic(new Random().nextInt(6)))
                            .placeholder(getRandomPic(new Random().nextInt(6)))
                            .fallback(getRandomPic(new Random().nextInt(6)))
                            .imageView(holder.ivVideo)
                            .override(width, height)
                            .build());
                } else {
                    holder.ivVideo.setImageResource(new Random().nextInt(6));
                }

                holder.tvVideoName.setText(title);
                holder.tvVideoSource.setText(source);
                holder.tvVideoViewCount.setText(playCount);

                holder.itemView.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(videoUrl)) {
                        if (System.currentTimeMillis() - clickShortVideoItemTimes > 1000) {
                            clickShortVideoItemTimes = System.currentTimeMillis();
                            videoBean.setNativeResponse(null);
                            Intent intent = new Intent(context.get().getActivity(), ShortDetailActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("video", videoBean);
                            intent.putExtra("position", position);
                            intent.putExtra("textSize", mTextSize);
                            intent.putExtra("playId", videoBean.getID());
                            if (position < mVideoList.size() - 1) {
                                VideoBean nextVideo = mVideoList.get(position + 1);
                                if (nextVideo.getType().equals("ad")) {
                                    VideoBean bean = new VideoBean();
                                    bean.setTitle(nextVideo.getTitle());
                                    bean.setVideoId(nextVideo.getVideoId());
                                    bean.setType(nextVideo.getType());
                                    bean.setSource(nextVideo.getSource());
                                    bean.setAvatar(nextVideo.getAvatar());
                                    bean.setDescribe(nextVideo.getDescribe());
                                    bean.setThumbUrl(nextVideo.getThumbUrl());
                                    bean.setCanShare(nextVideo.getCanShare());
                                    bean.setPlaybackCount(nextVideo.getPlaybackCount());
                                    bean.setUpdateTime(nextVideo.getUpdateTime());
                                    bean.setUrl(nextVideo.getUrl());
                                    bean.setWebUrl(nextVideo.getWebUrl());
                                    bean.setHeight(nextVideo.getHeight());
                                    bean.setWidth(nextVideo.getWidth());
                                    intent.putExtra("next_video", bean);
                                } else {
                                    intent.putExtra("next_video", nextVideo);
                                }
                            }
                            ArmsUtils.startActivity(intent);
                        }
                    }
                });
                break;
            case adsNativeVideoType://原生广告类型
                holder.ivVideo.setLayoutParams(new RelativeLayout.LayoutParams(width, height));

                if (!TextUtils.isEmpty(imageUrl)) {
                    imageLoader.loadImage(context.get().getContext(), CustomImageConfig.builder().url(imageUrl)
                            .isCenterCrop(true)
                            .isClearMemory(true)
                            .isClearDiskCache(true)
                            .isSkipMemoryCache(true)
                            .cacheStrategy(1)
                            .errorPic(getRandomPic(new Random().nextInt(6)))
                            .placeholder(getRandomPic(new Random().nextInt(6)))
                            .fallback(getRandomPic(new Random().nextInt(6)))
                            .override(width, height)
                            .imageView(holder.ivVideo)
                            .build());
                } else {
                    holder.ivVideo.setImageResource(new Random().nextInt(6));
                }

                holder.tvVideoName.setText(title);
                holder.tvVideoSource.setText(source);
                holder.tvVideoViewCount.setText(playCount);
                holder.itemView.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(mVideoList.get(position).getWebUrl())) {
                        Intent intent = new Intent(context.get().getActivity(), AdWebActivity.class);
                        intent.putExtra("AdUrl", videoBean.getWebUrl());
                        intent.putExtra("AdTitle", videoBean.getTitle());
                        ArmsUtils.startActivity(intent);
                    }
                });
                break;
            case adsBDVideoType://百度广告类型
                holder.ivVideo.setLayoutParams(new RelativeLayout.LayoutParams(width, width / 2 * 3));
                holder.ivVideo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                NativeResponse nrAd = videoBean.getNativeResponse();
                AQuery aQuery = new AQuery(holder.itemView);
                aQuery.id(holder.ivVideo).image(nrAd.getImageUrl(), false, true);
                aQuery.id(holder.tvVideoName).text(nrAd.getTitle());
                aQuery.id(holder.tvVideoSource).text(nrAd.getBrandName());
                aQuery.id(holder.tvVideoViewCount).text("广告");
                nrAd.recordImpression(holder.itemView);
                holder.itemView.setOnClickListener(nrAd::handleClick);
                break;
            case adsGDTVideoType://广点通广告类型
                break;
        }
    }

    /**
     * 获取随机图片
     */
    private int getRandomPic(int random) {
        switch (random) {
            case 0:
                return R.drawable.icon_dzkd_short_1;
            case 1:
                return R.drawable.icon_dzkd_short_2;
            case 2:
                return R.drawable.icon_dzkd_short_3;
            case 3:
                return R.drawable.icon_dzkd_short_4;
            case 4:
                return R.drawable.icon_dzkd_short_5;
            case 5:
                return R.drawable.icon_dzkd_short_6;
            default:
                return R.drawable.icon_dzkd_short_6;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVideo;
        TextView tvVideoName;
        TextView tvVideoSource;
        TextView tvVideoViewCount;
        RelativeLayout rlContent;
        LinearLayout llVideoContent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVideo = itemView.findViewById(R.id.iv_video);
            tvVideoName = itemView.findViewById(R.id.tv_video_name);
            tvVideoSource = itemView.findViewById(R.id.tv_video_source);
            tvVideoViewCount = itemView.findViewById(R.id.tv_video_view_count);
            rlContent = itemView.findViewById(R.id.rl_content);
            llVideoContent = itemView.findViewById(R.id.ll_video_content);
        }
    }
}