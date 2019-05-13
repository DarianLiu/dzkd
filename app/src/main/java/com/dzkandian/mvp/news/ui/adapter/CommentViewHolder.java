package com.dzkandian.mvp.news.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.likeview.ShineButton;
import com.dzkandian.storage.bean.news.CommentBean;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.dzkandian.storage.event.ThumbsUpEvent;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;

import java.lang.ref.SoftReference;
import java.util.List;

import butterknife.BindView;

public class CommentViewHolder extends BaseHolder<CommentBean> {


    @Nullable
    @BindView(R.id.iv_comment_head)
    ImageView ivCommentHead;
    @Nullable
    @BindView(R.id.tv_comment_name)
    TextView tvCommentName;
    @Nullable
    @BindView(R.id.tv_comment_time)
    TextView tvCommentTime;
    @Nullable
    @BindView(R.id.tv_comment_content)
    TextView tvCommentContent;
    @BindView(R.id.tv_comment_reply_1)
    TextView tvCommentReply1;
    @BindView(R.id.tv_comment_reply_2)
    TextView tvCommentReply2;
    @BindView(R.id.tv_comment_all)
    TextView tvCommentAll;
    @BindView(R.id.ll_comment_reply)
    LinearLayout llCommentReply;
    @BindView(R.id.tv_thumbs_count)
    TextView tvThumbsCount;
    @BindView(R.id.tv_reply_count)
    TextView tvReplyCount;
    @BindView(R.id.tv_thumbs_add)
    TextView tvThumbsAdd;
    @BindView(R.id.iv_comment_triangle)
    ImageView ivCommentTriangle;
    @BindView(R.id.sb_thumbs)
    ShineButton sbThumbs;
    ImageLoader imageLoader;

    private SoftReference<Context> mSoftReference;
    private long mOnClickThumbsTime;
    private AnimationSet set;


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        imageLoader = ArmsUtils.obtainAppComponentFromContext(itemView.getContext()).imageLoader();
        mSoftReference = new SoftReference<>(itemView.getContext());
    }

    @Override
    public void setData(@NonNull CommentBean data, int position) {
        if (!TextUtils.isEmpty(data.getUserImg())) {
            imageLoader.loadImage(mSoftReference.get(), CustomImageConfig.builder()
                    .url(data.getUserImg())
                    .isCenterCrop(true)
                    .isCircle(true)
                    .cacheStrategy(1)
                    .errorPic(R.drawable.icon_mine_head)
                    .placeholder(R.drawable.icon_mine_head)
                    .imageView(ivCommentHead)
                    .build());
        }
        String time = data.getCreateTime();
        if (time.length() >= 10) {
            time = time.substring(0, 10);
            tvCommentTime.setText(time);

        } else {
            tvCommentTime.setText(time);
        }
        tvCommentName.setText(data.getUserName());
        tvCommentContent.setText(data.getContent());

        updateView(data);

        List<ReplyBean> replyBeanList = data.getReplyList();

        //根据回复size判断回复内容区域是否隐藏
        if (null == replyBeanList || replyBeanList.size() <= 0) {
            llCommentReply.setVisibility(View.GONE);
            ivCommentTriangle.setVisibility(View.GONE);
        } else {
            llCommentReply.setVisibility(View.VISIBLE);
            ivCommentTriangle.setVisibility(View.VISIBLE);
            if (replyBeanList.size() >= 2) {
                tvCommentReply1.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(data.getReplyList().get(0).getReplyName())) {
                    setTextColorType(data, tvCommentReply1, 0, true);
                } else {
                    setTextColorType(data, tvCommentReply1, 0, false);
                }
                tvCommentReply2.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(data.getReplyList().get(1).getReplyName())) {
                    setTextColorType(data, tvCommentReply2, 1, true);
                } else {
                    setTextColorType(data, tvCommentReply2, 1, false);
                }
                tvCommentAll.setVisibility(View.VISIBLE);
            } else {
                tvCommentReply1.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(data.getReplyList().get(0).getReplyName())) {
                    setTextColorType(data, tvCommentReply1, 0, true);
                } else {
                    setTextColorType(data, tvCommentReply1, 0, false);
                }
                tvCommentReply2.setVisibility(View.GONE);
                tvCommentAll.setVisibility(View.GONE);
            }
        }

        //根评论，评论图标点击事件
        ivCommentHead.setOnClickListener(v -> EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_COMMENT_REPLY_SOMEONE));

        tvCommentName.setOnClickListener(v -> EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_COMMENT_REPLY_SOMEONE));

        tvReplyCount.setOnClickListener(v -> {
            EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_COMMENT_REPLY_SOMEONE);
        });


        //点赞的回调事件
        sbThumbs.setOnClickListener(v ->{
            if(NetworkUtils.checkNetwork(mSoftReference.get())) {
                if (data.isHasThumbs()) {
                    EventBus.getDefault().post(new ThumbsUpEvent(data.getId(), getLayoutPosition()), EventBusTags.TAG_COMMENT_THUMBS_UP);
                } else {
                    toast("点赞太多啦");
                }
            }else{
                toast("网络连接失败，请重试");
            }
        });

        //根评论，评论图标点击事件
        tvCommentContent.setOnClickListener(v -> {
            EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_COMMENT_REPLY_SOMEONE);
        });

        llCommentReply.setOnClickListener(v -> {
            EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_COMMENT_DETAIL);
        });
    }

    public void updateView(CommentBean data) {
        //有没回复和点赞logo，新发表的评论是没有的
        if (data.isHasReply()) {
            sbThumbs.setVisibility(View.VISIBLE);
            tvThumbsCount.setVisibility(View.VISIBLE);
            tvThumbsCount.setText(String.valueOf(data.getThumbsUp()));
            tvReplyCount.setVisibility(View.VISIBLE);
            tvReplyCount.setText(String.valueOf(data.getSubReplyCount()));
        } else {
            sbThumbs.setVisibility(View.GONE);
            tvThumbsCount.setVisibility(View.GONE);
            tvReplyCount.setVisibility(View.GONE);
        }

        //点赞图标是显示灰色还是红色
        if(data.isStatus()){
            sbThumbs.setSrcColor(sbThumbs.getBtnColor());
        }else{
            sbThumbs.setSrcColor(sbThumbs.getBtnFillColor());
        }

        //有没点赞的+1动效，一开始进来是没有的，点了点赞icon并且成功才有
        if (data.isHasAnimate()) {
            sbThumbs.showAnim();
            tvThumbsAdd.setVisibility(View.VISIBLE);
//            tvThumbsAdd.animate().alpha(0).translationY(-50f).setDuration(400).setStartDelay(400).start();
            set = new AnimationSet(true);
            set.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            set.addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, -2f));
            set.setDuration(800);
            set.setStartOffset(400);
            set.cancel();
            set.reset();
            tvThumbsAdd.startAnimation(set);
            set.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tvThumbsAdd.setVisibility(View.GONE);
                    tvThumbsAdd.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        } else {
            tvThumbsAdd.setVisibility(View.GONE);
        }

    }

    public void cancelPlusAnimate(){
        if(set != null){
            set.cancel();
            tvThumbsAdd.setVisibility(View.GONE);
        }
    }

    //两秒钟才能触发一次土司
    private void toast(String toast){
        if (System.currentTimeMillis() - mOnClickThumbsTime > 2000) {
            mOnClickThumbsTime = System.currentTimeMillis();
            ArmsUtils.makeText(mSoftReference.get(), toast);
        }
    }

    /*
     * position:第几条回复
     * type:回复种类，true为回复楼主，false为回复他人
     */
    private void setTextColorType(CommentBean data, TextView textView, int position, boolean type) {
        if (type) {
            AndroidUtil.setTextSizeColor(
                    textView,
                    new String[]{data.getReplyList().get(position).getUserName(), "：", data.getReplyList().get(position).getContent()},
                    new int[]{
                            mSoftReference.get().getResources().getColor(R.color.color_C70000),
                            mSoftReference.get().getResources().getColor(R.color.color_c999999),
                            mSoftReference.get().getResources().getColor(R.color.color_c333333)},
                    new int[]{16, 16, 16});
        } else {
            AndroidUtil.setTextSizeColor(
                    textView,
                    new String[]{data.getReplyList().get(position).getUserName(),
                            "回复",
                            "@" + data.getReplyList().get(position).getReplyName(),
                            "：",
                            data.getReplyList().get(position).getContent()},
                    new int[]{
                            mSoftReference.get().getResources().getColor(R.color.color_C70000),
                            mSoftReference.get().getResources().getColor(R.color.color_c999999),
                            mSoftReference.get().getResources().getColor(R.color.color_C70000),
                            mSoftReference.get().getResources().getColor(R.color.color_c999999),
                            mSoftReference.get().getResources().getColor(R.color.color_c333333)},
                    new int[]{16, 16, 16, 16, 16});
        }
    }

}