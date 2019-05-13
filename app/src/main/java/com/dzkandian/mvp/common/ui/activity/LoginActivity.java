package com.dzkandian.mvp.common.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.contract.LoginContract;
import com.dzkandian.mvp.common.di.component.DaggerLoginComponent;
import com.dzkandian.mvp.common.di.module.LoginModule;
import com.dzkandian.mvp.common.presenter.LoginPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.Subscriber;

import java.text.MessageFormat;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.layout_login_contain)
    LinearLayout llContain;
    @BindView(R.id.tab)
    TabLayout tabs;
    @BindView(R.id.vaContent)
    ViewAnimator vaContent;
    @BindView(R.id.tv_login_register)
    TextView tvLoginRegister;
    @BindView(R.id.tv_login_forget)
    TextView tvForgetPassword;
    @BindView(R.id.iv_login_wechat)
    ImageView ivLoginWeixin;

    @BindString(R.string.hint_mobile)
    String msgMobile;
    @BindString(R.string.hint_password)
    String msgPassword;
    @BindString(R.string.hint_smsCode)
    String msgSmsCode;

    private EditText etPwdPhone;
    private EditText etPwd;
    private Button btPwd;

    private EditText etCodePhone;
    private EditText etCode;
    private Button btCode;
    private Button btCodeLogin;

    public IWXAPI api;

    private int haveTouchHardware = 0;//是否有触摸硬件（触摸面积不为0）
    private String first_touch_area;

    private LoadingProgressDialog loadingProgressDialog;
    private AlertDialog mDialog;

    private long mWeChatLoginTime;//点击微信登录按钮的上一次时间；
    private long mPasswordLoginTime;//点击密码登录按钮的上一次时间；
    private long mCodeLoginTimes;//点击验证码登录按钮的上一次时间；
    private long mGetCodeTime;//点击获取验证码按钮的上一次时间；

    private long mLastPauseTime;//上一次暂停的时间戳；
    private int timeCountDown;//验证码倒计时的时间；
    private boolean isCountDown;//是否有验证码倒计时；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerLoginComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .loginModule(new LoginModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_login; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);

        initPasswordLoginView();
        initCaptchaLoginView();
        //设置分割线
        initTab();

        haveTouchHardware = DataHelper.getIntergerSF(getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);

        first_touch_area = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_TOUCH_AREA);
        checkTouchHardware();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://倒计时
//                    Timber.d("==time  mHandler  倒计时 " + timeCountDown);
                    if (btCode != null) {
                        btCode.setEnabled(false);
                        btCode.setText(MessageFormat.format("{0}秒后重发", String.valueOf(timeCountDown)));
                        btCode.setTextColor(getResources().getColor(R.color.color_c999999));
                    }
                    break;
                case 2://倒计时结束
//                    Timber.d("==time  mHandler  倒计时结束 " + timeCountDown);
                    isCountDown = false;//倒计时结束  置为false
                    timeCountDown = 0;
                    removeCountDown();//倒计时结束  移除验证码计时
                    if (btCode != null) {
                        btCode.setEnabled(true);
                        btCode.setText("重新发送");
                        btCode.setTextColor(getResources().getColor(R.color.color_C70000));
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


    /**
     * 检测触摸硬件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void checkTouchHardware() {
        if (haveTouchHardware != 1) {
            llContain.setOnTouchListener((v, event) -> {
                float touch_area = event.getSize();
//                Timber.d("========touch area：" + touch_area);
                if (touch_area > 0 && touch_area != 1 && haveTouchHardware != 1) {
                    if (TextUtils.isEmpty(first_touch_area)) {
                        DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_TOUCH_AREA, String.valueOf(touch_area));
                    } else if (!TextUtils.equals(first_touch_area, String.valueOf(touch_area))) {
                        haveTouchHardware = 1;
                        DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE, haveTouchHardware);
                    }
                }
                return false;
            });
        }
    }

    /**
     * 初始化密码登录View
     */
    private void initPasswordLoginView() {
        //添加标签（密码登录）
        tabs.addTab(tabs.newTab().setCustomView(R.layout.mine_login_tab), 0, true);
        TextView tvPwd = tabs.getTabAt(0).getCustomView().findViewById(R.id.tv_name);
        tvPwd.setText("密码登录");
        tvPwd.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        tvPwd.setPadding(0, 0, 20, 0);

        //添加标签页（密码登录页面）
        View pwdLoginView = getLayoutInflater().inflate(R.layout.login_passwrod, null);
        etPwdPhone = pwdLoginView.findViewById(R.id.et_login_pw_phone);
        etPwd = pwdLoginView.findViewById(R.id.et_login_pw_password);
        btPwd = pwdLoginView.findViewById(R.id.btn_login_password);
        vaContent.addView(pwdLoginView, 0);

        //监听输入事件
        etPwdPhone.addTextChangedListener(textWatcher);
        etPwd.addTextChangedListener(textWatcher);

        btPwd.setOnClickListener(view -> {
            if (System.currentTimeMillis() - mPasswordLoginTime > 2000) {
                mPasswordLoginTime = System.currentTimeMillis();
                passwordLogin();
            }
        });

    }

    /**
     * 初始化验证码登录View
     */
    private void initCaptchaLoginView() {
        //添加标签（验证码登录）
        tabs.addTab(tabs.newTab().setCustomView(R.layout.mine_login_tab), 1, false);
        TextView tvCode = tabs.getTabAt(1).getCustomView().findViewById(R.id.tv_name);
        tvCode.setText("验证码登录");
        tvCode.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        tvCode.setPadding(20, 0, 0, 0);

        //添加标签页（验证码登录页面）
        View captchaLoginView = getLayoutInflater().inflate(R.layout.login_code, null);
        etCodePhone = captchaLoginView.findViewById(R.id.et_login_cd_phone);
        etCode = captchaLoginView.findViewById(R.id.et_login_cd_code);
        btCode = captchaLoginView.findViewById(R.id.b_login_cd_getcode);
        btCodeLogin = captchaLoginView.findViewById(R.id.btn_login_code);
        vaContent.addView(captchaLoginView, 1);

        //监听输入事件
        etCodePhone.addTextChangedListener(textWatcher2);
        etCode.addTextChangedListener(textWatcher2);

        btCode.setOnClickListener(view -> {
            if (System.currentTimeMillis() - mGetCodeTime > 2000) {
                mGetCodeTime = System.currentTimeMillis();
                getCode();
            }
        });

        btCodeLogin.setOnClickListener(view -> {
            if (System.currentTimeMillis() - mCodeLoginTimes > 2000) {
                mCodeLoginTimes = System.currentTimeMillis();
                captchaLogin();
            }
        });

    }

    /**
     * 初始化TabLayout，设置分割线和选择监听事件
     */
    private void initTab() {
        LinearLayout linearLayout = (LinearLayout) tabs.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.shape_login_divider)); //设置分割线的样式
        linearLayout.setDividerPadding(ArmsUtils.dip2px(LoginActivity.this, 10)); //设置分割线间隔

        //tab选中监听事件
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        vaContent.setDisplayedChild(0);
                        break;
                    case 1:
                        vaContent.setDisplayedChild(1);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long exitTime = 0;

    /**
     * 退出应用
     */
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showMessage("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(this);
            ArmsUtils.exitApp();
        }
    }

    /**
     * 执行账号密码  登录成功后跳转逻辑
     */
    private void passwordLogin() {
        String phone = etPwdPhone.getText().toString();
        String password = etPwd.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            showMessage(msgMobile);
        } else if (TextUtils.isEmpty(password)) {
            showMessage(msgPassword);
        } else {
            if (mPresenter != null)
                mPresenter.login(phone, ArmsUtils.encodeToMD5(password), haveTouchHardware);
        }
    }

    /**
     * 获取登录验证码
     */
    public void getCode() {
        final String codePhone = etCodePhone.getText().toString();
        if (TextUtils.isEmpty(codePhone)) {
            showMessage(msgMobile);
        } else {
            if (mPresenter != null) {
                mPresenter.senSmsCode(codePhone);
            }
        }
    }

    /**
     * 调用验证登录
     */
    private void captchaLogin() {
        final String phone = etCodePhone.getText().toString();
        final String code = etCode.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            showMessage(msgMobile);
        } else if (TextUtils.isEmpty(code)) {
            showMessage(msgSmsCode);
        } else {
            if (mPresenter != null)
                mPresenter.smsLogin(phone, code, haveTouchHardware);
        }

    }

    /**
     * 微信授权成功后登录
     *
     * @param code 微信code
     */
    @Subscriber(tag = EventBusTags.WeChat_Login)
    public void onReceiveWeChatLogin(String code) {
        if (mPresenter != null) {
            mPresenter.wxLogin(code, haveTouchHardware);
        }
    }

    /**
     * 微信登录
     */
    private void weChatLogin() {
        // 判断是否安装了微信客户端
        if (!api.isWXAppInstalled()) {
            showMessage(getResources().getString(R.string.toast_login_fail));
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";//获取个人用户信息的权限
        req.state = Constant.WX_LOGIN + Math.random();//防止攻击
        api.sendReq(req);//向微信发送请求
    }


    /**
     * 显示设备异常dialog
     */
    @Override
    public void showNormalDialog() {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_prompt))
                    .setMessage(getResources().getString(R.string.dialog_root_reminder))
                    .setNegativeButton(getResources().getString(R.string.dialog_i_know),
                            (dialog, which) -> {
                                dialog.dismiss();
                                ArmsUtils.exitApp();
                            }).setCancelable(false)
                    .create();
        }
        mDialog.show();
    }

    @Override
    protected void onPause() {
        if (isCountDown) {
            mLastPauseTime = System.currentTimeMillis();
//            Timber.d("==time  onPause暂停计时  mLastPauseTime " + mLastPauseTime);
            removeCountDown();//onPause方法  移除验证码计时；
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCountDown) {
            mLastPauseTime = System.currentTimeMillis() - mLastPauseTime;
            int restTime = (int) (timeCountDown - (mLastPauseTime / 1000));
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
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
        }

        etCodePhone.removeTextChangedListener(textWatcher);
        etCode.removeTextChangedListener(textWatcher);
        etPwd.removeTextChangedListener(textWatcher2);
        etPwdPhone.removeTextChangedListener(textWatcher2);
        textWatcher = null;
        textWatcher2 = null;

        if (api != null) {
            api.detach();
        }
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }


        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        super.onDestroy();
    }

    @Override
    public void killMyself() {
        finish();
    }


    @OnClick({R.id.tv_login_register, R.id.tv_login_forget, R.id.iv_login_wechat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_login_register:
                //用户注册界面：
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, 200);
                break;
            case R.id.tv_login_forget:
                //忘记密码——找回密码：
                launchActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.iv_login_wechat:
                if (System.currentTimeMillis() - mWeChatLoginTime > 2000) {
                    mWeChatLoginTime = System.currentTimeMillis();
                    if (isInternet()) {
                        weChatLogin();
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 200) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            etPwdPhone.setText(phone);
            etPwd.setText(password);
            mHandler.postDelayed(this::showLoading, 100);
            mHandler.postDelayed(() -> {
                btPwd.performClick(); //模拟点击
            }, 1000);
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //密码登录
            if (etPwd.length() > 5 && etPwdPhone.length() == 11) {
                btPwd.setEnabled(true);
            } else {
                btPwd.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher textWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //验证码登录
            if (!isCountDown) {
                if (etCodePhone.length() == 11) {
                    btCode.setEnabled(true);
                    if (!TextUtils.isEmpty(btCode.getText()) && btCode.getText().equals("重新发送")) {
                        btCode.setTextColor(getResources().getColor(R.color.color_C70000));
                    }
                } else {
                    btCode.setEnabled(false);
                    if (!TextUtils.isEmpty(btCode.getText()) && btCode.getText().equals("重新发送")) {
                        btCode.setTextColor(getResources().getColor(R.color.color_c999999));
                    }
                }
            }

            if (etCodePhone.length() == 11 && etCode.length() > 3) {
                btCodeLogin.setEnabled(true);
            } else {
                btCodeLogin.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
