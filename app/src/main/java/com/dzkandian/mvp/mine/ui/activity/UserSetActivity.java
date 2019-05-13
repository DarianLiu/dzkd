package com.dzkandian.mvp.mine.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.JPush.TagAliasOperatorHelper;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.widget.OptionView;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.common.widget.laoding.SexDialogFragment;
import com.dzkandian.common.widget.laoding.date.DoubleTimeSelectDialog;
import com.dzkandian.db.DeviceInfoBeanDao;
import com.dzkandian.db.UserInfoBeanDao;
import com.dzkandian.mvp.common.ui.activity.ForgetPwdActivity;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.mine.contract.UserSetActivityContract;
import com.dzkandian.mvp.mine.di.component.DaggerUserSetActivityComponent;
import com.dzkandian.mvp.mine.di.module.UserSetActivityModule;
import com.dzkandian.mvp.mine.presenter.UserSetActivityPresenter;
import com.dzkandian.storage.bean.UserInfoBean;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dzkandian.common.JPush.TagAliasOperatorHelper.ACTION_DELETE;
import static com.dzkandian.common.JPush.TagAliasOperatorHelper.sequence;
import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 用户信息设置页面（数据库读取）
 */
public class UserSetActivity extends BaseActivity<UserSetActivityPresenter> implements UserSetActivityContract.View {

    @Inject
    ImageLoader imageLoader;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_user_set_head)
    ImageView ivUserSetHead;//用户头像
    @BindView(R.id.ll_user_set_head)
    RelativeLayout llUserSetHead;//用户设置头像
    @BindView(R.id.ov_user_set_name)
    OptionView ovUserSetName;//用户设置名称
    @BindView(R.id.ov_user_set_phone)
    OptionView ovUserSetPhone;//用户设置手机号
    @BindView(R.id.ov_user_set_sex)
    OptionView ovUserSetSex;//用户设置性别
    @BindView(R.id.ov_user_set_birthday)
    OptionView ovUserSetBirthday;//用户设置生日
    @BindView(R.id.ov_user_set_weChat)
    OptionView ovUserSetWeChat;//用户设置微信钱包
    @BindView(R.id.ov_user_set_aLiPay)
    OptionView ovUserSetALiPay;//用户设置支付宝钱包
    @BindView(R.id.ov_user_set_reset_pwd)
    OptionView ovUserResetPwd;//用户修改密码
    @BindView(R.id.tv_user_login_out)
    TextView tvUserLoginOut;//退出登录

    @Nullable
    private LoadingProgressDialog loadingProgressDialog;

    @Nullable
    private DoubleTimeSelectDialog mDoubleTimeSelectDialog;//日期选择

    private SexDialogFragment sexDialogFragment;

    private UserInfoBean mUserInfo;//用户信息
    private long mClickSexTime;//上一次点击性别的时间
    private long mClickBirthdayTime;//上一次点击生日的时间

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerUserSetActivityComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .userSetActivityModule(new UserSetActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_user_set; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvToolbarTitle.setText(R.string.title_user_set);
        toolbar.setNavigationOnClickListener(v -> killMyself());

        queryUserInfo();//进入后界面后--获取用户信息数据库

    }

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        if (!loadingProgressDialog.isShowing())
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
        finish();
    }

    @OnClick({R.id.iv_user_set_head, R.id.ll_user_set_head, R.id.ov_user_set_name, R.id.ov_user_set_phone,
            R.id.ov_user_set_sex, R.id.ov_user_set_birthday, R.id.ov_user_set_weChat,
            R.id.ov_user_set_aLiPay, R.id.ov_user_set_reset_pwd, R.id.tv_user_login_out})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.iv_user_set_head:
            case R.id.ll_user_set_head:
                //设置头像
                pictureSelector();
                break;
            case R.id.ov_user_set_name:
                //设置昵称
                launchActivity(new Intent(this, UpdateNicknameActivity.class));
                break;
            case R.id.ov_user_set_phone:
                //如果手机号为空，则绑定手机号，
                if (mUserInfo != null) {
                    Intent intent = new Intent(this, UpdatePhoneActivity.class);
                    if (TextUtils.isEmpty(mUserInfo.getPhone())) {
                        intent.putExtra(Constant.INTENT_KEY_TYPE, 0);
                    } else {
                        //如果密码不为空，则进行原密码验证
                        intent.putExtra(Constant.INTENT_KEY_PHONE, mUserInfo.getPhone());
                        intent.putExtra(Constant.INTENT_KEY_TYPE, 1);
                    }
                    launchActivity(intent);
                }
                break;
            case R.id.ov_user_set_sex:
                if (System.currentTimeMillis() - mClickSexTime > 2000) {
                    mClickSexTime = System.currentTimeMillis();
                    if (mUserInfo != null) {
                        int gender = mUserInfo.getGender().intValue();
                        //设置性别
                        sexDialogFragment = (SexDialogFragment) getSupportFragmentManager().findFragmentByTag("SexDialog");
                        if (sexDialogFragment == null) {
                            sexDialogFragment = new SexDialogFragment();
                            Bundle args = new Bundle();
                            args.putInt("sex", gender);
                            sexDialogFragment.setArguments(args);
                        } else {
                            getSupportFragmentManager().beginTransaction().remove(sexDialogFragment);
                        }

                        sexDialogFragment.show(getSupportFragmentManager(), "SexDialog");

                        sexDialogFragment.setOnCheckedChangeListener((sexType, sex) -> {
                            assert mPresenter != null;
                            mPresenter.uploadInfo("gender", sexType + "", "", "");
                        });
                    }
                }
                break;
            case R.id.ov_user_set_birthday://弹出生日选择Dialog
                if (System.currentTimeMillis() - mClickBirthdayTime > 2000) {
                    mClickBirthdayTime = System.currentTimeMillis();
                    if (mUserInfo != null) {
                        String birthday = mUserInfo.getBirthday();
                        String beginDeadTime = "1938-01-01";
                        String defaultWeekEnd = "2013-12-31";
                        if (mDoubleTimeSelectDialog == null) {
                            mDoubleTimeSelectDialog = new DoubleTimeSelectDialog(this, beginDeadTime, birthday, defaultWeekEnd);
                        }
                        if (!mDoubleTimeSelectDialog.isShowing()) {
                            mDoubleTimeSelectDialog.recoverButtonState();
                            mDoubleTimeSelectDialog.setOnClickListener(date -> {
                                ovUserSetBirthday.setRightText(date);
                                if (mPresenter != null)
                                    mPresenter.uploadInfo("birthday", date, "", "");
                            });
                            mDoubleTimeSelectDialog.show();
                        }
                    }
                }
                break;
            case R.id.ov_user_set_weChat:
                //管理微信钱包
                launchActivity(new Intent(this, UpdateWeChatPayActivity.class));
                break;
            case R.id.ov_user_set_aLiPay:
                //管理支付宝
                launchActivity(new Intent(this, UpdateALiPayActivity.class));
                break;
            case R.id.ov_user_set_reset_pwd://修改密码
                if (mUserInfo != null) {
                    if (!TextUtils.isEmpty(mUserInfo.getPassword()) && mUserInfo.getPassword().equals("Y")) {
                        launchActivity(new Intent(this, RevisePasswordActivity.class));//有密码状态去  修改密码
                    } else {
                        Intent intent = new Intent(this, ForgetPwdActivity.class);//没有密码状态去  设置密码
                        intent.putExtra(Constant.INTENT_KEY_PASSWORD, 1);
                        launchActivity(intent);
                    }
                }
                break;
            case R.id.tv_user_login_out:
                //退出登录
                AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(this);
                normalDialog.setMessage(getResources().getString(R.string.toast_sign))
                        .setTitle(getResources().getString(R.string.dialog_prompt))
                        .setPositiveButton(getResources().getString(R.string.toast_sign_continue),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                })
                        .setNegativeButton(getResources().getString(R.string.toast_sign_quit),
                                (dialog, which) -> {
                                    DataHelper.removeSF(this, Constant.SP_KEY_TOKEN);
                                    DataHelper.removeSF(this, Constant.SP_KEY_EXPIRE);
//                                    DataHelper.removeSF(this, Constant.SP_KEY_USER_INFO);
                                    DataHelper.removeSF(this, Constant.SP_KEY_TIME_LAST);
//                                    Timber.d("==shareUserSet  uploadHead:" + "有登录，更换用户头像，删除用户头像的路径");
                                    DataHelper.removeSF(this, Constant.SP_KEY_HAVE_TOUCH_HARDWARE); //退出时消除触摸触摸硬件
                                    DataHelper.removeSF(this, Constant.SP_KEY_INDEX_POP_CLOSE_ACTIVITY_HOUR); //上一次出现 首页弹窗活动 的时间
                                    DataHelper.removeSF(this, Constant.SP_KEY_HAS_APPRENTICE_CLOSE_DAY); //上一次出现 是否有收徒 的时间
                                    DataHelper.removeSF(this, Constant.SP_KEY_FINISH_NOVICE_TASK_CLOSE_DAY); //上一次出现 是否完成新手任务 的时间
                                    DataHelper.removeSF(this, Constant.SP_KEY_TODAY_SIGN_CLOSE_DAY); //上一次出现 今日是否签到 的时间
                                    DataHelper.removeSF(this, Constant.SP_KEY_UPDATE_APP); //退出登录删除是否更新版本信息
                                    DataHelper.removeSF(this, Constant.SP_KEY_NEWS_PROGRESS_SCALE); //退出时消除触摸触摸硬件
                                    if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME)
                                            && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
                                        new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).delete();
//                                        Timber.d("==UserSetActivity  退出登录  删除用户头像");
                                    }

                                    UserInfoBeanDao userInfoDao = MyApplication.get().getDaoSession().getUserInfoBeanDao();
                                    userInfoDao.deleteAll();
//                                    Timber.d("=db=    UserSetActivity - DeviceInfo - deleteAll：删除用户信息数据库表");

                                    DataHelper.removeSF(this, Constant.SP_KEY_USER_ID); //退出登录删除用户ID

                                    DeviceInfoBeanDao deviceInfoDao = MyApplication.get().getDaoSession().getDeviceInfoBeanDao();
                                    deviceInfoDao.deleteAll();
//                                    Timber.d("=db=    UserSetActivity - DeviceInfo - deleteAll：删除设备信息数据库表");

                                    MyApplication.get().getDaoSession().getNewsRecordBeanDao().deleteAll();//删除阅读记录

//                                    EventBus.getDefault().post(false, EventBusTags.TAG_PUSH_STATE);//退出登录，把红点提示隐藏
                                    EventBus.getDefault().post(false, EventBusTags.TAG_LOGIN_STATE);
                                    TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
                                    tagAliasBean.alias = "";
                                    tagAliasBean.isAliasAction = true;
                                    tagAliasBean.action = ACTION_DELETE;
                                    sequence++;
                                    TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);
                                    launchActivity(new Intent(this, LoginActivity.class));
                                    killMyself();
                                    dialog.dismiss();
                                }).show();
                break;
        }
    }

    /**
     * 头像选择 PictureSelector
     */
    private void pictureSelector() {
        PictureSelector.create(this).openGallery(PictureMimeType.ofImage())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(false)
                .isCamera(true)
                .enableCrop(true)
                .compress(true)
                .minimumCompressSize(100)
                .glideOverride(200, 200)
                .withAspectRatio(1, 1)
                .showCropFrame(true)
                .rotateEnabled(true)
                .isDragFrame(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 接收更新用户信息事件
     */
    @Subscriber(tag = EventBusTags.TAG_UPDATE_USER_INFO)
    private void receiveUpDateUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            mUserInfo = userInfoBean;
            updateUserInfo(mUserInfo);//收到用户信息改变后--获取用户信息数据库
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        // 1.media.getPath(); 为原图path
                        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                        if (media.isCompressed()) {
                            String compressedPath = media.getCompressPath();
//                            Timber.d("====compressedPath====" + compressedPath);
                            assert mPresenter != null;
                            mPresenter.uploadAvatar(new File(compressedPath));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 上传成功后清除图片缓存
     */
    @Override
    public void cachePicture() {
        PictureFileUtils.deleteCacheDirFile(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();

        if (mDoubleTimeSelectDialog != null && mDoubleTimeSelectDialog.isShowing())
            mDoubleTimeSelectDialog.dismiss();

        if (sexDialogFragment != null && sexDialogFragment.isVisible()) {
            sexDialogFragment.dismiss();
        }

        mUserInfo = null;
        loadingProgressDialog = null;
        mDoubleTimeSelectDialog = null;
        sexDialogFragment = null;
    }


    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
//            Timber.d("=db=    UserSetActivity - UserInfo - query 成功");
            mUserInfo = list.get(0);
            updateUserInfo(mUserInfo);
        }
//        else {
//            Timber.d("=db=    UserSetActivity - UserInfo - query 失败");
//        }
    }

    /**
     * 更新用户信息
     *
     * @param userInfoBean 用户信息
     */
    private void updateUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (!TextUtils.isEmpty(userInfoBean.getAvatar())) {
                imageLoader.loadImage(this, CustomImageConfig.builder()
                        .url(userInfoBean.getAvatar())
                        .isCenterCrop(true)
                        .isCircle(true)
                        .cacheStrategy(1)
                        .errorPic(R.drawable.icon_mine_head)
                        .placeholder(R.drawable.icon_mine_head)
                        .imageView(ivUserSetHead)
                        .build());
            }

            ovUserSetName.setRightText(TextUtils.isEmpty(userInfoBean.getUsername()) ?
                    getResources().getString(R.string.notSet) : userInfoBean.getUsername());

            if (!TextUtils.isEmpty(userInfoBean.getPhone())) {
                ovUserSetPhone.setRightText(userInfoBean.getPhone());
                ovUserResetPwd.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(userInfoBean.getPassword()) && userInfoBean.getPassword().equals("Y")) {
                    ovUserResetPwd.setLeftText("修改密码");
                } else {
                    ovUserResetPwd.setLeftText("设置密码");
                }
            } else {
                ovUserSetPhone.setRightText(getResources().getString(R.string.unbound));
                ovUserResetPwd.setVisibility(View.GONE);
            }

            if (userInfoBean.getGender() == 0) {
                ovUserSetSex.setRightText(getResources().getString(R.string.notSet));
            } else if (userInfoBean.getGender() == 1) {
                ovUserSetSex.setRightText("男");
            } else if (userInfoBean.getGender() == 2) {
                ovUserSetSex.setRightText("女");
            }

            ovUserSetBirthday.setRightText(TextUtils.isEmpty(userInfoBean.getBirthday()) ?
                    getResources().getString(R.string.notSet) : userInfoBean.getBirthday());
        }
    }

}
