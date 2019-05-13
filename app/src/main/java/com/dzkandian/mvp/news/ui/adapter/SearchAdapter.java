package com.dzkandian.mvp.news.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.mvp.news.ui.activity.NewsDetailActivity;
import com.dzkandian.mvp.video.ui.activity.ShortDetailActivity;
import com.dzkandian.mvp.video.ui.activity.VideoDetailActivity;
import com.dzkandian.storage.bean.SearchBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2019/1/21.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<SearchBean> mList = new ArrayList<>();     // 资讯数据列表
    private Random mRandom;
    private ImageLoader mImageLoader;

    private boolean isShowError = false;//是否显示异常布局，默认为false
    private final static int normalType = 1;  //常的item
    private final static int ErrorType = 0; //异常布局

    public SearchAdapter(Context context) {
        this.mContext = context;
        this.mRandom = new Random();
        this.mImageLoader = ArmsUtils.obtainAppComponentFromContext(context).imageLoader();
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        } else {
            return mList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0) {
            return ErrorType;
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
        mList.clear();
        this.isShowError = isShow;
        notifyDataSetChanged();
    }

    /**
     * 刷新搜索列表
     *
     * @param list 搜索列表
     */
    public void refreshSearch(List<SearchBean> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 加载搜索列表
     *
     * @param list 搜索列表
     */
    public void loadMoreSearch(List<SearchBean> list) {
        int size = mList.size();
        mList.addAll(list);
        notifyItemRangeInserted(size, list.size());
    }

    public void recycle() {
        mImageLoader = null;
        mContext = null;
        mList.clear();
        mList = null;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof SearchHolder) {
            mImageLoader.clear(mContext, CustomImageConfig.builder().imageView(((SearchHolder) holder).ivSearch).build());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case normalType:
                return new SearchHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_data, null));
            default:
                return new EmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_empty, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchHolder) {
            SearchBean searchBean = mList.get(position);
            SearchHolder searchHolder = (SearchHolder) holder;

            int screenWidth = ArmsUtils.getScreenWidth(mContext.getApplicationContext());
            int width = (screenWidth - ArmsUtils.dip2px(mContext.getApplicationContext(), 22)) / 3;
            int height = width * 3 / 4;
            searchHolder.ivSearch.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            String url = searchBean.getImg();//获取图片路径
            if (!TextUtils.isEmpty(url)) {
                if (!url.startsWith("http")) {
                    url = "http:" + url;
                }
                mImageLoader.loadImage(mContext, CustomImageConfig.builder().url(url)
                        .isCenterCrop(true)
                        .isClearMemory(true)
                        .isClearDiskCache(true)
                        .isSkipMemoryCache(true)
                        .cacheStrategy(1)
                        .errorPic(getRandomPic(mRandom.nextInt(6)))
                        .placeholder(getRandomPic(mRandom.nextInt(6)))
                        .fallback(getRandomPic(mRandom.nextInt(6)))
                        .override(width, height)
                        .imageView(searchHolder.ivSearch)
                        .build());
            } else {
                searchHolder.ivSearch.setImageResource(R.drawable.icon_dzkd_news_1);
            }

            if (!TextUtils.isEmpty(searchBean.getType()) && !searchBean.getType().equals("news")) {
                searchHolder.ivSearchVideo.setVisibility(View.VISIBLE);
            } else {
                searchHolder.ivSearchVideo.setVisibility(View.GONE);
            }

            searchHolder.tvSearchTitle.setText(searchBean.getTitle());
            searchHolder.tvSearchWriter.setText(searchBean.getSourceName());
            String time = searchBean.getCreateTime();
            if (!TextUtils.isEmpty(time) && time.length() >= 10) {
                time = time.substring(0, 10);
            }
            searchHolder.tvSearchTime.setText(time);

            searchHolder.itemView.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(searchBean.getType())) {
                    if (searchBean.getType().equals("news")) {
                        Intent intent = new Intent(mContext, NewsDetailActivity.class);
                        intent.putExtra("web_url", searchBean.getUrl());
                        intent.putExtra("id", searchBean.getId());
                        intent.putExtra("tab", searchBean.getCategory());
                        intent.putExtra("is_share", searchBean.getCanShare());
                        intent.putExtra("title", searchBean.getTitle());
                        intent.putExtra("IMAGES", searchBean.getImg());
                        ArmsUtils.startActivity(intent);
                    } else if (searchBean.getType().equals("video")) {
                        Intent intent = new Intent(mContext, VideoDetailActivity.class);
                        intent.putExtra("url", searchBean.getUrl());
                        intent.putExtra("image_url", searchBean.getImg());
                        intent.putExtra("title", searchBean.getTitle());
                        intent.putExtra("tab", searchBean.getCategory());
                        intent.putExtra("id", searchBean.getId());
                        intent.putExtra("is_share", searchBean.getCanShare());
                        intent.putExtra("web_url", searchBean.getWebUrl());
                        ArmsUtils.startActivity(intent);
                    } else if (searchBean.getType().equals("wuli")) {
                        VideoBean videoBean = new VideoBean();
                        videoBean.setVideoId(searchBean.getId());
                        videoBean.setTitle(searchBean.getTitle());
                        videoBean.setType("video");
                        videoBean.setThumbUrl(searchBean.getImg());
                        videoBean.setUrl(searchBean.getUrl());
                        videoBean.setSource(searchBean.getSourceName());
                        videoBean.setUpdateTime(searchBean.getCreateTime());
                        videoBean.setCanShare(String.valueOf(searchBean.getCanShare()));
                        videoBean.setWebUrl(searchBean.getWebUrl());
                        Intent intent = new Intent(mContext, ShortDetailActivity.class);
                        intent.putExtra("type", searchBean.getCategory());
                        intent.putExtra("video", videoBean);
                        intent.putExtra("shortCollection", "shortCollection"); //如果是小视频收藏页进去则不发送通知
                        ArmsUtils.startActivity(intent);
                    }
                }
            });

        } else if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            emptyViewHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            if (isShowError) {
                emptyViewHolder.tvError.setText(R.string.error_network);
                emptyViewHolder.tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                        mContext.getResources().getDrawable(R.drawable.icon_error_network),
                        null, null);
            } else {
                emptyViewHolder.tvError.setText(R.string.error_empty_search);
                emptyViewHolder.tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                        mContext.getResources().getDrawable(R.drawable.icon_search_none),
                        null, null);
            }
        }
    }

    private class SearchHolder extends RecyclerView.ViewHolder {
        public ImageView ivSearch;
        public ImageView ivSearchVideo;
        public TextView tvSearchTitle;
        public TextView tvSearchWriter;
        public TextView tvSearchTime;

        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            ivSearch = itemView.findViewById(R.id.iv_search);
            ivSearchVideo = itemView.findViewById(R.id.iv_search_video);
            tvSearchTitle = itemView.findViewById(R.id.tv_search_title);
            tvSearchWriter = itemView.findViewById(R.id.tv_search_writer);
            tvSearchTime = itemView.findViewById(R.id.tv_search_time);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvError;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvError = itemView.findViewById(R.id.tv_error);
        }
    }

    /**
     * 获取随机图片
     */
    private int getRandomPic(int random) {
        switch (random) {
            case 0:
                return R.drawable.icon_dzkd_news_1;
            case 1:
                return R.drawable.icon_dzkd_news_2;
            case 2:
                return R.drawable.icon_dzkd_news_3;
            case 3:
                return R.drawable.icon_dzkd_news_4;
            case 4:
                return R.drawable.icon_dzkd_news_5;
            case 5:
                return R.drawable.icon_dzkd_news_6;
            default:
                return R.drawable.icon_dzkd_news_6;
        }
    }
}
