package com.dzkandian.mvp.video.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.bdtt.sdk.wmsdk.AdSlot;
import com.bdtt.sdk.wmsdk.TTAdManager;
import com.bdtt.sdk.wmsdk.TTAdNative;
import com.bdtt.sdk.wmsdk.TTFeedAd;
import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.player.play.AssistPlayer;
import com.dzkandian.common.player.play.DataInter;
import com.dzkandian.common.player.play.ReceiverGroupManager;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.animator.AnimatorPath;
import com.dzkandian.common.uitls.animator.PathEvaluator;
import com.dzkandian.common.uitls.animator.PathPoint;
import com.dzkandian.common.uitls.ttAd.TTAdManagerHolder;
import com.dzkandian.common.widget.MobOneKeyShare;
import com.dzkandian.common.widget.SoundPoolManager;
import com.dzkandian.common.widget.arcprogress.AnswerChartView;
import com.dzkandian.common.widget.barrageview.DanmuContainerView;
import com.dzkandian.common.widget.barrageview.FlutteringLayout;
import com.dzkandian.common.widget.barrageview.KeyboardStateObserver;
import com.dzkandian.common.widget.explosion.ExplosionField;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.common.widget.recyclerview.LinearLayoutManagerWrapper;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.common.ui.activity.MainActivity;
import com.dzkandian.mvp.news.ui.activity.NewsCommentActivity;
import com.dzkandian.mvp.video.contract.VideoPlayContract;
import com.dzkandian.mvp.video.di.component.DaggerVideoPlayComponent;
import com.dzkandian.mvp.video.di.module.VideoPlayModule;
import com.dzkandian.mvp.video.presenter.VideoPlayPresenter;
import com.dzkandian.mvp.video.ui.adapter.DanmuVideoAdapter;
import com.dzkandian.mvp.video.ui.adapter.VideoPlayAdapter;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.ShareBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.news.BarrageBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsDanmuBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.video.VideoBean;
import com.dzkandian.storage.bean.video.VideoRecordBean;
import com.dzkandian.storage.event.DanmuEvent;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.kk.taurus.playerbase.utils.NetworkUtils;
import com.kk.taurus.playerbase.widget.BaseVideoView;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.impl.cookie.DateUtils;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class VideoDetailActivity extends BaseActivity<VideoPlayPresenter> implements VideoPlayContract.View, OnPlayerEventListener, OnReceiverEventListener, View.OnClickListener {

    @BindView(R.id.tasks_view)
    AnswerChartView tasks_view;

    @BindView(R.id.base_video)
    BaseVideoView baseVideoView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.iv_reward)
    ImageView ivReward;

    @BindView(R.id.tv_reward)
    TextView tvReward;

    @BindView(R.id.fl_reward)
    FrameLayout flReward;

    /**
     * 评论布局切换相关
     */

    @BindView(R.id.fl_video_comment)
    ViewGroup mSceneRootView;


    //弹幕相关
    @BindView(R.id.danmuContainerView)
    DanmuContainerView danmuContainerView;   //弹幕布局

    @BindView(R.id.flutteringLayout)
    FlutteringLayout flutteringLayout;   //弹幕点赞动画

    @BindView(R.id.rlayout_adview)
    RelativeLayout rLayoutAdView;
    @BindView(R.id.iv_adview_image)
    ImageView ivAdView;
    @BindView(R.id.tv_adview_countdown)
    TextView tvAdViewCount;
    @BindView(R.id.tv_adview_title)
    TextView tvAdViewTitle;
    private List<NativeResponse> adHorizontalList;    //播放完成的  横版  百度广告数据列表
    private BaiduNative adHorizontalNative;            //播放完成的  横版  百度广告
    private RequestParameters adHorizontalRequestParameters;//播放完成的  横版  百度广告参数设置
    private boolean isHasAdHorizontal;                      //播放完成的  横版  百度广告是否有显示  唯一标示
    private boolean isPauseAdHorizontal;                    //播放完成的  横版  百度广告是否有跑暂停方法  唯一标示
    private int countDownHorizontal = 5;                    //播放完成的  横版  百度广告  倒计时的秒数；

    private boolean mOpenThread; //是否开启线程
    private String isShowComment;//是否显示弹幕
    private List<BarrageBean> mBarrageBean;  //弹幕相关所有数据
    private int danmuCurrPosition;       //弹幕当前点击的位置
    private int position = 0; //弹幕显示位置
    private PlayTimeDanmu mPlayTimeDanmu;
    private String mInputString = ""; //用户输入好的字符
    private InputMethodManager imm;
    private DanmuVideoAdapter mDanmuAdapter;//弹幕Adapter
    private String mComment;
    private long mCommentTime; //每个视频至评论中间的时长
    private boolean notComment; //是否有评论
    private String mLastId = "";  //弹幕的最后一条ID


    private int arcProgressScale = 0;//进度条当前刻度
    private int mRecordScale = 0;//当前资讯阅读奖励的进度
    private int mReadGoldPercent = 0;//后台返回视频奖励结束进度(0-99)

    /**
     * 动画相关
     */
    private AnimatorSet squareAnimSet, returnAnimSet;
    private long onClickbtSendTime;//上一次点击发送的时间
    private long onClickCollectionTime;//上一次点击收藏的时间
    private long onClickCommentTime;//上一次点击开启弹幕的时间

    private int mShowCount;// 第一批弹幕显示完 下一批出来
    private Timer mTimer; //弹幕运行线程
    private int mDelayedPotisiton; //延时次数  每次为2秒
    private boolean mShow;   //进度条转满 一直显示评论布局


    /*获取Adapter传过来的相关值*/
    private String VideoUrl;   //视频播放url
    private String mImageUrl;   //视频图片url
    private String Title;      //视频标题
    private String mType;       //栏目
    private String mCurrId = "";     //当前视频id
    private String mCanVideoShare; //视频是否可以分享
    private String mWebShareUrl;   //和后台交换返回的分享链接

    private String mVideoShareTitle;//视频分享标题
    private String mVideoShareContent;//视频分享描述内容

    private String mWebUpApp;         //网页打开APP进入视频页
    private String mTextSize;//字体大小

    private VideoPlayAdapter videoPlayAdapter;//适配器
    private LoadingProgressDialog loadingProgressDialog;

    private ExplosionField explosionField;//爆裂效果
    private boolean isOneExplode = true;//是否第一次爆裂

    private int completeCount = 0;//同一视频只能领取两次

    private static IWXAPI api; // IWXAPI 是第三方app和微信通信的openapi接口
    private String imageUserPath = "";
    private MobOneKeyShare oneKeyShare;

    //    private List<Integer> mADViewPositions = new ArrayList<>();//记录广告实时位置列表
    private int videoGoldCircles = 2;//后台返回的视频金币转圈数

    Runnable runIvFinish, runIvShareRight;////返回键，分享按钮隐藏Runnable
    //    private boolean isShowPauseVideo;//触发滑动事件要不要暂停监听，默认是不暂停监听的；在视频暂停或者播放完成的时候暂停监听；
    private LinearLayoutManagerWrapper linearLayoutManager;

    //    private Visualizer mVisualizer;
    private ReceiverGroup mReceiverGroup;
    private boolean isLandscape;//是否全屏播放；
    private boolean isPauseTime;//是否暂停计时（播放完成or阅读奖励领取成功暂停计时）
    private boolean isRefresh = true;//是否刷新
//    private boolean isClickRefresh = false;//是否点击刷新（下一个、子项点击事件）

    private long onClickShareTime;//上一次点击分享的时间
    private long refreshVideoDetailLastTimes;//视频详情页 刷新 的上一次时间；
    private long loadMoreVideoDetailLastTimes;//视频详情页 加载 的上一次时间；

    private BaiduNative baiduNative;//百度广告
    private NativeExpressAD gdtNative; //广点通广告
    private RequestParameters requestParameters;//百度广告参数设置
    private List<Integer> baiduAdPosition = new ArrayList<>();//百度广告在列表中的位置集合
    private List<Integer> gdtAdPosition = new ArrayList<>();//广点通在列表中的位置集合
    private List<Integer> csjAdPosition = new ArrayList<>();//穿山甲在列表中的位置集合
    private boolean hasCompleteBaiduAd = false;
    private boolean hasCompleteGDTAd = false;
    private boolean hasCompleteCSJAd = false;

    private Scene mSceneCommentShare;
    private Scene mSceneCommentSend;
    private EditText etComment;
    private TextView tvCommentCount;
    private ImageView ivCommentBarrage;
    private ImageView ivVideoCommentCollection;
    private ImageView ivCommentShare;

    private EditText etInputComment;
    private Button btnSend;

    private boolean isShowKeyBoare; //有没有显示软键盘
    private int mHideSoftKeyHeight = -1;   //是否要减虚拟键

    private List<VideoBean> mVideoList = new ArrayList<>();

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerVideoPlayComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .videoPlayModule(new VideoPlayModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_video_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);//微信api
        oneKeyShare = new MobOneKeyShare(this);

        //是否显示弹幕
        isShowComment = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_VIDEO_COMMENT); //获取视频弹幕是否打开

        tasks_view.setClickable(true);
        adHorizontalList = new ArrayList<>();

        Uri uri = getIntent().getData();
        if (uri != null) {
            getWebValue(uri);
        } else {
            getIntentValue(getIntent());//获取Adapter传过来的值
        }
        initRecyclerView();//初始化RecyclerView
        init();
        adHorizontalInit();
        initSceneAnimation();

        queryDeviceInfo();//进入界面后 获取数据库

        initAnimator();

        initShare();   //初始化分享相关

        initRefresh(); //初始化刷新相关

        //弹幕
        getComment();

        explosionField = ExplosionField.attach2Window(this);//爆裂效果所需

        /*
         * 实时监听软键盘是否显示
         */
        KeyboardStateObserver.getKeyboardStateObserver(this).
                setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
                    @Override
                    public void onKeyboardShow() {
                        //软键盘弹出
                        isShowKeyBoare = true;
                    }

                    @Override
                    public void onKeyboardHide() {
                        //软键盘隐藏
                        mHideSoftKeyHeight = getKeyboardHeight(); //隐藏状态下 软键盘的值
                        isShowKeyBoare = false;
                    }
                });

    }

    //初始化分享相关
    private void initShare() {
        mCommentTime = System.currentTimeMillis();
        //评论布局 分享按钮是否能分享
        if (TextUtils.equals(mCanVideoShare, "1")) {
            ivCommentShare.setEnabled(true);
        } else {
            ivCommentShare.setEnabled(false);
        }
    }

    //初始化刷新控件
    private void initRefresh() {
        //        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {//加载
                if (isInternet()) {
//                    isClickRefresh = false;
                    if (mPresenter != null) {
                        mPresenter.getVideoList(false, false, mType, mCurrId);
                    }
                } else {
                    if (System.currentTimeMillis() - loadMoreVideoDetailLastTimes > 2000) {
                        loadMoreVideoDetailLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                    }
                    finishLoadMore();//隐藏加载更多
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {//刷新
                if (videoPlayAdapter != null && videoPlayAdapter.getVideoListSize() > 0) {
                    videoPlayAdapter.removeAllItem();
                }
                if (isInternet()) {
                    refreshLayout.setNoMoreData(false);
                    if (mPresenter != null) {
                        mPresenter.getVideoList(true, false, mType, mCurrId);
                    }
                    adHorizontalfetch();//刷新请求广告
                } else {
                    if (System.currentTimeMillis() - refreshVideoDetailLastTimes > 2000) {
                        refreshVideoDetailLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                        refreshFailed();
                    }
                    finishRefresh();//隐藏刷新
                }
            }
        });
        refreshLayout.autoRefresh();//第一次自动刷新

    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (imm != null && imm.isActive()) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    /**
     * 弹幕获取成功
     *
     * @param newBarrageBean 弹幕数据
     */
    @Override
    public void loadBarrage(NewBarrageBean newBarrageBean) {

        if (Integer.parseInt(newBarrageBean.getCmtCount()) > 999) {
            tvCommentCount.setVisibility(View.VISIBLE);
            tvCommentCount.setText("999+");
        } else if (Integer.parseInt(newBarrageBean.getCmtCount()) > 0) {
            tvCommentCount.setVisibility(View.VISIBLE);
            tvCommentCount.setText(newBarrageBean.getCmtCount());
        } else if (Integer.parseInt(newBarrageBean.getCmtCount()) == 0) {
            notComment = true;
        }

        mBarrageBean.addAll(newBarrageBean.getBarrageBeans());

        mOpenThread = true;
        if (TextUtils.equals(isShowComment, "videoHide")) {
            ivCommentBarrage.setSelected(false);
            isShowComment(0);
        } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "videoShow")) {
            ivCommentBarrage.setSelected(true);
            isShowComment(1);
        }
        if (newBarrageBean.getCollection() == 1) {
            ivVideoCommentCollection.setSelected(true);
        } else {
            ivVideoCommentCollection.setSelected(false);
        }
    }

    /**
     * 弹幕相关
     */
    public void getComment() {
        mBarrageBean = new ArrayList<>();
        mDanmuAdapter = new DanmuVideoAdapter(this.getApplicationContext());
        danmuContainerView.setAdapter(mDanmuAdapter);
        danmuContainerView.setGravity(DanmuContainerView.GRAVITY_FULL);

        requDanmu();  //请求后台弹幕接口

    }

    /**
     * 接收弹幕点击事件 显示点赞动画
     */
    @Subscriber(tag = EventBusTags.TAG_VIDEO_ANIMATION_THUBMS_UP)
    private void commentThubmsUpAnimation(DanmuEvent event) {
        if (flutteringLayout != null) {
            if (isShowKeyBoare) {
                if (mHideSoftKeyHeight == 0) {
                    flutteringLayout.updateDanmuView(event.getViewX(), event.getViewY() + getKeyboardHeight()); //设置爱心出来坐标位置
                } else if (mHideSoftKeyHeight > 0) {
                    int y = event.getViewY() + getKeyboardHeight() - getNavigationBarHeight(this);
//                    Timber.d("=======================点击软键盘的高度" + getKeyboardHeight() + "      虚拟键的高度" + getNavigationBarHeight(this) + "     " + y);
                    flutteringLayout.updateDanmuView(event.getViewX(), y); //设置爱心出来坐标位置
                }

            } else {
                if (mHideSoftKeyHeight == 0) {
                    flutteringLayout.updateDanmuView(event.getViewX(), event.getViewY()); //设置爱心出来坐标位置
                } else if (mHideSoftKeyHeight > 0) {
                    flutteringLayout.updateDanmuView(event.getViewX(), event.getViewY()); //设置爱心出来坐标位置
                }
//                Timber.d("=======================点击软键盘的高度" + getKeyboardHeight() + "      虚拟键的高度" + getNavigationBarHeight(this));

            }
            flutteringLayout.addHeart(); //添加爱心动画
        }
    }

    /**
     * 获取软键盘的高度
     */
    public int getKeyboardHeight() {
        View decorView = VideoDetailActivity.this.getWindow().getDecorView();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        //计算出可见屏幕的高度
        int displayHight = rect.bottom - rect.top;
        //获得屏幕整体的高度
        int hight = decorView.getHeight();
        //获得键盘高度
        return hight - displayHight;
    }

    //获取虚拟按键的高度
    public int getNavigationBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 接收弹幕点击事件 去调用点赞评论接口
     */
    @Subscriber(tag = EventBusTags.TAG_VIDEO_COMMENT_THUBMS_UP)
    private void commentThubmsUp(String position) {
        if (!TextUtils.isEmpty(position) && mPresenter != null && mBarrageBean != null) {
            danmuCurrPosition = Integer.parseInt(position);
            Timber.d("点赞 接收弹幕点击事件 " + danmuCurrPosition);
            mPresenter.commentThumbsUp(mBarrageBean.get(Integer.parseInt(position)).getId(), "video");
        }
    }


    /**
     * 弹幕点赞失败
     */
    @Override
    public void thumbsUpError(boolean type) {
        mBarrageBean.get(danmuCurrPosition).setCanThumbsUp(type);
        if (!TextUtils.isEmpty(mBarrageBean.get(danmuCurrPosition).getThumbsUpCount())) {
            mDanmuAdapter.updateDanmuView(Integer.parseInt(mBarrageBean.get(danmuCurrPosition).getThumbsUpCount())
                    , mBarrageBean.get(danmuCurrPosition).getCanThumbsUp(), false);
        }
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @return
     */
    private static boolean isNavigationBarAvailable() {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        return (!(hasBackKey && hasHomeKey));
    }

    //判断手机是否显示虚拟按键
    public boolean isNavigationBarShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = VideoDetailActivity.this.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            boolean result = realSize.y != size.y;
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(VideoDetailActivity.this).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 弹幕点赞成功
     *
     * @param count 点赞第几次
     */
    @Override
    public void thumbsUpSuccess(int count) {
        Timber.d("===thumbs" + count);
        if (count <= 0) {
            mBarrageBean.get(danmuCurrPosition).setCanThumbsUp(false);
        } else {
            mBarrageBean.get(danmuCurrPosition).setCanThumbsUp(true);
        }

        String thumbsUp = mBarrageBean.get(danmuCurrPosition).getThumbsUpCount();

        if (!TextUtils.isEmpty(thumbsUp)) {
            int thubmsUpCount = Integer.parseInt(thumbsUp);
            thubmsUpCount++;
            Timber.d("点赞成功下标 " + danmuCurrPosition + "        数量多少" + thubmsUpCount);
            mBarrageBean.get(danmuCurrPosition).setThumbsUpCount(String.valueOf(thubmsUpCount));
            mDanmuAdapter.updateDanmuView(thubmsUpCount, mBarrageBean.get(danmuCurrPosition).getCanThumbsUp(), true);
        }
    }


    /**
     * 评论成功
     */
    @Override
    public void commentSuccess() {
        if (!notComment) {  //如果弹幕有数据 正在显示则添加进去
//            NewsDanmuBean newsDanmuBean = new NewsDanmuBean();
//            newsDanmuBean.setContent(mComment);
//            danmuContainerView.addDanmu(newsDanmuBean);
            BarrageBean barrageBean = new BarrageBean();
            barrageBean.setContent(mComment);
            mBarrageBean.add(barrageBean);
        } else {// 如果弹幕为空 则新开线程显示弹幕
//            NewsDanmuBean newsDanmuBean = new NewsDanmuBean();
//            newsDanmuBean.setContent(mComment);
//            danmuContainerView.addDanmu(newsDanmuBean);
            BarrageBean barrageBean = new BarrageBean();
            barrageBean.setContent(mComment);
            mBarrageBean.add(barrageBean);
            mOpenThread = true;
            isShowComment(1);
        }
        clearEditText(); //清空EditText相关内容
        mInputString = "";
        showShareScene();
        hideKeyboard();
        showMessage("发布成功，优质评论将被优先展示");
    }

    @Override
    public void collectionValue(boolean value) {
        if (value) {
            showMessage("已收藏");
            ivVideoCommentCollection.setSelected(true);
        } else {
            showMessage("已取消收藏");
            ivVideoCommentCollection.setSelected(false);
        }
    }


    //显示评论发送布局
    private void showSendScene() {
        //不指定默认就是AutoTransition
        TransitionManager.go(mSceneCommentSend, new ChangeBounds());
    }

    //显示评论分享布局
    private void showShareScene() {
        //不指定默认就是AutoTransition
        TransitionManager.go(mSceneCommentShare, new ChangeBounds());
    }

    public void initSceneAnimation() {
        ViewGroup commentCount = (ViewGroup) getLayoutInflater().inflate(R.layout.scene_comment_share, null);
        ViewGroup commentSubmit = (ViewGroup) getLayoutInflater().inflate(R.layout.scene_comment_send, null);

        btnSend = commentSubmit.findViewById(R.id.bt_send);
        etInputComment = commentSubmit.findViewById(R.id.et_comment);

        etComment = commentCount.findViewById(R.id.et_comment);
        ImageView ivCommentBack = commentCount.findViewById(R.id.iv_comment_back);
        ivCommentShare = commentCount.findViewById(R.id.iv_comment_share);
        ivVideoCommentCollection = commentCount.findViewById(R.id.iv_comment_collection);
        ImageView ivCommentCount = commentCount.findViewById(R.id.iv_comment_count);
        tvCommentCount = commentCount.findViewById(R.id.tv_comment_count);
        ivCommentBarrage = commentCount.findViewById(R.id.iv_comment_barrage);

        etComment.setOnClickListener(this);
        etInputComment.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        ivCommentBack.setOnClickListener(this);
        ivCommentShare.setOnClickListener(this);
        ivVideoCommentCollection.setOnClickListener(this);
        ivCommentCount.setOnClickListener(this);
        ivCommentBarrage.setOnClickListener(this);
        tasks_view.setOnClickListener(this);

        mSceneCommentShare = new Scene(mSceneRootView, commentCount);
        mSceneCommentSend = new Scene(mSceneRootView, commentSubmit);

        /*
         * 切换到开始场景状态
         */
        TransitionManager.go(mSceneCommentShare, new ChangeBounds());

        etComment.addTextChangedListener(textWatcher);
        etInputComment.addTextChangedListener(textWatcher);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tasks_view: //进度条点击事件
                isShowComment(0);
                tasks_view.setClickable(false);
                if (tasks_view.getAlpha() != 0f) {
                    if (tasks_view != null && tasks_view.getVisibility() == View.VISIBLE)
                        showCommentLayout();
                }
                break;
            case R.id.iv_comment_back:   //返回
                killMyself();
                break;
            case R.id.et_comment:       //分享布局中的输入框
                showSendScene();
                etInputComment.requestFocus();
                if (!TextUtils.isEmpty(etInputComment.getText().toString())) {
                    mInputString = etInputComment.getText().toString();
                }
                if (!TextUtils.isEmpty(etComment.getText().toString())) {
                    mInputString = etInputComment.getText().toString();
                }
                imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(etInputComment, 0);
                break;
            case R.id.iv_comment_count:   //评论显示条数 跳至评论2级页面
                if (isLogin()) {
                    Intent intent = new Intent(VideoDetailActivity.this, NewsCommentActivity.class);
                    intent.putExtra("Id", mCurrId);
                    intent.putExtra("Type", mType);
                    intent.putExtra("Title", mVideoShareTitle);
                    intent.putExtra("Url", mWebShareUrl);
                    intent.putExtra("commitFrom", "video");
                    ArmsUtils.startActivity(intent);
                }
                break;
            case R.id.iv_comment_barrage:   //开启关闭弹幕
                if (TextUtils.equals(isShowComment, "videoHide")) { //如果是屏蔽状态  则打开弹幕
                    ivCommentBarrage.setSelected(true);
                    DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_VIDEO_COMMENT, "videoShow");
                    isShowComment = "videoShow";
                    isShowComment(1);
                } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "videoShow")) {
                    mShowCount = 0;
                    position = 0;
                    ivCommentBarrage.setSelected(false);
                    DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_VIDEO_COMMENT, "videoHide");
                    isShowComment = "videoHide";
                    isShowComment(0);
                }
                if (isClickTime(2)) {//0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (TextUtils.equals(isShowComment, "videoHide")) {
                        showMessage("弹幕已关闭");
                    } else {
                        showMessage("弹幕已开启");
                    }
                }
                break;
            case R.id.iv_comment_collection:    //收藏
                if (isClickTime(1) && isLogin()) {//0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (isInternet()) {
                        try {
                            String urlEncode = URLEncoder.encode(mWebShareUrl, "UTF-8");
                            if (mPresenter != null)
                                mPresenter.saveVideoCollection(urlEncode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.iv_comment_share:     //分享
                if (isClickTime(3) && isLogin()) {//0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (isInternet()) {
                        try {
//                                Timber.d("=================编码前的地址" + mWebShareUrl);
                            //编码后的url
                            String urlEncode = URLEncoder.encode(mWebShareUrl, "UTF-8");
                            //获取系统自带的ua
                            String userAgent = System.getProperty("http.agent");
                            if (mPresenter != null)
                                mPresenter.videoShare(urlEncode, userAgent);
//                                Timber.d("=================编码后的地址" + urlEncode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.bt_send:      //评论发送布局 发送按钮
                if (isLogin() && isClickTime(0)) {//0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    mComment = etInputComment.getText().toString();
                    if (TextUtils.isEmpty(mComment)) {
                        showMessage("评论不能为空");
                    } else {
                        if (isInternet()) {
                            //编码后的url
                            try {
                                mComment = mComment.replaceAll("\n", "  ");  //检测换行符 替换成空格
                                String commentString = URLEncoder.encode(mComment, "UTF-8");
                                String urlEncode = URLEncoder.encode(mWebShareUrl, "UTF-8");
                                String reqId = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + (int) ((Math.random() * 9 + 1) * 100000);
                                mCommentTime = System.currentTimeMillis() - mCommentTime;
                                int time = (int) mCommentTime / 1000;
                                ///非空处理
                                commentString = TextUtils.isEmpty(commentString) ? "" : commentString;
                                mCurrId = TextUtils.isEmpty(mCurrId) ? "" : mCurrId;
                                mType = TextUtils.isEmpty(mType) ? "" : mType;
                                urlEncode = TextUtils.isEmpty(urlEncode) ? "" : urlEncode;
                                mVideoShareTitle = TextUtils.isEmpty(mVideoShareTitle) ? "" : mVideoShareTitle;
                                reqId = TextUtils.isEmpty(reqId) ? "" : reqId;
                                if (mPresenter != null)
                                    mPresenter.foundComment(commentString, "video", time, mCurrId, mType, urlEncode, mVideoShareTitle, reqId, 0, 0, "");

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();

                            }
                        } else {
                            showMessage("网络请求失败，请连网后重试");
                        }
                    }
                }
                break;
        }
    }

    /**
     * 是否登录  没登录跳转登录界面
     */
    private boolean isLogin() {
        if (TextUtils.isEmpty(getToken())) {
            launchActivity(new Intent(VideoDetailActivity.this, LoginActivity.class));
            return false;
        }
        return true;
    }

    /**
     * 2次点击相隔
     */
    private boolean isClickTime(int type) {
        switch (type) {
            case 0:  //发送按钮的点击事件
                if (System.currentTimeMillis() - onClickbtSendTime > 2000) {
                    onClickbtSendTime = System.currentTimeMillis();
                    return true;
                }
                return false;
            case 1://收藏按钮的点击事件
                if (System.currentTimeMillis() - onClickCollectionTime > 2000) {
                    onClickCollectionTime = System.currentTimeMillis();
                    return true;
                }
                return false;
            case 2://弹幕按钮的点击事件
                if (System.currentTimeMillis() - onClickCommentTime > 2000) {
                    onClickCommentTime = System.currentTimeMillis();
                    return true;
                }
                return false;
            case 3://分享按钮的点击事件
                if (System.currentTimeMillis() - onClickShareTime > 2000) {
                    onClickShareTime = System.currentTimeMillis();
                    return true;
                }
                return false;
        }
        return false;
    }

    private String getToken() {
        return DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_TOKEN);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() > 0 && (charSequence.toString().substring(0, 1).equals(" ")
                    || charSequence.toString().substring(0, 1).equals("\n"))) {
                clearEditText(); //清空EditText相关内容
            }
            if (etInputComment != null && btnSend != null && !TextUtils.isEmpty(etInputComment.getText().toString())) {
                btnSend.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (etInputComment != null && btnSend != null && TextUtils.isEmpty(etInputComment.getText().toString())) {
                btnSend.setTextColor(getResources().getColor(R.color.color_text_tip));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 300) {  //判断返回码是否是30
            mInputString = data.getStringExtra("reText");
            etInputComment.setText(mInputString);
            etComment.setText(mInputString);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 是否开始弹幕
     */
    public void startDanmu(boolean play) {

        if (play) {
            try {
                if (mTimer == null)
                    mTimer = new Timer();
                if (mPlayTimeDanmu == null)
                    mPlayTimeDanmu = new PlayTimeDanmu();
//                Timber.d("========弹幕多少 showCount" + mShowCount);
//                Timber.d("========弹幕多少 mBarrages.size()" + mBarrages.size());
                mTimer.schedule(mPlayTimeDanmu, 0, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            if (mPlayTimeDanmu != null)
                mPlayTimeDanmu = null;
            if (position > 5) {
                position -= 5;
                if (mShowCount > 5) {
                    mShowCount -= 5;
                }
            }
        }
    }

    /**
     * 开始弹幕线程
     */
    public class PlayTimeDanmu extends TimerTask {

        @Override
        public void run() {

            if (mShowCount >= mBarrageBean.size()) {
                mDelayedPotisiton++;
                if (mDelayedPotisiton >= 3) {
//                    Timber.d("========弹幕多少 9秒");
                    mShowCount = 0;
                    position = 0;
                    mDelayedPotisiton = 0;
                }
            } else {
//                Timber.d("========弹幕多少 2秒");
                if (videoHandler != null) {
                    videoHandler.sendEmptyMessage(3); //开启弹幕线程
                }
            }
        }

    }

    // 0 不显示    1显示

    public void isShowComment(int isClose) {
        switch (isClose) {
            case 0:
                startDanmu(false);
                if (danmuContainerView != null) {
                    danmuContainerView.onDestroy();
                    danmuContainerView.removeAllViews();
                }
                break;
            case 1:
                //成功获取到数据  并弹幕数据大于0才开启线程
                if ((TextUtils.isEmpty(isShowComment) && mOpenThread && mBarrageBean.size() > 0) || (TextUtils.equals(isShowComment, "videoShow")) && mOpenThread && mBarrageBean.size() > 0) {
                    startDanmu(true);
                }

                break;
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler videoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:   //开启弹幕线程
                    if (danmuContainerView != null && mBarrageBean.size() > 0) {
                        if (position >= mBarrageBean.size()) {
                            position = 0;
                        } else {
                            NewsDanmuBean danmuEntity = new NewsDanmuBean();
                            String content = mBarrageBean.get(position).getContent();
                            //弹幕头像
                            if (mBarrageBean.size() > 0 && !TextUtils.isEmpty(mBarrageBean.get(position).getUserImg())) {
                                String headImg = mBarrageBean.get(position).getUserImg();
                                danmuEntity.setHeadImg(headImg);
                            }
                            //弹幕点赞数量
                            if (mBarrageBean.size() > 0 && !TextUtils.isEmpty(mBarrageBean.get(position).getThumbsUpCount())) {
                                String thumbsUpCount = mBarrageBean.get(position).getThumbsUpCount();
                                danmuEntity.setThumbsUpCount(thumbsUpCount);
                            }
                            //当前弹幕是否能点赞
                            if (mBarrageBean.size() > 0) {
                                danmuEntity.setCanThumbsUp(mBarrageBean.get(position).getCanThumbsUp());
                            }

                            danmuEntity.setContent(content); //弹幕内容
                            danmuEntity.setPosition(String.valueOf(position));
                            danmuEntity.setType(1);
                            danmuContainerView.addDanmu(danmuEntity);
//                            danmuContainerView.onProgress(mCurrentProgress);
                            position++;
                            mShowCount++;
//                            Timber.d("========弹幕多少 position" + position);
//                            Timber.d("========弹幕多少 mCurrentProgress" + mCurrentProgress);
                        }
                    }
                    break;
            }
        }
    };


    public void setProgress(PathPoint newLoc) {
        tasks_view.setTranslationX(newLoc.mX);
        tasks_view.setTranslationY(newLoc.mY);
    }

    private ObjectAnimator mOutPRAnimTranslation, mOutPRAnimAlpha,
            mEnterCommentAnimScaleX, mEnterCommentAnimAlpha;//扩展模块进入动画

    private ObjectAnimator mEnterPRAnimTranslation, mEnterPRAnimAlpha,
            mOutCommentAnimScaleX, mOutCommentAnimAlpha;//扩展模块退出动画

    private int SDK_VERSION = Build.VERSION.SDK_INT;

    /**
     * 初始化动画（进度条和扩展模块进场、出场动画集合）
     */
    private void initAnimator() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;

        /*
         * 进度条向下移动并消失
         */
        int v_width = ArmsUtils.dip2px(this, 58);
        int d_height = ArmsUtils.dip2px(this, 40);
        int d_width = width - v_width;


        //扩展模块X轴放大动画
        mEnterCommentAnimScaleX = ObjectAnimator.ofFloat(mSceneRootView, "scaleX", 0, 0.5f, 1f);
        mEnterCommentAnimScaleX.setDuration(300);
        mEnterCommentAnimScaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        //扩展模块透明度动画
        mEnterCommentAnimAlpha = ObjectAnimator.ofFloat(mSceneRootView, "alpha", 0f, 0.8f, 1.0f);
        mEnterCommentAnimAlpha.setDuration(300);
        mEnterCommentAnimAlpha.setInterpolator(new AccelerateDecelerateInterpolator());


        //进度条透明度动画
        mOutPRAnimAlpha = ObjectAnimator.ofFloat(tasks_view, "alpha", 1.0f, 0f);
        mOutPRAnimAlpha.setDuration(300);
        mOutPRAnimAlpha.setInterpolator(new AccelerateDecelerateInterpolator());
        //进度条曲线移动动画
        if (SDK_VERSION < 21) {
            AnimatorPath animatorPath = new AnimatorPath();
            animatorPath.moveTo(0, 0);
            animatorPath.secondBesselCurveTo(0, d_height, -d_width / 2, d_height);
            mOutPRAnimTranslation = ObjectAnimator.ofObject(this, "progress", new PathEvaluator(), animatorPath.getPoints().toArray());
            mOutPRAnimTranslation.setDuration(300);
            mOutPRAnimTranslation.setInterpolator(new AccelerateDecelerateInterpolator());

            mOutPRAnimTranslation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mOutPRAnimAlpha.start();

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mEnterCommentAnimAlpha.start();
                    mEnterCommentAnimScaleX.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            mEnterCommentAnimScaleX.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    if (mOpenThread) {
                        isShowComment(1);
                    }
                    if (mSceneRootView != null) {
                        if (mSceneRootView.getAlpha() == 0f || mSceneRootView.getVisibility() == View.GONE) {
                            mSceneRootView.setAlpha(1f);
                            mSceneRootView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            Path path = new Path();
            path.moveTo(0, 0);
            path.quadTo(0, d_height, -d_width / 2, d_height);//前面为曲线支撑点坐标，后面为结束点坐标，
            mOutPRAnimTranslation = ObjectAnimator.ofFloat(tasks_view, "translationX", "translationY", path);
            mOutPRAnimTranslation.setDuration(300);
            mOutPRAnimTranslation.setInterpolator(new AccelerateDecelerateInterpolator());

            /* 扩展模块（评论，分享...）进场动画 */
            squareAnimSet = new AnimatorSet();
            squareAnimSet.play(mEnterCommentAnimScaleX).with(mEnterCommentAnimAlpha)
                    .after(mOutPRAnimAlpha).after(mOutPRAnimTranslation);

            squareAnimSet.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (mSceneRootView != null) {
                        if (mSceneRootView.getVisibility() == View.GONE) {
                            mSceneRootView.setAlpha(0);
                            mSceneRootView.setVisibility(View.VISIBLE);
                        }
                    }

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    //重新显示评论
                    if (mOpenThread) {
                        isShowComment(1);
                    }
                }

            });
        }


        //扩展模块X轴放大动画
        mOutCommentAnimScaleX = ObjectAnimator.ofFloat(mSceneRootView, "scaleX", 1f, 0.8f, 0f);
        mOutCommentAnimScaleX.setDuration(300);
        mOutCommentAnimScaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        //扩展模块透明度动画
        mOutCommentAnimAlpha = ObjectAnimator.ofFloat(mSceneRootView, "alpha", 1.0f, 0f);
        mOutCommentAnimAlpha.setDuration(300);
        mOutCommentAnimAlpha.setInterpolator(new AccelerateDecelerateInterpolator());

        //进度条透明度动画
        mEnterPRAnimAlpha = ObjectAnimator.ofFloat(tasks_view, "alpha", 0f, 1.0f);
        mEnterPRAnimAlpha.setDuration(300);
        mEnterPRAnimAlpha.setInterpolator(new AccelerateDecelerateInterpolator());
        //进度条曲线移动动画
        if (SDK_VERSION < 21) {
            AnimatorPath animatorPath = new AnimatorPath();
            animatorPath.moveTo(-d_width / 2, d_height);
            animatorPath.secondBesselCurveTo(0, d_height, 0, 0);
            mEnterPRAnimTranslation = ObjectAnimator.ofObject(this, "progress", new PathEvaluator(), animatorPath.getPoints().toArray());
            mEnterPRAnimTranslation.setDuration(300);
            mEnterPRAnimTranslation.setInterpolator(new AccelerateDecelerateInterpolator());

            mOutCommentAnimScaleX.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mOutCommentAnimAlpha.start();
                    isShowComment(0);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mEnterPRAnimAlpha.start();
                    mEnterPRAnimTranslation.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            mEnterPRAnimTranslation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (mOpenThread) {
                        isShowComment(1);
                    }

                    if (mSceneRootView != null) {
                        mSceneRootView.setVisibility(View.GONE);
                    }
                    if (tasks_view != null) {
                        if (tasks_view.getAlpha() == 0f || tasks_view.getVisibility() == View.GONE) {
                            tasks_view.setAlpha(1f);
                            tasks_view.setVisibility(View.VISIBLE);
                            tasks_view.setClickable(true);
                        }
                        if (mSceneRootView != null) {
                            mSceneRootView.setVisibility(View.GONE);
                        }
                        tasks_view.setClickable(true);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            Path path_return = new Path();
            path_return.moveTo(-d_width / 2, d_height);
            path_return.quadTo(0, d_height, 0, 0);
            mEnterPRAnimTranslation = ObjectAnimator.ofFloat(tasks_view, "translationX", "translationY", path_return);
            mEnterPRAnimTranslation.setDuration(300);
            mEnterPRAnimTranslation.setInterpolator(new AccelerateDecelerateInterpolator());

            /* 扩展模块（评论，分享...）退出动画 */
            returnAnimSet = new AnimatorSet();
            returnAnimSet.play(mEnterPRAnimTranslation).with(mEnterPRAnimAlpha).after(mOutCommentAnimScaleX).after(mOutCommentAnimAlpha);
            returnAnimSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    isShowComment(0);

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mOpenThread) {
                        isShowComment(1);
                    }

                    if (tasks_view != null) {
                        if (tasks_view.getAlpha() == 0f || tasks_view.getVisibility() == View.GONE) {
                            tasks_view.setAlpha(1f);
                            tasks_view.setVisibility(View.VISIBLE);
                            tasks_view.setClickable(true);
                        }
                        if (mSceneRootView != null) {
                            mSceneRootView.setVisibility(View.GONE);
                        }
                        tasks_view.setClickable(true);
                    }
                }
            });
        }

    }

    //显示评论布局
    private void showCommentLayout() {
        if (SDK_VERSION < 21) {
            //（进度条移动和透明动画、评论进场透明和缩放动画）评论显示\隐藏动画开始时不再启动动画
            if (!mOutPRAnimTranslation.isStarted() && !mOutPRAnimAlpha.isStarted()
                    && !mEnterCommentAnimAlpha.isStarted() && !mEnterCommentAnimScaleX.isStarted()

                    && !mEnterPRAnimTranslation.isStarted() && !mEnterPRAnimTranslation.isStarted()
                    && !mOutCommentAnimScaleX.isStarted() && !mOutCommentAnimAlpha.isStarted())
                mOutPRAnimTranslation.start();

        } else {
            if (!squareAnimSet.isStarted() && !returnAnimSet.isStarted())
                squareAnimSet.start();
        }
    }

    //隐藏评论布局
    private void hideCommentLayout() {
        showShareScene();
        if (SDK_VERSION < 21) {
            //（进度条移动和透明动画、评论进场透明和缩放动画）评论显示\隐藏动画开始时不再启动动画
            if (!mOutPRAnimTranslation.isStarted() && !mOutPRAnimAlpha.isStarted()
                    && !mEnterCommentAnimAlpha.isStarted() && !mEnterCommentAnimScaleX.isStarted()
                    && !mEnterPRAnimTranslation.isStarted() && !mEnterPRAnimTranslation.isStarted()
                    && !mOutCommentAnimScaleX.isStarted() && !mOutCommentAnimAlpha.isStarted())
                mOutCommentAnimScaleX.start();
        } else {
            if (!squareAnimSet.isStarted() && !returnAnimSet.isStarted())
                returnAnimSet.start();
        }
    }


    /**
     * 没有登录  不可分享
     */
    @Override
    public void upView() {

    }

    /**
     * 获取到资讯分享链接  进行分享
     *
     * @param NewsOrVideoShareBean 资讯分享数据
     */
    @Override
    public void videoShare(NewsOrVideoShareBean NewsOrVideoShareBean) {
//        Timber.d("==================后台返回的url:" + NewsOrVideoShareBean.getUrl());
//        Timber.d("==================后台返回的mShareType:" + NewsOrVideoShareBean.getType() + "\n");
        if (!mImageUrl.startsWith("http") && !TextUtils.isEmpty(mImageUrl)) {
            mImageUrl = "http:" + mImageUrl;
        }

        String mVideoShareUrl = NewsOrVideoShareBean.getUrl();

        String mVideoShareType = NewsOrVideoShareBean.getType();
        oneKeyShare.setShareContent(new ShareBean.Builder()
                .title(mVideoShareTitle)
                .content(mVideoShareContent)
                .imagePath(imageUserPath)
                .imageUrl(mImageUrl)
                .pageUrl(mVideoShareUrl)
                .weChatShareType(mVideoShareType)
                .weChatMomentsShareType(mVideoShareType)
                .qqShareType(mVideoShareType)
                .qZoneShareType(mVideoShareType)
                .sinaShareType(mVideoShareType)
                .create());
        if (!TextUtils.isEmpty(mVideoShareUrl)) {
            oneKeyShare.show(api);
        } else {
            showMessage(getResources().getString(R.string.not_net));
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
        }
    }

    private void dbUpdateDeviceInfo(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean != null) {
            if (deviceInfoBean.getVideoGoldCircles() > 0) {//视频圈数获取；
                videoGoldCircles = deviceInfoBean.getVideoGoldCircles();
            }

            if (deviceInfoBean.getVideoGoldPercent() != null && deviceInfoBean.getVideoGoldPercent() > 0) {
                mReadGoldPercent = deviceInfoBean.getVideoGoldPercent();
            }
        }
    }

    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
//            Timber.d("=db=    VideoPlayActivity - UserInfo - query 成功");
            dbUpdateUserAvatar(list.get(0));
        }
    }

    /**
     * @param userInfoBean 获取到数据库的用户信息后，更新界面
     */
    public void dbUpdateUserAvatar(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (!TextUtils.isEmpty(userInfoBean.getAvatar())) {
                if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME) && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
//                    Timber.d("==shareMine  :" + "有登录，有图片：" + Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME);
                    imageUserPath = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
                } else {
//                    Timber.d("==shareMine  :" + "有登录，无图片");
                    assert mPresenter != null;
                    mPresenter.requestPermission(userInfoBean.getAvatar());
                }
            }
        }
    }

    /**
     * 下载成功回调
     *
     * @param filePath 文件位置
     */
    @Override
    public void downloadCallBack(String filePath) {
        imageUserPath = filePath;
//        Timber.d("==shareVideo  downloadCallBack：" + imageUserPath);
    }

    /*
     *获取网页端传过来的值
     */
    private void getWebValue(Uri uri) {
        mWebUpApp = uri.getScheme();//用于区分是否web唤醒打开
        mWebShareUrl = uri.getQueryParameter("webUrl");//资讯、视频网页url;
        mCurrId = uri.getQueryParameter("id");//资讯、视频网页ID;
        Title = uri.getQueryParameter("title");//资讯、视频网页标题;
        mType = uri.getQueryParameter("tab");//资讯、视频频道类型;
        mTextSize = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        mCanVideoShare = uri.getQueryParameter("canShare");//能否分享;
        mVideoShareTitle = Title;
        mImageUrl = uri.getQueryParameter("img");//资讯、视频分享图片url;
        VideoUrl = uri.getQueryParameter("url");//视频url;
        mVideoShareContent = uri.getQueryParameter("content");//资讯、视频描述文本;
    }

    /**
     * 获取Adapter传过来的值
     */
    private void getIntentValue(Intent intent) {
        VideoUrl = intent.getStringExtra("url");
        mImageUrl = intent.getStringExtra("image_url");
        Title = intent.getStringExtra("title");
//        Timber.d("===================Title = " + Title);
        mType = intent.getStringExtra("tab");
        mCurrId = intent.getStringExtra("id");
        mCanVideoShare = intent.getStringExtra("is_share");
        mWebShareUrl = intent.getStringExtra("web_url");
        mVideoShareContent = intent.getStringExtra("content");
        mVideoShareTitle = Title;
        mTextSize = intent.getStringExtra("textSize");

//        Timber.d("================getIntent数据：mCanVideoShare=" + mCanVideoShare);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentValue(intent);
        completeCount = 0;
        tasks_view.setVisibility(View.VISIBLE);
        if (mPresenter != null) {
            mPresenter.stopTime();
            isPauseTime = true;
        }

        init();
        refreshLayout.autoRefresh();//第一次自动刷新
//        Timber.d("================什么时候执行onNewIntent");
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        videoPlayAdapter = new VideoPlayAdapter(this, Title, mTextSize, mVideoList);
        linearLayoutManager = new LinearLayoutManagerWrapper(VideoDetailActivity.this.getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(videoPlayAdapter);

    }

    private void init() {
        videoPlayAdapter.setVideoTitle(mVideoShareTitle);//网页打开app时使用；
        //true
        updateVideo(false);
        baseVideoView.setOnPlayerEventListener(this);
        baseVideoView.setEventHandler(mOnEventAssistHandler);
        mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(this, null);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true);
        baseVideoView.setReceiverGroup(mReceiverGroup);

//        ButtonAnimation();//初始化返回，分享按钮动画，创建Runnable；
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // RecyclerView.SCROLL_STATE_IDLE  当前的recycleView不滑动(滑动已经停止时)
                    // RecyclerView.SCROLL_STATE_DRAGGING, //当前的recycleView被拖动滑动
                    // RecyclerView.SCROLL_STATE_SETTLING  //当前的recycleView在滚动到某个位置的动画过程,但没有被触摸滚动.调用 scrollToPosition(int) 应该会触发这个状态
//                    Timber.d("==video  addOnScrollListener  if  ");
                    if (mReceiverGroup != null) {
                        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_COVER_TOP_SHOW, true);
                    }

                    if (!mShow) {
                        if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {//1.软键盘隐藏 2.发送评论布局隐藏 3.控件弹幕评论布局显示时
                            mInputString = etInputComment.getText().toString();
                            etComment.setText(mInputString);
                            hideCommentLayout();
                            hideKeyboard();
                        }
                    } else if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {
                        mInputString = etInputComment.getText().toString();
                        etComment.setText(mInputString);
                        showShareScene();
                        hideKeyboard();
                    }
                }

            }
        });

        /*一进入页面的自动播放*/
        if (!TextUtils.isEmpty(VideoUrl)) {
            DataSource dataSource = new DataSource();
            dataSource.setData(VideoUrl);
//            if (mPresenter != null && !TextUtils.isEmpty(mPresenter.getToken()))
//                dataSource.setTitle(mCanVideoShare);
            dataSource.setTag(mImageUrl);
            baseVideoView.setDataSource(dataSource);
            mReceiverGroup.getGroupValue().putObject(DataInter.Key.KEY_DATA_SOURCE, dataSource);
            baseVideoView.start();
        }


        /*视频详情页 相关推荐：     点击事件*/
        videoPlayAdapter.setOnItemClickListener(position -> {
            adHorizontalRemove();//点击列表下一个视频，移除广告
            finishRefresh();
            finishLoadMore();
            hideLoading();

            saveReadRecord();//保存上一个视频观看记录

            //进入下一个视频  先清空再重新获取
            isShowComment(0);
            if (tvCommentCount != null) {
                tvCommentCount.setVisibility(View.GONE);
            }
            mInputString = "";
            mOpenThread = false;
            clearEditText(); //清空EditText相关内容
            if (mBarrageBean != null) {
                mBarrageBean.clear();
            }
            if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {
                hideCommentLayout();
            }
            mShow = false;

            if (mPresenter != null) {
                mPresenter.stopTime();
                isPauseTime = true;
            }

            removeShowButton();
//            isShowButton(0);
            if (videoPlayAdapter != null && videoPlayAdapter.getDataIndex(position) != null) {
                if (refreshLayout != null) {
                    refreshLayout.setEnableLoadMore(false);
                }

                VideoBean bean = videoPlayAdapter.getDataIndex(position);
                getShareData(bean);//这里视频详情页点击下面列表 获取分享链接

//            imageLoader("http:" + videoPlayAdapter.getDataIndex(position).getThumbUrl());//底图
//            tvVideoTitle.setText(videoPlayAdapter.getDataIndex(position).getTitle());//视频简介
                videoPlayAdapter.setVideoTitle(mVideoShareTitle);

                videoPlayAdapter.removeAllItem();


                if (!TextUtils.isEmpty(VideoUrl)) {
                    playVideo(); //视频地址不为空 则去播放视频
                }
            }

            if (mPresenter != null) {
                mPresenter.getVideoList(true, true, mType, mCurrId);
            }
        });
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
//                Timber.d("========PlayerEvent -ON_PLAY_COMPLETE");
                if (mPresenter != null) {
                    mPresenter.stopTime();
                    isPauseTime = true;
                }
                if (!isPauseAdHorizontal && !isLandscape) {//播放完成调用广告
                    adHorizontalShow();//播放完成调用广告
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE:
                if (bundle == null)
                    return;
                int status = bundle.getInt(EventKey.INT_DATA);
//                Timber.d("========PlayerEvent -ON_STATUS_CHANGE" + status);
                if (status == IPlayer.STATE_PAUSED) {
//                    Timber.d("========PlayerEvent -ON_STATUS_CHANGE + PAUSED");
                    if (mPresenter != null) {
                        mPresenter.stopTime();
                        isPauseTime = true;
//                        Timber.d("========stopTime -ON_STATUS_CHANGE + STATE_PAUSED");
                    }
                } else if (status == IPlayer.STATE_STARTED) {
//                    Timber.d("========PlayerEvent -ON_STATUS_CHANGE + STARTED");
                    if (mPresenter != null) {
                        mPresenter.stopTime();
                        if (completeCount < videoGoldCircles || (completeCount == videoGoldCircles && mReadGoldPercent >= arcProgressScale)) {
                            mPresenter.startTime();
                            isPauseTime = false;
                        }
//                        Timber.d("========startTime - ON_STATUS_CHANGE  STATE_STARTED");
                    }
                } else if (status == IPlayer.STATE_END || status == IPlayer.STATE_ERROR
                        || status == IPlayer.STATE_IDLE) {
//                    Timber.d("========PlayerEvent -ON_STATUS_CHANGE + END");
                    if (mPresenter != null) {
                        mPresenter.stopTime();
                        isPauseTime = true;
//                        Timber.d("========startTime - ON_STATUS_CHANGE  STATE_END");
                    }
                } else if (status == IPlayer.STATE_STOPPED
                        || status == IPlayer.STATE_PLAYBACK_COMPLETE) {
//                    Timber.d("========PlayerEvent -ON_STATUS_CHANGE + STOPPED or COMPLETE");
                    if (mPresenter != null) {
                        mPresenter.stopTime();
                        isPauseTime = true;
                        Timber.d("========stopTime - ON_STATUS_CHANGE  STATE_STARTED_COMPLETE");
                    }
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE:
//                Timber.d("========PlayerEvent -ON_PAUSE");
                if (mPresenter != null) {
                    mPresenter.stopTime();
                    isPauseTime = true;
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_STOP:
//                Timber.d("========PlayerEvent -ON_STOP");
                if (mPresenter != null) {
                    mPresenter.stopTime();
                    isPauseTime = true;
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START:
//                Timber.d("========PlayerEvent -ON_BUFFERING_START: " + AssistPlayer.get().getState());
                if ((AssistPlayer.get().getState() == IPlayer.STATE_STARTED ||
                        AssistPlayer.get().getState() == IPlayer.STATE_IDLE) && mPresenter != null) {
                    mPresenter.stopTime();
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
//                Timber.d("========PlayerEvent -ON_BUFFERING_END");
                if (mPresenter != null && AssistPlayer.get().getState() == IPlayer.STATE_STARTED) {
                    mPresenter.startTime();
                }
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO:
                if (mPresenter != null) {
                    mPresenter.stopTime();
//                    isPauseTime = true;
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
//                Timber.d("========PlayerEvent -PLAYER_EVENT_ON_SEEK_COMPLETE isPauseTime：" + isPauseTime);
                //                Timber.d("========PlayerEvent -ON_AUDIO_SEEK_RENDERING_START: "  + AssistPlayer.get().getState());
                if (!isPauseTime && mPresenter != null && completeCount < videoGoldCircles) {
                    mPresenter.stopTime();
                    isPauseTime = true;

                    if (isWifi() || (isInternet() && NetworkUtils.isNetConnected(getApplicationContext()) && MyApplication.ignoreMobile)) {
                        mPresenter.startTime();
                        isPauseTime = false;
                    }

                }
                break;

        }
    }

    private OnVideoViewEventHandler mOnEventAssistHandler = new OnVideoViewEventHandler() {
        @Override
        public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode) {
                case DataInter.Event.CODE_REQUEST_PAUSE:
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                    if (isLandscape) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        killMyself();
                    }
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_NEXT: //播放下一个
                    saveReadRecord();//保存上一个视频观看记录

                    //进入下一个视频  先清空上一个视频弹幕
                    if (tvCommentCount != null) {
                        tvCommentCount.setVisibility(View.GONE);
                    }
                    isShowComment(0);
                    mInputString = "";
                    mOpenThread = false;
                    clearEditText(); //清空EditText内容
                    if (mBarrageBean != null) {
                        mBarrageBean.clear();
                    }
                    if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {
                        hideCommentLayout();
                    }
                    mShow = false;

                    mCommentTime = System.currentTimeMillis();
                    if (videoPlayAdapter.getVideoListSize() > 0) {
                        if (refreshLayout != null) {
                            refreshLayout.setEnableLoadMore(false);
                        }

                        finishRefresh();
                        finishLoadMore();
                        hideLoading();

                        if (videoPlayAdapter.getDataIndex(0) != null) {
                            VideoBean bean = videoPlayAdapter.getDataIndex(0);
                            getShareData(bean);//这里视频详情页点击下面列表 获取分享链接
//            imageLoader("http:" + videoPlayAdapter.getDataIndex(position).getThumbUrl());//底图
//                        tvVideoTitle.setText(videoPlayAdapter.getDataIndex(0).getTitle());//视频简介
                            videoPlayAdapter.setVideoTitle(mVideoShareTitle);
                            if (videoPlayAdapter != null)
                                videoPlayAdapter.removeAllItem();
                        }

                        if (!TextUtils.isEmpty(VideoUrl)) {
                            playVideo(); //视频地址不为空 则去播放视频
                        }

                        if (mPresenter != null) {
                            mPresenter.getVideoList(true, true, mType, mCurrId);
                        }
                    } else {
                        DataSource dataSource = new DataSource();
                        dataSource.setData(VideoUrl);
//                        dataSource.setTitle(mCanVideoShare);
                        mReceiverGroup.getGroupValue().putObject(DataInter.Key.KEY_DATA_SOURCE, dataSource);
                        baseVideoView.setDataSource(dataSource);
                        baseVideoView.start();
                    }
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    setRequestedOrientation(isLandscape ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    if (baseVideoView != null)
                        baseVideoView.stop();
                    break;
            }
        }
    };


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
            updateVideo(true);
        } else {
            isLandscape = false;
            updateVideo(false);
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape);
    }

    private void updateVideo(boolean landscape) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) baseVideoView.getLayoutParams();
        if (landscape) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0, 0, 0, 0);
            baseVideoView.setAspectRatio(AspectRatio.AspectRatio_MATCH_PARENT);
            baseVideoView.setLayoutParams(layoutParams);
        } else {
//            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.width = ArmsUtils.getScreenWidth(this);
            layoutParams.height = (int) (layoutParams.width * 9f / 16f);
            layoutParams.setMargins(0, 0, 0, 0);
            baseVideoView.setLayoutParams(layoutParams);
        }
    }

    /**
     * 删除 返回和分享按钮 上一次的“三秒后隐藏”动画；
     */
    public void removeShowButton() {
        Timber.d("==video  removeShowButton  删除“三秒后隐藏”动画");
        if (handler != null) {
            handler.removeCallbacks(runIvShareRight);
            handler.removeCallbacks(runIvFinish);
        }

//        ivFinish.clearAnimation();
//        ivShareRight.clearAnimation();
    }

    @Override
    protected void onPause() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh(0);
            refreshLayout.finishLoadMore(0);
        }
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();

        adHorizontalRemove();//生命周期为暂停时移除广告
        isPauseAdHorizontal = true;
        super.onPause();
        isShowComment(0);
        SoundPoolManager.getInstance(getApplicationContext()).stopRinging();

        if (baseVideoView.isInPlaybackState()) {
            baseVideoView.pause();
        } else {
            baseVideoView.stop();
        }
        if (mPresenter != null) {
            mPresenter.stopTime();
            isPauseTime = true;
        }
    }

    @Override
    protected void onResume() {
        isPauseAdHorizontal = false;
        super.onResume();
        if (TextUtils.equals(isShowComment, "videoHide")) {
            ivCommentBarrage.setSelected(false);
            isShowComment(0);
        } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "videoShow")) {
            ivCommentBarrage.setSelected(true);
            isShowComment(1);
        }
    }

    /**
     * JZMediaManager在 startVideo（）前 和 complete（）后都是不存在的；
     * isOneNoNewwork   用来判断是否 startVideo（）前
     * isVideoComplete  用来判断是否 complete（）后
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.stopTime();
            isPauseTime = true;
        }
    }

    /**
     * onDestroy 时需要清理一下之前的视频缓存；
     */
    @Override
    protected void onDestroy() {
        clearDanmu(); //清空弹幕相关
        clearAnimation(); //清空动画相关
        clearVideo();  //清空视频相关
        explosionField = null;
        SoundPoolManager.getInstance(getApplicationContext()).release();

        if (textWatcher != null && etComment != null && etInputComment != null) {
            etComment.removeTextChangedListener(textWatcher);
            etInputComment.removeTextChangedListener(textWatcher);
            textWatcher = null;
        }

        if (videoHandler != null) {
            videoHandler.removeCallbacksAndMessages(null);
            videoHandler = null;
        }

        if (videoPlayAdapter != null) {
            videoPlayAdapter.recycle();
            videoPlayAdapter = null;
        }

        if (linearLayoutManager != null) {
            linearLayoutManager.removeAllViews();
            linearLayoutManager = null;
        }

        oneKeyShare.destroy();

        if (baiduNative != null) {
            baiduNative.destroy();
            baiduNative = null;
        }

        if (gdtNative != null) {
            gdtNative = null;
        }


        if (requestParameters != null) {
            requestParameters = null;
        }


        if (gdtAdPosition != null) {
            gdtAdPosition.clear();
        }

        if (csjAdPosition != null) {
            csjAdPosition.clear();
        }

        if (baiduAdPosition != null) {
            baiduAdPosition.clear();
        }

        if (api != null) {
            api.detach();
        }

        if (linearLayoutManager != null)
            linearLayoutManager = null;

        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        if (handler != null) {
            handler.removeCallbacks(mExplodeRunnable);
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        clearReadRecord();//清理视频阅读记录

        if (mRecyclerView != null) {
            mRecyclerView.clearOnScrollListeners();
        }
        super.onDestroy();

    }

    private void releaseVisualizer() {
//        if (mVisualizer != null)
//            mVisualizer.release();
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


    /**
     * 监听返回键
     */
    @Override
    public void onBackPressed() {
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        if (!TextUtils.isEmpty(mWebUpApp)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("app_status", Constant.APP_STATUS_RESTART);
            launchActivity(intent);
            completeCount = 0;
        }
        super.onBackPressed();
    }

    @Override
    public void killMyself() {
        if (!TextUtils.isEmpty(mWebUpApp)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("app_status", Constant.APP_STATUS_RESTART);
            launchActivity(intent);
            completeCount = 0;
        }
        finish();
    }


    private List<VideoBean> mRefreshVideoList = new ArrayList<>();
    private TTAdNative csjNative;//穿山甲广告
    private AdSlot csjAdSlot;//穿山甲广告配置

    /**
     * 刷新成功更新数据
     *
     * @param videoList 视频列表
     */
    @Override
    public void refreshData(@NonNull List<VideoBean> videoList) {
        mRefreshVideoList.clear();
        mRefreshVideoList = videoList;
        isRefresh = true;
        getNativeResponse();

        if (videoList.size() <= 0) {
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, false);
            refreshLayout.setEnableLoadMore(false);
        } else {
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true);
            refreshLayout.setEnableLoadMore(true);
        }

    }

    /**
     * 根据对应的ad字段请求不同的广告sdk
     */
    public void getNativeResponse() {

        boolean isRequestAd = false;//是否需要请求广告联盟广告

        baiduAdPosition.clear();
        gdtAdPosition.clear();
        csjAdPosition.clear();

        for (int i = 0; i < mRefreshVideoList.size(); i++) {
            VideoBean videoBean = mRefreshVideoList.get(i);
            if (TextUtils.equals(videoBean.getType(), "ad")) {
                if (TextUtils.equals(videoBean.getAdNewType(), "BaiDu")) {
                    isRequestAd = true;
                    baiduAdPosition.add(i);
                } else if (TextUtils.equals(videoBean.getAdNewType(), "GDT")) {
                    isRequestAd = true;
                    gdtAdPosition.add(i);
                } else if (TextUtils.equals(videoBean.getAdNewType(), "CSJ")) {
                    isRequestAd = true;
                    csjAdPosition.add(i);
                }
            }
        }
        if (baiduAdPosition != null && baiduAdPosition.size() > 0) {
            fetchBaiDuAd();
        } else {
            hasCompleteBaiduAd = true;
        }
        if (gdtAdPosition != null && gdtAdPosition.size() > 0) {
            fetchGDTAd();
        } else {
            hasCompleteGDTAd = true;
        }
        if (csjAdPosition != null && csjAdPosition.size() > 0) {
            fetchCSJAd();
        } else {
            hasCompleteCSJAd = true;
        }

        if (!isRequestAd) {
            refreshAdData();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshAdData() {
        if (hasCompleteBaiduAd && hasCompleteGDTAd && hasCompleteCSJAd) {
            if (isRefresh) {
                videoPlayAdapter.refreshVideo(mRefreshVideoList);
            } else {
                videoPlayAdapter.loadMoreVideo(mRefreshVideoList);
            }
            hasCompleteBaiduAd = false;
            hasCompleteGDTAd = false;
            hasCompleteCSJAd = false;
        }
    }

    /**
     * 请求穿山甲广告
     */
    private void fetchCSJAd() {
        if (csjNative == null) {
            TTAdManager ttAdManager = TTAdManagerHolder.getInstance(this);
            csjNative = ttAdManager.createAdNative(this);
        }

        if (csjAdSlot == null) {
            csjAdSlot = new AdSlot.Builder()
                    .setCodeId(Constant.CSJ_AD_ID_VIDEO_DETAIL)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(640, 320)
                    .setAdCount(3)
                    .build();
        }

        csjNative.loadFeedAd(csjAdSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {
                hasCompleteCSJAd = true;
                refreshAdData();
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> list) {
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < csjAdPosition.size(); i++) {
                        if (i < list.size()) {
                            VideoBean videoBean = mRefreshVideoList.get(csjAdPosition.get(i));
                            videoBean.setTtFeedAd(list.get(i));
                            mRefreshVideoList.set(csjAdPosition.get(i), videoBean);
                        }
                    }
                }
                hasCompleteCSJAd = true;
                refreshAdData();
            }
        });

    }

    /**
     * 请求百度广告
     */
    private void fetchBaiDuAd() {
        if (baiduNative == null) {
            baiduNative = new BaiduNative(this, Constant.BAIDU_AD_ID_VIDEO_DETAIL, new BaiduNative.BaiduNativeNetworkListener() {
                @Override
                public void onNativeLoad(List<NativeResponse> list) {
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < baiduAdPosition.size(); i++) {
                            VideoBean videoBean = mRefreshVideoList.get(baiduAdPosition.get(i));
                            videoBean.setNativeResponse(list.get(i));
                            mRefreshVideoList.set(baiduAdPosition.get(i), videoBean);
                        }
                    }
                    hasCompleteBaiduAd = true;
                    refreshAdData();
                }

                @Override
                public void onNativeFail(NativeErrorCode nativeErrorCode) {
                    hasCompleteBaiduAd = true;
                    refreshAdData();
                }
            });
        }

        if (requestParameters == null) {
            requestParameters = new RequestParameters.Builder()
                    .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
        }

        baiduNative.makeRequest(requestParameters);
    }

    /**
     * 请求腾讯广告
     */
    private void fetchGDTAd() {
        if (gdtNative == null) {
            gdtNative = new NativeExpressAD(this, new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT),
                    Constant.GDT_APP_ID,
                    Constant.GDT_AD_ID_VIDEO_PLAY,
                    new NativeExpressAD.NativeExpressADListener() {
                        @Override
                        public void onNoAD(AdError adError) {
                            Timber.d("=========Ad--------广告请求失败");
                            hasCompleteGDTAd = true;
                            refreshAdData();
                        }

                        @Override
                        public void onADLoaded(List<NativeExpressADView> list) {
                            int size = videoPlayAdapter.getVideoListSize();
                            HashMap<NativeExpressADView, Integer> map = new HashMap<>();
                            Timber.d("=========Ad--------广告请求成功：" + list.size() +
                                    "\n广告位置：" + gdtAdPosition + " size: " + size);
                            for (int i = 0; i < list.size(); i++) {
                                if (i < gdtAdPosition.size()) {
                                    if (gdtAdPosition.get(i) >= mRefreshVideoList.size()) {
                                        Timber.e("=========Ad-广告越界：" + mRefreshVideoList.size() +
                                                "\n广告位置：" + gdtAdPosition.get(i));
                                    } else {
                                        VideoBean videoBean = mRefreshVideoList.get(gdtAdPosition.get(i));
                                        videoBean.setIsGDTAD(true);
                                        mRefreshVideoList.set(gdtAdPosition.get(i), videoBean);
                                        map.put(list.get(i), gdtAdPosition.get(i) + size);
                                        Timber.d("=========Ad-广告插入：" + gdtAdPosition.get(i)
                                                + "\n广告位置：" + (gdtAdPosition.get(i) + size) + " size: " + size);
                                    }
                                } else {
                                    list.get(i).destroy();
                                }
                            }
                            videoPlayAdapter.insertGDTAD(map);
                            hasCompleteGDTAd = true;

                            refreshAdData();
                        }

                        @Override
                        public void onADClosed(NativeExpressADView nativeExpressADView) {
                            videoPlayAdapter.removeGDTAD(nativeExpressADView);
                        }

                        @Override
                        public void onRenderFail(NativeExpressADView nativeExpressADView) {
                        }

                        @Override
                        public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                        }

                        @Override
                        public void onADExposure(NativeExpressADView nativeExpressADView) {
                        }

                        @Override
                        public void onADClicked(NativeExpressADView nativeExpressADView) {
                        }

                        @Override
                        public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                        }

                        @Override
                        public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                        }

                        @Override
                        public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                        }
                    });
            gdtNative.setVideoOption(new VideoOption.Builder()
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI)
                    .setAutoPlayMuted(true)
                    .build());
        }

        gdtNative.loadAD(gdtAdPosition.size());
    }


    /**
     * 加载更多成功更新数据
     *
     * @param videoList 视频列表
     */
    @Override
    public void loadMoreData(@NonNull List<VideoBean> videoList) {
        isRefresh = false;
        if (videoList.size() == 0) {
            refreshLayout.setNoMoreData(true);
        } else {
            mRefreshVideoList.clear();
            mRefreshVideoList = videoList;
            getNativeResponse();
        }
    }

    /**
     * 结束刷新
     */
    @Override
    public void finishRefresh() {
        refreshLayout.finishRefresh(0);
    }

    /**
     * 结束加载更多
     */
    @Override
    public void finishLoadMore() {
        refreshLayout.finishLoadMore(0);
    }

    /**
     * 刷新失败 是否显示异常布局
     */
    @Override
    public void refreshFailed() {
        videoPlayAdapter.showEmptyView(true);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, false);
    }

    /*更新进度条*/
    @Override
    public void timeProgressNext(int i) {
        arcProgressScale = i;
//        Timber.d("进度条==========" + i);
        tasks_view.setInnerProgress(i);
        if (completeCount >= videoGoldCircles && mReadGoldPercent <= i) {   //返回后台返回的圈数 和刻度都相等时则展开评论布局
            if (mSceneRootView != null && mSceneRootView.getVisibility() == View.GONE) {  //当输入框布局没显示时才执行显示动画
                showCommentLayout();//隐藏进度条 显示评论布局
                isShowComment(0);
            }
            if (mPresenter != null) {
                mPresenter.stopTime();
                isPauseTime = true;
//                mPresenter.startTime();
//                isPauseTime = false;
            }
            mShow = true;
            tasks_view.setVisibility(View.GONE);
        }
        if (i == 100) {//奖励进度条100时领取奖励
            if (isInternet()) {
                saveReadRecord();//保存资讯阅读记录
                List<VideoRecordBean> recordList = getReadRecord();
                clearReadRecord();//清理资讯阅读记录
                String aList = new Gson().toJson(recordList);
                if (mPresenter != null)
                    mPresenter.videoReward(String.valueOf(System.currentTimeMillis()), mCurrId, mType, aList);
            } else {
                showMessage("连接网络可领取奖励");
                tasks_view.setInnerProgress(0);
                if (completeCount < videoGoldCircles) {
                    tasks_view.setVisibility(View.VISIBLE);
                    if (mPresenter != null) {
                        mPresenter.stopTime();
                        isPauseTime = true;
                        mPresenter.startTime();
                        isPauseTime = false;
                    }
                }
            }
        }
    }

    /**
     * 观看视频获得收益后爆裂效果
     *
     * @param integer 金币数值 int类型
     */
    @Override
    public void videoRewardInt(Integer integer) {
        tasks_view.setInnerProgress(0);
        completeCount++;
        if (completeCount <= videoGoldCircles && mReadGoldPercent >= 0) {
            tasks_view.setVisibility(View.VISIBLE);
            if (mPresenter != null) {
                mPresenter.stopTime();
                isPauseTime = true;
                mPresenter.startTime();
                isPauseTime = false;
            }
        }
        if (isLandscape) {//视频全屏播放不爆裂金币效果；
            if (TextUtils.isEmpty(DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND))
                    || DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND).equals("true")) {
                SoundPoolManager.getInstance(getApplicationContext()).playRinging();
            }
        } else {
            flReward.setVisibility(View.VISIBLE);
            tvReward.setText(String.valueOf(integer));
            if (isOneExplode) {
                explosionAnim();
                isOneExplode = false;
            } else {
                showRewardAnim();
            }
        }
    }

    /**
     * 由于网络原因领取失败了；
     */
    @Override
    public void errorVideoReward() {
        Timber.d("==VideoState" + "领取失败");
        tasks_view.setInnerProgress(0);
        if (completeCount < videoGoldCircles) {
            tasks_view.setVisibility(View.VISIBLE);
            if (mPresenter != null) {
                mPresenter.stopTime();
                isPauseTime = true;
                mPresenter.startTime();
                isPauseTime = false;
            }
        } else {
            tasks_view.setVisibility(View.GONE);
        }
    }


    /**
     * 接收从登录界面的登录事件，只要登录了，进度条为100就再次发起领取阅读奖励；
     *
     * @param isLogin 是否已经登录
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        if (isLogin) {
            mDanmuAdapter.updateDanmuView(Integer.parseInt(mBarrageBean.get(danmuCurrPosition).getThumbsUpCount())
                    , true, false);
            queryDeviceInfo();//登录界面返回后 获取数据库
            //获取弹幕
            requDanmu();  //请求后台弹幕接口

            if (tasks_view.getProgress() == 100) {
                if (isInternet()) {
                    clearReadRecord();
                    baseVideoView.start();
                    if (mPresenter != null && completeCount < videoGoldCircles) {
                        mPresenter.stopTime();
                        isPauseTime = true;
                        mPresenter.startTime();
                        isPauseTime = false;
                    }
                    tasks_view.setInnerProgress(0);
//                    JZVideoPlayer.goOnPlayOnResume();//这个方法里有：JZMediaManager.start()
                }
            }
        }
    }


    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.isNetConnected(getApplicationContext());
    }

    /*判断当前是否为WIFI网络*/
    private boolean isWifi() {
        return NetworkUtils.isWifiConnected(getApplicationContext());
    }

    /**
     * 金币爆裂效果
     */
    private void explosionAnim() {
        if (handler != null) {
            handler.postDelayed(mExplodeRunnable, 2000);
        }
    }

    private void explosionCoin() {
        if (explosionField != null && ivReward != null && tvReward != null) {
            explosionField.expandExplosionBound(
                    (int) DeviceUtils.getScreenWidth(getApplicationContext()),
                    (int) (DeviceUtils.getScreenHeight(getApplicationContext()) * 0.6));
            explosionField.explode(ivReward);
            explosionField.explode(tvReward);
            if (TextUtils.isEmpty(DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND))
                    || DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND).equals("true")) {
                SoundPoolManager.getInstance(getApplicationContext()).playRinging();
            }
        }
    }

    /**
     * 金币爆裂效果通知
     */
    Runnable mExplodeRunnable = new Runnable() {
        @Override
        public void run() {
            //发送爆裂金币通知
            if (handler != null)
                handler.sendEmptyMessage(1);
        }
    };

    /*视频奖励金币领取图恢复加爆裂*/
    private void showRewardAnim() {
        View root = findViewById(R.id.fl_reward);
        reset(root);
        explosionAnim();
        explosionField.clear();
    }

    /*金币领取图恢复*/
    private void reset(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                reset(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                onBackPressed();
                break;
            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    /**
     * 初始化播放完成后的广告
     */
    private void adHorizontalInit() {
        adHorizontalNative = new BaiduNative(this, Constant.BAIDU_AD_ID_VIDEO_DETAIL_TIMER, new BaiduNative.BaiduNativeNetworkListener() {
            @Override
            public void onNativeLoad(List<NativeResponse> list) {
                if (list != null && list.size() > 0) {
//                    Timber.d("==adHorizontal   onNativeLoad: 个数" + list.size());
                    if (adHorizontalList != null) {
                        adHorizontalList.clear();
                    }
                    adHorizontalList = list;
                }
            }

            @Override
            public void onNativeFail(NativeErrorCode nativeErrorCode) {
//                Timber.d("==adHorizontal   NativeErrorCode: " + nativeErrorCode);
            }
        });
        adHorizontalRequestParameters = new RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
    }

    /**
     * 请求播放完成后的广告
     */
    private void adHorizontalfetch() {
        if (adHorizontalNative != null)
            adHorizontalNative.makeRequest(adHorizontalRequestParameters);
    }

    /**
     * 展现播放完成后的广告
     */
    private void adHorizontalShow() {
        if (adHorizontalList != null && adHorizontalList.size() > 0) {
            isHasAdHorizontal = true;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) rLayoutAdView.getLayoutParams();
            layoutParams.width = ArmsUtils.getScreenWidth(this);
            layoutParams.height = (int) (layoutParams.width * 9f / 16f);
            layoutParams.setMargins(0, 0, 0, 0);
            rLayoutAdView.setLayoutParams(layoutParams);
            rLayoutAdView.setVisibility(View.VISIBLE);
            NativeResponse nrAd = adHorizontalList.get(new Random().nextInt(adHorizontalList.size()));
            AQuery aQuery = new AQuery(rLayoutAdView);
            ivAdView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            aQuery.id(ivAdView).image(nrAd.getImageUrl(), false, true);
            aQuery.id(tvAdViewTitle).text(nrAd.getTitle());
            nrAd.recordImpression(rLayoutAdView);
            rLayoutAdView.setOnClickListener(nrAd::handleClick);
            updateCountDown();
        } else {
            adHorizontalfetch();//展现广告方法adViewShow()，发现没有广告，再次请求广告；
        }
    }

    /**
     * 移除播放完成后的广告
     */
    private void adHorizontalRemove() {
        if (isHasAdHorizontal) {//移除播放完成后的广告时，
            countDownHorizontal = 5;
            isHasAdHorizontal = false;
            if (handler != null)
                handler.removeCallbacks(adTimeRunnable);
            if (rLayoutAdView != null)
                rLayoutAdView.setVisibility(View.GONE);
            if (adHorizontalList != null)
                adHorizontalList.clear();
            adHorizontalfetch();//移除广告后 再次请求广告；
        }
    }

    /**
     * 倒计时
     */
    public void updateCountDown() {
        if (isHasAdHorizontal) {//广告倒计时
            if (countDownHorizontal > 0) {
                if (tvAdViewCount != null) {
                    tvAdViewCount.setOnClickListener(v -> adHorizontalRemove());//点击跳过按钮
                    tvAdViewCount.setText("跳过 " + countDownHorizontal);
                }
                if (handler != null) {
                    handler.postDelayed(adTimeRunnable, 1000);
                }
            }
        }
    }

    /**
     * Handler更新UI
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://金币爆裂效果
                    explosionCoin();
                    break;
                case 2://广告倒计时
                    if (tvAdViewCount != null)
                        tvAdViewCount.setText("跳过 " + countDownHorizontal);
                    break;
                case 3://关闭横版计时广告
                    adHorizontalRemove();//倒计时结束
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 广告计时Runnable
     */
    Runnable adTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (handler != null) {
                countDownHorizontal--;
                if (countDownHorizontal > 0) {
                    handler.sendEmptyMessage(2);
                    handler.postDelayed(adTimeRunnable, 1000);
                } else {
                    handler.sendEmptyMessage(3);
                }
            }
        }
    };


    /**
     * 清理视频观看记录
     */
    private void clearReadRecord() {
        MyApplication.get().getDaoSession().getVideoRecordBeanDao().deleteAll();
    }

    /**
     * 保存视频观看记录
     */
    private void saveReadRecord() {
        int scale = arcProgressScale - mRecordScale;
        VideoRecordBean recordBean = new VideoRecordBean();
        recordBean.setVScale(scale < 0 ? 0 : scale);
        recordBean.setVId(mCurrId);
        recordBean.setVType(mType);
        MyApplication.get().getDaoSession().getVideoRecordBeanDao().save(recordBean);
//        Timber.d("==============RecordScale mRecordScale: " + mRecordScale);
//        Timber.d("==============RecordScale arcProgressScale: " + arcProgressScale);
//        Timber.d("==============RecordScale save: " + recordBean.getVScale());
//        Timber.d("==============RecordScale ID: " + recordBean.getVId());
        if (arcProgressScale >= 100) {
            mRecordScale = 0;
            arcProgressScale = 0;
        } else {
            mRecordScale = arcProgressScale;//记录当前视频奖励的进度
        }
    }

    /**
     * 获取视频观看记录
     */
    private List<VideoRecordBean> getReadRecord() {
        List<VideoRecordBean> list = MyApplication.get().getDaoSession().getVideoRecordBeanDao().loadAll();
        return list == null ? new ArrayList<>() : list;
    }


    /**
     * 清空EditText内容
     */
    public void clearEditText() {
        if (etComment != null)
            etComment.setText("");
        if (etInputComment != null)
            etInputComment.setText("");
    }

    /**
     * 获取弹幕
     */
    public void requDanmu() {
        if (mPresenter != null) {
            mType = TextUtils.isEmpty(mType) ? "" : mType;
            mCurrId = TextUtils.isEmpty(mCurrId) ? "" : mCurrId;
            mLastId = TextUtils.isEmpty(mLastId) ? "" : mLastId;
            mPresenter.getBarrage(mCurrId, mType, "video", 50, mLastId);
        }
    }

    /**
     * 这里视频详情页点击下面列表 获取分享链接
     */
    public void getShareData(VideoBean bean) {
        mCurrId = bean.getVideoId();
        mCanVideoShare = bean.getCanShare();
        mWebShareUrl = bean.getWebUrl();
        mImageUrl = bean.getThumbUrl();
        mVideoShareContent = bean.getDescribe();
        mVideoShareTitle = bean.getTitle();
        VideoUrl = bean.getUrl();//视频地址
    }

    /**
     * 视频地址不为空 播放视频
     */
    public void playVideo() {
        try {
            completeCount = 0;
            mCommentTime = System.currentTimeMillis();
            DataSource dataSource = new DataSource();
            dataSource.setData(VideoUrl);
//            if (mPresenter != null)
//                if (!TextUtils.isEmpty(mPresenter.getToken()))
//                    dataSource.setTitle(mCanVideoShare);
//                        dataSource.setTag(videoPlayAdapter.getDataIndex(position).getAvatar());
            baseVideoView.stop();
            baseVideoView.setDataSource(dataSource); //设置数据源
            mReceiverGroup.getGroupValue().putObject(DataInter.Key.KEY_DATA_SOURCE, dataSource);

//            if (isWifi() || (isInternet() && NetworkUtils.isNetConnected(getApplicationContext()) && MyApplication.ignoreMobile)) {
            baseVideoView.start();
            //获取弹幕
            requDanmu();  //请求后台弹幕接口  12.21 祥光确定 if(){}判断可以放开；
//            }

        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (tasks_view != null && tasks_view.getVisibility() == View.GONE) {
            tasks_view.setVisibility(View.VISIBLE);
        }


    }

    /**
     * 清空所有弹幕相关
     */
    public void clearDanmu() {
        mOpenThread = false;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mPlayTimeDanmu != null)
            mPlayTimeDanmu = null;
        if (mBarrageBean != null) {
            mBarrageBean.clear();
        }

        if (danmuContainerView != null) {
            danmuContainerView.onDestroy();
            danmuContainerView.removeAllViews();
            danmuContainerView = null;
        }
        if (mDanmuAdapter != null) {
            mDanmuAdapter = null;
        }
    }

    /**
     * 清空所有动画相关
     */
    public void clearAnimation() {
        if (mOutPRAnimTranslation != null) {
            mOutPRAnimTranslation.removeAllListeners();
            mOutPRAnimTranslation.cancel();
            mOutPRAnimTranslation = null;
        }

        if (mEnterCommentAnimScaleX != null) {
            mEnterCommentAnimScaleX.removeAllListeners();
            mEnterCommentAnimScaleX.cancel();
            mEnterCommentAnimScaleX = null;
        }

        if (mEnterPRAnimTranslation != null) {
            mEnterPRAnimTranslation.removeAllListeners();
            mEnterPRAnimTranslation.cancel();
            mEnterPRAnimTranslation = null;
        }

        if (mOutCommentAnimScaleX != null) {
            mOutCommentAnimScaleX.removeAllListeners();
            mOutCommentAnimScaleX.cancel();
            mOutCommentAnimScaleX = null;
        }

        if (returnAnimSet != null) {
            returnAnimSet.removeAllListeners();
            returnAnimSet.cancel();
            returnAnimSet = null;
        }
        if (squareAnimSet != null) {
            squareAnimSet.removeAllListeners();
            squareAnimSet.cancel();
            squareAnimSet = null;
        }

        if (mSceneCommentSend != null) {
            mSceneCommentSend.exit();
            mSceneCommentSend = null;
        }
        if (mSceneCommentShare != null) {
            mSceneCommentShare.exit();
            mSceneCommentShare = null;
        }
        if (mSceneRootView != null) {
            mSceneRootView.removeAllViews();
        }
        if (flutteringLayout != null) {
            flutteringLayout.removeAllViews();
            flutteringLayout.clearAnimation();
        }
    }

    /**
     * 清空视频相关
     */
    public void clearVideo() {
        MyApplication.ignoreMobile = false;
        baseVideoView.stopPlayback();
        releaseVisualizer();
        AssistPlayer.get().removeReceiverEventListener(this);
        AssistPlayer.get().removePlayerEventListener(this);
        AssistPlayer.get().destroy();
    }

}