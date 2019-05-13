package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.UpdateInfoContract;
import com.dzkandian.mvp.mine.di.component.DaggerUpdateInfoComponent;
import com.dzkandian.mvp.mine.di.module.UpdateInfoModule;
import com.dzkandian.mvp.mine.presenter.UpdateInfoPresenter;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class UpdateNicknameActivity extends BaseActivity<UpdateInfoPresenter> implements UpdateInfoContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private LoadingProgressDialog loadingProgressDialog;
    private long clickNameTimes;//点击注册按钮的上一次时间；
    private String DEFAULT_REGEX = "[&]";//英文&符号

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerUpdateInfoComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .updateInfoModule(new UpdateInfoModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_update_nickname; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.title_update_nickname);
        toolbar.setNavigationOnClickListener(v -> killMyself());
        etNickname.addTextChangedListener(textWatcher);
        /*有昵称，进来后填充到控件*/
        queryUserInfo();

        etNickname.setSelection(etNickname.length());
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etNickname.length() > 1) {
                btnConfirm.setEnabled(true);
            } else {
                btnConfirm.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = editable.toString();
            String inputString = clearLimitStr(DEFAULT_REGEX, str);
            if (etNickname != null) {
                etNickname.removeTextChangedListener(this);
            }
            editable.replace(0, editable.length(), inputString.trim());
            etNickname.addTextChangedListener(this);
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
        etNickname.removeTextChangedListener(textWatcher);
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

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        if (System.currentTimeMillis() - clickNameTimes > 2000) {
            clickNameTimes = System.currentTimeMillis();
            if (etNickname.length() >= 2 && etNickname.length() < 11) {
                final String name = etNickname.getText().toString();
                if (name.trim().isEmpty()) {
                    showMessage("修改失败，昵称不能全为空格");
                } else {
                    if (isInternet()) {
                        if (mPresenter != null)
                            mPresenter.updateInfo("username", name, "", "");
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
            } else {
                showMessage("请输入昵称(2-10位)");
            }
        }
    }


    /**
     * 更改昵称无需理会该回调
     */
    @Override
    public void updateWeChatPayBindInfo(String name, String headImageUrl) {

    }

    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            dbUpdateNickName(list.get(0));
        }
    }

    private void dbUpdateNickName(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (!TextUtils.isEmpty(userInfoBean.getUsername())) {
                etNickname.setText(TextUtils.isEmpty(userInfoBean.getUsername()) ? "" : userInfoBean.getUsername());
            }
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }
}
