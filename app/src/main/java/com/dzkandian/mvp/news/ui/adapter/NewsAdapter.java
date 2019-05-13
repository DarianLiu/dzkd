package com.dzkandian.mvp.news.ui.adapter;

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
import android.widget.TextView;

import com.baidu.mobad.feeds.NativeResponse;
import com.dzkandian.R;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.AdWebActivity;
import com.dzkandian.mvp.common.ui.activity.WebViewActivity;
import com.dzkandian.mvp.news.ui.activity.NewsDetailActivity;
import com.dzkandian.storage.bean.news.NewsBean;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 资讯列表适配器
 * Created by Administrator on 2018/1/19 0019.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String mType;
    private List<NewsBean> datas = new ArrayList<>();     // 资讯数据列表
    private WeakReference<Fragment> softReference;      // 上下文Context
    private final static int normalType = 0;  // 第一种ViewType，正常的item
    private final static int TwoType = 1;    // 三图的布局
    private final static int ErrorType = 2; //异常布局
    private final static int adsNativeNewsType = 5; //新闻广告布局；

    private boolean isShowError = false;//是否显示异常布局，默认为false
    private Random random;
    private String mTextSize;
    private ImageLoader imageLoader;

    public NewsAdapter(String type, Fragment context, String textSize) {
        //初始化变量
        softReference = new WeakReference<>(context);
        this.mType = type;
        this.mTextSize = textSize;
        this.random = new Random();
        this.imageLoader = ArmsUtils.obtainAppComponentFromContext(context.getContext()).imageLoader();
    }

    // 获取条目数量，之所以要加1是因为增加了一条footView
    @Override
    public int getItemCount() {
        if (datas.size() == 0) {
            return 1;
        } else {
            return datas.size();
        }
    }

    public int getNewsSize() {
        return datas.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (datas.size() == 0) {
            return ErrorType;
        } else if (TextUtils.equals(datas.get(position).getType(), "ad")) {
            return adsNativeNewsType;
        } else if (datas.get(position).getImages().size() >= 3) {
            return TwoType;
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
        datas.clear();
        this.isShowError = isShow;
        notifyDataSetChanged();
    }

    /**
     * 刷新资讯
     *
     * @param newsList 资讯列表
     */
    public void refreshNews(List<NewsBean> newsList) {
        int size = datas.size();
        datas.clear();
        notifyItemRangeRemoved(0, size);
//        destroyADView();
//        mAdViewPositionMap.clear();

        datas.addAll(newsList);
        notifyItemRangeChanged(0, newsList.size());
    }

    /**
     * 加载更多资讯
     *
     * @param newsList 资讯列表
     */
    public void loadMoreNews(List<NewsBean> newsList) {
        int size = datas.size();
        datas.addAll(newsList);
        notifyItemRangeInserted(size, newsList.size());
    }

    public void recycle() {
        imageLoader = null;
        softReference.clear();
        softReference = null;
        datas.clear();
        datas = null;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MyHolder) {
            imageLoader.clear(softReference.get().getContext(), CustomImageConfig.builder().imageView(((MyHolder) holder).iv).build());
        } else if (holder instanceof MyNew2Holder) {
            imageLoader.clear(softReference.get().getContext(), CustomImageConfig.builder().imageView(((MyNew2Holder) holder).iv1).build());
            imageLoader.clear(softReference.get().getContext(), CustomImageConfig.builder().imageView(((MyNew2Holder) holder).iv2).build());
            imageLoader.clear(softReference.get().getContext(), CustomImageConfig.builder().imageView(((MyNew2Holder) holder).iv3).build());
        } else if (holder instanceof AdBaiduHolder) {
            imageLoader.clear(softReference.get().getContext(), CustomImageConfig.builder().imageView(((AdBaiduHolder) holder).adsIamge).build());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据返回的ViewType，绑定不同的布局文件，这里只有两种
        switch (viewType) {
            case normalType:
                return new MyHolder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.item_news_pic_one, null));
            case TwoType:
                return new MyNew2Holder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.item_news_pic_three, null));
            case adsNativeNewsType:
                return new AdBaiduHolder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.item_ads_baidu_news, null));
            default:
                return new EmptyViewHolder(LayoutInflater.from(softReference.get().getContext()).inflate(R.layout.view_empty, null));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        NewsBean newsBean = null;
        int top = 0;
        String time = "",
                canShare = "",
                title = "",
                source = "",
                URL = "",
                type = "",
                id = "";
        if (datas.size() > 0) {
            newsBean = datas.get(position);
            top = newsBean.getTop();
            type = newsBean.getType();
            time = newsBean.getUpdateTime();
            canShare = newsBean.getCanShare();
            title = newsBean.getTitle();
            source = newsBean.getSource();
            URL = newsBean.getUrl();
            id = newsBean.getId();
        }

//        Timber.d("=====================NEWS-" + "title：" + title
//                + " isAd：" + (holder instanceof AdBaiduHolder)
//                + " isBaiDuAd：" + (newsBean != null && newsBean.getAdType() == 2 && newsBean.getNativeResponse() != null)
//                + "  adType: " + (newsBean != null ? newsBean.getAdType() : -1));

        mTextSize = DataHelper.getStringSF(softReference.get().getContext().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        if (holder instanceof MyHolder) {
            if (!TextUtils.isEmpty(mTextSize)) {
                if (mTextSize.equals("small")) {
                    ((MyHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                } else if (mTextSize.equals("medium")) {
                    ((MyHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else if (mTextSize.equals("big")) {
                    ((MyHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                }
            } else {
                ((MyHolder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }

            if (!TextUtils.isEmpty(type)) {
                if (type.equals("at")) {
                    ((MyHolder) holder).tvTop.setVisibility(View.VISIBLE);
                    ((MyHolder) holder).tvTop.setText("置顶");
                } else {
                    if (top == 1) {
                        ((MyHolder) holder).tvTop.setVisibility(View.VISIBLE);
                        ((MyHolder) holder).tvTop.setText("置顶");
                    } else {
                        ((MyHolder) holder).tvTop.setVisibility(View.GONE);
                    }
                }
            } else {
                ((MyHolder) holder).tvTop.setVisibility(View.GONE);
            }

            int screenWidth = ArmsUtils.getScreenWidth(softReference.get().getContext().getApplicationContext());
            int width = (screenWidth - ArmsUtils.dip2px(softReference.get().getContext().getApplicationContext(), 22)) / 3;
            int height = width * 3 / 4;
            ((MyHolder) holder).iv.setLayoutParams(new LinearLayout.LayoutParams(width, height));

            ((MyHolder) holder).tvTitle.setText(title);
            ((MyHolder) holder).tvZuoZhe.setText(source);

            if ("刚刚".equals(time)) {
                return;
            } else {
                time = time.substring(0, 10);
            }
            ((MyHolder) holder).tvTime.setText(time);

            String url = newsBean.getImages().get(0);//获取图片路径
            if (!TextUtils.isEmpty(url)) {
                if (!url.startsWith("http")) {
                    url = "http:" + url;
                }
                imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder().url(url)
                        .isCenterCrop(true)
                        .isClearMemory(true)
                        .isClearDiskCache(true)
                        .isSkipMemoryCache(true)
                        .cacheStrategy(1)
                        .errorPic(getRandomPic(random.nextInt(6)))
                        .placeholder(getRandomPic(random.nextInt(6)))
                        .fallback(getRandomPic(random.nextInt(6)))
                        .override(width, height)
                        .imageView(((MyHolder) holder).iv)
                        .build());
            } else {
                ((MyHolder) holder).iv.setImageResource(R.drawable.icon_dzkd_news_1);
            }

            String finalUrl = url;
            String finalURL = URL;
            String finalId = id;
            String finalCanShare = canShare;
            String finalTitle = title;
            String finalType = type;
            holder.itemView.setOnClickListener(v -> {
                if (finalType.equals("at")) {
                    Intent intent = new Intent(softReference.get().getActivity(), WebViewActivity.class);
                    intent.putExtra("URL", finalURL);
                    ArmsUtils.startActivity(intent);
                } else {
                    Intent intent = new Intent(softReference.get().getActivity(), NewsDetailActivity.class);
                    intent.putExtra("web_url", finalURL);
                    intent.putExtra("id", finalId);
                    intent.putExtra("tab", mType);
                    intent.putExtra("is_share", finalCanShare);
                    intent.putExtra("title", finalTitle);
                    intent.putExtra("IMAGES", finalUrl);
                    intent.putExtra("textSize", mTextSize);
                    ArmsUtils.startActivity(intent);
                }
            });
        } else if (holder instanceof MyNew2Holder) {
            if (!TextUtils.isEmpty(mTextSize)) {
                switch (mTextSize) {
                    case "small":
                        ((MyNew2Holder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        break;
                    case "medium":
                        ((MyNew2Holder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        break;
                    case "big":
                        ((MyNew2Holder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        break;
                }
            } else {
                ((MyNew2Holder) holder).tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }

            int screenWidth = ArmsUtils.getScreenWidth(softReference.get().getContext().getApplicationContext());
            int width = (screenWidth - ArmsUtils.dip2px(softReference.get().getContext().getApplicationContext(), 22)) / 3;
            int height = width * 3 / 4;
            ((MyNew2Holder) holder).iv1.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            ((MyNew2Holder) holder).iv2.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            ((MyNew2Holder) holder).iv3.setLayoutParams(new LinearLayout.LayoutParams(width, height));

            ((MyNew2Holder) holder).tvTitle.setText(title);
            ((MyNew2Holder) holder).tvZuoZhe.setText(source);

            if (time.equals("刚刚")) {
                return;
            } else {
                time = time.substring(0, 10);
            }
            ((MyNew2Holder) holder).tvTime.setText(time);

            String urla = datas.get(position).getImages().get(0);//获取图片路径
            if (!TextUtils.isEmpty(urla)) {
                if (!urla.startsWith("http")) {
                    urla = "http:" + urla;
                }

                imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder().url(urla)
                        .isCenterCrop(true)
                        .isClearMemory(true)
                        .isClearDiskCache(true)
                        .isSkipMemoryCache(true)
                        .cacheStrategy(1)
                        .errorPic(getRandomPic(random.nextInt(6)))
                        .placeholder(getRandomPic(random.nextInt(6)))
                        .fallback(getRandomPic(random.nextInt(6)))
                        .override(width, height)
                        .imageView(((MyNew2Holder) holder).iv1).build());
            } else {
                ((MyNew2Holder) holder).iv1.setImageResource(R.drawable.icon_dzkd_news_2);
            }

            String urlb = datas.get(position).getImages().get(1);//获取图片路径
            if (!TextUtils.isEmpty(urlb)) {
                if (!urlb.startsWith("http")) {
                    urlb = "http:" + urlb;
                }

                imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder().url(urlb)
                        .isCenterCrop(true)
                        .isClearMemory(true)
                        .isClearDiskCache(true)
                        .isSkipMemoryCache(true)
                        .cacheStrategy(1)
                        .errorPic(getRandomPic(random.nextInt(6)))
                        .placeholder(getRandomPic(random.nextInt(6)))
                        .fallback(getRandomPic(random.nextInt(6)))
                        .override(width, height)
                        .imageView(((MyNew2Holder) holder).iv2).build());
            } else {
                ((MyNew2Holder) holder).iv2.setImageResource(R.drawable.icon_dzkd_news_3);
            }

            String urlc = datas.get(position).getImages().get(2);//获取图片路径
            if (!TextUtils.isEmpty(urlc)) {
                if (!urlc.startsWith("http")) {
                    urlc = "http:" + urlc;
                }

                imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder().url(urlc)
                        .isCenterCrop(true)
                        .isClearMemory(true)
                        .isClearDiskCache(true)
                        .isSkipMemoryCache(true)
                        .cacheStrategy(1)
                        .errorPic(getRandomPic(random.nextInt(6)))
                        .placeholder(getRandomPic(random.nextInt(6)))
                        .fallback(getRandomPic(random.nextInt(6)))
                        .override(width, height)
                        .imageView(((MyNew2Holder) holder).iv3).build());
            } else {
                ((MyNew2Holder) holder).iv3.setImageResource(R.drawable.icon_dzkd_news_4);
            }

            String finalUrla = urla;
            String finalCanShare1 = canShare;
            String finalURL1 = URL;
            String finalId1 = id;
            String finalTitle1 = title;
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(softReference.get().getActivity(), NewsDetailActivity.class);
                intent.putExtra("web_url", finalURL1);
                intent.putExtra("id", finalId1);
                intent.putExtra("tab", mType);
                intent.putExtra("is_share", finalCanShare1);
                intent.putExtra("title", finalTitle1);
                intent.putExtra("IMAGES", finalUrla);
                intent.putExtra("textSize", mTextSize);
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
        } else if (holder instanceof AdBaiduHolder) {
            if (!TextUtils.isEmpty(mTextSize)) {//字体设置
                if (mTextSize.equals("small")) {
                    ((AdBaiduHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                } else if (mTextSize.equals("medium")) {
                    ((AdBaiduHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else if (mTextSize.equals("big")) {
                    ((AdBaiduHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                }
            } else {
                ((AdBaiduHolder) holder).adsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }

            int screenWidth = ArmsUtils.getScreenWidth(softReference.get().getContext().getApplicationContext());
            int width = (screenWidth - ArmsUtils.dip2px(softReference.get().getContext().getApplicationContext(), 22)) / 3;
            int height = width * 3 / 4;
            ((AdBaiduHolder) holder).adsIamge.setLayoutParams(new LinearLayout.LayoutParams(width, height));

            String url = newsBean.getImages().get(0);//获取图片路径
            if (!TextUtils.isEmpty(url)) {
                if (!url.startsWith("http")) {
                    url = "http:" + url;
                }
            }

            if (newsBean.getAdType() == 2 && newsBean.getNativeResponse() != null) {
                NativeResponse nativeResponse = newsBean.getNativeResponse();
                if (!TextUtils.isEmpty(nativeResponse.getImageUrl())) {
                    url = nativeResponse.getImageUrl();
                }

                if (!TextUtils.isEmpty(url)) {

                    imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder()
                            .url(url)
                            .isCenterCrop(true)
                            .isClearMemory(true)
                            .isClearDiskCache(true)
                            .isSkipMemoryCache(true)
                            .cacheStrategy(1)
                            .errorPic(getRandomPic(random.nextInt(6)))
                            .placeholder(getRandomPic(random.nextInt(6)))
                            .fallback(getRandomPic(random.nextInt(6)))
                            .override(width, height)
                            .imageView(((AdBaiduHolder) holder).adsIamge)
                            .build());
                } else {
                    ((AdBaiduHolder) holder).adsIamge.setImageResource(R.drawable.icon_dzkd_news_5);
                }
                ((AdBaiduHolder) holder).adsTitle.setText(nativeResponse.getTitle());
                ((AdBaiduHolder) holder).adsWriter.setText(nativeResponse.getBrandName());

                nativeResponse.recordImpression(holder.itemView);
                holder.itemView.setOnClickListener(view -> {
                    nativeResponse.handleClick(view);
                });
            } else {
                if (!TextUtils.isEmpty(url)) {

                    imageLoader.loadImage(softReference.get().getContext(), CustomImageConfig.builder().url(url)
                            .isCenterCrop(true)
                            .isClearMemory(true)
                            .isClearDiskCache(true)
                            .isSkipMemoryCache(true)
                            .cacheStrategy(1)
                            .errorPic(getRandomPic(random.nextInt(6)))
                            .placeholder(getRandomPic(random.nextInt(6)))
                            .fallback(getRandomPic(random.nextInt(6)))
                            .override(width, height)
                            .imageView(((AdBaiduHolder) holder).adsIamge)
                            .build());
                } else {
                    ((AdBaiduHolder) holder).adsIamge.setImageResource(R.drawable.icon_dzkd_news_6);
                }
                ((AdBaiduHolder) holder).adsTitle.setText(title);
                ((AdBaiduHolder) holder).adsWriter.setText(source);
                String finalURL2 = URL;
                String finalTitle2 = title;
                ((AdBaiduHolder) holder).itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(softReference.get().getActivity(), AdWebActivity.class);
                    intent.putExtra("AdUrl", finalURL2);
                    intent.putExtra("AdTitle", finalTitle2);
                    ArmsUtils.startActivity(intent);
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

    // 一图版，正常item的ViewHolder，用以缓存findView操作
    private class MyHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView tvTitle;
        public TextView tvZuoZhe;
        public TextView tvTime;
        public TextView tvTop;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_news_one);
            tvTitle = itemView.findViewById(R.id.tv_news_one_title);
            tvZuoZhe = itemView.findViewById(R.id.tv_news_one_writer);
            tvTime = itemView.findViewById(R.id.tv_news_one_time);
            tvTop = itemView.findViewById(R.id.tv_news_one_settop);
        }
    }

    // 三图版，正常item的ViewHolder，用以缓存findView操作
    private class MyNew2Holder extends RecyclerView.ViewHolder {
        public ImageView iv1;
        public ImageView iv2;
        public ImageView iv3;
        public TextView tvTitle;
        public TextView tvZuoZhe;
        public TextView tvTime;

        public MyNew2Holder(@NonNull View itemView) {
            super(itemView);
            iv1 = itemView.findViewById(R.id.iv_news_three1);
            iv2 = itemView.findViewById(R.id.iv_news_three2);
            iv3 = itemView.findViewById(R.id.iv_news_three3);
            tvTitle = itemView.findViewById(R.id.tv_news_three_title);
            tvZuoZhe = itemView.findViewById(R.id.tv_news_three_writer);
            tvTime = itemView.findViewById(R.id.tv_news_three_time);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvError;
//        public LinearLayout llError;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvError = itemView.findViewById(R.id.tv_error);
//            llError = itemView.findViewById(R.id.ll_error_view);
//            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                    (int) (DeviceUtils.getScreenHeight(itemView.getContext()) * 0.6)));
        }
    }

    private class AdBaiduHolder extends RecyclerView.ViewHolder {
        public LinearLayout adsLayout;
        public ImageView adsIamge;
        public TextView adsTitle;
        public TextView adsWriter;

        public AdBaiduHolder(@NonNull View itemView) {
            super(itemView);
            adsLayout = itemView.findViewById(R.id.layout_news_ads);
            adsIamge = itemView.findViewById(R.id.iv_news_ads);
            adsTitle = itemView.findViewById(R.id.tv_news_ads_title);
            adsWriter = itemView.findViewById(R.id.tv_news_ads_writer);
        }
    }
}
