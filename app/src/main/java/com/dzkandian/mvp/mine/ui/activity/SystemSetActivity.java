package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dzkandian.R;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.http.Api;
import com.dzkandian.common.uitls.CacheUtil;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.uitls.update.UpdateAppBean;
import com.dzkandian.common.uitls.update.UpdateDialogFragment;
import com.dzkandian.common.widget.OptionView;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.ui.activity.AgreementWebActivity;
import com.dzkandian.mvp.mine.contract.SystemSetContract;
import com.dzkandian.mvp.mine.di.component.DaggerSystemSetComponent;
import com.dzkandian.mvp.mine.di.module.SystemSetModule;
import com.dzkandian.mvp.mine.presenter.SystemSetPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.sevenheaven.segmentcontrol.SegmentControl;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SystemSetActivity extends BaseActivity<SystemSetPresenter> implements SystemSetContract.View {
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ov_system_set_update)
    OptionView ovSystemSetUpdate;
    @BindView(R.id.ov_system_set_clean)
    OptionView ovSystemSetClean;
    @BindView(R.id.ov_system_set_protocol)
    OptionView ovSystemSetProtocol;
    @BindView(R.id.ov_system_set_about)
    OptionView ovSystemSetAbout;
    @BindView(R.id.tb_system_set_sound)
    ToggleButton tbSetSound;
    @BindView(R.id.tb_system_set_push)
    ToggleButton tbSetPush;
    @BindView(R.id.sc_system_set_text_size)
    SegmentControl scSetTextSize;

    private LoadingProgressDialog loadingProgressDialog;
    private long setTextSizeTimes;//点击字体大小按钮的上一次时间；
    private UpdateAppBean mUpdateAppBean;//app检测更新数据
    private long onClickUpdateTime;//上一次点击检查更新的时间
    private long onClickCleanTime;//上一次点击清除缓存的时间
    private long onClickAgreementTime;//上一次点击用户协议的时间
    private String mTextSize;//刚进来系统设置的字体大小类型；
    private AlertDialog mPushDialog;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerSystemSetComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .systemSetModule(new SystemSetModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_system_set; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(String.format("%s%s", getResources().getString(R.string.system_set),
                Api.RELEASE_VERSION ? "" : "（内网版）"));
        toolbar.setNavigationOnClickListener(v -> killMyself());

        /*清除缓存右边填充已有缓存数值*/
        try {
            ovSystemSetClean.setRightText(CacheUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //初始化金币音效状态
        initToggleSound();

        //初始化通知栏消息状态
        initTogglePush();

        //初始化字体大小状态
        initToggleTextSize();
    }

    /**
     * 初始化金币音效状态
     */
    private void initToggleSound() {
        String openSound = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND);
        if (!TextUtils.isEmpty(openSound) && TextUtils.equals(openSound, "false")) {
            tbSetSound.setChecked(false);
        } else {
            //默认开启金币音效
            tbSetSound.setChecked(true);
        }

        tbSetSound.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            if (isCheck) {
                DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND, "true");
            } else {
                DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND, "false");
            }
        });
    }

    /**
     * 初始化通知栏推送状态
     */
    private void initTogglePush() {
        String openPush = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND);
        if (!TextUtils.isEmpty(openPush) && TextUtils.equals(openPush, "false")) {
            tbSetPush.setChecked(false);
        } else {
            //默认开启推送
            tbSetPush.setChecked(true);
        }

        tbSetPush.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            if (isCheck) {
                //开启通知栏消息
                DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_JPUSH, "true");
                if (JPushInterface.isPushStopped(getApplicationContext())) {
                    JPushInterface.resumePush(getApplicationContext());
                }
            } else {
                showPushDialog();
            }
        });
    }

    /**
     * 初始化字体大小状态
     */
    private void initToggleTextSize() {
        String textSize = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        if (!TextUtils.isEmpty(textSize)) {
            switch (textSize) {
                case "small":
                    scSetTextSize.setSelectedIndex(0);
                    mTextSize = "small";
                    break;
                case "medium":
                    scSetTextSize.setSelectedIndex(1);
                    mTextSize = "medium";
                    break;
                case "big":
                    scSetTextSize.setSelectedIndex(2);
                    mTextSize = "big";
                    break;
            }
        } else {
            //默认中等大小
            scSetTextSize.setSelectedIndex(1);
            DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE, "medium");
            mTextSize = "medium";
        }

        scSetTextSize.setOnSegmentControlClickListener(index -> {
            switch (index) {
                case 0:   //字体小
                    DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE, "small");
                    break;
                case 1:   //字体中
                    DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE, "medium");
                    break;
                case 2:   //字体大
                    DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE, "big");
                    break;
                default:
                    break;
            }
            if (System.currentTimeMillis() - setTextSizeTimes > 2000) {
                setTextSizeTimes = System.currentTimeMillis();
                showMessage("设置成功");
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
    protected void onDestroy() {
        if (mTextSize != null && !mTextSize.equals(DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE))) {
            EventBus.getDefault().post(true, EventBusTags.TAG_TEXT_SIZE);
        }
        super.onDestroy();
    }

    @Override
    public void killMyself() {
        finish();
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    @OnClick({R.id.ov_system_set_update, R.id.ov_system_set_clean, R.id.ov_system_set_protocol, R.id.ov_system_set_about})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.ov_system_set_update:
                //检查更新
                if (System.currentTimeMillis() - onClickUpdateTime > 2000) {
                    onClickUpdateTime = System.currentTimeMillis();
                    if (isInternet()) {
                        if (mPresenter != null)
                            mPresenter.checkUpdate("");
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.ov_system_set_clean:
                //清除缓存
                if (System.currentTimeMillis() - onClickCleanTime > 2000) {
                    onClickCleanTime = System.currentTimeMillis();
                    if ("0KB".equals(ovSystemSetClean.getRightText())) {
                        showMessage("没有缓存，不需要清理");
                    } else {
                        CacheUtil.clearAllCache(getApplicationContext());
                        try {
                            ovSystemSetClean.setRightText(CacheUtil.getTotalCacheSize(this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ("0KB".equals(ovSystemSetClean.getRightText())) {
                            showMessage("清除成功");
                        } else {
                            showMessage("清除失败");
                        }
                    }
                }
                break;
            case R.id.ov_system_set_protocol:
                //用户协议
                if (System.currentTimeMillis() - onClickAgreementTime > 2000) {
                    onClickAgreementTime = System.currentTimeMillis();
                    if (isInternet()) {
                        Intent intent = new Intent(this, AgreementWebActivity.class);
                        intent.putExtra("URL", getResources().getString(R.string.system_set_protocol_url));
                        intent.putExtra("title", getResources().getString(R.string.system_set_protocol));
                        launchActivity(intent);
                    } else {
                        showMessage("连接网络可查看");
                    }
                }
                break;
            case R.id.ov_system_set_about:
                //关于大众看点
                launchActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }

    @Override
    public void showUpdateDialog(UpdateAppBean updateAppBean) {
        mUpdateAppBean = updateAppBean;
        Bundle bundle = new Bundle();
        bundle.putSerializable("UpdateAppBean", mUpdateAppBean);
        UpdateDialogFragment
                .newInstance(bundle)
                .show(this.getSupportFragmentManager(), "dialog");
    }

    /**
     * 更新框架出错后重新拉起更新
     *
     * @param isError 是否更新异常
     */
    @Subscriber(tag = EventBusTags.TAG_UPDATE_DIALOG)
    private void receiveUpdateError(boolean isError) {
//        Timber.d("==updatedialog  errorUpdateDialog");
        if (isError && mUpdateAppBean != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("UpdateAppBean", mUpdateAppBean);
            UpdateDialogFragment
                    .newInstance(bundle)
                    .show(this.getSupportFragmentManager(), "dialog");
        }
    }

    /**
     * 显示关闭推送的Dialog
     */
    private void showPushDialog() {
        if (mPushDialog == null) {
            mPushDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_jpush_title))
                    .setMessage(getResources().getString(R.string.dialog_jpush_message))
                    .setPositiveButton(getResources().getString(R.string.dialog_jpush_positive),
                            (dialog, which) -> {
                                tbSetPush.setChecked(true);
                                dialog.dismiss();
                            })
                    .setNegativeButton(getResources().getString(R.string.dialog_jpush_negative),
                            (dialog, which) -> {
                                DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SET_JPUSH, "false");
                                if (!JPushInterface.isPushStopped(getApplicationContext())) {
                                    JPushInterface.stopPush(getApplicationContext());
                                }
                                dialog.dismiss();
                            })
                    .setOnKeyListener((dialogInterface, i, keyEvent) -> {
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            tbSetPush.setChecked(true);
                        }
                        return false;
                    }).create();
            mPushDialog.setCanceledOnTouchOutside(false);
        }
        mPushDialog.show();
    }
}
