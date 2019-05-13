package com.dzkandian.mvp.mine.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.mvp.mine.contract.AboutContract;
import com.dzkandian.mvp.mine.di.component.DaggerAboutComponent;
import com.dzkandian.mvp.mine.di.module.AboutModule;
import com.dzkandian.mvp.mine.presenter.AboutPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class AboutActivity extends BaseActivity<AboutPresenter> implements AboutContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_about_me_version)
    TextView tvVersion;
    @BindView(R.id.tv_about_me_content_one)
    TextView tvContentOne;
    @BindView(R.id.tv_about_me_content_two)
    TextView tvContentTwo;
    @BindView(R.id.tv_about_me_content_three)
    TextView tvContentThree;

    private long onClickAboutQQTime;//上一次“关于”界面复制QQ的时间

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerAboutComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .aboutModule(new AboutModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_about; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.system_set_about);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        //设置当前版本
        tvVersion.setText(String.format("V%s", DeviceUtils.getVersionName(getApplicationContext())));

        //初始化关于大众看点的介绍内容
        initAboutMeContent();
    }

    /**
     * 初始化关于大众看点的介绍内容
     */
    private void initAboutMeContent() {
        AndroidUtil.setTextSizeColor(
                tvContentOne,
                new String[]{getResources().getString(R.string.system_set_about_text1red), getResources().getString(R.string.system_set_about_text1black)},
                new int[]{getResources().getColor(R.color.color_text_red), getResources().getColor(R.color.color_text_title)},
                new int[]{16, 16});
        AndroidUtil.setTextSizeColor(
                tvContentTwo,
                new String[]{getResources().getString(R.string.system_set_about_text2red), getResources().getString(R.string.system_set_about_text2black)},
                new int[]{getResources().getColor(R.color.color_text_red), getResources().getColor(R.color.color_text_title)},
                new int[]{16, 16});
        AndroidUtil.setTextSizeColor(
                tvContentThree,
                new String[]{getResources().getString(R.string.system_set_about_text3red), getResources().getString(R.string.system_set_about_text3black)},
                new int[]{getResources().getColor(R.color.color_text_red), getResources().getColor(R.color.color_text_title)},
                new int[]{16, 16});
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.makeText(getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @OnClick(R.id.tv_about_me_qq)
    public void onViewClicked() {
        if (System.currentTimeMillis() - onClickAboutQQTime > 2000) {
            onClickAboutQQTime = System.currentTimeMillis();
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            assert cm != null;
            cm.setPrimaryClip(ClipData.newPlainText(null, "2023180373"));
            showMessage("复制成功");
        }
    }
}
