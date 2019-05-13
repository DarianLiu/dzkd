package com.dzkandian.mvp.common.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.contract.ForgetPwdContract;
import com.dzkandian.mvp.common.di.component.DaggerForgetPwdComponent;
import com.dzkandian.mvp.common.di.module.ForgetPwdModule;
import com.dzkandian.mvp.common.presenter.ForgetPwdPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 忘记密码页面
 */
public class ForgetPwdActivity extends BaseActivity<ForgetPwdPresenter> implements ForgetPwdContract.View {

    @BindView(R.id.et_forget_phone)     //找回密码的手机号
            EditText etForgetPhone;
    @BindView(R.id.btn_forget_getcode)    //找回密码的获取验证码按钮
            Button bForgetGetcode;
    @BindView(R.id.et_forget_code)      //找回密码的输入验证码
            EditText etForgetCode;
    @BindView(R.id.et_forget_password) //找回密码的密码
            EditText etForgetPassword;
    @BindView(R.id.btn_forget)            //找回密码的确认
            Button bForget;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int type;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long getCodeTimes;//点击获取验证码按钮的上一次时间；
    private long clickForgetPwdTimes;//点击找回密码按钮的上一次时间；

    private long lastPauseTime;//上一次暂停的时间戳；
    private int timeCountDown;//验证码倒计时的时间；
    private boolean isCountDown;//是否有验证码倒计时；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerForgetPwdComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .forgetPwdModule(new ForgetPwdModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_forget_pwd; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @NonNull
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            //手机号校验，验证码校验，密码校验
            if (!isCountDown) {
                if (etForgetPhone.length() == 11) {
                    bForgetGetcode.setEnabled(true);
                    if (!TextUtils.isEmpty(bForgetGetcode.getText()) && bForgetGetcode.getText().equals("重新发送")) {
                        bForgetGetcode.setTextColor(getResources().getColor(R.color.color_C70000));
                    }
                } else {
                    bForgetGetcode.setEnabled(false);
                    if (!TextUtils.isEmpty(bForgetGetcode.getText()) && bForgetGetcode.getText().equals("重新发送")) {
                        bForgetGetcode.setTextColor(getResources().getColor(R.color.color_c999999));
                    }
                }
            }
            if (etForgetPhone.length() == 11 && etForgetCode.length() > 3 && etForgetPassword.length() >= 6) {
                bForget.setEnabled(true);
            } else {
                bForget.setEnabled(false);
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
        tvToolbarTitle.setText(R.string.title_get_Pwd);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        type = getIntent().getIntExtra(Constant.INTENT_KEY_PASSWORD, 0);
        if (type == 1) {
            tvToolbarTitle.setText("设置密码");
        }

        /*验证码登录，获取验证码处理*/
        etForgetPhone.addTextChangedListener(textWatcher);

        /*验证码登录处理*/
        etForgetPassword.addTextChangedListener(textWatcher);

        /*密码登录处理*/
        etForgetCode.addTextChangedListener(textWatcher);

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://倒计时
//                    Timber.d("==time  mHandler  倒计时 " + timeCountDown);
                    if (bForgetGetcode != null) {
                        bForgetGetcode.setEnabled(false);
                        bForgetGetcode.setText(MessageFormat.format("{0}秒后重发", String.valueOf(timeCountDown)));
                        bForgetGetcode.setTextColor(getResources().getColor(R.color.color_c999999));
                    }
                    break;
                case 2://倒计时结束
//                    Timber.d("==time  mHandler  倒计时结束 " + timeCountDown);
                    isCountDown = false;//倒计时结束  置为false
                    timeCountDown = 0;
                    removeCountDown();//倒计时结束  移除验证码计时
                    if (bForgetGetcode != null) {
                        bForgetGetcode.setEnabled(true);
                        bForgetGetcode.setText("重新发送");
                        bForgetGetcode.setTextColor(getResources().getColor(R.color.color_C70000));
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 验证码计时Runnable
     */
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mHandler != null) {
                timeCountDown--;
                if (timeCountDown > 0) {
                    mHandler.sendEmptyMessage(1);
                    mHandler.postDelayed(timeRunnable, 1000);
                } else {
                    mHandler.sendEmptyMessage(2);
                }
            }
        }
    };

    /**
     * 开始验证码计时；
     *
     * @param time 倒计时时间（秒）
     */
    @Override
    public void startCountDown(int time) {
        //        Timber.d("==time  startCountDown开始计时 " + time);
        timeCountDown = time;
        if (mHandler != null) {
            isCountDown = true;//倒计时开始  置为false
            mHandler.sendEmptyMessage(1);
            mHandler.postDelayed(timeRunnable, 1000);
        }
    }

    /**
     * 移除验证码计时；
     */
    private void removeCountDown() {
        if (mHandler != null) {
//            Timber.d("==time  removeCountDown移除计时 ");
            mHandler.removeCallbacks(timeRunnable);
            mHandler.removeMessages(1);
            mHandler.removeMessages(2);
        }
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
        if (isCountDown) {
            lastPauseTime = System.currentTimeMillis();
//            Timber.d("==time  onPause暂停计时  mLastPauseTime " + mLastPauseTime);
            removeCountDown();//onPause方法  移除验证码计时；
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCountDown) {
            lastPauseTime = System.currentTimeMillis() - lastPauseTime;
            int restTime = (int) (timeCountDown - (lastPauseTime / 1000));
//            Timber.d("==time  onResume恢复计时  restTime剩余时间 " + restTime);
            if (restTime > 0) {//计时未完成
                startCountDown(restTime);//onResume方法  计时未完成重新计时；
            } else {//计时完成；
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        etForgetCode.removeTextChangedListener(textWatcher);
        etForgetPassword.removeTextChangedListener(textWatcher);
        etForgetPhone.removeTextChangedListener(textWatcher);
        textWatcher = null;
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        super.onDestroy();
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

    @OnClick({R.id.btn_forget, R.id.btn_forget_getcode})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_forget:
                ForgetPwd();//找回密码确认按钮
                break;
            case R.id.btn_forget_getcode:
                ForgetPwdCode();// 找回密码获取验证码
                break;
        }
    }

    /**
     * 找回密码获取验证码
     */
    private void ForgetPwdCode() {
        String codePhone = etForgetPhone.getText().toString();
        if (codePhone.isEmpty()) {
            showMessage("请先输入手机号");
        } else {
            if (System.currentTimeMillis() - getCodeTimes > 2000) {
                getCodeTimes = System.currentTimeMillis();
                if (isInternet()) {
                    if (mPresenter != null)
                        mPresenter.senSmsCode(codePhone);
                } else {
                    showMessage("网络请求失败，请连网后重试");
                }
            }
        }
    }

    /**
     * 找回密码确认按钮
     */

    private void ForgetPwd() {
        String phone = etForgetPhone.getText().toString();
        String code = etForgetCode.getText().toString();
        String password = etForgetPassword.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showMessage("手机号不能为空");
        } else if (TextUtils.isEmpty(code)) {
            showMessage("验证码不能为空");
        } else if (TextUtils.isEmpty(password)) {
            showMessage("密码不能为空");
        } else {
            if (System.currentTimeMillis() - clickForgetPwdTimes > 2000) {
                clickForgetPwdTimes = System.currentTimeMillis();
                if (isInternet()) {
                    assert mPresenter != null;
                    if (type == 1) {
                        mPresenter.revisePassword(phone, code, ArmsUtils.encodeToMD5(password));
                    } else {
                        mPresenter.forgetPassword(phone, code, ArmsUtils.encodeToMD5(password));
                    }
                } else {
                    showMessage("网络请求失败，请连网后重试");
                }
            }
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

}
