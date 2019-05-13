package com.dzkandian.mvp.task_center.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dsp.ads.TimerUtils;
import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.widget.MobOneKeyShare;
import com.dzkandian.common.widget.autoviewpager.AutoScrollViewPager;
import com.dzkandian.common.widget.laoding.BezierRefreshHeader;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.ui.activity.PlayWebViewActivity;
import com.dzkandian.mvp.common.ui.activity.WebViewActivity;
import com.dzkandian.mvp.mine.ui.activity.InvitationActivity;
import com.dzkandian.mvp.mine.ui.activity.MessageCenterActivity;
import com.dzkandian.mvp.mine.ui.activity.QuickCashActivity;
import com.dzkandian.mvp.mine.ui.activity.UpdatePhoneActivity;
import com.dzkandian.mvp.mine.ui.activity.UserSetActivity;
import com.dzkandian.mvp.task_center.contract.TaskCenterContract;
import com.dzkandian.mvp.task_center.di.component.DaggerTaskCenterComponent;
import com.dzkandian.mvp.task_center.di.module.TaskCenterModule;
import com.dzkandian.mvp.task_center.presenter.TaskCenterPresenter;
import com.dzkandian.mvp.task_center.ui.adapter.TaskAdapter;
import com.dzkandian.storage.bean.ShareBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.task.SignRecordBean;
import com.dzkandian.storage.bean.task.SignStateBean;
import com.dzkandian.storage.bean.task.TaskListBean;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.dzkandian.storage.event.RewardMainEvent;
import com.dzkandian.storage.event.WeChatBindEvent;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 任务中心
 */
public class TaskCenterFragment extends BaseFragment<TaskCenterPresenter> implements TaskCenterContract.View {

    @BindView(R.id.listView_task)
    ListView listViewTask;
    @BindView(R.id.ll_error_view)
    LinearLayout errorView;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    AutoScrollViewPager autoScrollViewPager;
    @BindView(R.id.brh)
    BezierRefreshHeader brh;
//    LinearLayout autoScrollIndicator;

    //轮播图低部滑动图片红点
    private ArrayList<ImageView> mScrollImageViews = new ArrayList<>();
    //轮播图图片
    private List<BannerBean> bannerBeans = new ArrayList<>();


    //头部签到布局
    private TextView tvSignFirstDay, tvOneDay, tvSignSecondDay, tvTwoDay, tvSignThirdDay, tvThreeDay,
            tvSignForthDay, tvFourDay, tvSignFifthDay, tvFiveDay, tvSignSixthDay, tvSixDay, tvSignSeventhDay, tvSevenDay;
    private Button btnSign;

    //头部线性布局
    private View viewOne, viewTwo, viewThree, viewfour, viewfive, viewsix;

    private int signDays = 0;
    private TaskAdapter mTaskAdapter;
    public IWXAPI api;

    private LoadingProgressDialog loadingProgressDialog;
    private String imageUserPath = "";
    private MobOneKeyShare mobOneKeyShare;//分享

    private long currTime; //当前时间
    private long updateTaskCenterLastTimes;//任务中心刷新的上一次时间；
    private long clickSignTimes;//点击签到按钮的上一次时间；
    private String inviteCode; //分享小程序 邀请码


    private TimerUtils mTimerUtils;//任务计时的工具类；
    private BannerView bannerView;//腾讯banner

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerTaskCenterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .taskCenterModule(new TaskCenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_center, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(getActivity(), Constant.APP_ID, false);
        api.registerApp(Constant.APP_ID);

//         if (mPresenter != null)
//        mPresenter.taskList();
//        mPresenter.signRecord();
        initTaskListView();

        mobOneKeyShare = new MobOneKeyShare(getActivity(), null);
    }


    /**
     * 设置banner控件的高度
     */
    private void setBannerHeight() {
        int screenWidth = ArmsUtils.getScreenWidth(getActivity());
        int height = (screenWidth / 6);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth, height);
        autoScrollViewPager.setLayoutParams(params);
    }

    /**
     * 从登录界面返回
     *
     * @param isLogin
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        if (isLogin) {
            if (mPresenter != null) {
                mPresenter.taskList();
                mPresenter.signRecord();
                mPresenter.inviteShare();//获取邀请分享的数据
            }
            queryUserInfo();//从登录成功返回--获取数据库用户信息
        }
    }

    /**
     * 改变用户信息后
     */
    @Subscriber(tag = EventBusTags.TAG_UPDATE_USER_INFO)
    private void upDateUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            dbUpdateUserAvatar(userInfoBean);//收到用户信息改变后--获取数据库用户信息
        }
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getContext().getApplicationContext());
    }

    /**
     * 初始化任务列表
     */
    private void initTaskListView() {
        View headView = getLayoutInflater().inflate(R.layout.include_task_sign, null);
//        autoScrollIndicator = headView.findViewById(R.id.autoScrollIndicator);
        autoScrollViewPager = headView.findViewById(R.id.autoScrollViewPager);

        tvSignFirstDay = headView.findViewById(R.id.tv_sign_first_day);
        tvOneDay = headView.findViewById(R.id.tv_one_day);

        tvSignSecondDay = headView.findViewById(R.id.tv_sign_second_day);
        tvTwoDay = headView.findViewById(R.id.tv_two_day);

        tvSignThirdDay = headView.findViewById(R.id.tv_sign_third_day);
        tvThreeDay = headView.findViewById(R.id.tv_three_day);

        tvSignForthDay = headView.findViewById(R.id.tv_sign_forth_day);
        tvFourDay = headView.findViewById(R.id.tv_four_day);

        tvSignFifthDay = headView.findViewById(R.id.tv_sign_fifth_day);
        tvFiveDay = headView.findViewById(R.id.tv_five_day);

        tvSignSixthDay = headView.findViewById(R.id.tv_sign_sixth_day);
        tvSixDay = headView.findViewById(R.id.tv_six_day);

        tvSignSeventhDay = headView.findViewById(R.id.tv_sign_seventh_day);
        tvSevenDay = headView.findViewById(R.id.tv_seven_day);

        //头部线性属性
        viewOne = headView.findViewById(R.id.view_line_one);

        viewTwo = headView.findViewById(R.id.view_line_two);

        viewThree = headView.findViewById(R.id.view_line_three);

        viewfour = headView.findViewById(R.id.view_line_four);

        viewfive = headView.findViewById(R.id.view_line_five);

        viewsix = headView.findViewById(R.id.view_line_six);

        btnSign = headView.findViewById(R.id.button_sign);
        btnSign.setOnClickListener(v -> {
            if (System.currentTimeMillis() - clickSignTimes > 2000) {
                clickSignTimes = System.currentTimeMillis();
                if (isInternet()) {
                    if (mPresenter != null)
                        mPresenter.sign();
                } else {
                    showMessage("网络请求失败，请连网后重试");
                }
            }
        });


//        listViewTask.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                View v = view.getChildAt(firstVisibleItem);
//                int top = v == null ? 0 : v.getTop();
//                if (firstVisibleItem > oldFirstVisibleItem) {
//                    //上滑
//                    if (!isScrollTop) {
//                        isScrollTop = true;
//                        Timber.d("=============上滑1");
//                        EventBus.getDefault().post(false, EventBusTags.TAG_BOTTOM_MENU);
//                    }
//                } else if (firstVisibleItem < oldFirstVisibleItem) {
//                    //下滑
//                    if (isScrollTop) {
//                        isScrollTop = false;
//                        Timber.d("=============下滑1");
//                        EventBus.getDefault().post(true, EventBusTags.TAG_BOTTOM_MENU);
//                    }
//                } else if (top < oldFirstVisibleItemTop) {
//                    //上滑
//                    if (!isScrollTop) {
//                        isScrollTop = true;
//                        Timber.d("=============上滑2");
//                        EventBus.getDefault().post(false, EventBusTags.TAG_BOTTOM_MENU);
//                    }
//                } else if (top > oldFirstVisibleItemTop) {
//                    //下滑
//                    if (isScrollTop) {
//                        isScrollTop = false;
//                        Timber.d("=============下滑2");
//                        EventBus.getDefault().post(true, EventBusTags.TAG_BOTTOM_MENU);
//                    }
//                }
//
//                oldFirstVisibleItem = firstVisibleItem;
//                oldFirstVisibleItemTop = top;
//            }
//        });

        setBannerHeight();

        refreshLayout.setEnableLoadMore(false);//关闭加载更多
        refreshLayout.setDisableContentWhenRefresh(true);//刷新时禁止滑动
        refreshLayout.setOnRefreshListener(refreshLayout -> {//刷新
            if (isInternet()) {
                if (mPresenter != null) {
                    mPresenter.taskList();
                    mPresenter.signRecord();
  //                  mPresenter.getBanner();
                    mPresenter.inviteShare();//获取邀请分享的数据
                }
                queryUserInfo();//“任务中心”刷新时--获取数据库用户信息
                if(autoScrollViewPager !=null){
                    autoScrollViewPager.stopAutoScroll();
                }
            } else {
                if (System.currentTimeMillis() - updateTaskCenterLastTimes > 2000) {
                    updateTaskCenterLastTimes = System.currentTimeMillis();
                    showMessage("网络请求失败，请连网后重试");
                    showErrorView();//显示网络页面
                }
                finishRefresh();//隐藏刷新
            }
        });
        refreshLayout.autoRefresh(0,400,1.2f);
        refreshLayout.setHeaderMaxDragRate(1.7f);
        brh.setOnFinishListener(new BezierRefreshHeader.OnFinishListener() {
            @Override
            public void onFinish() {
              if(mPresenter != null) {
                  mPresenter.getBanner();
              }
            }
        });


        if (listViewTask != null) {
            listViewTask.addHeaderView(headView);
        }
        View footView = getLayoutInflater().inflate(R.layout.view_menu, null);
        footView.setVisibility(View.INVISIBLE);
        listViewTask.addFooterView(footView);

        mTaskAdapter = new TaskAdapter(this);
        listViewTask.setAdapter(mTaskAdapter);

        mTaskAdapter.setOnItemViewClickListener((position, taskBean) -> {
            switch (taskBean.getEvent()) {
                case Constant.TASK_EVENT_BIND_WECHAT://绑定微信

                    if (taskBean.getReceive() == 0) {
                        if (mPresenter != null)

                            // 判断是否安装了微信客户端
                            if (api != null && !api.isWXAppInstalled()) {
                                showMessage("您还未安装微信客户端");
                                return;
                            }

                        /**绑定微信*/
                        final SendAuth.Req req = new SendAuth.Req();
                        req.scope = "snsapi_userinfo";//获取个人用户信息的权限
                        req.state = Constant.WX_BIND + position + "/" + Math.random();//防止攻击

                        if (api != null) {
                            api.sendReq(req);//向微信发送请求
                        }

                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    }


                    break;
                case Constant.TASK_EVENT_BIND_ALIPAY://绑定支付宝

                    break;
                case Constant.TASK_EVENT_BIND_PHONE://绑定手机

                    if (taskBean.getReceive() == 0) {
                        Intent intent = new Intent(getActivity(), UpdatePhoneActivity.class);
                        intent.putExtra(Constant.INTENT_KEY_TYPE, 0);
                        launchActivity(intent);
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    }
                    break;
                case Constant.TASK_EVENT_FULL_INFO://完善资料

                    if (taskBean.getReceive() == 0) {
                        launchActivity(new Intent(getActivity(), UserSetActivity.class));
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    }
                    break;
                case Constant.TASK_EVENT_READ_NEWS://阅读资讯
                    if (taskBean.getReceive() == 0) {
                        EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(0).build(), EventBusTags.TAG_CHANGE_TAB);
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    } else if (taskBean.getReceive() == 2) {
                        EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(0).build(), EventBusTags.TAG_CHANGE_TAB);
                    }
                    break;
                case Constant.TASK_EVENT_WATCH_VIDEO://看视频
                    if (taskBean.getReceive() == 0) {
                        EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(1).build(), EventBusTags.TAG_CHANGE_TAB);
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    } else if (taskBean.getReceive() == 2) {
                        EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(1).build(), EventBusTags.TAG_CHANGE_TAB);
                    }
                    break;
                case Constant.TASK_EVENT_WITHDRAWALS://提现
                    if (taskBean.getReceive() == 0) {
                        launchActivity(new Intent(getActivity(), QuickCashActivity.class));
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    }
                    break;
                case Constant.TASK_EVENT_INVITATION://邀请
                    if (taskBean.getReceive() == 0 || taskBean.getReceive() == 2) {
                        Intent intent = new Intent(getActivity(), InvitationActivity.class);
                        intent.putExtra("imageUserPath", imageUserPath);
                        launchActivity(intent);
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    }
                    break;
                case Constant.TASK_EVENT_INNER_JUMP://APP内跳转链接
                    if (taskBean.getReceive() == 0) {
                        if (taskBean.getDuration() == 0) {
                            taskFinished(position);//APP内跳转链接  计时为0；
                        } else if (taskBean.getDuration() > 0) {
                            taskTimerInit(position, taskBean.getDuration());//APP内跳转链接计时
                        }
                        String webUrl = taskBean.getBtnUrl();
                        Intent intent5 = new Intent(getActivity(), WebViewActivity.class);
                        intent5.putExtra("URL", webUrl);
                        launchActivity(intent5);
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    } else if (taskBean.getReceive() == 2) {
                        String webUrl = taskBean.getBtnUrl();
                        Intent intent5 = new Intent(getActivity(), WebViewActivity.class);
                        intent5.putExtra("URL", webUrl);
                        launchActivity(intent5);
                    }
                    break;
                case Constant.TASK_EVENT_OUTER_JUMP://第三方浏览器跳转链接
                    if (taskBean.getReceive() == 0) {
                        if (taskBean.getDuration() == 0) {
                            taskFinished(position);//第三方浏览器跳转链接  计时为0；
                        } else if (taskBean.getDuration() > 0) {
                            taskTimerInit(position, taskBean.getDuration());//第三方浏览器跳转链接计时
                        }
                        launchActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(taskBean.getBtnUrl())));
                    } else if (taskBean.getReceive() == 1) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    } else if (taskBean.getReceive() == 2) {
                        launchActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(taskBean.getBtnUrl())));
                    }

                    break;
                case Constant.TASK_EVENT_SHARE_TO_GROUP://分享到微信群
                    // 判断是否安装了微信客户端
                    if (api != null && !api.isWXAppInstalled()) {
                        showMessage("您还未安装微信客户端");
                        return;
                    }
                    if (taskBean.getReceive() == 0 && isNotReClick()) {
                        shareWeChat(position, 0, taskBean.getDuration());
                    } else if (taskBean.getReceive() == 1 && isNotReClick()) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    } else if (taskBean.getReceive() == 2 && isNotReClick()) {
                        shareWeChat(position, 2, -1);
                    }
                    break;
                case Constant.TASK_EVENT_SHARE_TO_COF://分享到朋友圈
                    // 判断是否安装了微信客户端
                    if (api != null && !api.isWXAppInstalled()) {
                        showMessage("您还未安装微信客户端");
                        return;
                    }
                    /*分享到朋友圈*/
                    if (taskBean.getReceive() == 0 && isNotReClick()) {
                        if (taskBean.getDuration() != -1) {
                            shareWeChatMoments(position, 0, taskBean.getDuration());
                        } else {
                            shareWeChatMoments(position, 0, 5);
                        }
                    } else if (taskBean.getReceive() == 1 && isNotReClick()) {
                        if (mPresenter != null)
                            mPresenter.taskFinish(position, System.currentTimeMillis(), taskBean.getId());
                    } else if (taskBean.getReceive() == 2 && isNotReClick()) {
                        shareWeChatMoments(position, 2, -1);
                    }

                    break;
                case Constant.TASK_EVENT_PLAY_JUMP://闲玩游戏
                    Intent intent5 = new Intent(getActivity(), PlayWebViewActivity.class);
                    //http://192.168.1.155/h5apptest/
                    //https://h5.51xianwan.com/try/try_list_plus_browser.aspx?ptype=2&deviceid=8883300242xw&appid=1010&appsign=1000&keycode=07688c3e9a783b245e593116b42e6df4
//                    intent5.putExtra("URL", taskBean.getBtnUrl());
                    Timber.d("=============闲玩游戏URL" + taskBean.getBtnUrl());
                    intent5.putExtra("URL", taskBean.getBtnUrl());
                    launchActivity(intent5);
                    break;
            }
        });

    }

    /**
     * 如果连续点击的间隔过快  则不做处理
     */
    public boolean isNotReClick() {
        if (System.currentTimeMillis() - currTime > 2000) {
            currTime = System.currentTimeMillis();
            return true;
        } else {
            currTime = System.currentTimeMillis();
            return false;
        }
    }

    @Subscriber(tag = EventBusTags.WeChat_Bind)
    public void onReceiveWeChatBind(@NonNull WeChatBindEvent weChatBindEvent) {
        if (mPresenter != null)
            mPresenter.wxBinding(weChatBindEvent.getCode(), weChatBindEvent.getPosition());
//        Timber.d("========position：" + weChatBindEvent.getPosition());
    }


    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(getActivity()).create();
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
        ArmsUtils.makeText(getActivity().getApplicationContext(), message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    /**
     * 任务已完成(未领取奖励)
     *
     * @param position 任务位置
     */
    @Override
    public void taskFinished(int position) {
        if (mTaskAdapter != null) {
            mTaskAdapter.updateTaskState(position, 1);
        }
    }

    /**
     * 领取奖励成功
     *
     * @param position 任务位置
     * @param reward   奖励金额
     */
    @Override
    public void receiveRewardSuccess(int position, int reward) {
        if (reward > 0) {
            EventBus.getDefault().post(
                    new RewardMainEvent.Builder()
                            .newRewardType("taskReawrd")
                            .newRewardGoid(reward)
                            .build(),
                    EventBusTags.TAG_COIN_REWARD);
        }
        if (mTaskAdapter != null) {
            mTaskAdapter.updateTaskState(position, 2);
        }
    }

    /**
     * 下载成功回调
     *
     * @param filePath 文件位置
     */
    @Override
    public void downloadCallBack(@NonNull String filePath) {
        imageUserPath = filePath;
        Timber.d("==shareTask  downloadCallBack下载成功：" + imageUserPath);
    }

    /**
     * 取出用户分享的   数据
     *
     * @param weChatShareBean 分享数据
     */
    @Override
    public void updateShareData(WeChatShareBean weChatShareBean) {
        String[] images = new String[]{};
        images = weChatShareBean.getInviteImages().toArray(images);
        inviteCode = weChatShareBean.getInviteCode();
        mobOneKeyShare.setShareContent(new ShareBean.Builder()
                .title(weChatShareBean.getInviteTitle())
                .content(weChatShareBean.getInviteContent())
                .imgUrls(images)
                .imagePath(imageUserPath)
                .imageUrl(weChatShareBean.getInviteImage())
                .pageUrl(weChatShareBean.getInviteURL())
                .weChatShareType(weChatShareBean.getSharingWechat())
                .weChatMomentsShareType(weChatShareBean.getSharingWechatCircle())
                .isUserHead(true)
                .inviteCode(weChatShareBean.getInviteCode())
                .wxAppInviteImage(weChatShareBean.getWxAppInviteImage())
                .create());
    }

    /**
     * 微信好友分享
     *
     * @param position 位置
     * @param type     分享后是否改变按钮状态
     */
    public void shareWeChat(int position, int type, int duration) {
        if (mobOneKeyShare.getShareContent() == null) {
            showMessage(getResources().getString(R.string.not_net));
            return;
        }
        if (type == 0) {
            if (duration == 0) {
                taskFinished(position);//微信好友分享  计时为0；
            } else if (duration > 0) {
                taskTimerInit(position, duration);//微信好友分享计时
            }
        }
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
//        weChat.setPlatformActionListener(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                if (type == 0 && mTaskAdapter != null) {
//                    mTaskAdapter.updateTaskState(position, 1);
//                }
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//            }
//        });

        //打开小程序  正式版
//        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
//        req.userName = "gh_e8b3dcc04714"; // 填小程序原始id
//        req.path = "pages/new/new?id=111";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
//        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;// 可选打开 开发版，体验版和正式版 // 正式版:0，测试版:1，体验版:2
//        api.sendReq(req);
        mobOneKeyShare.shareSimple(weChat, api);

        //分享体验版小程序
//        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
//        miniProgramObj.webpageUrl = "http://www.qq.com"; // 兼容低版本的网页链接
//        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW;// 正式版:0，测试版:1，体验版:2
//        miniProgramObj.userName = "gh_e8b3dcc04714";     // 小程序原始id
//        miniProgramObj.path = "pages/index/index?inviteCode=" + inviteCode;            //小程序页面路径
//        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
//        msg.title = "小程序消息Title";                    // 小程序消息title
//        msg.description = "小程序消息Desc";               // 小程序消息desc
//        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.icon_error_empty_disciple_earning); // 间接调用
//        msg.thumbData = Bitmap2Bytes(bitmap);                      // 小程序消息封面图片，小于128k
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("webpage");
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
//        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 微信朋友圈分享
     *
     * @param position 位置
     * @param type     是否计时
     * @param duration 计时多少
     */
    public void shareWeChatMoments(int position, int type, int duration) {
        if (mobOneKeyShare.getShareContent() == null) {
            showMessage(getResources().getString(R.string.not_net));
            return;
        }
        if (type == 0) {
            if (duration == 0) {
                taskFinished(position);//微信朋友圈分享  计时为0；
            } else if (duration > 0) {
                taskTimerInit(position, duration);//微信朋友圈分享计时
            }
        }
        Platform weChat = ShareSDK.getPlatform(WechatMoments.NAME);
        mobOneKeyShare.shareSimple(weChat, api);
    }

    /**
     * 刷新完成： 刷新销毁掉
     */
    @Override
    public void finishRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh(0);
        }
    }


    /**
     * 更新签到记录
     *
     * @param signRecordBean 签到记录
     */
    @Override
    public void updateSignRecord(@NonNull SignRecordBean signRecordBean) {
        //清空签到天数
        signDays = 0;

        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (listViewTask != null) {
            listViewTask.setVisibility(View.VISIBLE);
        }

        if (signRecordBean.isToday()) {
            btnSign.setEnabled(false);
            btnSign.setText(R.string.btn_signed);
            btnSign.setTextColor(Color.parseColor("#999999"));
        } else {
            btnSign.setEnabled(true);
            btnSign.setText(R.string.btn_sign_right_now);
            btnSign.setTextColor(Color.parseColor("#ffffff"));
        }

        List<SignStateBean> signStateList = signRecordBean.getList();

        tvOneDay.setText(MessageFormat.format("{0}天", signStateList.get(0).getDays()));
        tvSignFirstDay.setText(String.valueOf(signStateList.get(0).getReward()));
        if (signStateList.get(0).getReceive() == 2) {
            tvSignFirstDay.setSelected(true);
            tvSignFirstDay.setText("");
            signDays = 1;

            viewOne.setSelected(true);
        } else {
            tvSignFirstDay.setSelected(false);
            viewOne.setSelected(false);
        }

        tvTwoDay.setText(MessageFormat.format("{0}天", signStateList.get(1).getDays()));
        tvSignSecondDay.setText(String.valueOf(signStateList.get(1).getReward()));
        if (signStateList.get(1).getReceive() == 2) {
            tvSignSecondDay.setSelected(true);
            tvSignSecondDay.setText("");
            signDays = 2;

            viewTwo.setSelected(true);
        } else {
            tvSignSecondDay.setSelected(false);
            viewTwo.setSelected(false);
        }

        tvThreeDay.setText(MessageFormat.format("{0}天", signStateList.get(2).getDays()));
        if (signStateList.get(2).getReceive() == 2) {
            tvSignThirdDay.setSelected(true);
            tvSignThirdDay.setText("");
            signDays = 3;

            viewThree.setSelected(true);
        } else {
            tvSignThirdDay.setSelected(false);
            viewThree.setSelected(false);
        }

        tvFourDay.setText(MessageFormat.format("{0}天", signStateList.get(3).getDays()));
        tvSignForthDay.setText(String.valueOf(signStateList.get(3).getReward()));
        if (signStateList.get(3).getReceive() == 2) {
            tvSignForthDay.setSelected(true);
            tvSignForthDay.setText("");
            signDays = 4;

            viewfour.setSelected(true);
        } else {
            tvSignForthDay.setSelected(false);
            viewfour.setSelected(false);
        }

        tvFiveDay.setText(MessageFormat.format("{0}天", signStateList.get(4).getDays()));
        tvSignFifthDay.setText(String.valueOf(signStateList.get(4).getReward()));
        if (signStateList.get(4).getReceive() == 2) {
            tvSignFifthDay.setSelected(true);
            tvSignFifthDay.setText("");
            signDays = 5;

            viewfive.setSelected(true);
        } else {
            tvSignFifthDay.setSelected(false);
            viewfive.setSelected(false);
        }

        tvSixDay.setText(MessageFormat.format("{0}天", signStateList.get(5).getDays()));
        tvSignSixthDay.setText(String.valueOf(signStateList.get(5).getReward()));
        if (signStateList.get(5).getReceive() == 2) {
            tvSignSixthDay.setSelected(true);
            tvSignSixthDay.setText("");
            signDays = 6;

            viewsix.setSelected(true);
        } else {
            tvSignSixthDay.setSelected(false);
            viewsix.setSelected(false);
        }

        tvSevenDay.setText(MessageFormat.format("{0}天", signStateList.get(6).getDays()));
        if (signStateList.get(6).getReceive() == 2) {
            tvSignSeventhDay.setSelected(true);
            tvSignSeventhDay.setText("");
            signDays = 7;
        } else {
            tvSignSeventhDay.setSelected(false);
        }

    }

    /**
     * 更新任务列表
     *
     * @param taskListBean 任务列表
     */
    @Override
    public void updateTaskList(@NonNull TaskListBean taskListBean) {
        if (mTaskAdapter != null) {
            mTaskAdapter.addData(taskListBean.getDaily(), taskListBean.getNovice());
        }

        if (brh != null) {
            brh.setBackgroundColor(getResources().getColor(R.color.color_C70000));
        }
    }

    /**
     * 更新今天签到状态（成功）
     *
     * @param reward 签到奖励
     */
    @Override
    public void updateTodaySign(int reward) {
        if (reward > 0) {
            EventBus.getDefault().post(
                    new RewardMainEvent.Builder()
                            .newRewardType("taskReawrd")
                            .newRewardGoid(reward)
                            .build(),
                    EventBusTags.TAG_COIN_REWARD);
        }

        btnSign.setEnabled(false);
        btnSign.setText(R.string.btn_signed);
        btnSign.setTextColor(Color.parseColor("#999999"));
        switch (signDays) {
            case 0:
                tvSignFirstDay.setSelected(true);
                tvSignFirstDay.setText("");
                signDays = 1;

                viewOne.setSelected(true);
                break;
            case 1:
                tvSignSecondDay.setSelected(true);
                tvSignSecondDay.setText("");
                signDays = 2;

                viewTwo.setSelected(true);
                break;
            case 2:
                tvSignThirdDay.setSelected(true);
                tvSignThirdDay.setText("");
                signDays = 3;

                viewThree.setSelected(true);
                break;
            case 3:
                tvSignForthDay.setSelected(true);
                tvSignForthDay.setText("");
                signDays = 4;

                viewfour.setSelected(true);
                break;
            case 4:
                tvSignFifthDay.setSelected(true);
                tvSignFifthDay.setText("");
                signDays = 5;

                viewfive.setSelected(true);
                break;
            case 5:
                tvSignSixthDay.setSelected(true);
                tvSignSixthDay.setText("");
                signDays = 6;

                viewsix.setSelected(true);
                break;
            case 6:
                tvSignSeventhDay.setSelected(true);
                tvSignSeventhDay.setText("");
                signDays = 7;
                break;
        }
    }

    @Override
    public void onDestroy() {
        Glide.with(this).onDestroy();

        super.onDestroy();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        if (api != null) {
            api.detach();
        }
        if (mobOneKeyShare != null) {
            mobOneKeyShare.destroy();
        }

        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }

    }

    /**
     * 初始化轮播图控件
     */
    private void initAutoScrollViewPager() {
        autoScrollViewPager.setAdapter(mPagerAdapter);

        // viewPagerIndicator.setViewPager(autoScrollViewPager);
        // viewPagerIndicator.setSnap(true);

        autoScrollViewPager.setScrollFactgor(10); // 控制滑动速度
        autoScrollViewPager.setOffscreenPageLimit(5); //设置缓存屏数
        autoScrollViewPager.startAutoScroll(3000);  //设置间隔时间
        autoScrollViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                showSelectScrollImage(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 当前滑动的轮播图对应底部的标识
     *
     * @param position 当前位置
     */
    private void showSelectScrollImage(int position) {
        if (position < 0 || position >= mScrollImageViews.size()) return;
        if (mScrollImageViews != null) {
            for (ImageView iv : mScrollImageViews) {
                iv.setImageResource(R.drawable.icon_indicator_normal);
            }
            mScrollImageViews.get(position).setImageResource(R.drawable.icon_indicator_selected);
        }
    }

    /**
     * 轮播图底部的滑动的下划线
     *
     * @param size 轮播图数量
     */
    private void addScrollImage(int size) {
//        autoScrollIndicator.removeAllViews();
        mScrollImageViews.clear();

        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(getActivity());
            iv.setPadding(10, 0, 10, 20);
            if (i != 0) {
                iv.setImageResource(R.drawable.icon_indicator_normal);
            } else {
                iv.setImageResource(R.drawable.icon_indicator_selected);
            }
            iv.setLayoutParams(new ViewGroup.LayoutParams(40, 40));
//            autoScrollIndicator.addView(iv);// 将图片加到一个布局里
            mScrollImageViews.add(iv);
        }
    }

    /**
     * 轮播图适配器
     */
    PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mScrollImageViews.size() + 1;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
            if (position == 0) {
                Timber.d("=======position==" + position);
                bannerView = new BannerView(getActivity(), ADSize.BANNER, Constant.GDT_APP_ID, Constant.GDT_AD_ID_TASK_BANNER);
                bannerView.setRefresh(30);
                bannerView.setADListener(new AbstractBannerADListener() {
                    @Override
                    public void onNoAD(AdError adError) {
                        Timber.d("banner==adError==失败");
                    }

                    @Override
                    public void onADExposure() {
                        super.onADExposure();
                    }

                    @Override
                    public void onADReceiv() {
                        Timber.d("banner==onADReceiv==成功");
                    }
                });
                bannerView.loadAD();
                LinearLayout adLayout = new LinearLayout(getActivity());
                adLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                adLayout.setGravity(Gravity.CENTER);
                adLayout.addView(bannerView);
                container.addView(adLayout);
                return adLayout;
            } else {
                Timber.d("=======position2==" + position);
                View view = getLayoutInflater().inflate(R.layout.include_image, null);
                ImageView ivBanner = view.findViewById(R.id.bannerImg);

                GlideArms.with(ivBanner.getContext()).load(bannerBeans.get(position - 1).getImg())
                        .centerCrop().error(R.drawable.icon_task_banner).into(ivBanner);
                container.addView(view);

                view.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(bannerBeans.get(position - 1).getEvent())) {
                        skipType(bannerBeans.get(position - 1).getEvent(), bannerBeans.get(position - 1).getUrl());
                    }
                });
                return view;
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    };


    public void skipType(String type, String url) {
        switch (type) {
            case "innerJump":  //内部跳转
                Intent innerIntent = new Intent(getActivity(), WebViewActivity.class);
                innerIntent.putExtra("URL", url);
                startActivity(innerIntent);
                break;
            case "outnerJump": //外部跳转
                if (!TextUtils.isEmpty(url))
                    launchActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;
            case "invitation": //好友邀请
                launchActivity(new Intent(getActivity(), InvitationActivity.class));
                break;
            case "withdrawals": //快速提现
                launchActivity(new Intent(getActivity(), QuickCashActivity.class));
                break;
            case "activityPage":  //活动公告页
                Intent activityIntent = new Intent(getActivity(), MessageCenterActivity.class);
                activityIntent.putExtra("tab", 0);
                launchActivity(activityIntent);
                break;
            case "myMsgPage":   //我的消息页面
                Intent messageIntent = new Intent(getActivity(), MessageCenterActivity.class);
                messageIntent.putExtra("tab", 1);
                launchActivity(messageIntent);
                break;
            default:
                break;
        }
    }


    @Override
    public void banner(List<BannerBean> bannerBean) {
        Timber.d("============成功");
        bannerBeans = bannerBean;
        addScrollImage(bannerBean.size());
        initAutoScrollViewPager();
    }

    /**
     * 显示错误页面
     */
    @Override
    public void showErrorView() {
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
        }
        if (listViewTask != null) {
            listViewTask.setVisibility(View.GONE);
        }
        //网络异常
        if (tvError != null) {
            tvError.setText(R.string.error_network);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    tvError.getContext().getResources().getDrawable(R.drawable.icon_error_network),
                    null, null);
        }
        if (brh != null) {
            brh.setBackgroundColor(getResources().getColor(R.color.color_f2));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (autoScrollViewPager != null)
            autoScrollViewPager.startAutoScroll();
//        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        if (mTimerUtils != null) {
            mTimerUtils.stop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (autoScrollViewPager != null)
            autoScrollViewPager.stopAutoScroll();
//        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {  //如果当前页面隐藏了 则暂停轮播图
            if (autoScrollViewPager != null) {
                autoScrollViewPager.stopAutoScroll();
                Timber.d("====================hidden       " + "不可见隐藏");
            }
        } else {
            if (autoScrollViewPager != null) {
                autoScrollViewPager.startAutoScroll();

                Timber.d("====================hidden       " + "可见显示");
            }
        }
    }

    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            dbUpdateUserAvatar(list.get(0));
            Timber.d("=db=    TaskCenterFragment - UserInfo - query 成功");
        } else {
            Timber.d("=db=    TaskCenterFragment - UserInfo - query 成功");
        }
    }

    /**
     * @param userInfoBean 获取到数据库的用户信息后，更新界面
     */
    public void dbUpdateUserAvatar(UserInfoBean userInfoBean) {
        if (userInfoBean != null && !TextUtils.isEmpty(userInfoBean.getAvatar())) {
            if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME) && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
                imageUserPath = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
            } else {
//                    Timber.d("==shareMine  :" + "有登录，无图片");
                if (mPresenter != null)
                    mPresenter.requestPermission(userInfoBean.getAvatar());
            }
//            else {
//                Timber.d("==shareTask  userInfoBean.getAvatar():      null");
//            }
        }
    }

    /**
     * 任务计时；处理的事件：内部跳转url,外部跳转url,分享微信好友，分享微信朋友圈；
     *
     * @param position 位置
     * @param time     计时的时间（秒）
     */
    private void taskTimerInit(int position, int time) {
        if (mTimerUtils != null) {
            mTimerUtils = null;
        }
        mTimerUtils = new TimerUtils(new TimerUtils.OnTimerListener() {
            @Override
            public void onTimerFail() {
//                Timber.d("==tasktime  onTimerFail");
                if (mTimerUtils != null) {
                    mTimerUtils = null;
                }
            }

            @Override
            public void onTimerComplete() {
//                Timber.d("==tasktime  onTimerComplete");
                taskFinished(position);//计时任务完成；
                if (mTimerUtils != null) {
                    mTimerUtils = null;
                }
            }
        });
        mTimerUtils.start(time);//计时单位秒；
    }
}
