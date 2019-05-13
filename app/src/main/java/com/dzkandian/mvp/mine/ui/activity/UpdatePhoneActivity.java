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
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.AndroidUtil;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.UpdatePhoneContract;
import com.dzkandian.mvp.mine.di.component.DaggerUpdatePhoneComponent;
import com.dzkandian.mvp.mine.di.module.UpdatePhoneModule;
import com.dzkandian.mvp.mine.presenter.UpdatePhonePresenter;
import com.dzkandian.storage.event.UpdatePhoneEvent;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.Subscriber;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 重新绑定手机号、绑定新手机号
 */
public class UpdatePhoneActivity extends BaseActivity<UpdatePhonePresenter> implements UpdatePhoneContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.b_phone_getcode)
    Button bPhoneGetcode;
    @BindView(R.id.et_code)
    EditText etPhoneCode;
    @BindView(R.id.et_password)
    EditText etPhonePassword;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.b_phone)
    Button bPhone;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;

    private int type;
    private int oldCode;

    private LoadingProgressDialog loadingProgressDialog;
    private long getCodeTimes;//点击获取验证码按钮的上一次时间；
    private long clickPhoneTimes;//点击修改手机按钮的上一次时间；
    private long lastPauseTime;//上一次暂停的时间戳；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerUpdatePhoneComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .updatePhoneModule(new UpdatePhoneModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_update_phone; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @NonNull
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 检验手机号、验证码、密码
            if (etPhone.length() == 11) {
                bPhoneGetcode.setEnabled(true);
                if (!TextUtils.isEmpty(bPhoneGetcode.getText()) && bPhoneGetcode.getText().equals("重新发送")) {
                    bPhoneGetcode.setTextColor(getResources().getColor(R.color.color_text_red));
                }
            } else {
                bPhoneGetcode.setEnabled(false);
                if (!TextUtils.isEmpty(bPhoneGetcode.getText()) && bPhoneGetcode.getText().equals("重新发送")) {
                    bPhoneGetcode.setTextColor(getResources().getColor(R.color.color_text_red));
                }
            }
            if (etPhone.length() == 11 && etPhoneCode.length() > 3 && etPhonePassword.length() > 5) {
                bPhone.setEnabled(true);
            } else {
                bPhone.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> killMyself());
        type = getIntent().getIntExtra(Constant.INTENT_KEY_TYPE, 0);
//        String oldPhone = getIntent().getStringExtra(Constant.INTENT_KEY_PHONE);

        etPhoneCode.addTextChangedListener(textWatcher);
        etPhone.addTextChangedListener(textWatcher);
        etPhonePassword.addTextChangedListener(textWatcher);

        /*
         * 绑定手机号/重新绑定手机号
         */
        if (type == 0) {//type==0   没有绑定手机号，进来绑定手机号
            tvToolbarTitle.setText(R.string.title_update_phone);
            etPhone.setHint(R.string.hint_mobile);
            etPhonePassword.setHint(R.string.hint_set_password);
            tvForgetPassword.setVisibility(View.GONE);
        } else
//            if  (type == 1) {// type == 1  已绑定手机号，则有密码情况下进来，用密码进行重新绑定手机号
//            tvToolbarTitle.setText(R.string.title_afresh_update_phone);
//            etPhone.setHint(R.string.hint_afresh_update_phone);
//            etPhoneCode.setHint(R.string.hint_afresh_update_phone_code);
//            etPhonePassword.setHint(R.string.hint_input_Pwd);
//        } else if (type == 2)
        {// type == 2 已绑定手机号，忘记密码情况下用原手机验证码和新手机验证码进行重新绑定
            tvToolbarTitle.setText(R.string.title_afresh_update_phone);
            etPhone.setHint(R.string.hint_new_phone);
            etPhonePassword.setHint(R.string.hint_login_Pwd);
            tvForgetPassword.setVisibility(View.GONE);
        }


    }

    @Subscriber(tag = EventBusTags.TAG_UPDATE_PHONE)
    public void receiveUpdatePhone(@NonNull UpdatePhoneEvent event) {
        type = event.getType();
        oldCode = Integer.parseInt(event.getCode());
        etPhone.setHint(R.string.hint_afresh_update_phone);
        etPhoneCode.setHint(R.string.hint_afresh_update_phone_code);
        tvForgetPassword.setVisibility(View.GONE);
        etPhonePassword.setVisibility(View.GONE);
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
    protected void onPause() {
        lastPauseTime = System.currentTimeMillis();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        long resumeTime = System.currentTimeMillis() - lastPauseTime;
        resumeTime = resumeTime > 0 ? resumeTime : 0;
        if (mPresenter != null && resumeTime != 0) {
            mPresenter.resumeTime((int) resumeTime);
        }
    }

    @Override
    protected void onDestroy() {
        etPhonePassword.removeTextChangedListener(textWatcher);
        etPhone.removeTextChangedListener(textWatcher);
        etPhoneCode.removeTextChangedListener(textWatcher);
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

    /**
     * 检验输入手机号
     *
     * @param newPhone 新的手机号
     */
    public boolean checkPhone(@NonNull String newPhone) {
        if (TextUtils.isEmpty(newPhone) && newPhone.length() == 11) {
            showMessage("请输入正确新手机号");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检验输入数据
     *
     * @param newCode 新的验证码
     */
    public boolean checkCode(String newCode) {
        if (TextUtils.isEmpty(newCode)) {
            showMessage("请输入验证码");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 校验登录密码
     *
     * @param oldPassword 登录密码
     */
    public boolean checkLoginPassword(@NonNull String oldPassword) {
        if (TextUtils.isEmpty(oldPassword) && oldPassword.length() > 6 && oldPassword.length() < 17) {
            showMessage("密码格式错误");
            return false;
        } else {
            return true;
        }
    }

    @OnClick({R.id.b_phone_getcode, R.id.b_phone, R.id.tv_forget_password})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.b_phone_getcode:
                assert mPresenter != null;
                String phone = etPhone.getText().toString();
                if (checkPhone(phone)) {
                    if (System.currentTimeMillis() - getCodeTimes > 2000) {
                        getCodeTimes = System.currentTimeMillis();
                        if (isInternet()) {
                            mPresenter.sendSmsCode(phone);
                        } else {
                            showMessage("网络请求失败，请连网后重试");
                        }
                    }
                }
                break;
            case R.id.b_phone:
                assert mPresenter != null;
                String phone1 = etPhone.getText().toString();
                String newCode = etPhoneCode.getText().toString();
                String password = etPhonePassword.getText().toString();
                if (checkPhone(phone1) && checkCode(newCode)) {
                    if (System.currentTimeMillis() - clickPhoneTimes > 2000) {
                        clickPhoneTimes = System.currentTimeMillis();
                        if (isInternet()) {
                            switch (type) {
                                case 0://绑定手机号
                                    if (checkLoginPassword(password)) {
                                        mPresenter.bindPhone(phone1, newCode, ArmsUtils.encodeToMD5(password));
                                    }
                                    break;
                                case 1://密码方式更改绑定手机号
                                    if (checkLoginPassword(password)) {
                                        mPresenter.passwordUpdatePhone(phone1, newCode, ArmsUtils.encodeToMD5(password));
                                    }
                                    break;
                                case 2://验证码方式更改绑定手机号
                                    mPresenter.smsCodeUpdatePhone(phone1, newCode, String.valueOf(oldCode));
                                    break;
                            }
                        } else {
                            showMessage("网络请求失败，请连网后重试");
                        }
                    }
                }
                break;
            case R.id.tv_forget_password:
                //如果忘记密码，则跳转到原手机号验证
//                Intent intent = new Intent(this, OldPhoneCheckActivity.class);
//                intent.putExtra(Constant.INTENT_KEY_PHONE, oldPhone);
//                launchActivity(intent);
                break;
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }


    @Override
    public void SmsCountDown(long time) {
        if (time == 0) {
            bPhoneGetcode.setEnabled(true);
            bPhoneGetcode.setText("重新发送");
            bPhoneGetcode.setTextColor(getResources().getColor(R.color.color_text_red));
        } else {
            bPhoneGetcode.setEnabled(false);
            bPhoneGetcode.setText(MessageFormat.format("{0}秒后重发", String.valueOf(time)));
            bPhoneGetcode.setTextColor(getResources().getColor(R.color.color_text_tip));
        }
    }

    /**
     * 绑定的手机号已注册，显示该提示
     *
     * @param message 提示
     */
    @Override
    public void showNotice(String message) {
        AndroidUtil.setTextSize(tvNotice, message == null ? "" : "温馨提示：\n", 16, message == null ? "" : message, 14);
        if (message == null) {
            bPhone.setText("绑定手机号");
        } else {
            bPhone.setText("继续绑定");
        }
    }

}
