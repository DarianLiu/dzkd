package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.FeedBackContract;
import com.dzkandian.mvp.mine.di.component.DaggerFeedBackComponent;
import com.dzkandian.mvp.mine.di.module.FeedBackModule;
import com.dzkandian.mvp.mine.presenter.FeedBackPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity<FeedBackPresenter> implements FeedBackContract.View {

    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.et_back_text)
    EditText etBackText;
    @Nullable
    @BindView(R.id.et_back_phone)
    EditText etBackPhone;
    @Nullable
    @BindView(R.id.b_feedback)
    Button bFeedback;
    @Nullable
    @BindView(R.id.iv_feed_back_one)
    ImageView ivFeedBackOne;
    @Nullable
    @BindView(R.id.iv_feed_back_two)
    ImageView ivFeedBackTwo;
    @Nullable
    @BindView(R.id.iv_feed_back_three)
    ImageView ivFeedBackThree;

    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long feedBackLastTimes;//点击意见反馈的上一次时间；
    private String DEFAULT_REGEX = "[&]";//英文&符号

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerFeedBackComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .feedBackModule(new FeedBackModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_feed_back; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.mine_feedback);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        etBackPhone.addTextChangedListener(textWatcher);
        etBackText.addTextChangedListener(edittextWatcher);

        DeviceUtils.getVersionName(getApplicationContext());

    }

    @NonNull
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 检验反馈的手机号和反馈信息
            if (etBackPhone.length() == 11 && etBackText.length() > 1) {
                bFeedback.setEnabled(true);
            } else {
                bFeedback.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher edittextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 检验反馈的手机号和反馈信息
            if (etBackPhone.length() == 11 && etBackText.length() > 1) {
                bFeedback.setEnabled(true);
            } else {
                bFeedback.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = editable.toString();
            String inputString = clearLimitStr(DEFAULT_REGEX, str);
            if (etBackText != null) {
                etBackText.removeTextChangedListener(this);
            }
            editable.replace(0, editable.length(), inputString.trim());
            etBackText.addTextChangedListener(this);
        }
    };

    /*清除不符合条件的内容*/
    private String clearLimitStr(String regex, String str) {
        return str.replaceAll(regex, "");
    }

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        loadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        etBackText.removeTextChangedListener(edittextWatcher);
        etBackPhone.removeTextChangedListener(textWatcher);
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
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

    @OnClick({R.id.b_feedback, R.id.iv_feed_back_one, R.id.iv_feed_back_two, R.id.iv_feed_back_three})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.b_feedback:
                String feedback = etBackText.getText().toString();
                String phone = etBackPhone.getText().toString();
                if (!feedback.equals("")) {
                    if (!phone.equals("")) {
                        if (System.currentTimeMillis() - feedBackLastTimes > 2000) {
                            feedBackLastTimes = System.currentTimeMillis();
                            if (isInternet()){
                                assert mPresenter != null;
                                mPresenter.feedBack(feedback, phone, DeviceUtils.getVersionName(getApplicationContext()));
                            }else {
                                showMessage("网络请求失败，请连网后重试");
                            }
                        }
                    } else {
                        showMessage("没有手机号码");
                    }
                } else {
                    showMessage("请填写意见");
                }
                break;
            case R.id.iv_feed_back_one:

                break;
            case R.id.iv_feed_back_two:

                break;
            case R.id.iv_feed_back_three:

                break;
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }
}
