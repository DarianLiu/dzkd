package com.dzkandian.mvp.mine.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.JPush.JPushUtil;
import com.dzkandian.common.JPush.TagAliasOperatorHelper;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.MinAdUtils;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.OptionView;
import com.dzkandian.common.widget.laoding.BezierRefreshHeader;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.mine.contract.MineContract;
import com.dzkandian.mvp.mine.di.component.DaggerMineComponent;
import com.dzkandian.mvp.mine.di.module.MineModule;
import com.dzkandian.mvp.mine.presenter.MinePresenter;
import com.dzkandian.mvp.mine.ui.activity.FeedBackActivity;
import com.dzkandian.mvp.mine.ui.activity.InvitationActivity;
import com.dzkandian.mvp.mine.ui.activity.MessageCenterActivity;
import com.dzkandian.mvp.mine.ui.activity.MineOrderActivity;
import com.dzkandian.mvp.mine.ui.activity.MyCollectionActivity;
import com.dzkandian.mvp.mine.ui.activity.ProfitDetailActivity;
import com.dzkandian.mvp.mine.ui.activity.QuestionAllActivity;
import com.dzkandian.mvp.mine.ui.activity.QuickCashActivity;
import com.dzkandian.mvp.mine.ui.activity.SystemSetActivity;
import com.dzkandian.mvp.mine.ui.activity.UserSetActivity;
import com.dzkandian.storage.bean.CoinBean;
import com.dzkandian.storage.bean.MarqueeBean;
import com.dzkandian.storage.bean.RandomAdBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.event.PushEvent;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dzkandian.common.JPush.TagAliasOperatorHelper.ACTION_SET;
import static com.dzkandian.common.JPush.TagAliasOperatorHelper.sequence;
import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 个人中心
 */
public class MineFragment extends BaseFragment<MinePresenter> implements MineContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rl_mine_native_ad)
    RelativeLayout adsNativeMineView;//广告位
    @BindView(R.id.ll_mine_contain)
    LinearLayout llContain;
    @BindView(R.id.ll_mine_user)
    LinearLayout llUser;
    @BindView(R.id.iv_system_set)
    ImageView ivSystemSet;//系统设置
    @BindView(R.id.tv_mine_coin_today)
    TextView tvCoinToday;//今日金币
    @BindView(R.id.tv_mine_coin_surplus)
    TextView tvCoinSurplus;//剩余金币
    @BindView(R.id.tv_mine_coin_total)
    TextView tvCoinTotal;//总金币
    @BindView(R.id.iv_mine_head)
    ImageView ivMineHead;//用户头像

    @BindView(R.id.tv_mine_phone)
    TextView tvPhone;//手机号
    @BindView(R.id.iv_mine_name)
    TextView tvName;//用户昵称

    @BindView(R.id.tv_mine_withdraw)
    TextView tvWithdraw;//快速提现
    @BindView(R.id.tv_mine_invite)
    TextView tvInvite;//好友邀请
    @BindView(R.id.tv_mine_order)
    TextView tvOrder;//我的订单
    @BindView(R.id.tv_mine_income)
    TextView tvIncome;//收益明细

    @BindView(R.id.ov_mine_message)
    OptionView ovMsgCenter;//消息中心
    @BindView(R.id.ov_mine_feedback)
    OptionView ovFeedback;//意见反馈
    @BindView(R.id.ov_mine_question)
    OptionView ov_Question;//常见问题
    @BindView(R.id.ov_mine_collection)
    OptionView ovMineCollection;//我的收藏
    @BindView(R.id.ov_mine_my_set)
    OptionView ovMySet;//用户设置

    @BindView(R.id.mine_marquee_view)
    TextView mineMarqueeView;
    @BindView(R.id.mine_marquee_layout)
    RelativeLayout mineMarqueeLayout;
    @BindView(R.id.mine_marquee_line)
    View mineMarqueeLine;
    @BindView(R.id.brh)
    BezierRefreshHeader brh;

    @Nullable
    private LoadingProgressDialog mLoadingProgressDialog;

    private TagAliasOperatorHelper.TagAliasBean mTagAliasBean;
    private TextView centralityPoint;//新消息提示
    private String mUserPath = "";//用户头像地址

    private boolean isHaveActivePush;//有活动公告推送
    private boolean isHaveNotificaPush;//有系统通知推送
    private boolean isHaveMsgPush;//有我的消息推送（评论回复）
    private int isHaveTouchHardware;//是否有触摸硬件（触摸面积不为0）
    private String mFirstTouchArea;
    private long mRefreshLastTime;//我的"界面刷新的上一次时间；


    private String marqueeString;//跑马灯文字
    private ObjectAnimator mAnimMarquee;


    /*金币自增动画相关参数*/
    private ValueAnimator animatorToday, animatorSurplus, animatorTotal;
    private static final String TAG_RESUME = "Resume";//金币自增动画常量  Resume
    private static final String TAG_PAUSE = "Pause";//金币自增动画常量  Pause
    private static final String TAG_DESTROY = "Destroy";//金币自增动画常量  Destroy
    private MinAdUtils minAdUtils;

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMineComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mineModule(new MineModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        //限定广告位宽高16：9
        int bigWidth = ArmsUtils.getScreenWidth(getActivity());
        int bigHeight = bigWidth * 9 / 16;
        adsNativeMineView.setLayoutParams(new LinearLayout.LayoutParams(bigWidth, bigHeight));

        initRedPoint();

        //广告
        minAdUtils = MinAdUtils.getInstance(getContext(), adsNativeMineView);
        initRefreshLayout();

        EventBus.getDefault().registerSticky(this);

        isHaveTouchHardware = DataHelper.getIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);

        mFirstTouchArea = DataHelper.getStringSF(getActivity().getApplicationContext(), Constant.SP_KEY_TOUCH_AREA);

        checkTouchHardware();
    }


    /**
     * 初始化刷新事件
     */
    private void initRefreshLayout() {
        refreshLayout.setEnableLoadMore(false);//关闭加载更多
        refreshLayout.setDisableContentWhenRefresh(true);//刷新时禁止滑动
        refreshLayout.setOnRefreshListener(refreshLayout -> {//刷新
            if (isInternet()) {
                if (mPresenter != null) {
                    //获取金币信息
                    marqueeString = "";
                    mPresenter.getCoin();
                    mPresenter.getMarquee();
                    mPresenter.getEssentialParameter();
                    //广告刷新
                    mPresenter.requestAd();
                }
            } else {
                if (System.currentTimeMillis() - mRefreshLastTime > 2000) {
                    mRefreshLastTime = System.currentTimeMillis();
                    showMessage("网络请求失败，请连网后重试");
                }
                finishRefresh();
            }
            queryUserInfo();//“我的界面”刷新时--获取数据库用户信息
        });
        refreshLayout.autoRefresh(0,400,1.2f);
        refreshLayout.setHeaderMaxDragRate(1.7f);

    }

    /**
     * 设置红点new样式
     */
    private void initRedPoint() {
        centralityPoint = ovMsgCenter.getRightTextView();
        centralityPoint.setText("NEW");
        centralityPoint.setTextSize(12);
        centralityPoint.setBackground(getResources().getDrawable(R.drawable.shape_red_point));
        centralityPoint.setTextColor(getResources().getColor(R.color.color_title_text));
        centralityPoint.setVisibility(View.GONE);
        centralityPoint.setPadding(20, 5, 20, 5);
        centralityPoint.setGravity(Gravity.CENTER);
    }

    /**
     * 设置跑马灯相关参数
     *
     * @param marqueeBean
     */
    @Override
    public void SetMarquee(MarqueeBean marqueeBean) {
        cancelMarqueeAnim();

        marqueeString = marqueeBean.getText();
        if (mineMarqueeLayout != null && mineMarqueeLine != null && mineMarqueeView != null) {
            if (!TextUtils.isEmpty(marqueeString)) {
                mineMarqueeLayout.setVisibility(View.VISIBLE);
                mineMarqueeLine.setVisibility(View.GONE);
                mineMarqueeView.setText(marqueeString);
                mineMarqueeView.setEllipsize(TextUtils.TruncateAt.END);

                ViewTreeObserver viewTreeObserver = mineMarqueeView.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Layout layout = mineMarqueeView.getLayout();
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                        if (layout == null) {
                            mineMarqueeLayout.setVisibility(View.GONE);
                            mineMarqueeLine.setVisibility(View.VISIBLE);
                            return;
                        }
                        int ellipsisCount = layout.getEllipsisCount(0);
                        if (ellipsisCount > 0) {
                            mineMarqueeView.setGravity(Gravity.CENTER_VERTICAL);
                            mineMarqueeView.setSelected(true);
                            mineMarqueeView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            mineMarqueeView.setMarqueeRepeatLimit(-1);
                        } else {
                            mineMarqueeView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);

                            float wide = DeviceUtils.getScreenWidth(getContext().getApplicationContext())
                                    - DeviceUtils.dpToPixel(getContext().getApplicationContext(), 104);
                            float textWide = mineMarqueeView.getPaint().measureText(marqueeString);

                            mAnimMarquee = ObjectAnimator.ofFloat(mineMarqueeView, "translationX", textWide, -wide);
                            mAnimMarquee.setDuration(5000 + Float.valueOf(5000 * (textWide / wide)).longValue());
                            mAnimMarquee.setInterpolator(new LinearInterpolator());
                            mAnimMarquee.setRepeatMode(ValueAnimator.RESTART);
                            mAnimMarquee.setRepeatCount(-1);
                            mAnimMarquee.start();

                        }
                    }
                });
            } else {
                mineMarqueeLayout.setVisibility(View.GONE);
                mineMarqueeLine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 广告请求成功
     *
     * @param randomAdBean
     */

    @Override
    public void successSelfAd(RandomAdBean randomAdBean) {
        if (!TextUtils.isEmpty(randomAdBean.getAd_type())) {
            minAdUtils.updateShowAdType(randomAdBean);
        } else {
            adsNativeMineView.removeAllViews();
        }
    }

    /**
     * 跑马灯是否开启
     *
     * @param isRun 是否运行
     */
    private void runMarquee(boolean isRun) {
        if (mAnimMarquee != null) {
            if (isRun) {
                mAnimMarquee.resume();
            } else {
                mAnimMarquee.pause();
            }
        }
    }

    /**
     * 取消跑马灯动画
     */
    private void cancelMarqueeAnim() {
        if (mAnimMarquee != null && (mAnimMarquee.isRunning() || mAnimMarquee.isStarted())) {
            mAnimMarquee.cancel();
        }
    }


    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getContext().getApplicationContext());
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    /**
     * 检测触摸硬件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void checkTouchHardware() {
        if (isHaveTouchHardware != 1) {
            llContain.setOnTouchListener((v, event) -> {
                float touch_area = event.getSize();
//                Timber.d("========touch area：" + touch_area);
                if (touch_area > 0 && touch_area != 1 && isHaveTouchHardware != 1) {
                    if (TextUtils.isEmpty(mFirstTouchArea)) {
                        DataHelper.setStringSF(getActivity().getApplicationContext(), Constant.SP_KEY_TOUCH_AREA, String.valueOf(touch_area));
                    } else if (!TextUtils.equals(mFirstTouchArea, String.valueOf(touch_area))) {
                        isHaveTouchHardware = 1;
                        DataHelper.setIntergerSF(getActivity().getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE, isHaveTouchHardware);
                    }
                }
                return false;
            });
        }
    }

    @Override
    public void showLoading() {
        if (mLoadingProgressDialog == null)
            mLoadingProgressDialog = new LoadingProgressDialog.Builder(getActivity()).create();
        mLoadingProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mLoadingProgressDialog != null && mLoadingProgressDialog.isShowing())
            mLoadingProgressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        animatorState(TAG_RESUME);
        runMarquee(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        animatorState(TAG_PAUSE);
        runMarquee(false);
    }

    @Override
    public void onDestroy() {
        animatorState(TAG_DESTROY);
        cancelMarqueeAnim();//取消跑马灯动画
        mTagAliasBean = null;
        super.onDestroy();
        if (mLoadingProgressDialog != null && mLoadingProgressDialog.isShowing()) {
            mLoadingProgressDialog.dismiss();
            mLoadingProgressDialog = null;
        }
        minAdUtils.destroyNativeAds();
    }

    /**
     * 显示状态变化
     *
     * @param hidden 隐藏
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            updateAd();刷新广告
            if (mPresenter != null) {
                mPresenter.getCoin();
                mPresenter.requestAd();
            }
        }
        runMarquee(!hidden);
    }


    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.makeText(getActivity().getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    /**
     * 从登录界面返回
     *
     * @param isLogin 是否登录
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        if (isLogin) {
            if (isInternet() && mPresenter != null) {
                //获取金币信息
                marqueeString = "";
                mPresenter.updateCoin();
                mPresenter.getMarquee();
            }
            queryUserInfo();//从登录成功返回--获取数据库用户信息
        } else {
            clearUserInfo();
        }
    }

    /**
     * 改变用户信息后
     */
    @Subscriber(tag = EventBusTags.TAG_UPDATE_USER_INFO)
    private void upDateUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            updateUserInfo(userInfoBean);//收到用户信息改变后--获取数据库用户信息
        }
    }

    /**
     * 接收未读消息推送
     *
     * @param pushEvent 推送相关信息
     */
    @Subscriber(tag = EventBusTags.TAG_PUSH_MESSAGE)
    public void receivePushEvent(PushEvent pushEvent) {
        if (!pushEvent.isPush()) {
            centralityPoint.setVisibility(View.GONE);
        } else {
            centralityPoint.setVisibility(View.VISIBLE);
        }

        if (pushEvent.getNewActive() >= 0)
            isHaveActivePush = pushEvent.getNewActive() > 0;
        if (pushEvent.getNewNotification() >= 0)
            isHaveNotificaPush = pushEvent.getNewNotification() > 0;
        if (pushEvent.getNewMessage() >= 0)
            isHaveMsgPush = pushEvent.getNewMessage() > 0;
        EventBus.getDefault().removeStickyEvent(PushEvent.class, EventBusTags.TAG_PUSH_MESSAGE);
    }


    /**
     * 清空用户信息
     */
    private void clearUserInfo() {
        tvName.setText("");
        tvPhone.setText("未登录");
        tvCoinToday.setText("0");
        tvCoinSurplus.setText("0");
        tvCoinTotal.setText("0");
    }

    /**
     * @return 是否登录
     */
    private boolean isLogin() {
        String userId = DataHelper.getStringSF(getContext().getApplicationContext(), Constant.SP_KEY_USER_ID);
        return !TextUtils.isEmpty(userId);
    }

    @OnClick({R.id.ll_mine_user, R.id.iv_mine_head, R.id.iv_system_set, R.id.tv_mine_withdraw, R.id.tv_mine_invite, R.id.tv_mine_order, R.id.tv_mine_income,
            R.id.ov_mine_message, R.id.mine_marquee_layout, R.id.mine_marquee_view, R.id.ov_mine_feedback, R.id.ov_mine_question, R.id.ov_mine_collection, R.id.ov_mine_my_set})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.ll_mine_user:
            case R.id.iv_mine_head:
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), UserSetActivity.class));//跳转到UserSetActivity类
                } else {
                    // 头像  skip();//跳转到UserSetActivity类
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.iv_system_set:
                //getCode();//右上角设置图标
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), SystemSetActivity.class));//跳转到UserSetActivity类
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }

                break;
            case R.id.tv_mine_withdraw:
                // judge();//快速提现
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), QuickCashActivity.class));
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.tv_mine_invite:
                // yanzhen();//好友邀请
                if (isLogin()) {
                    Intent intent = new Intent(getActivity(), InvitationActivity.class);
                    intent.putExtra("mUserPath", mUserPath);
                    launchActivity(intent);
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.tv_mine_order:
                //我的订单：
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), MineOrderActivity.class));
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.tv_mine_income:
                //收益明细：
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), ProfitDetailActivity.class));
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.ov_mine_message:
            case R.id.mine_marquee_view:
            case R.id.mine_marquee_layout:
                //消息中心
                if (isLogin()) {
                    Intent intent = new Intent(getActivity(), MessageCenterActivity.class);
                    intent.putExtra("active_push", isHaveActivePush);
                    intent.putExtra("notification_push", isHaveNotificaPush);
                    intent.putExtra("message_push", isHaveMsgPush);
                    launchActivity(intent);
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.ov_mine_feedback:
                //意见反馈
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), FeedBackActivity.class));
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.ov_mine_question:
                //常见问题
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), QuestionAllActivity.class));
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.ov_mine_collection:
                //我的收藏
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), MyCollectionActivity.class));
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.ov_mine_my_set:
                //帐户设置
                if (isLogin()) {
                    launchActivity(new Intent(getActivity(), UserSetActivity.class));
                } else {
                    launchActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
        }
    }


    @Override
    public void killMyself() {

    }

    /**
     * @param mCoinBean 更新金币显示区
     */
    @Override
    public void updateCoin(@NonNull CoinBean mCoinBean) {
        textAnimator(mCoinBean.getToday(), mCoinBean.getSurplus(), mCoinBean.getTotal());
    }

    /**
     * 获取数据库的用户信息
     */
    public void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            updateUserInfo(list.get(0));
//            Timber.d("=db=    MineFragment - UserInfo - query 成功");
        } else {
            if (mPresenter != null)
                mPresenter.userInfo();
        }
    }

    /**
     * @param userInfoBean 获取到数据库的用户信息后，更新界面
     */
    @Override
    public void updateUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (!TextUtils.isEmpty(userInfoBean.getAvatar())) {
                Glide.with(this).asDrawable()
                        .load(userInfoBean.getAvatar())
                        .apply(new RequestOptions().centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .circleCrop()
                                .error(R.drawable.icon_mine_head)
                                .placeholder(R.drawable.icon_mine_head))
                        .into(ivMineHead);
                if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME) && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
//                    Timber.d("==shareMine  :" + "有登录，有图片：" + Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME);
                    mUserPath = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
                } else {
//                    Timber.d("==shareMine  :" + "有登录，无图片");
                    if (mPresenter != null)
                        mPresenter.requestPermission(userInfoBean.getAvatar());
                }
            }

            if (tvName != null) {
                tvName.setText(TextUtils.isEmpty(userInfoBean.getUsername()) ? "" : userInfoBean.getUsername());
            }
            if (tvPhone != null) {
                tvPhone.setText(TextUtils.isEmpty(userInfoBean.getPhone()) ? "" : userInfoBean.getPhone());
            }

//        设置别名Alias
            long userId = userInfoBean.getUserId();
            mTagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
            if (!TextUtils.isEmpty(String.valueOf(userId)) && JPushUtil.isValidTagAndAlias(String.valueOf(userId))) {
                mTagAliasBean.alias = String.valueOf(userId);
                mTagAliasBean.isAliasAction = true;
                mTagAliasBean.action = ACTION_SET;
                sequence++;
            }
            TagAliasOperatorHelper.getInstance().handleAction(getActivity(), sequence, mTagAliasBean);
        }
    }

    @Override
    public void finishRefresh() {
        if (refreshLayout != null)
            refreshLayout.finishRefresh();
    }

    @Override
    public void downloadCallBack(String filePath) {
        mUserPath = filePath;
//        Timber.d("==shareMine  downloadCallBack下载成功：" + mUserPath);
    }


    /**
     * 金币自增动画
     *
     * @param mTodayValue   今日金币
     * @param mSurplusValue 剩余金币
     * @param mTotalValue   总金币
     */
    public void textAnimator(int mTodayValue, int mSurplusValue, int mTotalValue) {
        animatorState(TAG_DESTROY);
        if (mTodayValue > 0 && tvCoinToday != null) {
            animatorToday = ValueAnimator.ofInt(0, mTodayValue);
            animatorToday.setDuration(1500);
            animatorToday.addUpdateListener(animation -> {
                tvCoinToday.setText(animation.getAnimatedValue().toString());
            });
            animatorToday.start();
        }

        if (mSurplusValue > 0 && tvCoinSurplus != null) {
            animatorSurplus = ValueAnimator.ofInt(0, mSurplusValue);
            animatorSurplus.setDuration(1500);
            animatorSurplus.addUpdateListener(animation -> {
                tvCoinSurplus.setText(animation.getAnimatedValue().toString());
            });
            animatorSurplus.start();
        }

        if (mTotalValue > 0 && tvCoinTotal != null) {
            animatorTotal = ValueAnimator.ofInt(0, mTotalValue);
            animatorTotal.setDuration(1500);
            animatorTotal.addUpdateListener(animation -> {
                tvCoinTotal.setText(animation.getAnimatedValue().toString());
            });
            animatorTotal.start();
        }
    }

    /**
     * 金币自增动画的状态控制；
     *
     * @param state
     */
    private void animatorState(String state) {
        if (animatorToday != null && animatorSurplus != null && animatorTotal != null) {
            if (animatorToday.isStarted() || animatorToday.isRunning()) {
                if (state.equals(TAG_RESUME)) {
                    animatorToday.resume();
                } else if (state.equals(TAG_PAUSE)) {
                    animatorToday.pause();
                } else {
                    animatorToday.cancel();
                }
            }
            if (animatorSurplus.isStarted() || animatorSurplus.isRunning()) {
                if (state.equals(TAG_RESUME)) {
                    animatorSurplus.resume();
                } else if (state.equals(TAG_PAUSE)) {
                    animatorSurplus.pause();
                } else {
                    animatorSurplus.cancel();
                }
            }
            if (animatorTotal.isStarted() || animatorTotal.isRunning()) {
                if (state.equals(TAG_RESUME)) {
                    animatorTotal.resume();
                } else if (state.equals(TAG_PAUSE)) {
                    animatorTotal.pause();
                } else {
                    animatorTotal.cancel();
                }
            }
        }
    }


}
