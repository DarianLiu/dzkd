package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.ui.activity.ForgetPwdActivity;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.mine.contract.RevisePasswordContract;
import com.dzkandian.mvp.mine.di.component.DaggerRevisePasswordComponent;
import com.dzkandian.mvp.mine.di.module.RevisePasswordModule;
import com.dzkandian.mvp.mine.presenter.RevisePasswordPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 修改密码
 */
public class RevisePasswordActivity extends BaseActivity<RevisePasswordPresenter> implements RevisePasswordContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_modify_wornpassword)
    EditText oldPassword;
    @BindView(R.id.et_modify_newpassword1)
    EditText newPassword;
    @BindView(R.id.b_modifypassword)
    Button bModifypassword;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    private LoadingProgressDialog loadingProgressDialog;
    private long existPwdLastTimes;//点击修改密码按钮的上一次时间；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerRevisePasswordComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .revisePasswordModule(new RevisePasswordModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_revise_password; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.title_revise_Pwd);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        oldPassword.addTextChangedListener(textWatcher);
        newPassword.addTextChangedListener(textWatcher);

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
        newPassword.removeTextChangedListener(textWatcher);
        oldPassword.removeTextChangedListener(textWatcher);
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


    @Override
    public void CompleteRevisePwd() {
        showMessage("密码修改成功");
        launchActivity(new Intent(this, LoginActivity.class));
    }


    @OnClick({R.id.tv_forget_password, R.id.b_modifypassword})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password:
                launchActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.b_modifypassword:
                //有密码时修改密码
                String oldPwd = oldPassword.getText().toString();
                String newPwd = newPassword.getText().toString();
                if (!TextUtils.isEmpty(oldPwd)) {
                    if (!TextUtils.isEmpty(newPwd)) {
                        if (System.currentTimeMillis() - existPwdLastTimes > 2000) {
                            existPwdLastTimes = System.currentTimeMillis();
                            if (isInternet()) {
                                assert mPresenter != null;
                                mPresenter.existPwdRevisePwd(ArmsUtils.encodeToMD5(oldPwd), ArmsUtils.encodeToMD5(newPwd));
                            } else {
                                showMessage("网络请求失败，请连网后重试");
                            }
                        }
                    } else {
                        showMessage("请输入新密码");
                    }
                } else {
                    showMessage("请输入原密码");
                }
                break;
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 检验
            if (newPassword.length() > 5 && oldPassword.length() > 5) {
                bModifypassword.setEnabled(true);
            } else {
                bModifypassword.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }
}
