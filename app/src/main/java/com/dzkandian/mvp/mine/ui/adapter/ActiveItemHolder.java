package com.dzkandian.mvp.mine.ui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dzkandian.R;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.mvp.common.ui.activity.WebViewActivity;
import com.dzkandian.mvp.mine.ui.activity.InvitationActivity;
import com.dzkandian.storage.bean.ActiveBean;
import com.jess.arms.base.BaseHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;

import butterknife.BindView;
import timber.log.Timber;

/**
 * 公告通知ItemHolder
 * Created by liuli on 2018/5/2.
 */
public class ActiveItemHolder extends BaseHolder<ActiveBean> {

    @BindView(R.id.active_click)
    LinearLayout activeClick;
    @Nullable
    @BindView(R.id.tv_active_type)
    TextView tvActiveType;
    @Nullable
    @BindView(R.id.tv_active_name)
    TextView tvActiveName;
    @Nullable
    @BindView(R.id.iv_active)
    ImageView ivActive;
    @Nullable
    @BindView(R.id.tv_active_describe)
    TextView tvActiveDescribe;
    @Nullable
    @BindView(R.id.tv_active_link)
    TextView tvActiveLink;

    @BindView(R.id.tv_active_time)
    TextView tvActiveTime;

    private String imageUserPath = "";

    public ActiveItemHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setData(@NonNull ActiveBean data, int position) {
        int width = (ArmsUtils.getScreenWidth(ivActive.getContext()) - ArmsUtils.dip2px(ivActive.getContext(), 40));
        int height = width * 9 / 16;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins((int) DeviceUtils.dpToPixel(ivActive.getContext(), 10),
                (int) DeviceUtils.dpToPixel(ivActive.getContext(), 5),
                (int) DeviceUtils.dpToPixel(ivActive.getContext(), 10),
                (int) DeviceUtils.dpToPixel(ivActive.getContext(), 10));
        ivActive.setLayoutParams(layoutParams);

        imageUserPath = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
        tvActiveType.setText(data.getClassification());

        if (TextUtils.equals(data.getClassification(), "活动")) {
            tvActiveType.setBackgroundColor(tvActiveType.getContext().getResources().getColor(R.color.forget));
        } else if (TextUtils.equals(data.getClassification(), "公告")) {
            tvActiveType.setBackgroundColor(tvActiveType.getContext().getResources().getColor(R.color.active_netice));
        }
        tvActiveName.setText(data.getTitle());
        if (!TextUtils.isEmpty(data.getCreateTime())) {
            tvActiveTime.setText(data.getCreateTime().substring(0, data.getCreateTime().indexOf(" ")));
        }
        ImageLoader imageLoader = ArmsUtils.obtainAppComponentFromContext(ivActive.getContext()).imageLoader();
        if (!TextUtils.isEmpty(data.getImgUrl())) {
            Timber.d("==share  getBannerImg  :  " + data.getImgUrl());
            imageLoader.loadImage(ivActive.getContext(), CustomImageConfig.builder()
                    .url(data.getImgUrl())
                    .isCenterCrop(true)
                    .cacheStrategy(1)
                    .errorPic(R.drawable.icon_dzkd_news_4)
                    .placeholder(R.drawable.icon_dzkd_news_4)
                    .imageView(ivActive)
                    .build());
        }

        tvActiveDescribe.setText(data.getDesc());
        tvActiveLink.setText(data.getBtnText());
        activeClick.setOnClickListener(v -> {
            if (data.getTag() != null) {
                if (data.getTag().equals("1")) {
                    if (isInternet()) {
                        Intent intent = new Intent(tvActiveType.getContext(), InvitationActivity.class);
                        intent.putExtra("imageUserPath", imageUserPath);
                        tvActiveType.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(tvActiveType.getContext(), "连接网络可查看", Toast.LENGTH_SHORT).show();
                    }
                } else if (data.getTag().equals("2") && !TextUtils.isEmpty(data.getLink())) {
                    if (isInternet()) {
                        Intent intent = new Intent(tvActiveType.getContext(), WebViewActivity.class);
                        intent.putExtra("URL", data.getLink());
                        tvActiveType.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(tvActiveType.getContext(), "连接网络可查看", Toast.LENGTH_SHORT).show();
                    }
                } else if (data.getTag().equals("3") && !TextUtils.isEmpty(data.getLink())) {
                    if (isInternet()) {
                        Uri uri = Uri.parse(data.getLink());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        tvActiveType.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(tvActiveType.getContext(), "连接网络可查看", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(tvActiveType.getContext().getApplicationContext());
    }
}
