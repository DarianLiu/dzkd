package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.news.ui.activity.NewsDetailActivity;
import com.dzkandian.storage.bean.CollectionNewsBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;

import org.simple.eventbus.EventBus;

import java.util.Random;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/10/30.
 */

public class CollectionNewsHolder extends BaseHolder<CollectionNewsBean> {
    @BindView(R.id.collection_news_layout)
    LinearLayout collectionLayout;
    @BindView(R.id.collection_news_iv)
    ImageView collectionImage;
    @BindView(R.id.collection_news_title)
    TextView collectionTitle;
    @BindView(R.id.collection_news_time)
    TextView collectionTime;
    @BindView(R.id.collection_news_close)
    TextView collectionClose;
    private Random random;

    public CollectionNewsHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(CollectionNewsBean data, int position) {
        random = new Random();
        String mTextSize = DataHelper.getStringSF(collectionTitle.getContext().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        String url = data.getImages();//获取图片路径
        if (!url.startsWith("http")) {
            url = "http:" + url;
        }
        int screenWidth = ArmsUtils.getScreenWidth(collectionTitle.getContext().getApplicationContext());
        int width = (screenWidth - ArmsUtils.dip2px(collectionTitle.getContext().getApplicationContext(), 22)) / 3;
        int height = width * 3 / 4;
        collectionImage.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        ImageLoader imageLoader = ArmsUtils.obtainAppComponentFromContext(collectionTitle.getContext()).imageLoader();
        imageLoader.loadImage(collectionTitle.getContext(), CustomImageConfig.builder()
                .url(url)
                .isCenterCrop(true)
                .isClearMemory(true)
                .isClearDiskCache(true)
                .isSkipMemoryCache(true)
                .cacheStrategy(1)
                .errorPic(getRandomPic(random.nextInt(6)))
                .placeholder(getRandomPic(random.nextInt(6)))
                .fallback(getRandomPic(random.nextInt(6)))
                .imageView(collectionImage)
                .build());

        collectionTitle.setText(data.getTitle());

        String time = data.getCreateTime();
        if (time.length() >= 10) {
            time = time.substring(0, 10);
        }
        collectionTime.setText(time);

        collectionClose.setOnClickListener(view -> EventBus.getDefault().post(position, EventBusTags.TAG_COLLECTION_NEWS));

        collectionLayout.setOnClickListener(view -> {
            Intent intent = new Intent(collectionTitle.getContext(), NewsDetailActivity.class);
            intent.putExtra("web_url", data.getUrl());
            intent.putExtra("id", data.getId());
            intent.putExtra("tab", data.getType());
            intent.putExtra("is_share", data.getCanShare());
            intent.putExtra("title", data.getTitle());
            intent.putExtra("IMAGES", data.getImages());
            intent.putExtra("textSize", mTextSize);
            ArmsUtils.startActivity(intent);
        });
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
