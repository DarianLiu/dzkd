package com.dzkandian.mvp.news.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.ChangeBounds;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.SideslipBaseActivity;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.common.broadcast.AdbBroadcastReceiver;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.uitls.animator.AnimatorPath;
import com.dzkandian.common.uitls.animator.PathEvaluator;
import com.dzkandian.common.uitls.animator.PathPoint;
import com.dzkandian.common.uitls.processes.AndroidAppProcess;
import com.dzkandian.common.uitls.processes.AndroidProcesses;
import com.dzkandian.common.widget.MobOneKeyShare;
import com.dzkandian.common.widget.ScrollWebView;
import com.dzkandian.common.widget.SoundPoolManager;
import com.dzkandian.common.widget.arcprogress.AnswerChartView;
import com.dzkandian.common.widget.barrageview.DanmuContainerView;
import com.dzkandian.common.widget.barrageview.FlutteringLayout;
import com.dzkandian.common.widget.barrageview.KeyboardStateObserver;
import com.dzkandian.common.widget.explosion.ExplosionField;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.common.ui.activity.MainActivity;
import com.dzkandian.mvp.news.contract.NewsDetailContract;
import com.dzkandian.mvp.news.di.component.DaggerNewsDetailComponent;
import com.dzkandian.mvp.news.di.module.NewsDetailModule;
import com.dzkandian.mvp.news.presenter.NewsDetailPresenter;
import com.dzkandian.mvp.news.ui.adapter.DanmuAdapter;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.ShareBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.news.BarrageBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsDanmuBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.news.NewsRecordBean;
import com.dzkandian.storage.event.DanmuEvent;
import com.google.gson.Gson;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.impl.cookie.DateUtils;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 新闻详情页 WebView
 */
public class NewsDetailActivity extends SideslipBaseActivity<NewsDetailPresenter> implements NewsDetailContract.View, View.OnClickListener {
    @BindView(R.id.fl_reward)
    FrameLayout flReward;
    @BindView(R.id.progress_reward)
    AnswerChartView progressReward;
    @BindView(R.id.fl_newsNormal)
    FrameLayout flNewsNormal;
    @BindView(R.id.iv_reward)
    ImageView ivReward;
    @BindView(R.id.tv_reward)
    TextView tvReward;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.news_empty)
    LinearLayout llNewsEmpty;

    @BindView(R.id.ll_web_load)
    ConstraintLayout llWebLoad;
    @BindView(R.id.iv_webload_ring)
    ImageView ivWebLoadRing;
    @BindView(R.id.iv_webload_light)
    ImageView ivWebLoadLight;
    @BindView(R.id.iv_webload_ufo)
    ImageView ivWebLoadUFO;
    @BindView(R.id.iv_webload_building)
    ImageView ivWebLoadBuilding;
    @BindView(R.id.iv_webload_bicycle)
    ImageView ivWebLoadBicycle;
    @BindView(R.id.iv_webload_tree_one)
    ImageView ivWebLoadTreeOne;
    @BindView(R.id.iv_webload_tree_two)
    ImageView ivWebLoadTreeTwo;
    @BindView(R.id.iv_webload_sun)
    ImageView ivWebLoadSun;


    @BindView(R.id.danmuContainerView)
    DanmuContainerView mDanmuContainerView;   //弹幕布局

    @BindView(R.id.flutteringLayout)
    FlutteringLayout flutteringLayout;   //弹幕点赞动画

    /**
     * 评论布局切换相关
     */
    @BindView(R.id.fl_news_comment)
    ViewGroup mSceneRootView;

    ScrollWebView webView;

    private boolean isRun = true;//奖励进度条是否还在跑20刻度
    private int arcProgressScale = 0;//奖励进度条刻度

    private int mRecordScale = 0;//当前资讯阅读奖励的进度

    private ExplosionField explosionField;//爆裂效果
    private boolean isOne = true;//是否第一次爆裂

    /*需要上传的参数*/
    private String mId = "";
    private String mType = "";

    private String mUrl = "";   //用来跟后台换的url
    private String mShareType; //后台或页端返回的分享类型
    private String mShareUrl; //后台返回的分享链接
    private String mCanShare; //是否可以分享
    private String mTitleShare = ""; //分享标题
    private String mImageShare; //分享图片
    private String mContentShare; //分享描述

    private String mWebUpApp;         //网页打开APP进入视频页
    private String mPushUpApp;         //推送打开APP进入新闻详情页
    private String mTextSize;//字体大小

    private static IWXAPI api; // IWXAPI 是第三方app和微信通信的openapi接口
    private String mImageUserPath = "";

    private LoadingProgressDialog loadingProgressDialog;

    private String mUserId;  //用户Id
    private int textZoom;   //设置网页布局大小
    private int readCount = 2;   //后台返回的进度条圈数
    private int completeCount;//当前执行的进度条圈数

    private int maxSameTouchAreaCount = -1;//最大相同触摸面积次数
    private int touch_count = 0;//相同触摸面积执行次数
    private float last_touch_area;//最后一次触摸面积
    private int haveTouchHardware;//是否有触摸硬件（触摸面积不为0）
    private int isCheat = 0;//是否作弊(0为否，1为是)
    private MobOneKeyShare oneKeyShare;

    private String first_touch_area; //上一次保存的触摸面积

    private List<String> mProcessPackList = new ArrayList<>();  //手机后台正在运行的程序
    private List<String> mServicePackList = new ArrayList<>();  //后台返回的作弊程序
    private StringBuffer mUploadPack = new StringBuffer();  //上传给后台的作弊程序
    private List<NewsRecordBean> mRecordList = new ArrayList<>();//资讯浏览记录
    private int mRecordNo = 0;//当前阅读的位置（从原生列表进入，当前看的第几篇资讯）
    private int mRecordAction = 1;//标识原生列表进入
    private boolean isBack;//是否后退到原生资讯

    private AdbBroadcastReceiver mReceiver;  //广播接收器
    private int isConnentADB = 0;  //是否连接电脑并且开启ADB调试
    private boolean mEnableAdb;     //是否开启ADB

    private boolean isFirst = true;//是否是第一次打开文章

    /**
     * 弹幕相关
     */
    private DanmuAdapter mDanmuAdapter;//弹幕Adapter

    private String isShowComment;//是否显示弹幕
    private long mCommentTime; //资讯加载成功至发表评论的阅读时长

    private int mReadGoldPercent = 0;//阅读奖励结束进度(0-99)
    private List<BarrageBean> mBarrageBean;  //弹幕相关所有数据

    private int position = 0; //弹幕显示位置
    private int danmuCurrPosition;       //弹幕当前点击的位置

    private boolean isShowKeyBoare; //有没有显示软键盘
    private String mInputString = ""; //用户输入好的字符
    private String mComment;
    private boolean mOpenThread; //是否开启线程

    private boolean mShow;   //进度条是否转满
    private boolean noAd;   //有没有JS交互  没有则是广告
    private boolean mNotComment; //是否有评论
    private mPlayTimeDanmu mPlayTimeDanmu;


    /**
     * 动画相关
     */
    private AnimatorSet squareAnimSet, returnAnimSet;
    private long onClickShareTime;//上一次点击分享的时间
    private long onClickCollectionTime;//上一次点击收藏的时间
    private long onClickbtnSendTime;//上一次点击发送的时间
    private long onClickCommentTime;//上一次点击开启弹幕的时间

    private int mShowCount;// 第一批弹幕显示完 下一批出来
    private Timer mTimer; //弹幕运行线程
    private int delayedPotisiton; //延时次数  每次为2秒

    private Scene mSceneCommentShare;   //显示评论分享布局
    private Scene mSceneCommentSend;    //显示评论发送布局
    private EditText etComment;                 //评论输入框
    private TextView tvCommentCount;            //评论条数
    private ImageView ivCommentBarrage;         //开启弹幕
    private ImageView ivCommentCollection;      //收藏
    private ImageView ivCommentShare;           //分享

    private EditText etInputComment;            //发送评论布局输入框
    private Button btnSend;                     //发送评论布局发送按钮

    private int mHideSoftKeyHeight = -1;   //是否要减虚拟键

    private boolean mIsLoading = true;

    private static final int MSG_COIN_EXPLOSION_TAG = 1;
    private static final int MSG_DANMU_TAG = 2;
    private AnimatorSet animatorSetWebLoad;//原生网页加载动画；

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerNewsDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newsDetailModule(new NewsDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_news_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    private void highApiEffects() {
        //        //透明状态栏 @顶部
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.);
        //透明导航栏 @底部    这一句不要加，目的是防止沉浸式状态栏和部分底部自带虚拟按键的手机（比如华为）发生冲突，注释掉就好了
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        highApiEffects();

        initValueAnim();

        webView = new ScrollWebView(this);
        webView.setBackgroundColor(0);
        flNewsNormal.addView(webView);
        mRecordList = getReadRecord();
        clearReadRecord();//清理资讯阅读记录

        initAnimator();//初始化进度条与评论模块动画

        initTouch();//初始化触摸硬件

        initSceneAnimation();  //初始化评论布局动画

        initShare(); //分享相关

        //得到当前界面的装饰视图
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //设置系统UI元素的可见性
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            //设置状态栏颜色
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));

        }


        //获取弹幕
        getComment();

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

    /**
     * 初始化分享相关
     */
    public void initShare() {
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
        explosionField = ExplosionField.attach2Window(this);

        //初始化一键分享页面
        oneKeyShare = new MobOneKeyShare(this);

        isInternetGetIntent(getIntent());  // 判断是否有网  并获取Intent传过来的值加载网页

        queryDeviceInfo();//进入界面后 获取数据库


        //评论布局 分享按钮是否能分享
        if (TextUtils.equals(mCanShare, "1")) {
            ivCommentShare.setEnabled(true);
        } else {
            ivCommentShare.setEnabled(false);
        }
    }


    //初始化触摸硬件
    public void initTouch() {
        //是否有触摸硬件（触摸面积不为0）
        haveTouchHardware = DataHelper.getIntergerSF(getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE);

        first_touch_area = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_TOUCH_AREA);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() > 0 && (charSequence.toString().substring(0, 1).equals(" ")
                    || charSequence.toString().substring(0, 1).equals("\n"))) {
                clearEditText(); //清空EditText字符内容
            }
            if (etInputComment != null && btnSend != null && !TextUtils.isEmpty(etInputComment.getText().toString())) {
                btnSend.setTextColor(getResources().getColor(R.color.colorPrimary));
                mInputString = etInputComment.getText().toString();
            } else if (etInputComment != null && btnSend != null && TextUtils.isEmpty(etInputComment.getText().toString())) {
                btnSend.setTextColor(getResources().getColor(R.color.color_text_tip));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * 弹幕相关
     */
    public void getComment() {
        mBarrageBean = new ArrayList<>();
        mDanmuAdapter = new DanmuAdapter(this.getApplicationContext());
        mDanmuContainerView.setAdapter(mDanmuAdapter);

        mDanmuContainerView.setGravity(DanmuContainerView.GRAVITY_FULL);
    }

    /**
     * 清空输入框所有数据
     */
    public void clearEditText() {
        if (etComment != null)
            etComment.setText("");
        if (etInputComment != null)
            etInputComment.setText("");
    }

    /**
     * 接收弹幕点击事件 去调用点赞评论接口
     */
    @Subscriber(tag = EventBusTags.TAG_NEWS_COMMENT_THUBMS_UP)
    private void commentThubmsUp(String position) {
        if (!TextUtils.isEmpty(position) && mPresenter != null && mBarrageBean != null) {
            danmuCurrPosition = Integer.parseInt(position);
            mPresenter.commentThumbsUp(mBarrageBean.get(Integer.parseInt(position)).getId(), "news");
            //Timber.d("=============" + position);
        }
    }

    /**
     * 接收弹幕点击事件 显示点赞动画
     */
    @Subscriber(tag = EventBusTags.TAG_NEWS_ANIMATION_THUBMS_UP)
    private void commentThubmsUpAnimation(DanmuEvent event) {
        if (flutteringLayout != null) {
            if (isShowKeyBoare) {
                if (mHideSoftKeyHeight == 0) {
                    flutteringLayout.updateDanmuView(event.getViewX(), event.getViewY() + getKeyboardHeight()); //设置爱心出来坐标位置
                } else if (mHideSoftKeyHeight > 0) {
                    int y = event.getViewY() + getKeyboardHeight() - getNavigationBarHeight(this);
                    //Timber.d("=======================点击软键盘的高度" + getKeyboardHeight() + "      虚拟键的高度" + getNavigationBarHeight(this) + "     " + y);
                    flutteringLayout.updateDanmuView(event.getViewX(), y); //设置爱心出来坐标位置
                }

            } else {
                if (mHideSoftKeyHeight == 0) { //软键隐藏状态下 获取软键盘为0时
                    flutteringLayout.updateDanmuView(event.getViewX(), event.getViewY()); //设置爱心出来坐标位置
                } else if (mHideSoftKeyHeight > 0) {
                    flutteringLayout.updateDanmuView(event.getViewX(), event.getViewY()); //设置爱心出来坐标位置
                }
                //Timber.d("=======================点击软键盘的高度" + getKeyboardHeight() + "      虚拟键的高度" + getNavigationBarHeight(this));

            }
            flutteringLayout.addHeart(); //添加爱心动画
        }
    }


    /**
     * 获取软键盘的高度
     */
    public int getKeyboardHeight() {
        View decorView = NewsDetailActivity.this.getWindow().getDecorView();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        //计算出可见屏幕的高度
        int displayHight = rect.bottom - rect.top;
        //获得屏幕整体的高度
        int height = decorView.getHeight();
        //获得键盘高度
        return height - displayHight;
    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid > 0) {
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
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
            //Timber.d("===点赞异常情况  是否能点击" + mBarrageBean.get(danmuCurrPosition).getCanThumbsUp());
        }
    }

    /**
     * 弹幕点赞成功
     *
     * @param count 点赞第几次
     */
    @Override
    public void thumbsUpSuccess(int count) {
        if (count <= 0) {
            mBarrageBean.get(danmuCurrPosition).setCanThumbsUp(false); //没有点击次数 设置不可点击
        } else {
            mBarrageBean.get(danmuCurrPosition).setCanThumbsUp(true);  //允许点击
        }

        String thumbsUp = mBarrageBean.get(danmuCurrPosition).getThumbsUpCount(); //获取点击总数

        if (!TextUtils.isEmpty(thumbsUp)) {
            int thubmsUpCount = Integer.parseInt(thumbsUp);
            thubmsUpCount++;
            mBarrageBean.get(danmuCurrPosition).setThumbsUpCount(String.valueOf(thubmsUpCount));
            mDanmuAdapter.updateDanmuView(thubmsUpCount, mBarrageBean.get(danmuCurrPosition).getCanThumbsUp(), true);
        }
    }

    /**
     * 评论成功
     */
    @Override
    public void commentSuccess() {
        if (!mNotComment) {  //如果弹幕有数据 正在显示则添加进去
//            NewsDanmuBean newsDanmuBean = new NewsDanmuBean();
//            newsDanmuBean.setContent(mComment);
//            mDanmuContainerView.addDanmu(newsDanmuBean);
            BarrageBean barrageBean = new BarrageBean();
            barrageBean.setContent(mComment);
            mBarrageBean.add(barrageBean);
        } else {// 如果弹幕为空 则新开线程显示弹幕
//            NewsDanmuBean newsDanmuBean = new NewsDanmuBean();
//            newsDanmuBean.setContent(mComment);
//            mDanmuContainerView.addDanmu(newsDanmuBean);
            BarrageBean barrageBean = new BarrageBean();
            barrageBean.setContent(mComment);
            mBarrageBean.add(barrageBean);
            mOpenThread = true;
            isShowComment(1);
        }
        clearEditText(); //清空EditText字符内容
        mInputString = "";
        showShareScene();
        hideKeyboard();
        showMessage("发布成功，优质评论将被优先展示");
    }

    @Override
    public void collectionValue(boolean value) {
        if (value) {
            showMessage("已收藏");
            ivCommentCollection.setSelected(true);
        } else {
            showMessage("已取消收藏");
            ivCommentCollection.setSelected(false);
        }
    }

    /**
     * 弹幕获取成功
     *
     * @param newBarrageBean 资讯弹幕
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void loadBarrage(NewBarrageBean newBarrageBean) {

        if (Integer.parseInt(newBarrageBean.getCmtCount()) > 999) {
            tvCommentCount.setVisibility(View.VISIBLE);
            tvCommentCount.setText("999+");
        } else if (Integer.parseInt(newBarrageBean.getCmtCount()) > 0) {
            tvCommentCount.setVisibility(View.VISIBLE);
            tvCommentCount.setText(newBarrageBean.getCmtCount());
        } else if (Integer.parseInt(newBarrageBean.getCmtCount()) == 0) {
            mNotComment = true;
        }


        //Timber.d("===========弹幕获取成功" + cotent);
        mBarrageBean.addAll(newBarrageBean.getBarrageBeans());
        //Timber.d("=========弹幕多少 mBarrages.size()" + mBarrages.size());

        mOpenThread = true;
        if (TextUtils.equals(isShowComment, "hide")) {
            ivCommentBarrage.setSelected(false);
            isShowComment(0);
        } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "show")) {
            ivCommentBarrage.setSelected(true);
//            isShowComment(1);
        }
        if (newBarrageBean.getCollection() == 1) {
            ivCommentCollection.setSelected(true);
        } else {
            ivCommentCollection.setSelected(false);
        }
    }


    /**
     * 是否开始弹幕
     */
    public void startDanmu(boolean play) {
        if (play) {
            try {
                if (mPlayTimeDanmu == null)
                    mPlayTimeDanmu = new mPlayTimeDanmu();
                if (mTimer == null) {
                    mTimer = new Timer();
                    mTimer.schedule(mPlayTimeDanmu, 0, 2000);
                }
                //Timber.d("========弹幕多少 mBarrages.size()" + mBarrages.size());
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
    public class mPlayTimeDanmu extends TimerTask {

        @Override
        public void run() {
            if (mShowCount >= mBarrageBean.size()) {
                delayedPotisiton++;
                if (delayedPotisiton >= 3) {
                    //Timber.d("========弹幕多少 8秒");
                    mShowCount = 0;
                    position = 0;
                    delayedPotisiton = 0;
                }
            } else {
                //Timber.d("========弹幕多少 2秒");
                if (myHandler != null) {
                    myHandler.sendEmptyMessage(MSG_DANMU_TAG); //开启弹幕线程
                }
            }
        }
    }


    @Override
    public void onPanelOpened(View panel) {
        killMyself();
    }

    /**
     * 判断是否有网  并获取Intent传过来的值加载网页
     */
    public void isInternetGetIntent(Intent intent) {

        //   showWebLoading(true);//显示Web加载动画
        Uri uri = intent.getData();
        if (uri != null) {
            getWebValue(uri);
        } else {
            getIntentValue(intent);
        }

        init();

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        } else {
            cookieManager.setAcceptCookie(true);
        }


        webView.setWebViewClient(new NewsWebViewClient());//设置Web视图
        if (!isInternet()) {
            showErrorView();
        } else {
            showWebLoading(true);
            webView.loadUrl(mUrl);//加载需要显示的网页
        }
    }

    /**
     * 初始化控件、WebView
     */
    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        /*奖励进度条相关*/

        //每次进入新闻详情页 更新进度条
        arcProgressScale = DataHelper.getIntergerSF(getApplicationContext(), Constant.SP_KEY_NEWS_PROGRESS_SCALE);
        mRecordScale = arcProgressScale;
        //Timber.d("==News" + arcProgressScale);
        if (arcProgressScale == -1) {
            arcProgressScale = 0;
            mRecordScale = 0;
        }
        progressReward.setInnerProgress(arcProgressScale);

        assert webView != null;
        WebSettings webSettings = webView.getSettings();//设置WebView属性
        webView.setEnabled(true);
        webView.setVerticalScrollBarEnabled(false);//去掉垂直滚动条
        webSettings.setJavaScriptEnabled(true);//设置WebView属性，能够执行Javascript脚本
        webView.addJavascriptInterface(new JavaScripObject(), "android");

//        webSettings.setAllowFileAccess(false);//设置可以访问文件
        if (!TextUtils.isEmpty(mTextSize)) {
            switch (mTextSize) {
                case "small":
                    textZoom = 100;
                    webSettings.setTextZoom(100);
                    break;
                case "medium":
                    textZoom = 112;
                    webSettings.setTextZoom(112);
                    break;
                case "big":
                    textZoom = 124;
                    webSettings.setTextZoom(124);
                    break;
            }
        } else {
            textZoom = 112;
            webSettings.setTextZoom(112);
        }

        //提高渲染等级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setBuiltInZoomControls(false); //设置支持缩放

        //开启WebView缓存   销毁界面时要清除缓存
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);//开启DOM缓存，关闭的话H5自身的一些操作是无效的
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setBlockNetworkImage(false);//先加载文字，再加载图片

        //播放视频  支持加载http和https混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        /* 添加网页进度条事件：*/
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(NewsDetailActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            //设置响应js 的Confirm()函数
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(NewsDetailActivity.this);
                b.setTitle("Confirm");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
                b.setNegativeButton(android.R.string.cancel, (dialog, which) -> result.cancel());
                b.create().show();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                Timber.d("-============onProgressChanged：" + newProgress);
                if (newProgress >= 75) {
                    showNormalView();//显示网页视图
//                    showWebLoading(false);
//                    webSettings.setBlockNetworkImage(true);

                }
            }
        });

        //(显示/隐藏 按钮  暂时去掉)
        isShowButton();


        /*WebView触摸监听事件：*/
        webView.setOnTouchListener((view, motionEvent) -> {
            Timber.d("-============onTouch");
            if (maxSameTouchAreaCount > 0) {
                checkIsCheat(motionEvent.getSize());
            }

            //根据软键盘状态 显示隐藏布局
            keyBoareState();

            if (isRun) {
                if (arcProgressScale >= 100) {//必须大于等于100；
                    arcProgressScale = 0;
                }
                Timber.d("-============onTouch: " + mIsLoading);
                if (mPresenter != null && !mIsLoading) {
                    isRun = false;
                    mPresenter.timing();
                    Timber.d("-============timing: " + mIsLoading);
                }

            }
            return false;
        });
        /*WebView文件下载监听事件：*/
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

    }

    /*
     *获取网页端传过来的值
     */
    private void getWebValue(Uri uri) {
        mWebUpApp = uri.getScheme();//用于区分是否web唤醒打开
        mUrl = uri.getQueryParameter("webUrl");//资讯、视频网页url;
        mId = uri.getQueryParameter("id");//资讯、视频网页ID;
        mTitleShare = uri.getQueryParameter("title");//资讯、视频网页标题;
        mType = uri.getQueryParameter("tab");//资讯、视频频道类型;
        mTextSize = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_TEXTSIZE);
        mCanShare = uri.getQueryParameter("canShare");//能否分享;
    }

    /**
     * 获取Adapter传过来的值
     */
    private void getIntentValue(Intent intent) {
        mUrl = intent.getStringExtra("web_url");
        mId = intent.getStringExtra("id");
        mType = intent.getStringExtra("tab");
        mCanShare = intent.getStringExtra("is_share");
        mTitleShare = intent.getStringExtra("title");
        mImageShare = intent.getStringExtra("IMAGES");
        mPushUpApp = intent.getStringExtra("mPushUpApp");
        mTextSize = intent.getStringExtra("textSize");
        mWebUpApp = intent.getStringExtra("scheme");
        //Timber.d("==================mUrl  ：" + mUrl);
        //Timber.d("==================id  ：" + id);
        //Timber.d("==================mPushUpApp  ：" + mPushUpApp);
        //Timber.d("==================mCanShare  ：" + mCanShare);
        //Timber.d("==================mTitleShare  ：" + mTitleShare);
        //Timber.d("==================mImageShare  ：" + mImageShare);
        //Timber.d("==================mTextSize  ：" + mTextSize);
    }


    /**
     * 没有登录  不可分享
     */
    @Override
    public void upView() {
//        assert ivShareRight != null;
//        ivShareRight.setVisibility(View.GONE);
    }


    /**
     * 获取到资讯分享链接  进行分享
     *
     * @param newsOrVideoShareBean 分享数据实体
     */
    @Override
    public void newsShare(NewsOrVideoShareBean newsOrVideoShareBean) {
        //Timber.d("==================后台返回的url:" + newsOrVideoShareBean.getUrl());
        //Timber.d("==================后台返回的mShareType:" + newsOrVideoShareBean.getType());
        //Timber.d("==================后台返回的getContent" + newsOrVideoShareBean.getContent());
        //Timber.d("==================后台返回的getTitle:" + newsOrVideoShareBean.getTitle());
        //Timber.d("==================后台返回的     http:" + newsOrVideoShareBean.getBigPicUrl());
        mContentShare = newsOrVideoShareBean.getContent();
        mTitleShare = newsOrVideoShareBean.getTitle();
        mImageShare = newsOrVideoShareBean.getBigPicUrl();
        if (!mImageShare.startsWith("http") && !TextUtils.isEmpty(mImageShare)) {
            mImageShare = "http:" + mImageShare;
        }
        mShareUrl = newsOrVideoShareBean.getUrl();
        mShareType = newsOrVideoShareBean.getType();
        oneKeyShare.setShareContent(new ShareBean.Builder()
                .title(mTitleShare)
                .content(mContentShare)
                .imagePath(mImageUserPath)
                .imageUrl(mImageShare)
                .pageUrl(mShareUrl)
                .weChatShareType(mShareType)
                .weChatMomentsShareType(mShareType)
                .qqShareType(mShareType)
                .qZoneShareType(mShareType)
                .sinaShareType(mShareType)
                .create());
        //Timber.d("==================mContentShare:" + mContentShare);
        if (!TextUtils.isEmpty(mShareUrl)) {
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
            //新闻圈数获取；
            if (deviceInfoBean.getReadGoldCircles() > 0) {
                readCount = deviceInfoBean.getReadGoldCircles();
            }
            //Timber.d("==news  新闻圈数  ：" + readCount);

            //后台返回的作弊信息包名
            getServiceCheatPackage(deviceInfoBean);

            //最大相同触摸面积次数
            if (deviceInfoBean.getMaxSameTouchAreaCount() != null)
                maxSameTouchAreaCount = deviceInfoBean.getMaxSameTouchAreaCount();

            if (deviceInfoBean.getReadGoldPercent() != null && deviceInfoBean.getReadGoldPercent() > 0) {
                mReadGoldPercent = deviceInfoBean.getReadGoldPercent();
            }
        }
    }

    //后台返回的作弊信息包名
    public void getServiceCheatPackage(DeviceInfoBean deviceInfoBean) {
        if (!TextUtils.isEmpty(deviceInfoBean.getRiskDdeviceNname())) {
            String process = deviceInfoBean.getRiskDdeviceNname();
            if (!TextUtils.isEmpty(process)) {
                String temp[] = process.split(";");
                mServicePackList.addAll(Arrays.asList(temp));
            }
        }
    }

    /**
     * 获取数据库的用户信息
     */
    private void queryUserInfo() {
        List<UserInfoBean> list = MyApplication.get().getDaoSession().getUserInfoBeanDao().loadAll();
        if (list != null && list.size() > 0) {
            //Timber.d("=db=    NewsDetailActivity - UserInfo - query 成功");
            dbUpdateUserAvatar(list.get(0));
        }
    }

    /**
     * @param userInfoBean 获取到数据库的用户信息后，更新界面
     */
    public void dbUpdateUserAvatar(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            mUserId = String.valueOf(userInfoBean.getUserId());
            if (!TextUtils.isEmpty(userInfoBean.getAvatar())) {
                if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME) && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
                    //Timber.d("==shareMine  :" + "有登录，有图片：" + Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME);
                    mImageUserPath = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
                } else {
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
        mImageUserPath = filePath;
    }


    /**
     * 检测是否作弊
     *
     * @param touch_area 触摸面积
     */
    private void checkIsCheat(float touch_area) {
        //检测是否有触摸硬件
        if (haveTouchHardware != 1 && touch_area != 1 && touch_area != 0) {//有触摸硬件
            if (TextUtils.isEmpty(first_touch_area)) {
                DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_TOUCH_AREA, String.valueOf(touch_area));
            } else if (!TextUtils.equals(first_touch_area, String.valueOf(touch_area))) {
                haveTouchHardware = 1;
                DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_HAVE_TOUCH_HARDWARE, haveTouchHardware);
            }
        }

        //Timber.d("========作弊touch_area：" + touch_area + " 重复次数: " + touch_count);

        if (haveTouchHardware != 1) {
            isCheat = 0;
        } else if (isCheat == 1) {
            isCheat = 1;
        } else if (touch_area == 0) {
            isCheat = 1;//有触摸硬件，触摸面积为0，则定性为作弊
            //Timber.d("========作弊：" + "有触摸硬件，触摸面积为0");
        } else if (touch_count >= maxSameTouchAreaCount) {
            //Timber.d("========作弊：" + "相同触摸面积触摸次数达到最大数" + touch_count);
            isCheat = 1;//有触摸硬件，相同触摸面积触摸次数达到最大数，则定性为作弊
            touch_count = 0;
        } else if (touch_area != last_touch_area) {
            //有触摸硬件、触摸面积不为0，且与上次触摸面积不同，更新最新触摸面积进行下次比对（清空相同触摸面积次数）
            last_touch_area = touch_area;
            touch_count = 0;
        } else if (touch_area == last_touch_area) {
            touch_count++;
        } else {
            touch_count = 0;
            isCheat = 0;
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
        try {
//            isfinish = true;
            webView.evaluateJavascript("window.uploadReadTime();", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(mWebUpApp)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("app_status", Constant.APP_STATUS_RESTART);
            launchActivity(intent);
        }
        if (!TextUtils.isEmpty(mPushUpApp)) {
            Intent intent = new Intent(this, MainActivity.class);
            launchActivity(intent);
        }
        mOpenThread = false;
        finish();
    }

    @Override
    protected void onPause() {
        SoundPoolManager.getInstance(getApplicationContext()).stopRinging();
        if (mReceiver != null) { //解绑广播
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        isShowComment(0);
        try {
            webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();

        webView.onPause();
    }


    @Override
    protected void onResume() {
        //注册广播
        mEnableAdb = (Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
        if (mEnableAdb) { //如果开启了USB调试才开启广播
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.SP_KEY_ACTION);
            if (mReceiver == null) {
                mReceiver = new AdbBroadcastReceiver();
                registerReceiver(mReceiver, filter);
            }
        }

        if (TextUtils.equals(isShowComment, "hide")) {
            ivCommentBarrage.setSelected(false);
            isShowComment(0);
        } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "show")) {
            ivCommentBarrage.setSelected(true);
            isShowComment(1);
        }

        try {
            webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onResume();

        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        //保存资讯阅读记录
        saveReadRecord();
        MyApplication.get().getDaoSession().getNewsRecordBeanDao().insertInTx(mRecordList);

        clearDanmu(); //清空所有弹幕相关
        clearWebView();//销毁WebView
        clearAnimation();//清空所有动画

        hideKeyboard(); //隐藏软键盘

        explosionField = null;
        SoundPoolManager.getInstance(getApplicationContext()).release();
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }

        if (textWatcher != null && etComment != null && etInputComment != null) {
            etComment.removeTextChangedListener(textWatcher);
            etInputComment.removeTextChangedListener(textWatcher);
            textWatcher = null;
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mProcessPackList.clear();
        mServicePackList.clear();

        oneKeyShare.destroy();
        //奖励进度条数值保存
        DataHelper.setIntergerSF(getApplicationContext(), Constant.SP_KEY_NEWS_PROGRESS_SCALE, mRecordScale);

        if (api != null) {
            api.detach();
            api = null;
        }

        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;

        }

        if (animatorSetWebLoad != null && (animatorSetWebLoad.isStarted() || animatorSetWebLoad.isRunning()))
            animatorSetWebLoad.end();
        super.onDestroy();
    }


    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isBack = true;
            if (webView != null && webView.canGoBack()) {
                //设置缓存模式：无论是否有网络，只要本地有缓存，都使用缓存。本地没有缓存时才从网络上获取
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                //Timber.d("=============Record: goBack");
                webView.goBack(); // goBack()表示返回WebView的上一页面
                return true;
            } else {
                killMyself();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        completeCount = 0;
        progressReward.setVisibility(View.VISIBLE);
        isInternetGetIntent(intent);// 判断是否有网  并获取Intent传过来的值加载网页
        //Timber.d("================什么时候执行onNewIntent");
    }


    /*更新进度条*/
    @Override
    public void updateArcProgress(boolean complete) {
        if (complete) {
            isRun = true;
        } else {
            if (completeCount < readCount) {
                arcProgressScale++;

                if (progressReward != null)
                    progressReward.setInnerProgress(arcProgressScale);
                if (arcProgressScale == 100) {//奖励进度条100时领取奖励
                    saveReadRecord();//保存资讯阅读记录
                    String aList = new Gson().toJson(mRecordList);
                    mRecordList.clear();//清理资讯阅读记录

                    arcProgressScale = 0;
                    mProcessPackList.clear(); //每次获取前先清空上次保存的数据
                    mUploadPack.delete(0, mUploadPack.length()); //清空上次保存的数据

                    uploadCheatApp(); //上传后台作弊运行APP
                    checkConnectAdb();//检测是否连接ADB并开启USB调试模式


                    completeCount++;  //进度条满一圈  次数加1
                    //Timber.d("=============进度条到100  completeCount" + completeCount + "  readCount" + readCount);

                    if (isInternet()) {
                        if (mPresenter != null)
                            mPresenter.readingReward(String.valueOf(System.currentTimeMillis()),
                                    mId, mType, isCheat, Build.MODEL, isConnentADB, "",
                                    mUploadPack.length() > 1 ? 1 : 0, mUploadPack.toString(), aList);
                    } else {
                        arcProgressScale = 0;
                        if (progressReward != null)
                            progressReward.setInnerProgress(arcProgressScale);
                        showMessage("连接网络可领取奖励");
                    }
                }
            } else if (completeCount == readCount && arcProgressScale <= mReadGoldPercent) {
                arcProgressScale++;
                if (progressReward != null)
                    progressReward.setInnerProgress(arcProgressScale);
                if (arcProgressScale >= mReadGoldPercent) {
                    if (progressReward != null) {
                        progressReward.setAlpha(0f);
                    }
//                        progressReward.setVisibility(View.GONE);
                    mShow = true;   //进度条转完消失后 输入框布局一直显示
                    if (progressReward != null && mSceneRootView != null && mSceneRootView.getVisibility() == View.GONE && noAd) {  //当输入框布局没显示时才执行显示动画
                        showCommentLayout();//隐藏进度条 显示评论布局
                        isShowComment(0);
                    }
                }
            }
        }
    }


    /**
     * 获取后台运行APP 并上传服务器
     */
    public void uploadCheatApp() {
        //每次进度条满了之后获取后台正在运行的程序包名
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        //Timber.d("==============processes" + processes.size());
        for (int i = 0; i < processes.size(); i++) {
            //Timber.d("==============processes" + processes.get(i).name);
            mProcessPackList.add(processes.get(i).name);
        }


        //双重for循环 后台返回的作弊包名遍历正在运行的包名  有没有作弊APP
        for (int i = 0; i < mServicePackList.size(); i++) {
            String pack = mServicePackList.get(i);
            for (int j = 0; j < mProcessPackList.size(); j++) {
                if (mProcessPackList.get(j).contains(pack)) {
                    mUploadPack.append(mProcessPackList.get(j)).append(";");
                    //Timber.d("==============processes      " + mUploadPack);
                }
            }
        }
    }

    /**
     * 检测是否连接ADB并开启USB调试模式
     */
    public void checkConnectAdb() {
        mEnableAdb = (Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
        if (mEnableAdb) {
            if (mReceiver == null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Constant.SP_KEY_ACTION);
                mReceiver = new AdbBroadcastReceiver();
                registerReceiver(mReceiver, filter);
            }
            isConnentADB = mReceiver.isConnectAdb();
            //Timber.d("==============连接" + mReceiver.isConnectAdb());
        }

    }

    /**
     * 接收从登录界面的登录事件，只要登录了，进度条为100就再次发起领取阅读奖励；
     */
    @Subscriber(tag = EventBusTags.TAG_LOGIN_STATE)
    private void receiveLoginState(boolean isLogin) {
        if (isLogin) {
            mDanmuAdapter.updateDanmuView(Integer.parseInt(mBarrageBean.get(danmuCurrPosition).getThumbsUpCount())
                    , true, false);
            queryDeviceInfo();//登录界面返回后 获取数据库

            if (progressReward.getProgress() == 100) {
                if (isInternet()) {
                    arcProgressScale = 0;
                    if (progressReward != null)
                        progressReward.setInnerProgress(arcProgressScale);
//                    assert mPresenter != null;
//                    mPresenter.readingReward(String.valueOf(System.currentTimeMillis()), id, type, isCheat, Build.MODEL);
                }
            }
        }
    }

    /*判断当前是否有网络*/

    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    /*阅读获得收益后爆裂效果*/
    @Override
    public void readingRewardInt(Integer integer) {
        touch_count = 0;//清空计数
        isCheat = 0;//作弊状态默认设为0

        flReward.setVisibility(View.VISIBLE);
        tvReward.setText(String.valueOf(integer));
        if (progressReward != null)
            progressReward.setInnerProgress(0);
        if (isOne) {
            explosionAnim();
            isOne = false;
        } else {
            setYueDu();
        }
    }


    /*金币领取爆裂效果*/
    private void explosionAnim() {
        myHandler.sendEmptyMessageDelayed(MSG_COIN_EXPLOSION_TAG, 2000);
    }


    /*时段奖励金币领取图恢复加爆裂*/
    private void setYueDu() {
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
            root.setScaleX(1f);
            root.setScaleY(1f);
            root.setAlpha(1f);
        }
    }

    /**
     * 上拉下滑 显示/隐藏 按钮
     */
    public void isShowButton() {

        webView.setOnScrollChangeListener(new ScrollWebView.OnScrollChangeListener() {
            @Override
            public void onPageEnd(int mScrollX, int mScrollY, int oldX, int oldY) {
            }

            @Override
            public void onPageTop(int mScrollX, int mScrollY, int oldX, int oldY) {
            }

            @Override
            public void onScrollChanged(int mScrollX, int mScrollY, int oldX, int oldY) {

                //进度条没显示  并且次数不足才显示
//                if (progressReward != null && !progressReward.isShown() && completeCount < readCount) {
//                    progressReward.setVisibility(View.VISIBLE);
//                }

            }
        });

    }


    //Web视图    刚进入Web时调用
    private class NewsWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            completeCount = 0;
//            boolean b = hybridAdManager.shouldOverrideUrlLoading(view, url);
//            if (b) {
//                return true;
//            } else {
            return !(url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://"));
//            }
//            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // 一定要在onPageStarted调用百度广告SDK的onPageStarted
//            hybridAdManager.onPageStarted(webView, url, favicon);
            super.onPageStarted(view, url, favicon);

            if (isFirst) {
                mRecordNo++;//当前页面;
                isFirst = false;
            } else {
                if (!mIsLoading && !isWebAd(url))
                    showWebLoading(true);

                //保存阅读记录
                saveReadRecord();
                if (isBack) {//返回上一页
                    if (TextUtils.isEmpty(mId)) {
                        //广告页返回
                        //Timber.d("==============saveReadRecord 广告页返回 当前位置：" + mRecordNo);
//                        return;
                    } else {
                        //资讯页返回上一页
                        mRecordNo--;
                        isBack = false;
                        //Timber.d("==============saveReadRecord 资讯页返回 当前位置：" + mRecordNo);
                    }

                } else {
                    //跳转新页面
                    if (isWebAd(url)) {
                        //跳转广告页
                        //Timber.d("==============saveReadRecord 跳转广告页 当前位置：" + mRecordNo);
//                        return;
                    } else {
                        //跳转资讯页
                        mRecordNo++;
                        //Timber.d("==============saveReadRecord 跳转资讯页 当前位置：" + mRecordNo);
                    }
                }
            }


            noAd = false;
            mTitleShare = "";
            clearCash();
            if (progressReward != null)
                progressReward.setClickable(false);

            //新闻详情页加载重定向时
            loadComplete(url);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            showNormalView();//显示网页视图
            Timber.d("==============saveReadRecord onPageFinished " + url);
            if (webView != null) {
                //Timber.d("==============saveReadRecord onPageFinished" + webView.getTitle());
                mTitleShare = webView.getTitle();
            }
            if (noAd)
                isShowComment(1);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // !!!!!!!!!!!!!!!!!!!!!!!!!!! 非常非常重要 !!!!!!!!!!!!!!!!!!!!!!!!!!!
            // 一定要在onReceivedError调用百度广告SDK的onReceivedError
//            hybridAdManager.onReceivedError(view, errorCode, description, failingUrl);
            //Timber.d("-============showWebLoading 1" + errorCode + " " + description);
//            super.onReceivedError(view, errorCode, description, failingUrl);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return;
            }
            //显示错误页面
            showErrorView();
        }

        // 新版本，只会在Android6及以上调用
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            //Timber.d("-============showWebLoading2 " + error.getErrorCode() + " " + error.getDescription());
            // !!!!!!!!!!!!!!!!!!!!!!!!!!! 非常非常重要 !!!!!!!!!!!!!!!!!!!!!!!!!!!
            // 一定要在onReceivedError调用百度广告SDK的onReceivedError
//            hybridAdManager.onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().getPath());
//            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) { // 或者： if(request.getUrl().toString() .equals(getUrl()))
                //在这里显示自定义错误页
                showErrorView();
            }
        }

    }

    /**
     * 是否为广告页（www.dzkandian.com?&id=********&s=**********）
     *
     * @return 是广告返回true
     */
    public boolean isWebAd(String url) {
        if (TextUtils.isEmpty(url))
            return true;

        //资讯链接标识（有"&id=" 为参数起始位置；"&s=" 为结束位置的标识）
        int idIndex = url.indexOf("&id=") + 4;//资讯链接标识起始位置
        int idEnd = url.indexOf("&s=");//资讯标识结束位置

        return !(idIndex > 4 && idIndex < url.length() && idEnd > idIndex && !TextUtils.isEmpty(url.substring(idIndex, idEnd)));

//        if (idIndex < 4 || idEnd <= 0 || idIndex >= url.length()) {
//            return true;
//        } else if (idEnd < idIndex) {
//            return true;
//        } else {
//            return TextUtils.isEmpty(url.substring(idIndex, idEnd));
//        }
    }


    /**
     * 新闻页加载完成时 获取ID 换分享链接  获取弹幕数据
     */
    public void loadComplete(String url) {
        int idIndex = url.indexOf("&id=") + 4;
        int idEnd = url.indexOf("&s=");
        int shareIndex = url.indexOf("&s=") + 3;
        if (idIndex > 4 && idIndex < url.length() && idEnd > 0 && idEnd < url.length()
                && shareIndex > 3 && shareIndex < url.length() && idIndex < idEnd) {//数组越界判断
            mId = url.substring(idIndex, idEnd);  //截取URL中的ID
            mCanShare = url.substring(shareIndex, url.length());//截取URL中的 是否能分享
            mUrl = url;

            if ((mId.length() == 32 && TextUtils.equals(mCanShare, "0")) || (mId.length() == 32 && TextUtils.equals(mCanShare, "1"))) {//不是广告
                mCommentTime = System.currentTimeMillis();
                mShow = false; //清空显示状态
                noAd = true;  //判定不是广告
                //Timber.d("==================后台请求id: " + mId + "         canShare" + mCanShare);

                if (tvCommentCount != null)
                    tvCommentCount.setVisibility(View.GONE);
                //当JS交互成功后才能点击进度条
                if (progressReward != null) {
                    progressReward.setClickable(true);
                    if (progressReward.getVisibility() == View.GONE) {
                        progressReward.setVisibility(View.VISIBLE);
                    }
                }
                //是否显示弹幕
                isShowComment = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_NEW_COMMENT);

                //获取弹幕
                String lastId = "";
                if (mPresenter != null)
                    mPresenter.getBarrage(mId, mType, "news", 50, lastId);

                if (ivCommentShare != null) {
                    if (TextUtils.equals(mCanShare, "1")) {
                        ivCommentShare.setEnabled(true);
                    } else {
                        ivCommentShare.setEnabled(false);
                    }
                }
            }
        } else {
            mId = "";//清空资讯id
            mCanShare = "0";//广告无法分享
            if (!TextUtils.isEmpty(mUrl)) {
                //获取第三方网页地址
                int end = url.indexOf("?");
                mUrl = end > 0 ? url.substring(0, url.indexOf("?")) : url;
            }
        }
    }


    public class JavaScripObject {
        /**
         * 给Web端传用户信息、字体大小、版本号
         */
        @JavascriptInterface
        public String getAndroidData(int type) { //getAndroidData
            switch (type) {
                case 1:   //上传用户ID
                    //Timber.d("================上传JS数据：" + userId);
                    return TextUtils.isEmpty(mUserId) ? "" : mUserId;
                case 2:   //上传字体大小
                    String text = String.valueOf(textZoom);
                    //Timber.d("================上传JS数据：" + text);
                    return TextUtils.isEmpty(text) ? "" : text;
                case 3:   //上传版本  如:2.2.2
                    //Timber.d("================上传JS数据：" + DeviceUtils.getVersionName(NewsDetailActivity.this));
                    return DeviceUtils.getVersionName(NewsDetailActivity.this);
                default:
                    //Timber.d("================上传JS数据：" + userId);
                    return TextUtils.isEmpty(mUserId) ? "" : mUserId;
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_COIN_EXPLOSION_TAG:
                    if (explosionField != null) {
                        explosionField.expandExplosionBound(
                                (int) DeviceUtils.getScreenWidth(getApplicationContext()),
                                (int) (DeviceUtils.getScreenHeight(getApplicationContext()) * 0.6));
                        if (ivReward != null && tvReward != null) {
                            explosionField.explode(ivReward);
                            explosionField.explode(tvReward);
                        }
                        if (TextUtils.isEmpty(DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND))
                                || DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SET_SOUND).equals("true")) {
                            SoundPoolManager.getInstance(getApplicationContext()).playRinging();
                        }
                    }
                    break;
                case MSG_DANMU_TAG:   //开启弹幕线程
                    if (mDanmuContainerView != null && mBarrageBean.size() > 0) {
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
                            mDanmuContainerView.addDanmu(danmuEntity);
//                            mDanmuContainerView.onProgress(mCurrentProgress);
                            position++;
                            mShowCount++;
                            //Timber.d("========弹幕多少 position" + position);
                            //Timber.d("========弹幕多少 mCurrentProgress" + mCurrentProgress);
                        }
                    }
                    break;
            }
        }
    };


    /**
     * 点击下面列表 或点击返回按钮时调用
     */
    public void clearCash() {
        //每次进入进的新闻页把数据清空
        mShareUrl = "";
        mImageShare = "";
        mContentShare = "";
        mNotComment = false;
        mShowCount = 0;
        position = 0;

        if (tvCommentCount != null) {
            tvCommentCount.setVisibility(View.GONE);
        }
        if (completeCount >= readCount) {
            completeCount = 0;
            if (progressReward != null) {
                progressReward.setVisibility(View.VISIBLE);
                progressReward.setAlpha(1f);
            }
            hideCommentLayout();

            if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE)
                mSceneRootView.setVisibility(View.GONE);
        } else {
            completeCount = 0;
            if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {
                mSceneRootView.setVisibility(View.GONE);
                hideCommentLayout();
            } else {
                if (progressReward != null && progressReward.getAlpha() == 0f) {
                    progressReward.setVisibility(View.VISIBLE);
                    progressReward.setAlpha(1f);
                }
            }

        }

        //点击下一个新闻关闭监听事件
        if (progressReward != null) {
            if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {
                mSceneRootView.setVisibility(View.GONE);
                hideCommentLayout();
            }
        }
        //点击下一个新闻时 清除弹幕
        mInputString = "";
        mOpenThread = false;
        clearEditText(); //清空EditText内容
        if (mBarrageBean != null)
            mBarrageBean.clear();
        isShowComment(0);//线程暂停
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(etInputComment, 0);
                break;
            case R.id.iv_comment_count:   //跳至评论2级页面
                if (isLogin() && !TextUtils.isEmpty(mTitleShare)) {
                    Intent intent = new Intent(NewsDetailActivity.this, NewsCommentActivity.class);
                    intent.putExtra("Id", mId);
                    intent.putExtra("Type", mType);
                    intent.putExtra("Title", mTitleShare);
                    intent.putExtra("Url", mUrl);
                    intent.putExtra("commitFrom", "news");
                    ArmsUtils.startActivity(intent);
                }
                break;
            case R.id.iv_comment_barrage:   //开启关闭弹幕
                if (TextUtils.equals(isShowComment, "hide")) { //如果是屏蔽状态  则打开弹幕
                    ivCommentBarrage.setSelected(true);
                    DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_NEW_COMMENT, "show");
                    isShowComment = "show";
                    isShowComment(1);
                } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "show")) {
                    mShowCount = 0;
                    position = 0;
                    ivCommentBarrage.setSelected(false);
                    DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_NEW_COMMENT, "hide");
                    isShowComment = "hide";
                    isShowComment(0);
                }
                if (isClickTime(2)) {      //0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (TextUtils.equals(isShowComment, "hide")) {
                        showMessage("弹幕已关闭");
                    } else {
                        showMessage("弹幕已开启");
                    }
                }
                break;
            case R.id.iv_comment_collection:    //收藏
                if (isClickTime(1) && isLogin()) {     //0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (isInternet()) {
                        try {
                            String urlEncode = URLEncoder.encode(mUrl, "UTF-8");
                            if (mPresenter != null)
                                mPresenter.saveNewsCollection(urlEncode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.iv_comment_share:     //分享
                if (isClickTime(3) && isLogin()) {     //0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (isInternet()) {
                        try {
                            if (!TextUtils.isEmpty(getToken()) && TextUtils.equals(mCanShare, "1")
                                    && TextUtils.isEmpty(mShareUrl) && TextUtils.isEmpty(mContentShare)) {//如果可以分享则请求后台接口
                                //Timber.d("=================编码前的url:" + mUrl);
                                //编码后的url
                                String urlEncode = URLEncoder.encode(mUrl, "UTF-8");
                                //获取系统自带的ua
                                String userAgent = System.getProperty("http.agent");
                                assert mPresenter != null;
                                mPresenter.newsShare(urlEncode, userAgent);
                                //Timber.d("=================编码后的地址" + urlEncode);
                            } else {
                                //Timber.d("接口请求成功有数据，则直接拉起分享");
                                oneKeyShare.setShareContent(new ShareBean.Builder()
                                        .title(mTitleShare)
                                        .content(mContentShare)
                                        .imagePath(mImageUserPath)
                                        .imageUrl(mImageShare)
                                        .pageUrl(mShareUrl)
                                        .weChatShareType(mShareType)
                                        .weChatMomentsShareType(mShareType)
                                        .qqShareType(mShareType)
                                        .qZoneShareType(mShareType)
                                        .sinaShareType(mShareType)
                                        .create());
                                //Timber.d("==================mContentShare:" + mContentShare);
                                if (!TextUtils.isEmpty(mShareUrl)) {
                                    oneKeyShare.show(api);
                                } else {
                                    showMessage(getResources().getString(R.string.not_net));
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.bt_send:      //发送布局 发送按钮
                if (isLogin() && isClickTime(0)) {     //0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    mComment = etInputComment.getText().toString();
                    if (TextUtils.isEmpty(mComment)) {
                        showMessage("评论不能为空");
                    } else {
                        if (isInternet()) {
                            //编码后的url
                            try {
                                mComment = mComment.replaceAll("\n", "  ");  //检测换行符 替换成空格
                                String commentString = URLEncoder.encode(mComment, "UTF-8");
                                String urlEncode = URLEncoder.encode(mUrl, "UTF-8");
                                String reqId = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + (int) ((Math.random() * 9 + 1) * 100000);
                                mCommentTime = mCommentTime - System.currentTimeMillis();
                                int time = (int) mCommentTime / 1000;
                                ///非空处理
                                commentString = TextUtils.isEmpty(commentString) ? "" : commentString;
                                mId = TextUtils.isEmpty(mId) ? "" : mId;
                                mType = TextUtils.isEmpty(mType) ? "" : mType;
                                urlEncode = TextUtils.isEmpty(urlEncode) ? "" : urlEncode;
                                mTitleShare = TextUtils.isEmpty(mTitleShare) ? "" : mTitleShare;
                                reqId = TextUtils.isEmpty(reqId) ? "" : reqId;
                                if (mPresenter != null)
                                    mPresenter.foundComment(commentString, "news", time, mId, mType, urlEncode, mTitleShare, reqId, 0, 0, "");

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

    //控件点击事件
    @OnClick({R.id.tv_error, R.id.progress_reward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_error:  //网络异常界面按钮
                if (isInternet()) {
//                    showWebLoading(true);
                    webView.reload();
//                    webView.loadUrl(getIntent().getStringExtra("web_url"));//加载需要显示的网页
                }
                break;
            case R.id.progress_reward: //进度条点击事件
                progressReward.setClickable(false);
                if (progressReward.getAlpha() != 0f && progressReward.getVisibility() == View.VISIBLE) {
                    showCommentLayout();
                    isShowComment(0);
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 300) {  //判断返回码是否是30
            mInputString = data.getStringExtra("reText");
            etInputComment.setText(mInputString);
            etComment.setText(mInputString);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getToken() {
        return DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_TOKEN);
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            if (this.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    /**
     * 根据软键盘状态来显示隐藏布局
     */
    private void keyBoareState() {
        if (!mShow) { // 进度条没跑完时
            if (isShowKeyBoare && mSceneRootView.getVisibility() == View.VISIBLE) {  //1.软键盘弹起状态  2.发送评论布局显示
                mInputString = etInputComment.getText().toString();
                etComment.setText(mInputString);
                showShareScene(); //执行切换评论布局
                hideKeyboard();
            } else if (!isShowKeyBoare && mSceneRootView.getVisibility() == View.VISIBLE) {//1.软键盘隐藏 2.发送评论布局隐藏 3.控件弹幕评论布局显示时
                hideCommentLayout();
                mInputString = etInputComment.getText().toString();
                etComment.setText(mInputString);
            } else if (!isShowKeyBoare && mSceneRootView.getVisibility() == View.VISIBLE) {//1.软键盘隐藏 2.2.发送评论布局显示
                mInputString = etInputComment.getText().toString();
                etComment.setText(mInputString);
                showShareScene();//执行切换评论布局
            } else if (!isShowKeyBoare && mSceneRootView.getVisibility() == View.VISIBLE) {
                hideCommentLayout();
            }
        } else {  //进度条跑满时    控件弹幕评论布局一直显示
            if (isShowKeyBoare && mSceneRootView.getVisibility() == View.VISIBLE) { //1.软键盘弹起状态  2.发送评论布局显示
                hideKeyboard();
                mInputString = etInputComment.getText().toString();
                etComment.setText(mInputString);
                showShareScene();//执行切换评论布局
            } else if (!isShowKeyBoare && mSceneRootView.getVisibility() == View.VISIBLE) { //1.软键盘隐藏 2.2.发送评论布局显示
                mInputString = etInputComment.getText().toString();
                etComment.setText(mInputString);
                showShareScene();//执行切换评论布局
            }
        }
    }

    /**
     * 是否显示弹幕
     *
     * @param isClose 0 不显示    1显示
     */
    public void isShowComment(int isClose) {
        switch (isClose) {
            case 0:
                startDanmu(false);
                if (mDanmuContainerView != null) {
                    mDanmuContainerView.onDestroy();
                    mDanmuContainerView.removeAllViews();
                }
                break;
            case 1:
                //成功获取到数据  并弹幕数据大于0才开启线程
                if ((TextUtils.isEmpty(isShowComment) && mOpenThread && mBarrageBean.size() > 0) || (TextUtils.equals(isShowComment, "show")) && mOpenThread && mBarrageBean.size() > 0 && noAd) {
                    startDanmu(true);
                }

                break;
        }

    }

    public void setProgress(PathPoint newLoc) {
        progressReward.setTranslationX(newLoc.mX);
        progressReward.setTranslationY(newLoc.mY);
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
        mOutPRAnimAlpha = ObjectAnimator.ofFloat(progressReward, "alpha", 1.0f, 0f);
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
            mOutPRAnimTranslation = ObjectAnimator.ofFloat(progressReward, "translationX", "translationY", path);
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
        mEnterPRAnimAlpha = ObjectAnimator.ofFloat(progressReward, "alpha", 0f, 1.0f);
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
                    if (progressReward != null) {
                        if (progressReward.getAlpha() == 0f || progressReward.getVisibility() == View.GONE) {
                            progressReward.setAlpha(1f);
                            progressReward.setVisibility(View.VISIBLE);
                            if (noAd)
                                progressReward.setClickable(true);
                        }
                        if (mSceneRootView != null) {
                            mSceneRootView.setVisibility(View.GONE);
                        }
                        if (noAd)
                            progressReward.setClickable(true);
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
            mEnterPRAnimTranslation = ObjectAnimator.ofFloat(progressReward, "translationX", "translationY", path_return);
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

                    if (progressReward != null) {
                        if (progressReward.getAlpha() == 0f || progressReward.getVisibility() == View.GONE) {
                            progressReward.setAlpha(1f);
                            progressReward.setVisibility(View.VISIBLE);
                            if (noAd)
                                progressReward.setClickable(true);
                        }
                        if (mSceneRootView != null) {
                            mSceneRootView.setVisibility(View.GONE);
                        }
                        if (noAd)
                            progressReward.setClickable(true);
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
            if (squareAnimSet != null && !squareAnimSet.isStarted()) {
                if (returnAnimSet != null && returnAnimSet.isStarted())
                    returnAnimSet.cancel();
                squareAnimSet.start();
            }
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
            if (returnAnimSet != null && !returnAnimSet.isStarted()) {
                if (squareAnimSet != null && squareAnimSet.isStarted())
                    squareAnimSet.cancel();
                returnAnimSet.start();
            }
        }
    }

    //初始化评论布局相关控件
    public void initSceneAnimation() {
        ViewGroup commentCount = (ViewGroup) getLayoutInflater().inflate(R.layout.scene_comment_share, null);
        ViewGroup commentSubmit = (ViewGroup) getLayoutInflater().inflate(R.layout.scene_comment_send, null);

        btnSend = commentSubmit.findViewById(R.id.bt_send);
        etInputComment = commentSubmit.findViewById(R.id.et_comment);

        etComment = commentCount.findViewById(R.id.et_comment);
        ImageView ivCommentBack = commentCount.findViewById(R.id.iv_comment_back);
        ivCommentShare = commentCount.findViewById(R.id.iv_comment_share);
        ivCommentCollection = commentCount.findViewById(R.id.iv_comment_collection);
        ImageView ivCommentCount = commentCount.findViewById(R.id.iv_comment_count);
        tvCommentCount = commentCount.findViewById(R.id.tv_comment_count);
        ivCommentBarrage = commentCount.findViewById(R.id.iv_comment_barrage);

        etComment.setOnClickListener(this);
        etInputComment.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        ivCommentBack.setOnClickListener(this);
        ivCommentShare.setOnClickListener(this);
        ivCommentCollection.setOnClickListener(this);
        ivCommentCount.setOnClickListener(this);
        ivCommentBarrage.setOnClickListener(this);

        mSceneCommentShare = new Scene(mSceneRootView, commentCount);
        mSceneCommentSend = new Scene(mSceneRootView, commentSubmit);

        /*
         * 切换到开始场景状态
         */
        TransitionManager.go(mSceneCommentShare, new ChangeBounds());

        etComment.addTextChangedListener(textWatcher);
        etInputComment.addTextChangedListener(textWatcher);
    }


    // 显示发送评论布局
    private void showSendScene() {
        //不指定默认就是AutoTransition
        TransitionManager.go(mSceneCommentSend, new ChangeBounds());
    }

    //显示分享评论布局
    private void showShareScene() {
        //不指定默认就是AutoTransition
        TransitionManager.go(mSceneCommentShare, new ChangeBounds());
    }


    /**
     * 清理资讯阅读记录
     */
    private void clearReadRecord() {
        MyApplication.get().getDaoSession().getNewsRecordBeanDao().deleteAll();
    }

    /**
     * 保存资讯阅读记录
     */
    private void saveReadRecord() {
        int scale = arcProgressScale - mRecordScale;
        NewsRecordBean recordBean = new NewsRecordBean();
        recordBean.setAScale(scale < 0 ? 0 : scale);
        recordBean.setAId(mId);
        recordBean.setAType(mType);

        if (TextUtils.isEmpty(mId)) {
            //如果为广告默认轨迹为0
            recordBean.setNo(0);
        } else {
            recordBean.setNo(mRecordNo);
        }

        recordBean.setAction(mRecordAction);
        recordBean.setAd(TextUtils.isEmpty(mId) ? mUrl : "");//id为空即为第三方网页,上传网页地址
        //Timber.d("==============saveReadRecord " + recordBean.getNo() + " id：" + mId + " ad： " + recordBean.getAd() + "\n ----");
        mRecordList.add(recordBean);
        mRecordAction = 0;

        mRecordScale = arcProgressScale == 100 ? 0 : arcProgressScale;//记录当前资讯阅读奖励的进度
    }

    /**
     * 获取资讯阅读记录
     */
    private List<NewsRecordBean> getReadRecord() {
        List<NewsRecordBean> list = MyApplication.get().getDaoSession().getNewsRecordBeanDao().loadAll();
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * 是否登录  没登录跳转登录界面
     */
    private boolean isLogin() {
        if (TextUtils.isEmpty(getToken())) {
            launchActivity(new Intent(NewsDetailActivity.this, LoginActivity.class));
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
                if (System.currentTimeMillis() - onClickbtnSendTime > 2000) {
                    onClickbtnSendTime = System.currentTimeMillis();
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

        if (mDanmuContainerView != null) {
            mDanmuContainerView.onDestroy();
            mDanmuContainerView.removeAllViews();
            mDanmuContainerView = null;
        }
        if (mDanmuAdapter != null) {
            mDanmuAdapter = null;
        }
    }

    /**
     * 销毁WebView
     */
    public void clearWebView() {
        if (flNewsNormal != null)
            flNewsNormal.removeAllViews();
        if (webView != null) {
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.stopLoading();
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearView();
            webView.removeAllViews();
            webView.addOnLayoutChangeListener(null);
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            //清理Webview缓存数据库
            try {
                deleteDatabase("webview.db");
                deleteDatabase("webviewCache.db");
            } catch (Exception e) {
                e.printStackTrace();
            }
//            webView.loadUrl("about:blank");
//            webView.pauseTimers();
            webView.destroy();
            webView = null;
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
     * 显示异常视图
     */
    private void showErrorView() {
        //Timber.d("-============showErrorView");

        if (mIsLoading)
            showWebLoading(false);//隐藏Web加载动画

        if (flNewsNormal != null)//隐藏Web视图
            flNewsNormal.setVisibility(View.GONE);

        if (llNewsEmpty != null)
            llNewsEmpty.setVisibility(View.VISIBLE);
        if (tvError != null) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(R.string.error_network);
            tvError.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.icon_error_network),
                    null, null);
        }
    }

    /**
     * 隐藏异常视图
     */
    private void hideErrorView() {
//        //Timber.d("-============hideErrorView");
        if (llNewsEmpty != null && tvError != null) {
            if (llNewsEmpty.getVisibility() == View.GONE)
                return;

            llNewsEmpty.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        }
    }

    /**
     * 显示正常视图
     */
    private void showNormalView() {
//        //Timber.d("-============showNormalView");
        if (mIsLoading)
            showWebLoading(false);//隐藏Web加载动画

        if (flNewsNormal != null) {
            if (flNewsNormal.getVisibility() == View.VISIBLE)
                return;

            flNewsNormal.setVisibility(View.VISIBLE);
        }

        hideErrorView();//隐藏异常视图

    }

    /**
     * Web加载动画
     *
     * @param isShow 显示/隐藏（显示正常视图）
     */
    private void showWebLoading(boolean isShow) {
        mIsLoading = isShow;
//        Timber.d("-============showWebLoading: " + mIsLoading);
        if (llWebLoad != null) {
            llWebLoad.setVisibility(isShow ? View.VISIBLE : View.GONE);
            if (isShow) {
                ivWebLoadUFO.setAlpha(1f);
                ivWebLoadBicycle.setAlpha(1f);
                if (animatorSetWebLoad != null && (!animatorSetWebLoad.isStarted() || !animatorSetWebLoad.isRunning()))
                    animatorSetWebLoad.start();
            } else {
                ivWebLoadUFO.setAlpha(0f);
                ivWebLoadBicycle.setAlpha(0f);
                if (animatorSetWebLoad != null && (animatorSetWebLoad.isStarted() || animatorSetWebLoad.isRunning()))
                    animatorSetWebLoad.end();
            }
        }

        if (isShow) {
            hideErrorView();//隐藏异常视图
        }
    }

    /**
     * 初始化网页加载的属性动画
     */
    private void initValueAnim() {
        //UFO动画
        float ufoWidth = dipToPx(this, 57);
        PropertyValuesHolder ufoTranslationX = PropertyValuesHolder.ofFloat(
                "translationX",
                -ufoWidth * 4.5f, ufoWidth * 0.5f, 0f,//UFO飞来
                0f,                                           //放射光环放大
                0f, 0f, 0f, 0f,                              //吸取物体
                0f,                                         //放射光环缩小
                ufoWidth * 0.2f,                           //UFO倾斜
                ufoWidth * 5f);                           //UFO飞走
        PropertyValuesHolder ufoRotation = PropertyValuesHolder.ofFloat("rotation",
                15f, 15f, 0f,  //UFO飞来
                0f,                    //放射光环放大
                0f, 0f, 0f, 0f,       //吸取物体
                0f,                  //放射光环缩小
                15f,                //UFO倾斜
                15f);              //UFO飞走
        ObjectAnimator ufoAnim = ObjectAnimator.ofPropertyValuesHolder(ivWebLoadUFO, ufoTranslationX, ufoRotation);
        ufoAnim.setRepeatMode(ValueAnimator.RESTART);
        ufoAnim.setRepeatCount(10000);
        ufoAnim.setDuration(4000);


        //光环动画
        PropertyValuesHolder ringScale = PropertyValuesHolder.ofFloat("scaleX",
                0f, 0f, 0f,  //UFO飞来
                1f,                  //放射光环放大
                1f, 1f, 1f, 1f,     //吸取物体
                0f,                //放射光环缩小
                0f,               //UFO倾斜
                0f);             //UFO飞走
        PropertyValuesHolder ringAlpha = PropertyValuesHolder.ofFloat("alpha",
                0f, 0f, 0f,  //UFO飞来
                1f,                           //放射光环放大
                1f, 1f, 1f, 1f,              //吸取物体
                0f,                         //放射光环缩小
                0f,                        //UFO倾斜
                0f);                      //UFO飞走
        ObjectAnimator ringAnim = ObjectAnimator.ofPropertyValuesHolder(ivWebLoadRing, ringScale, ringAlpha);
        ringAnim.setRepeatMode(ValueAnimator.RESTART);
        ringAnim.setRepeatCount(10000);
        ringAnim.setDuration(4000);


        //光线动画
        float lightHeight = dipToPx(this, 139);
        PropertyValuesHolder lightTranslationY = PropertyValuesHolder.ofFloat("translationY",
                0f, 0f, 0f,                                                            //UFO飞来
                0f,                                                                             //放射光环放大
                -lightHeight * 0.5f, -lightHeight * 1f, -lightHeight * 1.5f, -lightHeight * 2f,//吸取物体
                0f,                                                                           //放射光环缩小
                0f,                                                                          //UFO倾斜
                0f);                                                                        //UFO飞走
        PropertyValuesHolder lightAlpha = PropertyValuesHolder.ofFloat("alpha",
                0f, 0f, 0f,     //UFO飞来
                1f,                     //放射光环放大
                1f, 1f, 0.8f, 0f,      //吸取物体
                0f,                   //放射光环缩小
                0f,                  //UFO倾斜
                0f);                //UFO飞走
        ObjectAnimator lightAnim = ObjectAnimator.ofPropertyValuesHolder(ivWebLoadLight, lightTranslationY, lightAlpha);
        lightAnim.setRepeatMode(ValueAnimator.RESTART);
        lightAnim.setRepeatCount(10000);
        lightAnim.setDuration(4000);

        //自行车动画
        float bicycleHeight = dipToPx(this, 15);
        PropertyValuesHolder bicycleRotation = PropertyValuesHolder.ofFloat("rotation",
                0f, 0f, 0f,  //UFO飞来
                0f,                  //放射光环放大
                -20, -20, -20, -20, //吸取物体
                0f,                //放射光环缩小
                0f,               //UFO倾斜
                0f);             //UFO飞走
        PropertyValuesHolder bicycleTranslationY = PropertyValuesHolder.ofFloat("translationY",
                0f, 0f, 0f,                                                                         //UFO飞来
                0f,                                                                                         //放射光环放大
                -bicycleHeight * 1.25f, -bicycleHeight * 2.5f, -bicycleHeight * 3.75f, -bicycleHeight * 5f,//吸取物体
                0f,                                                                                       //放射光环缩小
                0f,                                                                                      //UFO倾斜
                0f);                                                                                    //UFO飞走
        PropertyValuesHolder bicycleAlpha = PropertyValuesHolder.ofFloat("alpha",
                1f, 1f, 1f,  //UFO飞来
                1f,                  //放射光环放大
                1f, 0.6f, 0.1f, 0f, //吸取物体
                0f,                //放射光环缩小
                0f,               //UFO倾斜
                0f);             //UFO飞走
        ObjectAnimator bicycleAnim = ObjectAnimator.ofPropertyValuesHolder(ivWebLoadBicycle, bicycleRotation,
                bicycleTranslationY, bicycleAlpha);
        bicycleAnim.setRepeatMode(ValueAnimator.RESTART);
        bicycleAnim.setRepeatCount(10000);
        bicycleAnim.setDuration(4000);

        //树木动画
        float treeHeight = dipToPx(this, 12);
        float treeWidth = dipToPx(this, 9);
        PropertyValuesHolder treeOneRotation = PropertyValuesHolder.ofFloat("rotation",
                0f, 0f, 0f,  //UFO飞来
                0f,                  //放射光环放大
                0f, 10f, 0f, -10f,  //吸取物体
                0f,                //放射光环缩小
                0f,               //UFO倾斜
                0f);             //UFO飞走
        PropertyValuesHolder treeOneTranslationX = PropertyValuesHolder.ofFloat("translationX",
                0f, 0f, 0f,                              //UFO飞来
                0f,                                              //放射光环放大
                0f, 0f, -treeWidth * 1.25f, -treeWidth * 2.5f,  //吸取物体
                0f,                                            //放射光环缩小
                0f,                                           //UFO倾斜
                0f);                                         //UFO飞走
        PropertyValuesHolder treeOneTranslationY = PropertyValuesHolder.ofFloat("translationY",
                0f, 0f, 0f,                            //UFO飞来
                0f,                                            //放射光环放大
                0f, 0f, -treeHeight * 1.5f, -treeHeight * 3f, //吸取物体
                0f,                                          //放射光环缩小
                0f,                                         //UFO倾斜
                0f);                                       //UFO飞走
        PropertyValuesHolder treeAlpha = PropertyValuesHolder.ofFloat("alpha",
                1f, 1f, 1f, //UFO飞来
                1f,                 //放射光环放大
                1f, 1f, 0.6f, 0f,  //吸取物体
                0f,               //放射光环缩小
                0f,              //UFO倾斜
                0f);            //UFO飞走
        ObjectAnimator treeOneAnim = ObjectAnimator.ofPropertyValuesHolder(ivWebLoadTreeOne, treeOneRotation,
                treeOneTranslationX, treeOneTranslationY, treeAlpha);
        treeOneAnim.setRepeatMode(ValueAnimator.RESTART);
        treeOneAnim.setRepeatCount(10000);
        treeOneAnim.setDuration(4000);

        PropertyValuesHolder treeTwoRotation = PropertyValuesHolder.ofFloat("rotation",
                0f, 0f, 0f,  //UFO飞来
                0f,                  //放射光环放大
                0f, 10f, 0f, -15f,  //吸取物体
                0f,                //放射光环缩小
                0f,               //UFO倾斜
                0f);             //UFO飞走
        PropertyValuesHolder treeTwoTranslationX = PropertyValuesHolder.ofFloat("translationX",
                0f, 0f, 0f,                              //UFO飞来
                0f,                                              //放射光环放大
                0f, 0f, -treeWidth * 1.75f, -treeWidth * 3.5f,  //吸取物体
                0f,                                            //放射光环缩小
                0f,                                           //UFO倾斜
                0f);                                         //UFO飞走
        PropertyValuesHolder treeTwoTranslationY = PropertyValuesHolder.ofFloat("translationY",
                0f, 0f, 0f,                               //UFO飞来
                0f,                                               //放射光环放大
                0f, 0f, -treeHeight * 2.25f, -treeHeight * 4.5f, //吸取物体
                0f,                                             //放射光环缩小
                0f,                                            //UFO倾斜
                0f);                                          //UFO飞走
        ObjectAnimator treeTwoAnim = ObjectAnimator.ofPropertyValuesHolder(ivWebLoadTreeTwo, treeTwoRotation,
                treeTwoTranslationX, treeTwoTranslationY, treeAlpha);
        treeTwoAnim.setRepeatMode(ValueAnimator.RESTART);
        treeTwoAnim.setRepeatCount(10000);
        treeTwoAnim.setDuration(4000);

        ObjectAnimator runAnim = ObjectAnimator.ofFloat(ivWebLoadSun, "rotation", 0f, 360f);
        runAnim.setRepeatMode(ValueAnimator.RESTART);
        runAnim.setRepeatCount(10000);
        runAnim.setDuration(2000);

        animatorSetWebLoad = new AnimatorSet();
        animatorSetWebLoad.setInterpolator(new LinearInterpolator());
        animatorSetWebLoad.playTogether(ufoAnim, bicycleAnim, lightAnim, ringAnim, treeOneAnim, treeTwoAnim, runAnim);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
