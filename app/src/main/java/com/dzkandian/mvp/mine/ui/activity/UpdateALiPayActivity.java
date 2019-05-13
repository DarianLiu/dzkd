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
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.mine.contract.UpdateInfoContract;
import com.dzkandian.mvp.mine.di.component.DaggerUpdateInfoComponent;
import com.dzkandian.mvp.mine.di.module.UpdateInfoModule;
import com.dzkandian.mvp.mine.presenter.UpdateInfoPresenter;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 管理支付宝
 */
public class UpdateALiPayActivity extends BaseActivity<UpdateInfoPresenter> implements UpdateInfoContract.View {

    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.et_managezhifibao_account)//支付宝账户
            EditText etManagezhifibaoAccount;
    @Nullable
    @BindView(R.id.et_managezhifubao_name)//支付宝姓名
            EditText etManagezhifubaoName;
    @Nullable
    @BindView(R.id.b_managezhifubao)//绑定按钮
            Button bManagezhifubao;

    @Nullable
    @BindView(R.id.tv_alipay_name)
    TextView tvAlipayName;
    @Nullable
    @BindView(R.id.iv_alipay_tou_xiang)
    ImageView ivAlipayTouXiang;
    @Nullable
    @BindView(R.id.ll_alipay_power)
    LinearLayout llManageweixiPower;

    @Nullable
    @Inject
    ImageLoader imageLoader;

    private String account;
    private String name;

    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long clickALiPayTimes;//点击支付宝授权的上一次时间；
    private long clickUpdateALiPayTimes;//点击修改支付宝的上一次时间；
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
        return R.layout.activity_update_alipay; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @NonNull
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 检验支付宝的信息
            if (etManagezhifubaoName.length() > 0 && etManagezhifibaoAccount.length() > 0) {
                bManagezhifubao.setEnabled(true);
            } else {
                bManagezhifubao.setEnabled(false);
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
            // 检验支付宝的信息
            if (etManagezhifubaoName.length() > 0 && etManagezhifibaoAccount.length() > 0) {
                bManagezhifubao.setEnabled(true);
            } else {
                bManagezhifubao.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = editable.toString();
            String inputString = clearLimitStr(DEFAULT_REGEX, str);
            if (etManagezhifubaoName != null) {
                etManagezhifubaoName.removeTextChangedListener(this);
            }
            // et.setText方法可能会引起键盘变化,所以用editable.replace来显示内容
            editable.replace(0, editable.length(), inputString.trim());
            etManagezhifubaoName.addTextChangedListener(this);
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
        tvToolbarTitle.setText(R.string.title_update_aLiPay);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        queryUserInfo();//获取数据库用户信息
        etManagezhifibaoAccount.addTextChangedListener(textWatcher);
        etManagezhifubaoName.addTextChangedListener(editTextWatcher);

        /**有绑定支付宝钱包，进来后填充到控件**/
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
        etManagezhifubaoName.removeTextChangedListener(editTextWatcher);
        etManagezhifibaoAccount.removeTextChangedListener(textWatcher);
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

    @OnClick({R.id.ll_alipay_power, R.id.b_managezhifubao})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.b_managezhifubao:
                account = etManagezhifibaoAccount.getText().toString();
                name = etManagezhifubaoName.getText().toString();

                etManagezhifibaoAccount.setSelection(etManagezhifibaoAccount.length());
                //弹出键盘
                InputMethodManager inputManager = (InputMethodManager) etManagezhifibaoAccount.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etManagezhifibaoAccount, 0);

                if (bManagezhifubao.getText().equals("修改")) {
                    etManagezhifibaoAccount.setEnabled(true);
                    etManagezhifubaoName.setEnabled(true);
                    llManageweixiPower.setEnabled(true);
                    bManagezhifubao.setText("确定");
                } else {
                    if (System.currentTimeMillis() - clickUpdateALiPayTimes > 2000) {
                        clickUpdateALiPayTimes = System.currentTimeMillis();
                        if (TextUtils.isEmpty(account)) {
                            showMessage("支付宝账户不能为空");
                        } else if (TextUtils.isEmpty(name)) {
                            showMessage("支付宝姓名不能为空");
                        } else if (TextUtils.isEmpty(tvAlipayName.getText().toString())) {
                            showMessage("没有授权支付宝");
                        } else {
                            if (isInternet()) {
                                assert mPresenter != null;
                                mPresenter.updateInfo("alipayAccount", account, "alipayName", name);
                            } else {
                                showMessage("网络请求失败，请连网后重试");
                            }
                        }
                    }
                }
                break;
            case R.id.ll_alipay_power://支付宝钱包——支付宝授权
                alipayLogin();
                break;
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    public void alipayLogin() {
        if (System.currentTimeMillis() - clickALiPayTimes > 2000) {
            clickALiPayTimes = System.currentTimeMillis();
            if (isInternet()) {
                assert mPresenter != null;
                mPresenter.aLiPayLoginParam();
            } else {
                showMessage("网络请求失败，请连网后重试");
            }
        }
    }

    /**
     * 更新支付宝绑定信息（跟微信钱包绑定通用，忽略方法名；暂未集成故暂时忽略该回调）
     *
     * @param name         昵称
     * @param headImageUrl 头像
     */
    @Override
    public void updateWeChatPayBindInfo(String name, String headImageUrl) {
        tvAlipayName.setText("已授权");
    }

    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            Timber.d("=db=    UpdateALiPayActivity - UserInfo - query 成功");
            dbUpdateAlipay(list.get(0));
        } else {
            Timber.d("=db=    UpdateALiPayActivity - UserInfo - query 失败");
        }
    }

    private void dbUpdateAlipay(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (!TextUtils.isEmpty(userInfoBean.getAlipayName())) {
                etManagezhifibaoAccount.setText(TextUtils.isEmpty(userInfoBean.getAlipayAccount()) ? "" : userInfoBean.getAlipayAccount());
                etManagezhifubaoName.setText(TextUtils.isEmpty(userInfoBean.getAlipayName()) ? "" : userInfoBean.getAlipayName());
                etManagezhifibaoAccount.setEnabled(false);
                etManagezhifubaoName.setEnabled(false);
                bManagezhifubao.setText("修改");
                bManagezhifubao.setEnabled(true);
                llManageweixiPower.setEnabled(false);
            }
        }
    }
}
