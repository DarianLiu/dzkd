package com.dzkandian.mvp.mine.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.UpdateInfoContract;
import com.dzkandian.mvp.mine.di.component.DaggerUpdateInfoComponent;
import com.dzkandian.mvp.mine.di.module.UpdateInfoModule;
import com.dzkandian.mvp.mine.presenter.UpdateInfoPresenter;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.simple.eventbus.Subscriber;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 管理微信钱包
 */
public class UpdateWeChatPayActivity extends BaseActivity<UpdateInfoPresenter> implements UpdateInfoContract.View {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_manageweixi_name)
    EditText etManageweixiName;
    @BindView(R.id.et_manageweixi_phone)
    EditText etManageweixiPhone;
    @BindView(R.id.tv_weixin_name)
    TextView tvWeixinName;
    @BindView(R.id.iv_weixin_tou_xiang)
    ImageView ivWeixinTouXiang;
    @BindView(R.id.ll_manageweixi_power)
    LinearLayout llManageweixiPower;
    @BindView(R.id.b_manageweixi)
    Button bManageweixi;

    @Inject
    ImageLoader imageLoader;

    public IWXAPI api;
    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long clickWeChatPayTimes;//点击微信授权的上一次时间；
    private long clickUpdateWeChatPayTimes;//点击修改微信的上一次时间；
    private String mWeixinPayAppId;//绑定微信钱包授权使用的appid;
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
        return R.layout.activity_update_wechatpay; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @NonNull
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 检验微信手机号和用户名
            if (etManageweixiPhone != null && etManageweixiPhone.length() == 11
                    && etManageweixiName != null && etManageweixiName.length() > 1) {
                if (bManageweixi != null) {
                    bManageweixi.setEnabled(true);
                }
            } else {
                if (bManageweixi != null) {
                    bManageweixi.setEnabled(false);
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @NonNull
    TextWatcher editTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if ((etManageweixiPhone != null ? etManageweixiPhone.length() : 0) == 11
                    && (etManageweixiName != null ? etManageweixiName.length() : 0) > 1) {
                if (bManageweixi != null) {
                    bManageweixi.setEnabled(true);
                }
            } else {
                if (bManageweixi != null) {
                    bManageweixi.setEnabled(false);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = editable.toString();
            String inputString = clearLimitStr(DEFAULT_REGEX, str);
            if (etManageweixiName != null) {
                etManageweixiName.removeTextChangedListener(this);
            }
            // et.setText方法可能会引起键盘变化,所以用editable.replace来显示内容
            editable.replace(0, editable.length(), inputString.trim());
            etManageweixiName.addTextChangedListener(this);
        }
    };

    /*清除不符合条件的内容*/
    private String clearLimitStr(String regex, String str) {
        return str.replaceAll(regex, "");
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.title_update_weChatPay);
        toolbar.setNavigationOnClickListener(v -> killMyself());
        queryDeviceInfo();//获取数据库信息
        etManageweixiPhone.addTextChangedListener(textWatcher);
        etManageweixiName.addTextChangedListener(editTextWatcher);
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

    /**
     * 接收微信授权事件,并上传后台
     */
    @Subscriber(tag = EventBusTags.WeChat_Warrant)
    private void ReceiveWeChatWarrantState(String code) {
        assert mPresenter != null;
        mPresenter.weChatPayBind(code, mWeixinPayAppId);
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

    @OnClick({R.id.ll_manageweixi_power, R.id.b_manageweixi})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.ll_manageweixi_power://微信钱包——微信授权
                if (System.currentTimeMillis() - clickWeChatPayTimes > 2000) {
                    clickWeChatPayTimes = System.currentTimeMillis();
                    if (isInternet()) {
                        weixinlogin();
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.b_manageweixi://微信钱包——确定
                String name = etManageweixiName.getText().toString();
                String phone = etManageweixiPhone.getText().toString();
                String weiXin = tvWeixinName.getText().toString();

                etManageweixiName.setSelection(etManageweixiName.length());
                //弹出键盘
                InputMethodManager inputManager = (InputMethodManager) etManageweixiName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.showSoftInput(etManageweixiName, 0);
                }

                if (bManageweixi.getText().equals("修改")) {
                    etManageweixiName.setEnabled(true);
                    etManageweixiPhone.setEnabled(true);
                    llManageweixiPower.setEnabled(true);
                    bManageweixi.setText("确定");
                } else if (TextUtils.isEmpty(name)) {
                    showMessage("微信姓名不能为空");
                } else if (TextUtils.isEmpty(phone)) {
                    showMessage("微信手机号不能为空");
                } else if (TextUtils.isEmpty(weiXin)) {
                    showMessage("没有授权微信");
                } else {
                    if (mPresenter != null) {
                        if (System.currentTimeMillis() - clickUpdateWeChatPayTimes > 2000) {
                            clickUpdateWeChatPayTimes = System.currentTimeMillis();
                            if (isInternet()) {
                                mPresenter.updateInfo("weixinPayName", name, "weixinPayPhone", phone);
                            } else {
                                showMessage("网络请求失败，请连网后重试");
                            }
                        }
                    }
                }
                break;
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    //使用微信授权
    private void weixinlogin() {
        // 判断是否安装了微信客户端
        if (api != null && !api.isWXAppInstalled()) {
            showMessage(getResources().getString(R.string.toast_authorize_fail));
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";//获取个人用户信息的权限
        req.state = Constant.WX_WARRANT + Math.random();//防止攻击
        if (api != null)
            api.sendReq(req);//向微信发送请求
    }

    @Override
    protected void onDestroy() {
        etManageweixiName.removeTextChangedListener(editTextWatcher);
        etManageweixiPhone.removeTextChangedListener(textWatcher);
        if (api != null) {
            api.detach();
        }
        super.onDestroy();
        imageLoader = null;
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
    }

    /**
     * 更新微信钱包绑定信息
     *
     * @param name         昵称
     * @param headImageUrl 头像
     */
    @Override
    public void updateWeChatPayBindInfo(@Nullable String name, String headImageUrl) {
        tvWeixinName.setText(name == null ? "" : name);
        if (!TextUtils.isEmpty(headImageUrl)) {
            imageLoader.loadImage(this, CustomImageConfig.builder()
                    .url(headImageUrl)
                    .isCenterCrop(true)
                    .isCircle(true)
                    .cacheStrategy(1)
                    .errorPic(R.drawable.icon_dzkd_place)
                    .placeholder(R.drawable.icon_dzkd_place)
                    .imageView(ivWeixinTouXiang)
                    .build());
        } else {
            ivWeixinTouXiang.setBackgroundResource(R.drawable.icon_mine_head);
        }
    }

    /**
     * 获取数据库的设备信息
     */
    private void queryDeviceInfo() {
        List<DeviceInfoBean> list = MyApplication.get().getDaoSession().getDeviceInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            dbUpdateDeviceInfo(list.get(0));
            queryUserInfo();
            Timber.d("=db=    UpdateWeChatPayActivity - DeviceInfo - query 成功");
        } else {
            Timber.d("=db=    UpdateWeChatPayActivity - DeviceInfo - query 失败");
        }
    }

    /**
     * @param deviceInfoBean 获取到数据库的设备信息后，更新界面
     */
    public void dbUpdateDeviceInfo(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean != null && deviceInfoBean.getWeixinPayAppid() != null) {
            mWeixinPayAppId = deviceInfoBean.getWeixinPayAppid();
            api = WXAPIFactory.createWXAPI(this, mWeixinPayAppId, false);
        }
    }

    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            Timber.d("=db=    UpdateWeChatPayActivity - UserInfo - query 成功");
            dbUpdateWeixin(list.get(0));
        } else {
            Timber.d("=db=    UpdateWeChatPayActivity - UserInfo - query 失败");
        }
    }

    private void dbUpdateWeixin(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (!TextUtils.isEmpty(userInfoBean.getWeixinPayName()) && !TextUtils.isEmpty(userInfoBean.getWeixinPayPhone())) {
                etManageweixiName.setText(TextUtils.isEmpty(userInfoBean.getWeixinPayName()) ? "" : userInfoBean.getWeixinPayName());
                etManageweixiPhone.setText(TextUtils.isEmpty(userInfoBean.getWeixinPayPhone()) ? "" : userInfoBean.getWeixinPayPhone());
                bManageweixi.setText("修改");
                bManageweixi.setEnabled(true);
                etManageweixiName.setEnabled(false);
                etManageweixiPhone.setEnabled(false);
                llManageweixiPower.setEnabled(false);
            }
            if (!TextUtils.isEmpty(userInfoBean.getWeixinPayNickname())) {
                tvWeixinName.setText(TextUtils.isEmpty(userInfoBean.getWeixinPayNickname()) ? "" : userInfoBean.getWeixinPayNickname());
                if (!TextUtils.isEmpty(userInfoBean.getWeixinPayAvatar())) {
                    imageLoader.loadImage(this, CustomImageConfig.builder()
                            .url(userInfoBean.getWeixinPayAvatar())
                            .isCenterCrop(true)
                            .isCircle(true)
                            .cacheStrategy(1)
                            .errorPic(R.drawable.icon_dzkd_place)
                            .placeholder(R.drawable.icon_dzkd_place)
                            .imageView(ivWeixinTouXiang)
                            .build());
                } else {
                    ivWeixinTouXiang.setBackgroundResource(R.drawable.icon_mine_head);
                }
            }
        }
    }
}
