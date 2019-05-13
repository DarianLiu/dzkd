package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.common.ui.activity.MainActivity;
import com.dzkandian.mvp.mine.contract.QuickCashContract;
import com.dzkandian.mvp.mine.di.component.DaggerQuickCashComponent;
import com.dzkandian.mvp.mine.di.module.QuickCashModule;
import com.dzkandian.mvp.mine.presenter.QuickCashPresenter;
import com.dzkandian.mvp.mine.ui.adapter.QuickCashAdapter;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.mine.CoinExchangeBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 快速提现
 */
public class QuickCashActivity extends BaseActivity<QuickCashPresenter> implements QuickCashContract.View {

    @Nullable
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @BindView(R.id.tv_quickcash_typewechat)
    TextView tvCashWeixin;           //微信钱包 顶部标题
    @Nullable
    @BindView(R.id.tv_quickcash_typealipay)
    TextView tvCashZhifubao;        //支付宝   顶部标题
    @Nullable
    @BindView(R.id.tv_cash_weixinPayPhone)
    TextView tvCashWeixinPayPhone; //微信手机号
    @Nullable
    @BindView(R.id.ll_cash_weixin)
    RelativeLayout rlCashWeixin;      //微信帐户布局
    @Nullable
    @BindView(R.id.tv_cash_alipayAccount)
    TextView tvCashAlipayAccount;   //支付宝帐户名
    @Nullable
    @BindView(R.id.ll_cash_zhifubao)
    RelativeLayout rlCashZhifubao;            //支付宝布局
    @Nullable
    @BindView(R.id.cash_recycler_view)
    RecyclerView cashRecyclerView;
    @Nullable
    @BindView(R.id.tv_cash_surplus)     //提现的当前余额
            TextView tvDangqian;
    @Nullable
    @BindView(R.id.tv_quickcash_cozy)             //提现的温馨提醒字体
            TextView tvWen1;
    @Nullable
    @BindView(R.id.tv_quickcash_tip)         //提现的温馨提醒
            TextView tvCashTip;
    @Nullable
    @BindView(R.id.tv_cash_gold)      //提现的合计金币
            TextView tvCashGold;
    @Nullable
    @BindView(R.id.b_cash_tixian)    //提现的立即兑换按钮
            Button bCashTixian;
    @BindView(R.id.rLayout_quickcash)
    RelativeLayout rlQuickCash;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.layout_errorview)
    LinearLayout llErrorView;
    @BindView(R.id.tv_cash_weixinPayName)
    TextView tvCashWeixinPayName;
    @BindView(R.id.tv_cash_alipayName)
    TextView tvCashAlipayName;

    private boolean cashValue = false;       //判断用哪个提现类型的值

    @Nullable
    private QuickCashAdapter quickCashAdapter;
    private int redeemNowCoin; //提现金币: 元
    private int currCoin; //当前金币剩余金币
    private int totalCoin;//提现的合计 总金币
    @NonNull
    private String type = "微信钱包";   //兑换金币的类型   微信|支付宝
    private String mPushUpApp;         //推送打开APP进入新闻详情页

    @Nullable
    private LoadingProgressDialog loadingProgressDialog;
    private long clickSureCashTimes;//点击确定兑换的上一次时间；dialog中的
    private long clickCashTimes;//点击立即兑换的上一次时间；
    private long clickRetryTimes;//点击重新加载的上一次时间；
    private String mWeixinPayAppId;//快速提现返回的appid;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerQuickCashComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .quickCashModule(new QuickCashModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_quick_cash; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.title_cash);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        mPushUpApp = getIntent().getStringExtra("mPushUpApp");
        Timber.d("==================mPushUpApp  ：" + mPushUpApp);
        if (isInternet()) {
            mPresenter.getCoinExchange();
        } else {
            setErrorLayout();
        }
        /*重新加载按钮*/
        btnRetry.setOnClickListener(view -> {
            if (System.currentTimeMillis() - clickRetryTimes > 2000) {
                clickRetryTimes = System.currentTimeMillis();
                if (isInternet() && mPresenter != null) {
                    mPresenter.getCoinExchange();
                } else {
                    showMessage("网络请求失败，请连网后重试");
                }
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
    public void killMyself() {
        if (!TextUtils.isEmpty(mPushUpApp)) {
            Intent intent = new Intent(this, MainActivity.class);
            launchActivity(intent);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            killMyself();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick({R.id.tv_quickcash_typewechat, R.id.tv_quickcash_typealipay, R.id.ll_cash_weixin, R.id.ll_cash_zhifubao, R.id.b_cash_tixian})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.tv_quickcash_typewechat:
                cashWeixin();
                break;
            case R.id.tv_quickcash_typealipay:
                cashZhifubao();
                break;
            case R.id.ll_cash_weixin:
                //管理微信钱包
                launchActivity(new Intent(this, UpdateWeChatPayActivity.class));
                break;
            case R.id.ll_cash_zhifubao:
                //管理支付宝
                launchActivity(new Intent(this, UpdateALiPayActivity.class));
                break;
            case R.id.b_cash_tixian:
                //立即兑换
                if (System.currentTimeMillis() - clickCashTimes > 2000) {
                    clickCashTimes = System.currentTimeMillis();
                    isNoPay();
                }
                break;
        }
    }

    /**
     * 判断是否有微信钱包，支付宝钱包；
     */
    private void isNoPay() {
        if (!cashValue) {//微信
            if (tvCashWeixinPayName.getText().equals("你尚未绑定微信账户")) {
                showMessage("请先绑定微信钱包");
            } else {
                redeemNow();
            }
        } else {//支付宝
            if (tvCashAlipayName.getText().equals("你尚未设置支付宝账户")) {
                showMessage("请先绑定支付宝钱包");
            } else {
                redeemNow();
            }
        }
    }

    /**
     * 点击立即兑换按钮
     */
    private void redeemNow() {
        if (totalCoin > currCoin || totalCoin == 0) {
            showMessage(getResources().getString(R.string.toast_gold_not));
        } else {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(this);
            normalDialog.setMessage("您确定要提现" + redeemNowCoin + "元至" + type + "吗？" + "这将消耗" + totalCoin + "金币");
            normalDialog.setPositiveButton("暂不兑换",
                    (dialog, which) -> {

                    });
            normalDialog.setNegativeButton("确定兑换",
                    (dialog, which) -> {
                        if (System.currentTimeMillis() - clickSureCashTimes > 2000) {
                            clickSureCashTimes = System.currentTimeMillis();
                            if (isInternet()) {
                                assert mPresenter != null;
                                if (!cashValue) {
                                    mPresenter.redeemNow(String.valueOf(redeemNowCoin * 100), "weixinPay");
                                } else {
                                    mPresenter.redeemNow(String.valueOf(redeemNowCoin * 100), "alipay");
                                }
                            } else {
                                showMessage("网络请求失败，请连网后重试");
                            }
                        }
                    });
            // 显示
            normalDialog.show();
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    //微信提现文字点击
    private void cashWeixin() {
        if (cashValue) {
            tvCashWeixin.setTextColor(getResources().getColor(R.color.red_select));
            rlCashWeixin.setVisibility(View.VISIBLE);

            tvCashZhifubao.setTextColor(getResources().getColor(R.color.black_select));
            rlCashZhifubao.setVisibility(View.GONE);
            cashValue = false;
            type = "微信钱包";
        }
    }

    //支付宝提现文字点击
    private void cashZhifubao() {
        if (!cashValue) {
            tvCashWeixin.setTextColor(getResources().getColor(R.color.black_select));
            rlCashWeixin.setVisibility(View.GONE);
            tvCashZhifubao.setTextColor(getResources().getColor(R.color.red_select));
            rlCashZhifubao.setVisibility(View.VISIBLE);
            cashValue = true;
            type = "支付宝钱包";
        }
    }

    /**
     * 无网络显示“重新加载”按钮
     */
    @Override
    public void setErrorLayout() {
        rlQuickCash.setVisibility(View.GONE);
        llErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCoinExchangeBean(@NonNull CoinExchangeBean bean) {
        rlQuickCash.setVisibility(View.VISIBLE);
        llErrorView.setVisibility(View.GONE);
        List<CoinExchangeBean.ListBean> listBeans = bean.getList();

        //当数据加载出来了  把所有界面都显示
        tvDangqian.setVisibility(View.VISIBLE);
        tvWen1.setVisibility(View.VISIBLE);
        tvCashTip.setVisibility(View.VISIBLE);
        cashRecyclerView.setVisibility(View.VISIBLE);

        currCoin = bean.getSurplus();

        //设置数据
        tvWen1.setText("温馨提示：");
        tvCashTip.setText(bean.getTip());
        tvDangqian.setText(MessageFormat.format("当前余额：  {0} 金币", currCoin));
        tvCashGold.setText(MessageFormat.format("合计：  {0}  金币", listBeans.get(0).getGold()));
        if (rlCashZhifubao.getVisibility() == View.GONE) {
            rlCashWeixin.setVisibility(View.VISIBLE);
        }

        if (quickCashAdapter == null) {
            quickCashAdapter = new QuickCashAdapter(this, listBeans);
            cashRecyclerView.setAdapter(quickCashAdapter);
            cashRecyclerView.setLayoutManager(new GridLayoutManager(QuickCashActivity.this, 3));
        }

        //回调Adapter的Item方法
        quickCashAdapter.setOnItemClickListener(position -> {
            tvCashGold.setText(MessageFormat.format("合计：  {0}  金币", listBeans.get(position).getGold()));
            redeemNowCoin = listBeans.get(position).getRmb() / 100; //提现的金币  元
            totalCoin = listBeans.get(position).getGold(); //提现的合计 总金币
        });
        mWeixinPayAppId = bean.getWeixinPayAppid();
        queryUserInfo();
    }

    //收到通知改变当前余额
    @Override
    public void setCurrCoinBean(CoinExchangeBean bean) {
        if (tvDangqian != null) {
            tvDangqian.setText(MessageFormat.format("当前余额：  {0} 金币", currCoin - totalCoin));
        }
    }

    //管理微信钱包信息
    @Subscriber(tag = EventBusTags.TAG_UPDATE_USER_INFO)
    public void setWeChat(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            dbUpdateWeixinAlipay(userInfoBean);
        }
    }

    /**
     * 接收从登录界面的登录事件，
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        if (isLogin && mPresenter != null) {
            mPresenter.getCoinExchange();
        }
    }

    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            dbUpdateWeixinAlipay(list.get(0));
        }
    }

    private void dbUpdateWeixinAlipay(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (TextUtils.equals(mWeixinPayAppId, userInfoBean.getWeixinPayAppid())) {
                if (!TextUtils.isEmpty(userInfoBean.getWeixinPayName()) && !TextUtils.isEmpty(userInfoBean.getWeixinPayPhone())) {
                    tvCashWeixinPayName.setSelected(true);
                    String name = userInfoBean.getWeixinPayName();
                    if (name.length() > 6) {
                        name = name.substring(0, 6) + "...";
                    }
                    tvCashWeixinPayName.setText(name);
                    tvCashWeixinPayPhone.setText(userInfoBean.getWeixinPayPhone());
                } else {
                    tvCashWeixinPayName.setSelected(false);
                    tvCashWeixinPayName.setText(R.string.cash_not_bind_Wechat);
                    tvCashWeixinPayPhone.setText("");
                }
            } else {
                tvCashWeixinPayName.setSelected(false);
                tvCashWeixinPayName.setText(R.string.cash_not_bind_Wechat);
                tvCashWeixinPayPhone.setText("");
                dbUpdateWeixin();
            }

            if (!TextUtils.isEmpty(userInfoBean.getAlipayAccount()) && !TextUtils.isEmpty(userInfoBean.getAlipayName())) {
                tvCashAlipayName.setSelected(true);
                String name = userInfoBean.getAlipayName();
                if (name.length() > 6) {
                    name = name.substring(0, 6) + "...";
                }
                tvCashAlipayName.setText(name);
                tvCashAlipayAccount.setText(userInfoBean.getAlipayAccount());
            } else {
                tvCashAlipayName.setSelected(false);
                tvCashAlipayName.setText(R.string.cash_not_bind_Payment);
                tvCashAlipayAccount.setText("");
            }
        }
    }

    private void dbUpdateWeixin() {
        UserInfoBeanDao userInfoDao = MyApplication.get().getDaoSession().getUserInfoBeanDao();
        List<UserInfoBean> list = userInfoDao.loadAll();
        if (list != null && list.size() > 0) {
            UserInfoBean userInfoBean = (list.get(0));
            userInfoBean.setWeixinPayName("");
            userInfoBean.setWeixinPayPhone("");
            userInfoBean.setWeixinPayAvatar("");
            userInfoBean.setWeixinPayNickname("");
            userInfoBean.setWeixinPayAppid("");
            userInfoDao.update(userInfoBean);
        }
    }

    @Override
    protected void onDestroy() {
        quickCashAdapter = null;
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
    }
}
