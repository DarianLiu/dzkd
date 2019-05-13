package com.dzkandian.mvp.common.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
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
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.contract.RegisterContract;
import com.dzkandian.mvp.common.di.component.DaggerRegisterComponent;
import com.dzkandian.mvp.common.di.module.RegisterModule;
import com.dzkandian.mvp.common.presenter.RegisterPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 用户注册页面
 */
public class RegisterActivity extends BaseActivity<RegisterPresenter> implements RegisterContract.View {
    @Nullable
    @BindView(R.id.et_register_phone)      //注册的手机号
            EditText etRegisterPhone;
    @Nullable
    @BindView(R.id.btn_register_getcode)    //注册的获取验证码按钮
            Button bRegisterGetcode;
    @Nullable
    @BindView(R.id.et_register_code)      //注册的验证码
            EditText etRegisterCode;
    @Nullable
    @BindView(R.id.et_register_password) //注册的设置密码
            EditText etRegisterPassword;
    @Nullable
    @BindView(R.id.tv_register_agreement)//注册的用户协议
            TextView tvRegisterAgreement;
    @Nullable
    @BindView(R.id.btn_register)            //注册的注册按钮
            Button bRegister;
    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private LoadingProgressDialog loadingProgressDialog;
    private long getCodeTimes;//点击获取验证码按钮的上一次时间；
    private long clickRegisterTimes;//点击注册按钮的上一次时间；
    private long agreementLastTimes;//点击“用户协议”按钮的上一次时间；

    private long lastPauseTime;//上一次暂停的时间戳；
    private int timeCountDown;//验证码倒计时的时间；
    private boolean isCountDown;//是否有验证码倒计时；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerRegisterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .registerModule(new RegisterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_register; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @NonNull
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 检验注册 手机号、验证码、密码
            if (!isCountDown) {
                if (etRegisterPhone.length() == 11) {
                    bRegisterGetcode.setEnabled(true);
                    if (!TextUtils.isEmpty(bRegisterGetcode.getText()) && bRegisterGetcode.getText().equals("重新发送")) {
                        bRegisterGetcode.setTextColor(getResources().getColor(R.color.color_C70000));
                    }
                } else {
                    bRegisterGetcode.setEnabled(false);
                    if (!TextUtils.isEmpty(bRegisterGetcode.getText()) && bRegisterGetcode.getText().equals("重新发送")) {
                        bRegisterGetcode.setTextColor(getResources().getColor(R.color.color_c999999));
                    }
                }
            }

            if (etRegisterPhone.length() == 11 && etRegisterCode.length() > 3 && etRegisterPassword.length() > 5) {
                bRegister.setEnabled(true);
            } else {
                bRegister.setEnabled(false);
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
        tvToolbarTitle.setText(R.string.register);
        toolbar.setNavigationOnClickListener(v -> killMyself());


        tvRegisterAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        /*注册验证码*/
        etRegisterCode.addTextChangedListener(textWatcher);

        /*注册的密码*/
        etRegisterPassword.addTextChangedListener(textWatcher);

        /*注册手机号*/
        etRegisterPhone.addTextChangedListener(textWatcher);

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://倒计时
//                    Timber.d("==time  mHandler  倒计时 " + timeCountDown);
                    if (bRegisterGetcode != null) {
                        bRegisterGetcode.setEnabled(false);
                        bRegisterGetcode.setText(MessageFormat.format("{0}秒后重发", String.valueOf(timeCountDown)));
                        bRegisterGetcode.setTextColor(getResources().getColor(R.color.color_c999999));
                    }
                    break;
                case 2://倒计时结束
//                    Timber.d("==time  mHandler  倒计时结束 " + timeCountDown);
                    isCountDown = false;//倒计时结束  置为false
                    timeCountDown = 0;
                    removeCountDown();//倒计时结束  移除验证码计时
                    if (bRegisterGetcode != null) {
                        bRegisterGetcode.setEnabled(true);
                        bRegisterGetcode.setText("重新发送");
                        bRegisterGetcode.setTextColor(getResources().getColor(R.color.color_C70000));
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
        etRegisterPhone.removeTextChangedListener(textWatcher);
        etRegisterPassword.removeTextChangedListener(textWatcher);
        etRegisterCode.removeTextChangedListener(textWatcher);
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

    /**
     * 校验手机号码
     *
     * @param phone 手机号
     */
    private boolean checkPhone(@NonNull String phone) {
        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
            return true;
        } else {
            showMessage("手机号格式错误");
            return false;
        }
    }

    /**
     * 校验验证码和密码
     *
     * @param smsCode  手机验证码
     * @param password 密码
     */
    private boolean checkSmsCodeAndPwd(String smsCode, @NonNull String password) {
        if (TextUtils.isEmpty(smsCode)) {
            showMessage("请输入验证码");
            return false;
        } else if (TextUtils.isEmpty(password) || password.length() < 6 || password.length() > 16) {
            showMessage("请设置6-16位的密码");
            return false;
        } else {
            return true;
        }
    }

    @OnClick({R.id.btn_register_getcode, R.id.btn_register, R.id.tv_register_agreement})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_register_getcode:
                //获取验证码
                String phone = etRegisterPhone.getText().toString();
                if (checkPhone(phone)) {
                    if (System.currentTimeMillis() - getCodeTimes > 2000) {
                        getCodeTimes = System.currentTimeMillis();
                        if (isInternet()) {
                            assert mPresenter != null;
                            mPresenter.senSmsCode(phone);
                        } else {
                            showMessage("网络请求失败，请连网后重试");
                        }
                    }
                }
                break;
            case R.id.btn_register:
                //注册
                String mobile = etRegisterPhone.getText().toString();
                String smsCode = etRegisterCode.getText().toString();
                String password = etRegisterPassword.getText().toString();

                if (checkPhone(mobile) && checkSmsCodeAndPwd(smsCode, password)) {
                    if (System.currentTimeMillis() - clickRegisterTimes > 2000) {
                        clickRegisterTimes = System.currentTimeMillis();
                        if (isInternet()) {
                            assert mPresenter != null;
                            mPresenter.register(mobile, smsCode, ArmsUtils.encodeToMD5(password));
                        } else {
                            showMessage("网络请求失败，请连网后重试");
                        }
                    }
                }

                break;
            case R.id.tv_register_agreement://用户协议
                if (System.currentTimeMillis() - agreementLastTimes > 2000) {
                    agreementLastTimes = System.currentTimeMillis();
                    if (isInternet()) {
                        Intent intent = new Intent(this, AgreementWebActivity.class);
                        intent.putExtra("URL", getResources().getString(R.string.system_set_protocol_url));
                        intent.putExtra("title", getResources().getString(R.string.system_set_protocol));
                        launchActivity(intent);
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
        }
    }

    @Override
    public void regSuccess() {
        Intent intent = new Intent();
        intent.putExtra("phone", etRegisterPhone.getText().toString());
        intent.putExtra("password", etRegisterPassword.getText().toString());
        setResult(100, intent);
        killMyself();
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }
}

