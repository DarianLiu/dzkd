package com.dzkandian.mvp.news.ui.adapter;

import android.graphics.drawable.Drawable;
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
import com.dzkandian.common.widget.likeview.ShineButton;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;

import butterknife.BindView;

/**
 * 回复详情子项
 * Created by Administrator on 2018/12/6.
 */

public class ReplyDetailHolder extends BaseHolder<ReplyBean> {
    @BindView(R.id.iv_reply_head)
    ImageView ivReplyHead;
    @BindView(R.id.tv_reply_name)
    TextView tvReplyName;
    @BindView(R.id.tv_reply_content)
    TextView tvReplyContent;
    @BindView(R.id.tv_reply_time)
    TextView tvReplyTime;
    @BindView(R.id.tv_reply_thumbs)
    TextView tvReplyThumbs;
    @BindView(R.id.tv_reply_thumbs_add)
    TextView tvReplyThumbsAdd;
    @BindView(R.id.view_reply_line)
    View viewReplyLine;
    @BindView(R.id.layout_reply_first)
    LinearLayout layoutFirst;
    @BindView(R.id.sb_thumbs)
    ShineButton sbThumbs;
    private ImageLoader imageLoader;

    public ReplyDetailHolder(View itemView) {
        super(itemView);
        imageLoader = ArmsUtils.obtainAppComponentFromContext(itemView.getContext()).imageLoader();
    }

    @Override
    public void setData(ReplyBean data, int position) {
        /*是否含有  全部回复  */
        layoutFirst.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        /*是否含有  点赞数  */
        tvReplyThumbs.setVisibility(data.isBogusData() ? View.GONE : View.VISIBLE);
        sbThumbs.setVisibility(data.isBogusData() ? View.GONE : View.VISIBLE);

        /*用户头像*/
        if (!TextUtils.isEmpty(data.getUserImg())) {
            imageLoader.loadImage(itemView.getContext(), CustomImageConfig.builder()
                    .url(data.getUserImg())
                    .isCenterCrop(true)
                    .isCircle(true)
                    .cacheStrategy(1)
                    .errorPic(R.drawable.icon_mine_head)
                    .placeholder(R.drawable.icon_mine_head)
                    .imageView(ivReplyHead)
                    .build());
        }

        /*用户名*/
        tvReplyName.setText(data.getUserName());

        /*内容填充*/
        if (TextUtils.equals("0", data.getParentId()) || TextUtils.equals("0", data.getReplyId())) {
            tvReplyContent.setText(data.getContent());
        } else {
            AndroidUtil.setTextSizeColor(
                    tvReplyContent,
                    new String[]{"回复", "@" + data.getReplyName(), "：", data.getContent()},
                    new int[]{
                            itemView.getContext().getResources().getColor(R.color.color_c999999),
                            itemView.getContext().getResources().getColor(R.color.color_C70000),
                            itemView.getContext().getResources().getColor(R.color.color_c999999),
                            itemView.getContext().getResources().getColor(R.color.color_c333333)},
                    new int[]{16, 16, 16, 16});
        }

        /*时间*/
        String time = data.getCreateTime();
        if (time.length() >= 10) {
            time = time.substring(0, 10);
        }
        tvReplyTime.setText(time);

        updateView(data);


        //点击头像，昵称，内容；
        ivReplyHead.setOnClickListener(v -> {
            if (!data.isBogusData()) {
                EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_REPLY_CLICK_POSITION);
            }
        });

        tvReplyName.setOnClickListener(view -> {
//            Timber.d("==reply  holder tvReplyName点击  " + position);
            if (!data.isBogusData()) {
                EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_REPLY_CLICK_POSITION);
            }
        });

        tvReplyContent.setOnClickListener(view -> {
//            Timber.d("==reply  holder tvReplyContent点击  " + position);
            if (!data.isBogusData()) {
                EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_REPLY_CLICK_POSITION);
            }
        });

        /*点赞事件*/
        sbThumbs.setOnClickListener(view -> {
            EventBus.getDefault().post(getLayoutPosition(), EventBusTags.TAG_REPLY_CLICK_PRAISE);
        });
    }

    /** 更新点赞效果；
     * @param data
     */
    public void updateView(ReplyBean data) {
        tvReplyThumbs.setText(String.valueOf(data.getThumbsUp()));
        //有没点赞的+1动效，一开始进来是没有的，点了点赞icon并且成功才有
//        Timber.d("==reply  holder  " + position + "  data.isAnimated()：" + data.isAnimated());
        if (data.isAnimated()) {
            tvReplyThumbsAdd.setVisibility(View.VISIBLE);
            sbThumbs.showAnim();
            AnimationSet set = new AnimationSet(true);
            set.addAnimation(new AlphaAnimation(1.0f, 0.0f));
            set.addAnimation(new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, -2f));
            set.setDuration(800);
            set.setStartOffset(400);
            set.cancel();
            set.reset();
            tvReplyThumbsAdd.startAnimation(set);
            set.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tvReplyThumbsAdd.setVisibility(View.GONE);
                    tvReplyThumbsAdd.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            tvReplyThumbsAdd.setVisibility(View.GONE);
        }

        //点赞图标是显示灰色还是红色
//        Timber.d("==reply  holder  " + position + "  !data.isStatus():" + !data.isStatus() + "   data.isHasPraise():" + data.isHasPraise());
        if (!data.isStatus() || data.isHasPraise()) {
            sbThumbs.setSrcColor(sbThumbs.getBtnFillColor());
        } else {
            sbThumbs.setSrcColor(sbThumbs.getBtnColor());
        }
    }
}
