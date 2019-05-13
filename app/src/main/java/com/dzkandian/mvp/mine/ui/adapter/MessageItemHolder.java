package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.mvp.news.ui.activity.NewsDetailActivity;
import com.dzkandian.mvp.news.ui.activity.ReplyDetailActivity;
import com.dzkandian.mvp.video.ui.activity.ShortDetailActivity;
import com.dzkandian.mvp.video.ui.activity.VideoDetailActivity;
import com.dzkandian.storage.bean.mine.MessageBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/12/04
 */

public class MessageItemHolder extends BaseHolder<MessageBean> {
    @BindView(R.id.iv_replypraise_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_replypraise_name)
    TextView tvName;
    @BindView(R.id.tv_replypraise_time)
    TextView tvTime;
    @BindView(R.id.tv_replypraise_content)
    TextView tvContent;
    @BindView(R.id.rLayout_replypraise_article)
    RelativeLayout rLayoutArticle;
    @BindView(R.id.iv_replypraise_article)
    ImageView ivArticle;
    @BindView(R.id.iv_replypraise_article_video)
    ImageView ivArticleVideo;
    @BindView(R.id.tv_replypraise_article_title)
    TextView tvArticleTitle;
    ImageLoader imageLoader;
    private String time;

    public MessageItemHolder(View itemView) {
        super(itemView);
        imageLoader = ArmsUtils.obtainAppComponentFromContext(itemView.getContext()).imageLoader();
    }

    @Override
    public void setData(MessageBean data, int position) {
        if (!TextUtils.isEmpty(data.getAvatar())) {
            imageLoader.loadImage(itemView.getContext(), CustomImageConfig.builder()
                    .url(data.getAvatar())
                    .isCenterCrop(true)
                    .isCircle(true)
                    .cacheStrategy(1)
                    .errorPic(R.drawable.icon_mine_head)
                    .placeholder(R.drawable.icon_mine_head)
                    .imageView(ivAvatar)
                    .build());
        }

        tvName.setText(data.getUsername());

        time = data.getCreateTime();
        if (time.length() >= 10) {
            time = time.substring(0, 10);
        }
        tvTime.setText(time);

        if (data.getFlag() == 0) {
            AndroidUtil.setTextSizeColor(
                    tvContent,
                    new String[]{"回复了我：", data.getContent()},
                    new int[]{itemView.getContext().getResources().getColor(R.color.color_c999999),
                            itemView.getContext().getResources().getColor(R.color.color_c333333)},
                    new int[]{16, 16});
        } else {
            tvContent.setText(data.getContent());
        }

        String url = data.getImages();
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http")) {
                url = "http:" + url;
            }
            imageLoader.loadImage(itemView.getContext(), CustomImageConfig.builder()
                    .url(url)
                    .isCenterCrop(true)
                    .cacheStrategy(1)
                    .errorPic(R.drawable.icon_dzkd_news_1)
                    .placeholder(R.drawable.icon_dzkd_news_2)
                    .imageView(ivArticle)
                    .build());
        }

        if (TextUtils.equals("news", data.getType())) {
            ivArticleVideo.setVisibility(View.GONE);
        } else if (TextUtils.equals("video", data.getType())) {
            ivArticleVideo.setVisibility(View.VISIBLE);
        } else if (TextUtils.equals("wuli", data.getType())) {
            ivArticleVideo.setVisibility(View.VISIBLE);
        }

        tvArticleTitle.setText(data.getTitle());

        tvContent.setOnClickListener(view -> {
            if (data.getFlag() == 0) {
                intentReply(data);
            } else {
                if (TextUtils.equals("news", data.getType())) {
                    intentNews(data);
                } else if (TextUtils.equals("video", data.getType())) {
                    intentVideo(data);
                } else if (TextUtils.equals("wuli", data.getType())) {
                    intentShort(data);
                }
            }
        });

        rLayoutArticle.setOnClickListener(view -> {
            if (TextUtils.equals("news", data.getType())) {
                intentNews(data);
            } else if (TextUtils.equals("video", data.getType())) {
                intentVideo(data);
            } else if (TextUtils.equals("wuli", data.getType())) {
                intentShort(data);
            }
        });
    }

    private void intentReply(MessageBean data) {
        Intent intent = new Intent(itemView.getContext(), ReplyDetailActivity.class);
        intent.putExtra("userId", data.getObjId());
        intent.putExtra("userName", data.getObjname());
        intent.putExtra("userAvatar", data.getObjImage());
        intent.putExtra("aId", data.getId());
        intent.putExtra("aType", data.getColumnType());
        intent.putExtra("aTitile", data.getTitle());
        intent.putExtra("aUrl", data.getUrl());
        intent.putExtra("commitFrom", data.getType());
        intent.putExtra("parentId", data.getCommentId());
        intent.putExtra("parentName", data.getObjname());
        intent.putExtra("lastReplyId", data.getReplyId());
        intent.putExtra("lastReplyName", data.getUsername());
        ArmsUtils.startActivity(intent);
    }

    private void intentNews(MessageBean data) {
        Intent intent = new Intent(itemView.getContext(), NewsDetailActivity.class);
        intent.putExtra("web_url", data.getUrl());
        intent.putExtra("id", data.getId());
        intent.putExtra("tab", data.getColumnType());
        intent.putExtra("is_share", data.getCanShare());
        intent.putExtra("title", data.getTitle());
        intent.putExtra("IMAGES", data.getImages());
        ArmsUtils.startActivity(intent);
    }

    private void intentVideo(MessageBean data) {
        Intent intent = new Intent(itemView.getContext(), VideoDetailActivity.class);
        intent.putExtra("url", data.getUrl());
        intent.putExtra("image_url", data.getImages());
        intent.putExtra("title", data.getTitle());
        intent.putExtra("tab", data.getColumnType());
        intent.putExtra("id", data.getId());
        intent.putExtra("is_share", data.getCanShare());
        intent.putExtra("web_url", data.getWebUrl());
        intent.putExtra("content", data.getDescrible());
        ArmsUtils.startActivity(intent);
    }

    private void intentShort(MessageBean data) {
        VideoBean videoBean = new VideoBean();
        videoBean.setVideoId(data.getId());
        videoBean.setTitle(data.getTitle());
        videoBean.setType("video");
        videoBean.setThumbUrl(data.getImages());
        videoBean.setUrl(data.getUrl());
        videoBean.setSource(data.getSource());
        videoBean.setUpdateTime(data.getUpdateTime());
        videoBean.setCanShare(data.getCanShare());
        videoBean.setWebUrl(data.getWebUrl());
        videoBean.setDescribe(data.getDescrible());
        Intent intent = new Intent(itemView.getContext(), ShortDetailActivity.class);
        intent.putExtra("type", data.getColumnType());
        intent.putExtra("video", videoBean);
        ArmsUtils.startActivity(intent);
    }
}
