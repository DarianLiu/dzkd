package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.video.ui.activity.VideoDetailActivity;
import com.dzkandian.storage.bean.CollectionVideoBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;

import org.simple.eventbus.EventBus;

import java.util.Random;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/11/1.
 */

public class CollectionVIdeoHolder extends BaseHolder<CollectionVideoBean> {
    @BindView(R.id.collection_video_layout)
    LinearLayout collectionLayout;
    @BindView(R.id.collection_video_relativeLayout)
    RelativeLayout collectionReLayout;
    @BindView(R.id.collection_video_iv)
    ImageView collectionImage;
    @BindView(R.id.collection_video_title)
    TextView collectionTitle;
    @BindView(R.id.collection_video_time)
    TextView collectionTime;
    @BindView(R.id.collection_video_close)
    TextView collectionClose;
    private Random random;

    public CollectionVIdeoHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(CollectionVideoBean data, int position) {
        random = new Random();
        String mTextSize = DataHelper.getStringSF(collectionTitle.getContext().getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        String url = data.getThumbUrl();//获取图片路径
        if (!url.startsWith("http")) {
            url = "http:" + url;
        }
        int width = ArmsUtils.getScreenWidth(collectionTitle.getContext().getApplicationContext());
        int height = width * 9 / 16;
        collectionReLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
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

        collectionClose.setOnClickListener(view -> EventBus.getDefault().post(position, EventBusTags.TAG_COLLECTION_VIDEO));

        collectionLayout.setOnClickListener(view -> {
            Intent intent = new Intent(collectionTitle.getContext(), VideoDetailActivity.class);
            intent.putExtra("url", data.getUrl());
            intent.putExtra("image_url", data.getThumbUrl());
            intent.putExtra("title", data.getTitle());
            intent.putExtra("tab", data.getType());
            intent.putExtra("id", data.getId());
            intent.putExtra("is_share", data.getCanShare());
            intent.putExtra("web_url", data.getWebUrl());
            intent.putExtra("content", data.getDescrible());
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
