package com.dzkandian.mvp.video.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Path;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.InterstitialAd;
import com.bdtt.sdk.wmsdk.AdSlot;
import com.bdtt.sdk.wmsdk.TTAdDislike;
import com.bdtt.sdk.wmsdk.TTAdNative;
import com.bdtt.sdk.wmsdk.TTBannerAd;
import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.app.ShortVideoSideslipBaseActivity;
import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.app.config.imageloader.CustomImageConfig;
import com.dzkandian.common.player.play.AssistPlayer;
import com.dzkandian.common.player.play.DataInter;
import com.dzkandian.common.player.play.ReceiverGroupManager;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.common.uitls.NetworkUtils;
import com.dzkandian.common.uitls.animator.AnimatorPath;
import com.dzkandian.common.uitls.animator.PathEvaluator;
import com.dzkandian.common.uitls.animator.PathPoint;
import com.dzkandian.common.uitls.ttAd.TTAdManagerHolder;
import com.dzkandian.common.widget.MobOneKeyShare;
import com.dzkandian.common.widget.SoundPoolManager;
import com.dzkandian.common.widget.arcprogress.AnswerChartView;
import com.dzkandian.common.widget.barrageview.DanmuContainerView;
import com.dzkandian.common.widget.barrageview.FlutteringLayout;
import com.dzkandian.common.widget.explosion.ExplosionField;
import com.dzkandian.common.widget.laoding.LoadingProgressDialog;
import com.dzkandian.common.widget.recyclerview.MyLinearLayoutManager;
import com.dzkandian.db.VideoBeanDao;
import com.dzkandian.mvp.common.ui.activity.AdWebActivity;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.dzkandian.mvp.news.ui.activity.NewsCommentActivity;
import com.dzkandian.mvp.video.contract.ShortVideoPlayContract;
import com.dzkandian.mvp.video.di.component.DaggerShortVideoPlayComponent;
import com.dzkandian.mvp.video.di.module.ShortVideoPlayModule;
import com.dzkandian.mvp.video.presenter.ShortVideoPlayPresenter;
import com.dzkandian.mvp.video.ui.adapter.DanmuShortAdapter;
import com.dzkandian.mvp.video.ui.adapter.ShortDetailAdapter;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.RandomAdBean;
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
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.comm.util.AdError;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 视频详情页
 * Created by LiuLi on 2018/8/17.
 */

public class ShortDetailActivity extends ShortVideoSideslipBaseActivity<ShortVideoPlayPresenter>
        implements ShortVideoPlayContract.View, OnReceiverEventListener, OnPlayerEventListener, View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.tasks_view)
    AnswerChartView mTasksView;
    @BindView(R.id.refreshFooter)
    ClassicsFooter mRefreshFooter;
    @BindView(R.id.iv_reward)
    ImageView ivReward;
    @BindView(R.id.tv_reward_coin)
    TextView tvReward;
    @BindView(R.id.fl_reward_anim)
    FrameLayout flRewardAnim;
    @BindView(R.id.express_ad_container)
    FrameLayout expressADContainer;
    @BindView(R.id.express_ad_close)
    ImageView ivAdClose;

    @BindView(R.id.danmuContainerView)
    DanmuContainerView mDanmuContainerView;  //弹幕

    /**
     * 评论布局切换相关
     */
    @BindView(R.id.fl_short_video_comment)
    ViewGroup mSceneRootView;

    @BindView(R.id.flutteringLayout)
    FlutteringLayout flutteringLayout;   //弹幕点赞动画


    /**
     * 弹幕相关
     */
    private boolean mOpenThread; //是否开启线程
    private String isShowComment;//是否显示弹幕
    private List<BarrageBean> mBarrageBean;  //弹幕相关所有数据
    private int danmuCurrPosition;       //弹幕当前点击的位置
    private int mPosition = 0; //弹幕显示位置
    private PlayTimeDanmu mPlayTimeDanmu;
    private String mInputString = ""; //用户输入好的字符
    private InputMethodManager imm;
    private DanmuShortAdapter mDanmuAdapter;//弹幕Adapter
    private String mComment;
    private long mCommentTime; //每个视频至评论中间的时长
    private String mLastId = "";  //弹幕的最后一条ID
    private String mCurrId = "";  //当前视频的id

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
    private int mDelayedPotisiton; //延时次数  每次为2秒
    private String mVideoTitle;    //视频标题
    private String mVideoWebUrl;    //视频Web_Url
    private String mVideoUrl;      //视频播放Url
    private String mVideoImageUrl;  //视频的图片Url;
    private boolean mShow;         //进度条转满 一直显示评论布局


    private ExplosionField explosionField;//爆裂效果
    private View mErrorView;//异常布局
    private MobOneKeyShare oneKeyShare;//一键分享

    /* 广点通广告 */
    private InterstitialAD interstitialAD;//广点通插屏广告
    private BannerView bannerView;//广点通Banner广告

    private AdView baiDuBannerView;//百度SSP横幅广告
    private InterstitialAd baiDuInterAd;//百度SSP插屏广告

    private LoadingProgressDialog loadingProgressDialog;

    private MyLinearLayoutManager manager;
    private ShortDetailAdapter mAdapter;

    private List<VideoBean> mVideoList;
    private String mType;//视频类型
    private static IWXAPI api; // IWXAPI 是第三方app和微信通信的openapi接口
    private String imageUserPath = "";//用户头像本地地址
    private String shareImageUrl = "";//用户头像网络地址

    private boolean isOneExplode = true;//是否第一次爆裂
    private boolean isPauseTime;//是否暂停计时（播放完成or阅读奖励领取成功暂停计时）

    private String mTextSize;//字体大小
    private int mCurrPlayPosition; //视频当前播放位置
    private long mCurrPlayId; //小视频当前播放Id

    private ReceiverGroup mReceiverGroup;
    //    private long onClickShareTime;//上一次点击分享的时间
    private long refreshShortDetailLastTimes;//小视频详情页 刷新 的上一次时间；
    private long loadMoreShortDetailLastTimes;//小视频详情页 加载 的上一次时间；

    private int arcProgressScale = 0;//进度条当前刻度
    private int mRecordScale = 0;//当前资讯阅读奖励的进度
    private int mReadGoldPercent = 0;//后台返回视频奖励结束进度(0-99)
    private boolean isAd;   //广告  不显示弹幕  不显示评论布局
    private boolean isAdCloseBotton = false;//是否有广告的 X 关闭按钮；


    private Scene mSceneCommentShare;     //显示评论分享布局
    private Scene mSceneCommentSend;      //显示评论发送布局
    private ImageView ivVideoCommentBack; //返回
    private EditText etVideoComment;      //输入框
    private ImageView ivVideoCommentCount;//跳转至评论详情页
    private TextView tvVideoCommentCount;
    private ImageView ivVideoCommentBarrage;//
    private ImageView ivVideoCommentCollection;
    private ImageView ivVideoCommentShare;

    private EditText etInputComment;
    private Button btnSend;
    private boolean isNextVideo;  //进入下一个视频不加载广告
    private TTAdNative mTTAdNative;//接入demo   BannerActivity
    private TTBannerAd mTTBannerAd;
    private AdSlot mAdSlot;
    private boolean isHasCsjAd;
    private String mIsCollection; //是否小视频收藏页进入
    RandomAdBean mRandomAdBean;//DSP广告参数；
    @Inject
    ImageLoader imageLoader;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: //奖励动效、音乐
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
                    break;
                case 3:   //开启弹幕线程
                    if (mDanmuContainerView != null && mBarrageBean.size() > 0) {
                        if (mPosition >= mBarrageBean.size()) {
                            mPosition = 0;
                        } else {
                            NewsDanmuBean danmuEntity = new NewsDanmuBean();
                            String content = mBarrageBean.get(mPosition).getContent();
                            //弹幕头像
                            if (mBarrageBean.size() > 0 && !TextUtils.isEmpty(mBarrageBean.get(mPosition).getUserImg())) {
                                String headImg = mBarrageBean.get(mPosition).getUserImg();
                                danmuEntity.setHeadImg(headImg);
                            }
                            //弹幕点赞数量
                            if (mBarrageBean.size() > 0 && !TextUtils.isEmpty(mBarrageBean.get(mPosition).getThumbsUpCount())) {
                                String thumbsUpCount = mBarrageBean.get(mPosition).getThumbsUpCount();
                                danmuEntity.setCanThumbsUp(false);
                                danmuEntity.setThumbsUpCount(thumbsUpCount);
                            }
                            //当前弹幕是否能点赞
                            if (mBarrageBean.size() > 0) {
                                danmuEntity.setCanThumbsUp(mBarrageBean.get(mPosition).getCanThumbsUp());
                            }

                            danmuEntity.setContent(content); //弹幕内容
                            danmuEntity.setPosition(String.valueOf(mPosition));
                            danmuEntity.setType(1);
                            mDanmuContainerView.addDanmu(danmuEntity);
//                            mDanmuContainerView.onProgress(mCurrentProgress);
                            mPosition++;
                            mShowCount++;
//                            //Timber.d("========弹幕多少 position" + position);
//                            //Timber.d("========弹幕多少 mCurrentProgress" + mCurrentProgress);
                        }
                    }
                    break;
            }
        }
    };


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerShortVideoPlayComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .shortVideoPlayModule(new ShortVideoPlayModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_short_video_play; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        highApiEffects();
        mVideoList = new ArrayList<>();

        //是否显示弹幕
        isShowComment = DataHelper.getStringSF(getApplicationContext(), Constant.SP_KEY_SHORT_VIDEO_COMMENT);

        queryDeviceInfo();//获取数据库设备信息
        initSceneAnimation();  //初始化评论布局动画

        getIntentData();//获取adapter传过来的数据

        initRecyclerView();
        initRefreshLayout();
        initAnimator();     //初始化动画

        getComment();       //获取评论


        //分享初始化
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);//微信api
        oneKeyShare = new MobOneKeyShare(this);

        //奖励动画初始化
        explosionField = ExplosionField.attach2Window(this);//初始化爆裂效果

        //初始化广点通插屏广告
//        initGDTInterAd();
//        //初始化百度SSP插屏广告
//        initBaiDuInterAd();

        ivAdClose.setOnClickListener(view -> closeBannerAD());
    }

    //获取adapter传过来的数据
    private void getIntentData() {
        mType = getIntent().getStringExtra("type");
        mTextSize = getIntent().getStringExtra("textSize");
        mCurrPlayPosition = getIntent().getIntExtra("position", 0);
        mCurrPlayId = getIntent().getLongExtra("playId", 0);
        mIsCollection = getIntent().getStringExtra("shortCollection"); //是否是收藏小视频页跳转

        if (TextUtils.isEmpty(mIsCollection)) {//如果这个值为空 则说明是从小视频列表进入  则发送通知 瞄点到指定位置
            List<VideoBean> videoBeans = MyApplication.get().getDaoSession().getVideoBeanDao().queryBuilder()
                    .where(VideoBeanDao.Properties.ID.between(mCurrPlayId, mCurrPlayId + 10)).list();
//            Timber.d("=============小视频数据库  刷新" + MyApplication.get().getDaoSession().getVideoBeanDao().loadAll().size());

//            Timber.d("=============小视频  数据库前十条数据" + videoBeans.size() + " 当前视频ID   " + mCurrPlayId);
            //如果是小视频列表进来  数据取到了值
            if (videoBeans.size() > 0 && !TextUtils.isEmpty(videoBeans.get(0).getVideoId()) && !TextUtils.isEmpty(videoBeans.get(0).getUrl())) {
                mVideoTitle = videoBeans.get(0).getTitle();
                mCurrId = videoBeans.get(0).getVideoId();
                mVideoUrl = videoBeans.get(0).getUrl();
                mVideoWebUrl = videoBeans.get(0).getWebUrl();
                mVideoImageUrl = videoBeans.get(0).getThumbUrl();

                if (videoBeans.size() == 1) {   //如果传进来的视频只有一条  则直接请求后台接口
                    if (isInternet() && mPresenter != null) {
                        mPresenter.getVideoList(false, mCurrId, mType);
                    }
                }
                mVideoList.addAll(videoBeans);
//            Timber.d("mVideoTitle================" + mVideoTitle);
//            Timber.d("mVideoTitle================" + mCurrId);
            } else { //数据库取值失败 则用传进来的视频播放 并去请求后台
                VideoBean videoBean = (VideoBean) getIntent().getSerializableExtra("video");
                if (!TextUtils.isEmpty(videoBean.getVideoId()) && !TextUtils.isEmpty(videoBean.getUrl())) {
                    mVideoTitle = videoBean.getTitle();
                    mCurrId = videoBean.getVideoId();
                    mVideoUrl = videoBean.getUrl();
                    mVideoWebUrl = videoBean.getWebUrl();
                    mVideoImageUrl = videoBean.getThumbUrl();
                    mVideoList.add(videoBean);
                    if (isInternet() && mPresenter != null) {
                        mPresenter.getVideoList(false, mCurrId, mType);
                    }
                }
            }
        } else {   //这里是小视频收藏页进来
            VideoBean videoBean = (VideoBean) getIntent().getSerializableExtra("video");
            mVideoTitle = videoBean.getTitle();
            mCurrId = videoBean.getVideoId();
            mVideoUrl = videoBean.getUrl();
            mVideoWebUrl = videoBean.getWebUrl();
            mVideoImageUrl = videoBean.getThumbUrl();
            mVideoList.add(videoBean);
            if (isInternet() && mPresenter != null) {
                mPresenter.getVideoList(false, mCurrId, mType);
            }
        }


    }

    private void initRefreshLayout() {
        mRefreshLayout.setEnableRefresh(false);
//        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setFooterHeight(0);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                if (!TextUtils.isEmpty(mIsCollection)) { //如果是从收藏小视频页进来
                    if (isInternet()) {
                        if (mPresenter != null && mVideoList != null && mVideoList.size() > 0) {
                            mPresenter.getVideoList(false, mVideoList.get(mVideoList.size() - 1).getVideoId(), mType);
                        }
                    } else {
                        if (System.currentTimeMillis() - loadMoreShortDetailLastTimes > 2000) {
                            loadMoreShortDetailLastTimes = System.currentTimeMillis();
                            showMessage("网络请求失败，请连网后重试");
                        }
                        refreshLayout.finishLoadMore();
                    }
                    return;
                }

                List<VideoBean> videoBeans = MyApplication.get().getDaoSession().getVideoBeanDao().queryBuilder()
                        .where(VideoBeanDao.Properties.ID.between((mVideoList.get(mVideoList.size() - 1).getID()) + 1, (mVideoList.get(mVideoList.size() - 1).getID()) + 11)).list();
//                Timber.d("=============小视频   onLoadMore      " + videoBeans.size() + "");
                if (videoBeans.size() == 0) {
                    if (isInternet()) {
                        if (mPresenter != null)
                            mPresenter.getVideoList(false, mVideoList.get(mVideoList.size() - 1).getVideoId(), mType);
//                        Timber.d("=============小视频   onLoadMore mVideoId    " + mVideoList.get(mVideoList.size() - 1).getVideoId());
                    } else {
//                        Timber.d("=============小视频     没网");
                        if (System.currentTimeMillis() - loadMoreShortDetailLastTimes > 2000) {
                            loadMoreShortDetailLastTimes = System.currentTimeMillis();
                            showMessage("网络请求失败，请连网后重试");
                        }
                        refreshLayout.finishLoadMore();
                    }
                } else { //如果数据库还有数据则取数据库数据
                    int size = mVideoList.size();
                    mVideoList.addAll(videoBeans);
                    mAdapter.notifyItemRangeInserted(size, videoBeans.size());
                    refreshLayout.finishLoadMore();
//                    if (videoBeans.size() != 0) {
//                        mCurrId = mVideoList.get(mVideoList.size() - 1).getVideoId();
//                    Timber.d("=============小视频 调用加载更多拿数据库数据" + videoBeans.size());
//                        Timber.d("=============小视频  取   " + (mCurrPlayId + 1) + " 至    " + (mCurrPlayId + 10));
//                    }
                }

            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (isInternet()) {
                    mRefreshLayout.setRefreshContent(mRecyclerView);
                    mRefreshLayout.setEnableLoadMore(true);
                    if (mPresenter != null) {
                        mPresenter.getVideoList(true, mCurrId, mType);
                    }
                } else {
                    if (System.currentTimeMillis() - refreshShortDetailLastTimes > 2000) {
                        refreshShortDetailLastTimes = System.currentTimeMillis();
                        showMessage("网络请求失败，请连网后重试");
                        refreshFailed();
                    }
                    finishRefresh();//隐藏刷新
                }
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new ShortDetailAdapter(this, mRecyclerView, mVideoList, mTextSize, new ShortDetailAdapter.OnVideoSwitchListener() {
            @Override
            public void showAd(boolean showInsertAd) {
                if (mTasksView != null)
                    mTasksView.setVisibility(View.VISIBLE);

                isPauseTime = false;

//                Timber.d("========PlayerEvent    ShortDetailAdapter   showAd     ");
                if (mPresenter != null) {
//                    Timber.d("========PlayerEvent    ShortDetailAdapter   showAd     mPresenter != null");
                    mPresenter.getRandomAd();
                }
                if (showInsertAd) {
//                    Timber.d("===ad广告");
                    if (mTasksView != null) {
                        mTasksView.setClickable(false);
                    }
                    isAd = true;
                } else {
//                    Timber.d("===ad不是广告");
                    if (mTasksView != null) {
                        mTasksView.setClickable(true);
                    }
                    isAd = false;
                }

                showBannerAd();
            }

            @Override
            public void showComment() {
                isPauseTime = false;
                saveReadRecord();//保存上一个视频观看记录

                VideoBean videoBean = mVideoList.get(mAdapter.getListPlayLogic().getPlayPosition());
                Timber.d("=============小视频 当前视频 " + mAdapter.getListPlayLogic().getPlayPosition() + "  传进来的         " + mCurrPlayPosition);


                if (videoBean != null && !TextUtils.isEmpty(videoBean.getVideoId()) && !TextUtils.isEmpty(videoBean.getUrl())) {
                    mVideoTitle = videoBean.getTitle();
                    mCurrId = videoBean.getVideoId();
                    mVideoUrl = videoBean.getUrl();
                    mVideoWebUrl = videoBean.getWebUrl();
                    mVideoImageUrl = videoBean.getThumbUrl();
                }
                //进入下一个视频  先清空再重新获取
                Timber.d("测试==================进入下一个视频");
                isShowComment(0);
                if (tvVideoCommentCount != null) {
                    tvVideoCommentCount.setVisibility(View.GONE);
                }
                mShowCount = 0;
                mInputString = "";
                clearEditText(); //清空EditText相关内容
                if (mBarrageBean != null) {
                    mBarrageBean.clear();
                }
                mOpenThread = false;

                if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {
                    hideCommentLayout();
                }
                mShow = false;
                Timber.d("=============加载下一个视频");

                //获取弹幕
                if (!isAd) {
                    isNextVideo = true;
                    requDanmu();  //请求后台弹幕接口
                } else {
                    isNextVideo = false;
                }
                if (mTasksView != null && mTasksView.getVisibility() == View.VISIBLE) {
                    isNextVideo = false;
                }

            }
        });
        manager = new MyLinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mShow) {
                    if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {//1.软键盘隐藏 2.发送评论布局隐藏 3.控件弹幕评论布局显示时
                        mInputString = etInputComment.getText().toString();
                        etVideoComment.setText(mInputString);
                        hideCommentLayout();
                        hideKeyboard();
                        closeBannerAD();
                    }
                } else if (mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE) {
                    mInputString = etInputComment.getText().toString();
                    etVideoComment.setText(mInputString);
                    showShareScene();
                    hideKeyboard();
                }
            }
        });

    }

    @Override
    protected void slideBack() {
        closeBannerAD();
        if (interstitialAD != null)
            interstitialAD.closePopupWindow();
        killMyself();
    }

    /**
     * 显示加载动画
     */

    @Override
    public void showLoading() {
        if (loadingProgressDialog == null)
            loadingProgressDialog = new LoadingProgressDialog.Builder(this).create();
        loadingProgressDialog.show();
    }

    /**
     * 隐藏加载动画
     */
    @Override
    public void hideLoading() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing())
            loadingProgressDialog.dismiss();
    }

    /**
     * 弹出Toast
     *
     * @param message 消息
     */
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

    @Override
    public void finishRefresh() {
        mRefreshLayout.finishRefresh(0);
    }

    @Override
    public void finishLoadMore() {
        mRefreshLayout.finishLoadMore(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void refreshData(List<VideoBean> videoList) {
//        Timber.d("=============小视频        刷新方法");
        mVideoList.clear();
        mAdapter.notifyItemRangeRemoved(0, mVideoList.size());
        mVideoList.addAll(videoList);
        mAdapter.notifyItemRangeChanged(0, videoList.size());
    }

    @Override
    public void loadMoreData(List<VideoBean> videoList) {

        if (TextUtils.isEmpty(mIsCollection)) {//如果这个值为空 则说明是从小视频列表进入  则存入数据库
            List<VideoBean> videoDBBeans = MyApplication.get().getDaoSession().getVideoBeanDao().loadAll();
            String videoId = videoList.get(0).getVideoId();
            for (int i = 0; i < videoDBBeans.size(); i++) {
                if (videoDBBeans.get(i).getVideoId().equals(videoId)) { //如果数据库和后台返回的视频有相同ID 则移除后面全部
                    for (int j = i; j < videoDBBeans.size(); j++) {
                        MyApplication.get().getDaoSession().getVideoBeanDao().delete(videoDBBeans.get(j));
                    }
                }
            }
            MyApplication.get().getDaoSession().getVideoBeanDao().saveInTx(videoList); //加载数据存入数据库中
            Timber.d("=============小视频        加载方法" + MyApplication.get().getDaoSession().getVideoBeanDao().loadAll().size());
        }

        int size = mVideoList.size();
        mVideoList.addAll(videoList);
        mAdapter.notifyItemRangeInserted(size, videoList.size());
    }

    @Override
    public void refreshFailed() {
        if (mErrorView == null)
            mErrorView = getLayoutInflater().inflate(R.layout.view_error_network, null);
        mRefreshLayout.setRefreshContent(mErrorView);
        mRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void videoRewardProgress(Long progress) {
        arcProgressScale = progress.intValue();

        if (arcProgressScale == mReadGoldPercent) { //如果进度条与后台返回的控制刻度相等则显示评论布局
            if (mSceneRootView != null && mSceneRootView.getVisibility() == View.GONE) {  //当输入框布局没显示时才执行显示动画
                showCommentLayout();//隐藏进度条 显示评论布局
                isShowComment(0);
            }
            mShow = true;
            isPauseTime = true;
            if (mPresenter != null) {
                mPresenter.stopTime();
            }
        }
        //Timber.d("========startTime - 视频奖励进度： " + progress);
        mTasksView.setInnerProgress(progress);
        if (progress == 100) {
            if (NetworkUtils.checkNetwork(getApplicationContext())) {

                saveReadRecord();//保存资讯阅读记录
                List<VideoRecordBean> recordList = getReadRecord();
                clearReadRecord();//清理资讯阅读记录
                String aList = new Gson().toJson(recordList);
//                Timber.d("==============RecordScale:  sum: " + sum(recordList));
                if (mPresenter != null)
                    mPresenter.videoReward(String.valueOf(System.currentTimeMillis()), mCurrId, mType, aList);
            } else {
                showMessage("连接网络可领取奖励");
                mTasksView.setInnerProgress(0);
            }
        }
    }

    @Override
    public void videoRewardSuccess(Integer rewardCoin) {
        //视频奖励进度清零
        mTasksView.setInnerProgress(0);
        isPauseTime = true;
        if (mPresenter != null) {
            mPresenter.stopTime();
        }

        mTasksView.setVisibility(View.GONE);

        if (mSceneRootView != null && mSceneRootView.getVisibility() == View.GONE) {  //当输入框布局没显示时才执行显示动画
            showCommentLayout();//隐藏进度条 显示评论布局
            isShowComment(0);
        }
        mShow = true;

        //设置奖励金币、动画和音效
        flRewardAnim.setVisibility(View.VISIBLE);
        tvReward.setText(String.valueOf(rewardCoin));
        //爆裂效果
        if (isOneExplode) {
            explosionAnim();
            isOneExplode = false;
        } else {
            showRewardAnim();
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
            //视频奖励进度清零
            clearReadRecord();
            mTasksView.setInnerProgress(0);
            if (AssistPlayer.get().isPlaying() && mPresenter != null) {
                mPresenter.stopTime();
                mPresenter.startTime();
                //Timber.d("========startTime - receiveLoginState");
            }
            queryDeviceInfo();//登录界面返回后 获取数据库设备信息
            //获取弹幕
            requDanmu();  //请求后台弹幕接口
        }
    }

    @Override
    public void videoRewardFail() {
//        //Timber.d("==VideoState" + "领取失败");
        //视频奖励进度清零
        mTasksView.setInnerProgress(0);
        isPauseTime = false;
        if (AssistPlayer.get().isPlaying() && mPresenter != null) {
            mPresenter.stopTime();
            mPresenter.startTime();
            //Timber.d("========startTime - videoRewardFail");
        }
    }

    @Override
    public void setVideoShareContent(NewsOrVideoShareBean shareContent) {
        if (!mVideoImageUrl.startsWith("http") && !TextUtils.isEmpty(mVideoImageUrl)) {
            mVideoImageUrl = "http:" + mVideoImageUrl;
        }
        oneKeyShare.setShareType(shareContent.getType());
        oneKeyShare.setShareWebUrl(shareContent.getUrl());
        oneKeyShare.setShareImagePath(imageUserPath);
        oneKeyShare.setShareImageUrl(mVideoImageUrl);
        if (!TextUtils.isEmpty(shareContent.getUrl())) {
            oneKeyShare.show(api);
        } else {
            showMessage(getResources().getString(R.string.not_net));
        }
    }

    /**
     * 下载回调
     *
     * @param filePath 文件地址
     */
    @Override
    public void downloadCallBack(String filePath) {
        imageUserPath = filePath;
        //Timber.d("==shareVideo  downloadCallBack：" + imageUserPath);
    }

    /*判断当前是否有网络*/
    private boolean isInternet() {
        return NetworkUtils.checkNetwork(getApplicationContext());
    }

    /**
     * 显示奖励动画
     */
    private void showRewardAnim() {
        resetReward(flRewardAnim);
        explosionAnim();
        explosionField.clear();
    }

    /**
     * 金币领取图恢复
     */
    private void resetReward(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                resetReward(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }

    /**
     * 金币爆裂效果
     */
    private void explosionAnim() {
        handler.postDelayed(mExplodeRunnable, 2000);
    }

    Runnable mExplodeRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1); //金币爆裂
        }
    };

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
            dbUpdateUserAvatar(list.get(0));
        }
    }

    /**
     * @param userInfoBean 获取到数据库的用户信息后，更新界面
     */
    public void dbUpdateUserAvatar(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            if (!TextUtils.isEmpty(userInfoBean.getAvatar())) {
                shareImageUrl = userInfoBean.getAvatar();
                if (!TextUtils.isEmpty(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME) && new File(Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME).exists()) {
                    //Timber.d("==shareMine  :" + "有登录，有图片：" + Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME);
                    imageUserPath = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
                } else {
                    //Timber.d("==shareMine  :" + "有登录，无图片");
                    assert mPresenter != null;
                    mPresenter.requestPermission(userInfoBean.getAvatar());
                }
            }
        }
    }

    /**
     * 获取分享数据
     */
    private void getShareContent() {

        VideoBean videoBean = mVideoList.get(mAdapter.getListPlayLogic().getPlayPosition());
        oneKeyShare.setShareContent(new ShareBean.Builder()
                .title(videoBean.getTitle())
                .content(videoBean.getDescribe())
                .create());

        if (mPresenter != null) {
            String urlEncode = null;
            //Timber.d("=================编码前的地址" + videoBean.getWebUrl());
            try {
                urlEncode = URLEncoder.encode(videoBean.getWebUrl(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //获取系统自带的ua
            String userAgent = System.getProperty("http.agent");
            mPresenter.videoShare(urlEncode, userAgent);
        }
    }

    /**
     * 显示Banner广告
     */
    private void showBannerAd() {
//        Timber.d("========PlayerEvent    进入showBannerAd");
//        Timber.d("========PlayerEvent getCurrentPosition：" + AssistPlayer.get().getCurrentPosition());
//        Timber.d("========PlayerEvent getDuration：" + AssistPlayer.get().getDuration());
        //  isAd  为是不是广告页，广告页的banner广告   有   关闭按钮；
        //  当 llFabToolbar 可以看到  或者  llSend 可以看到时
        //  AssistPlayer.get().getCurrentPosition() 为当前的小视频播放进度；
        //  AssistPlayer.get().getDuration()  为当前的小视频播放  总  进度；
        //  当 getCurrentPosition() 为 0 ；小视频不是播放状态，广告页的banner广告   无   关闭按钮；
        //  当 getCurrentPosition() >= getDuration();小视频是播放完成状态，广告页的banner广告   无   关闭按钮；
        if (isAd) {
            isAdCloseBotton = true;
//            Timber.d("========PlayerEvent 1 有关闭广告");
        } else if ((mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE && mSceneRootView.getAlpha() == 1f)) {
            isAdCloseBotton = true;
//            Timber.d("========PlayerEvent 2 有关闭广告");
        } else if (AssistPlayer.get().getCurrentPosition() == 0
                || AssistPlayer.get().getCurrentPosition() >= AssistPlayer.get().getDuration()) {
            isAdCloseBotton = false;
//            Timber.d("========PlayerEvent 3 无关闭广告");
        } else {
            isAdCloseBotton = true;
//            Timber.d("========PlayerEvent 4 有关闭广告");
        }

        if (expressADContainer != null && expressADContainer.getChildCount() > 0) {
            if (ivAdClose != null && ivAdClose.getVisibility() == View.VISIBLE) {
                ivAdClose.setVisibility(View.GONE);
            }
            expressADContainer.removeAllViews();
            expressADContainer.setVisibility(View.GONE);
        }
        if (baiDuBannerView != null) {
            baiDuBannerView.destroy();
            baiDuBannerView = null;
        }

        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
        isHasCsjAd = false;
        if (mTTBannerAd != null) {
            mTTBannerAd = null;
        }
        if (mAdSlot != null) {
            mAdSlot = null;
        }
        if (mTTAdNative != null) {
            mTTAdNative = null;
        }

//        Timber.d("========PlayerEvent    进入showBannerAd   getAd_type     ");
        if (mRandomAdBean != null && !TextUtils.isEmpty(mRandomAdBean.getAd_type())) {
//            Timber.d("========PlayerEvent    进入showBannerAd   ==DSP广告   "+ mRandomAdBean.getAd_type());
//            Timber.d("==DSP广告 :" + mRandomAdBean.getAd_type());
            switch (mRandomAdBean.getAd_type()) {
                case Constant.AD_SUPPORT_SDK_GDT:
                    getBannerGDT().loadAD();
                    break;
                case Constant.AD_SUPPORT_SDK_BAIDU:
                    showBaiduBanner();
                    break;
                case Constant.AD_SUPPORT_SDK_CSJ:
                    isHasCsjAd = true;
                    showTTBanner();
                    break;
                case Constant.AD_SUPPORT_SDK_OWN:
                    showOWNBanner();//判断广告
                    break;
            }
        } else {
//            Timber.d("========PlayerEvent    进入showBannerAd   getAd_type     :mRandomAdBean null");
            Timber.d("==DSP广告 :mRandomAdBean null");
        }
    }


    @Override
    public void randomDSPAd(RandomAdBean randomAdBean) {
        mRandomAdBean = randomAdBean;
//        Timber.d("========PlayerEvent    randomDSPAd     "+mRandomAdBean);
        Timber.d("==DSP  小视频详情页  randomDSPAd  ："
                + "\n mRandomAdBean.getAd_type():" + mRandomAdBean.getAd_type()
                + "\n mRandomAdBean.getAd_info_title():" + mRandomAdBean.getAd_info_title()
                + "\n mRandomAdBean.getAd_info_images():" + mRandomAdBean.getAd_info_images()
                + "\n mRandomAdBean.getAd_info_click_action():" + mRandomAdBean.getAd_info_click_action()
                + "\n mRandomAdBean.getAd_info_click_url():" + mRandomAdBean.getAd_info_click_url());
    }

    /**
     * 显示穿山甲banner广告
     */
    private void showTTBanner() {
        int width = ArmsUtils.getScreenWidth(this.getApplicationContext());
        int height = width / 4;
        if (mTTAdNative == null) {
            mTTAdNative = TTAdManagerHolder.getInstance(this).createAdNative(this);
        }
        if (mAdSlot == null) {
            mAdSlot = new AdSlot.Builder()
                    .setCodeId(Constant.CSJ_AD_ID_SHORT_BANNER)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(width, height)
                    .build();
        }
        if (mTTAdNative != null) {
            mTTAdNative.loadBannerAd(mAdSlot, new TTAdNative.BannerAdListener() {
                @Override
                public void onError(int code, String message) {
                    showOWNBanner();//穿山甲banner广告获取不成功时；
//                    Timber.d("==TTAd 小视频详情页banner  loadBannerAd()  加载广告错误onError  code:" + code + "  message:" + message);
                }

                @Override
                public void onBannerAdLoad(TTBannerAd ttBannerAd) {
//                    Timber.d("==TTAd 小视频详情页banner  loadBannerAd()  加载广告成功onBannerAdLoad  ");
                    if (isHasCsjAd && mTTAdNative != null && ttBannerAd != null && ttBannerAd.getBannerView() != null) {
                        mTTBannerAd = ttBannerAd;
                        View banner = mTTBannerAd.getBannerView();
                        if (isHasCsjAd && expressADContainer != null) {
                            expressADContainer.removeAllViews();
                            expressADContainer.addView(banner);
                            expressADContainer.setVisibility(View.VISIBLE);
                        }

                        //设置广告互动监听回调
                        mTTBannerAd.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                            @Override
                            public void onAdClicked(View view, int type) {
//                                Timber.d("==TTAd 小视频详情页banner  loadBannerAd()  onBannerAdLoad()  广告被点击");
                            }

                            @Override
                            public void onAdShow(View view, int type) {
                                if (mTTBannerAd != null) {
                                    if (bannerView != null) {
                                        bannerView.destroy();
                                        bannerView = null;
                                    }
                                    if (baiDuBannerView != null) {
                                        baiDuBannerView.destroy();
                                        baiDuBannerView = null;
                                    }
                                    if (ivAdClose != null)
                                        ivAdClose.setVisibility(View.GONE);
                                }
//                                Timber.d("==TTAd 小视频详情页banner  loadBannerAd()  onBannerAdLoad()  广告展示");
                            }
                        });

                        //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
                        mTTBannerAd.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                            @Override
                            public void onSelected(int position, String value) {
//                                Timber.d("==TTAd 小视频详情页banner  loadBannerAd()  onBannerAdLoad()  点击 " + value);
                                //用户选择不喜欢原因后，移除广告展示
                                closeBannerAD();
                            }

                            @Override
                            public void onCancel() {
//                                Timber.d("==TTAd 小视频详情页banner  loadBannerAd()  onBannerAdLoad()  点击取消");
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 显示自营banner广告
     */
    private void showOWNBanner() {
        int width = ArmsUtils.getScreenWidth(this.getApplicationContext());
        int height = width / 7;
        if (mRandomAdBean != null && expressADContainer != null) {
            expressADContainer.removeAllViews();
            View ownBanner = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.item_short_ad_banner, expressADContainer, false);
            if (ownBanner != null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                ownBanner.setLayoutParams(layoutParams);
                ImageView adImage = ownBanner.findViewById(R.id.iv_adbanner_short);
                TextView adTitle = ownBanner.findViewById(R.id.tv_adbanner_short);
                TextView adBotton = ownBanner.findViewById(R.id.bt_adbanner_short);

                String strAdImage = mRandomAdBean.getAd_info_images().get(0);
                String strAdUrl = mRandomAdBean.getAd_info_click_url();
                String strAdTitle = mRandomAdBean.getAd_info_title();
                String strAdAction = mRandomAdBean.getAd_info_click_action();
                if (!TextUtils.isEmpty(strAdImage))
                    imageLoader.loadImage(this.getApplicationContext(),
                            CustomImageConfig.builder()
                                    .url(strAdImage)
                                    .isCenterCrop(true)
                                    .cacheStrategy(1)
                                    .errorPic(R.drawable.icon_activity_transparent)
                                    .placeholder(R.drawable.icon_activity_transparent)
                                    .imageView(adImage)
                                    .build());
                adTitle.setText(mRandomAdBean.getAd_info_title());
                ownBanner.setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(strAdAction) && !TextUtils.isEmpty(strAdUrl)) {
                        if (strAdAction.equals(Constant.AD_JUMP_INTERNAL_URL)) {
                            Intent innerIntent = new Intent(ShortDetailActivity.this, AdWebActivity.class);
                            innerIntent.putExtra("AdUrl", strAdUrl);
                            innerIntent.putExtra("AdTitle", strAdTitle);
                            launchActivity(innerIntent);
                        } else if (strAdAction.equals(Constant.AD_JUMP_DOWNLOAD_APK)) {

                        } else if (strAdAction.equals(Constant.AD_JUMP_EXTERNAL_URL)) {

                        }
                    }
                });
                expressADContainer.addView(ownBanner);
                expressADContainer.setVisibility(View.VISIBLE);
                if (isAdCloseBotton && ivAdClose != null)
                    ivAdClose.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 初始化广点通广告
     */
    private BannerView getBannerGDT() {
//        closeBannerAD();

        //初始化广点通Banner广告
        bannerView = new BannerView(this, ADSize.BANNER, Constant.GDT_APP_ID, Constant.GDT_AD_ID_BANNER);
        bannerView.setRefresh(30);//设置刷新频率,为0或30~120之间的数字，单位为s,0标识不自动轮播,默认30S
        bannerView.setADListener(new AbstractBannerADListener() {
            @Override
            public void onNoAD(AdError adError) {
                showOWNBanner();//广点通banner广告获取不成功时；
                //Timber.d("======BannerView -- 无广告加载: " + adError.getErrorCode() + " " + adError.getErrorMsg());
            }


            @Override
            public void onADReceiv() {
                if (bannerView != null && isAdCloseBotton && ivAdClose != null)
                    ivAdClose.setVisibility(View.VISIBLE);
                //Timber.d("======BannerView -- 广告加载成功");
            }

            @Override
            public void onADExposure() {
                super.onADExposure();
            }

            @Override
            public void onADClosed() {
                super.onADClosed();
                if (ivAdClose != null)
                    ivAdClose.setVisibility(View.GONE);
            }
        });

        if (expressADContainer != null && bannerView != null) {
            expressADContainer.removeAllViews();
            expressADContainer.addView(bannerView);
            expressADContainer.setVisibility(View.VISIBLE);
        }
        return this.bannerView;
    }

    /**
     * 关闭Banner广告
     */
    private void closeBannerAD() {
        if (expressADContainer != null) {
            expressADContainer.removeAllViews();
            expressADContainer.setVisibility(View.GONE);
        }
        if (ivAdClose != null)
            ivAdClose.setVisibility(View.GONE);
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
        if (baiDuBannerView != null) {
            baiDuBannerView.destroy();
            baiDuBannerView = null;
        }
        isHasCsjAd = false;
        if (mTTBannerAd != null) {
            mTTBannerAd = null;
        }
        if (mAdSlot != null) {
            mAdSlot = null;
        }
        if (mTTAdNative != null) {
            mTTAdNative = null;
        }
    }

    private void showBaiduBanner() {
        baiDuBannerView = new AdView(this, Constant.BAIDU_AD_ID_BANNER);
        baiDuBannerView.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {

                //Timber.d("======Baidu MSSP onAdReady");
            }

            @Override
            public void onAdShow(JSONObject jsonObject) {
                if (baiDuBannerView != null && isAdCloseBotton && ivAdClose != null) {
                    ivAdClose.setVisibility(View.VISIBLE);
                }
                //Timber.d("======Baidu MSSP onAdShow " + jsonObject.toString());
            }

            @Override
            public void onAdClick(JSONObject jsonObject) {
                //Timber.d("======Baidu MSSP onAdClick " + jsonObject.toString());
            }

            @Override
            public void onAdFailed(String s) {
                showOWNBanner();//百度banner广告获取不成功时；
                //Timber.d("======Baidu MSSP onAdFailed " + s);
            }

            @Override
            public void onAdSwitch() {
                //Timber.d("======Baidu MSSP onAdSwitch");
            }

            @Override
            public void onAdClose(JSONObject jsonObject) {
                if (expressADContainer != null) {
                    expressADContainer.removeAllViews();
                }
                if (ivAdClose != null)
                    ivAdClose.setVisibility(View.GONE);
                //Timber.d("======Baidu MSSP onAdClose " + jsonObject.toString());
            }
        });

        if (expressADContainer != null && baiDuBannerView != null) {
            expressADContainer.removeAllViews();
            expressADContainer.addView(baiDuBannerView);
            expressADContainer.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 初始化插屏广告
     */
//    private void initGDTInterAd() {
//        interstitialAD = new InterstitialAD(this, Constant.GDT_APP_ID, Constant.GDT_AD_ID_SCREEN);
//        interstitialAD.setADListener(new InterstitialADListener() {
//            @Override
//            public void onADReceive() {
//                //Timber.d("======InterstitialAD -- 插屏广告加载成功");
//                interstitialAD.show();
//                if (mPresenter != null) {
//                    mPresenter.stopTime();
//                }
//                AssistPlayer.get().pause();
//            }
//
//            @Override
//            public void onNoAD(AdError adError) {
//                //Timber.d("======InterstitialAD -- 无插屏广告填充" + adError.getErrorMsg() + " " + adError.getErrorCode());
//            }
//
//            @Override
//            public void onADOpened() {
//                //Timber.d("======InterstitialAD -- onADOpened");
//            }
//
//            @Override
//            public void onADExposure() {
//                //Timber.d("======InterstitialAD -- onADExposure");
//            }
//
//            @Override
//            public void onADClicked() {
//                //Timber.d("======InterstitialAD -- onADClicked");
//            }
//
//            @Override
//            public void onADLeftApplication() {
//                //Timber.d("======InterstitialAD -- onADLeftApplication");
//            }
//
//            @Override
//            public void onADClosed() {
////                AssistPlayer.get().rePlay(0);
//                if (interstitialAD != null)
//                    interstitialAD.loadAD();
//                //Timber.d("======InterstitialAD -- onADClosed");
//            }
//        });
//
//    }

//    private void initBaiDuInterAd() {
//        baiDuInterAd = new InterstitialAd(this, Constant.BAIDU_AD_ID_INTER);
//        baiDuInterAd.setListener(new InterstitialAdListener() {
//            @Override
//            public void onAdReady() {
//            }
//
//            @Override
//            public void onAdPresent() {
//                //Timber.d("======Baidu MSSP 插屏广告 onAdPresent ");
//                AssistPlayer.get().pause();
//                if (mPresenter != null) {
//                    mPresenter.stopTime();
//                }
//            }
//
//            @Override
//            public void onAdClick(InterstitialAd interstitialAd) {
//                //Timber.d("======Baidu MSSP 插屏广告 onAdClick ");
//            }
//
//            @Override
//            public void onAdDismissed() {
//                if (baiDuInterAd != null)
//                    baiDuInterAd.loadAd();
////                AssistPlayer.get().rePlay(0);
//                //Timber.d("======Baidu MSSP 插屏广告 onAdDismissed ");
//            }
//
//            @Override
//            public void onAdFailed(String s) {
//                //Timber.d("======Baidu MSSP 插屏广告 onAdClose " + s);
//            }
//        });
//        baiDuInterAd.loadAd();
//    }
    private void initVideoPlayer() {
        AssistPlayer.get().addOnReceiverEventListener(this);
        AssistPlayer.get().addOnPlayerEventListener(this);
        if (mReceiverGroup == null) {
            mReceiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(this);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, false);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_BOTTOM_ENABLE, false);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_COMPLETE_AUTO_REPLAY, true);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_SCREEN_SWITCH_ENABLE, false);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, false);
        }
        AssistPlayer.get().setReceiverGroup(mReceiverGroup);
        if (mAdapter != null) {
            mAdapter.getListPlayLogic().attachPlay();
        }
        AssistPlayer.get().setSourceSize(AspectRatio.AspectRatio_FILL_PARENT);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                //小视频播放完成  显示评论布局
                if (mSceneRootView != null && mSceneRootView.getVisibility() == View.GONE) {  //当输入框布局没显示时才执行显示动画
                    showCommentLayout();//隐藏进度条 显示评论布局
                    isShowComment(0);
                }
                mShow = true;
                showBannerAd();
//                Timber.d("========PlayerEvent -ON_PLAY_COMPLETE");
                if (mPresenter != null) {
                    mPresenter.stopTime();
                }

                isPauseTime = true;
                AssistPlayer.get().rePlay(0);
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
//                        Timber.d("========stopTime -ON_STATUS_CHANGE + STATE_PAUSED");
                    }
                    showBannerAd();
                } else if (status == IPlayer.STATE_STARTED) {
//                    Timber.d("========PlayerEvent -ON_STATUS_CHANGE + STARTED isPauseTime: " + isPauseTime + AssistPlayer.get().isPlaying());
                    if (!isPauseTime && expressADContainer != null) {
                        expressADContainer.removeAllViews();
                    }
                    if (mPresenter != null && !isPauseTime && AssistPlayer.get().isPlaying()) {
                        mPresenter.stopTime();
                        mPresenter.startTime();
//                        Timber.d("========startTime - ON_STATUS_CHANGE  STATE_STARTED");
                    }
                } else if (status == IPlayer.STATE_END || status == IPlayer.STATE_ERROR
                        || status == IPlayer.STATE_IDLE) {
//                    Timber.d("========PlayerEvent -ON_STATUS_CHANGE + END");
                    if (mPresenter != null) {
                        mPresenter.stopTime();
//                        Timber.d("========stopTime - ON_STATUS_CHANGE  STATE_STARTED");
                    }
                    showBannerAd();
                } else if (status == IPlayer.STATE_STOPPED
                        || status == IPlayer.STATE_PLAYBACK_COMPLETE) {
//                    Timber.d("========PlayerEvent -ON_STATUS_CHANGE + STOPPED or COMPLETE");
                    showBannerAd();
                    if (mPresenter != null && !isPauseTime) {
                        mPresenter.stopTime();
//                        Timber.d("========stopTime - ON_STATUS_CHANGE  STATE_STARTED_COMPLETE");
                    }
                } else {
//                    showBannerAd();//注意点
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR:
//                Timber.d("========PlayerEvent -ON_PROVIDER_DATA_ERROR");
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_START:
//                Timber.d("========PlayerEvent -ON_START" + AssistPlayer.get().isPlaying());
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE:
//                Timber.d("========PlayerEvent -ON_PAUSE");
//                showBannerAd();
                if (mPresenter != null) {
                    mPresenter.stopTime();
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_STOP:
//                Timber.d("========PlayerEvent -ON_STOP");
//                showBannerAd();
                if (mPresenter != null) {
                    mPresenter.stopTime();
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START:
//                Timber.d("========PlayerEvent -ON_BUFFERING_START");
                showBannerAd();
                if (AssistPlayer.get().getState() == IPlayer.STATE_STARTED && mPresenter != null) {
                    mPresenter.stopTime();
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
//                Timber.d("========PlayerEvent -ON_BUFFERING_END");
                if (AssistPlayer.get().getState() == IPlayer.STATE_STARTED && mPresenter != null && !isPauseTime) {
                    mPresenter.stopTime();
                    mPresenter.startTime();
                    if (expressADContainer != null) {
                        expressADContainer.removeAllViews();
                    }

                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
//                showBannerAd();
//                Timber.d("========PlayerEvent -ON_DATA_SOURCE_SET");
                break;
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
            case DataInter.Event.EVENT_CODE_REQUEST_CONTINUE:
                if ((mSceneRootView != null && mSceneRootView.getVisibility() == View.VISIBLE && mSceneRootView.getAlpha() == 1f))
                    showBannerAd();//从暂停状态恢复到播放；
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initVideoPlayer();  //初始化播放器相关

        if (mAdapter != null && mReceiverGroup != null) {
            mAdapter.getListPlayLogic().setReceiverGroup(mReceiverGroup);
        }

        if (TextUtils.equals(isShowComment, "shortDetailHide")) {
            ivVideoCommentBarrage.setSelected(false);
            isShowComment(0);
        } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "shortDetailShow")) {
            ivVideoCommentBarrage.setSelected(true);
            isShowComment(1);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isShowComment(0);
//        handler.removeCallbacksAndMessages(null);
        SoundPoolManager.getInstance(getApplicationContext()).stopRinging();
        AssistPlayer.get().pause();
    }


    @Override
    protected void onDestroy() {
        if (TextUtils.isEmpty(mIsCollection))
            EventBus.getDefault().post(mVideoList.get(mAdapter.getListPlayLogic().getPlayPosition()).getVideoId(), EventBusTags.TAG_SHORT_FINISH); //发送信息到列表滑动到播放位置
        clearDanmu(); //清空所有弹幕相关
        clearAnimation();//清空所有动画
        clearAd(); //清理所有广告相关
        explosionField = null;
        SoundPoolManager.getInstance(getApplicationContext()).release();

        if (textWatcher != null && etVideoComment != null && etInputComment != null) {
            etVideoComment.removeTextChangedListener(textWatcher);
            etInputComment.removeTextChangedListener(textWatcher);
            textWatcher = null;
        }
        if (mVideoList != null) {
            mVideoList.clear();
            mVideoList = null;
        }

        if (handler != null) {
            handler.removeCallbacks(mExplodeRunnable);
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        clearReadRecord();//清理资讯阅读记录
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.recycle();
            mAdapter = null;
        }

        if (manager != null) {
            manager = null;
        }

        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
            loadingProgressDialog = null;
        }
        if (api != null) {
            api.detach();
        }
        oneKeyShare.destroy();
        oneKeyShare = null;


        if (mReceiverGroup != null) {
            mReceiverGroup.clearReceivers();
            mReceiverGroup = null;
        }
        AssistPlayer.get().removeReceiverEventListener(this);
        AssistPlayer.get().removePlayerEventListener(this);
        AssistPlayer.get().destroy();

        MyApplication.ignoreMobile = false;

    }

    /**
     * 接收弹幕点击事件 显示点赞动画
     */
    @Subscriber(tag = EventBusTags.TAG_SHORT_ANIMATION_THUBMS_UP)
    private void commentThubmsUpAnimation(DanmuEvent event) {
        if (flutteringLayout != null && mPresenter != null && mBarrageBean != null) {
            flutteringLayout.updateDanmuView(event.getViewX(), event.getViewY());
            flutteringLayout.addHeart();
        }
    }

    /**
     * 接收弹幕点击事件 去调用点赞评论接口
     */
    @Subscriber(tag = EventBusTags.TAG_SHORT_COMMENT_THUBMS_UP)
    private void commentThubmsUp(String position) {
        if (!TextUtils.isEmpty(position) && mPresenter != null && mBarrageBean != null) {
            danmuCurrPosition = Integer.parseInt(position);
            mPresenter.commentThumbsUp(mBarrageBean.get(Integer.parseInt(position)).getId(), "wuli");
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
            mBarrageBean.get(danmuCurrPosition).setThumbsUpCount(String.valueOf(thubmsUpCount));
            mDanmuAdapter.updateDanmuView(thubmsUpCount, mBarrageBean.get(danmuCurrPosition).getCanThumbsUp(), true);
        }
    }

    /**
     * 评论成功
     */
    @Override
    public void commentSuccess() {
//        NewsDanmuBean newsDanmuBean = new NewsDanmuBean();
//        newsDanmuBean.setContent(mComment);
//        mDanmuContainerView.addDanmu(newsDanmuBean);
        BarrageBean barrageBean = new BarrageBean();
        barrageBean.setContent(mComment);
        mBarrageBean.add(barrageBean);

        clearEditText(); //清空EditText相关内容
        mInputString = "";
        showShareScene();
        hideKeyboard();
        showMessage("发布成功，优质评论将被优先展示");
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
     * @param newBarrageBean
     */
    @Override
    public void loadBarrage(NewBarrageBean newBarrageBean) {
        if (Integer.parseInt(newBarrageBean.getCmtCount()) > 999) {
            tvVideoCommentCount.setVisibility(View.VISIBLE);
            tvVideoCommentCount.setText("999+");
        } else if (Integer.parseInt(newBarrageBean.getCmtCount()) > 0) {
            tvVideoCommentCount.setVisibility(View.VISIBLE);
            tvVideoCommentCount.setText(newBarrageBean.getCmtCount());
        } else if (Integer.parseInt(newBarrageBean.getCmtCount()) == 0) {
            //如果没有弹幕 则把小视频标题添加进去 显示
            if (!TextUtils.isEmpty(mVideoTitle) && mBarrageBean != null && mBarrageBean.size() < 1) {
                mBarrageBean.clear();
                BarrageBean barrageBean = new BarrageBean();
                barrageBean.setContent(mVideoTitle);
                mBarrageBean.add(barrageBean);
            }
        }

        //            //Timber.d("===========弹幕获取成功" + cotent);
        mBarrageBean.addAll(newBarrageBean.getBarrageBeans());
        //Timber.d("=========弹幕多少 mBarrages.size()" + mBarrages.size());

        mOpenThread = true;
        if (TextUtils.equals(isShowComment, "shortDetailHide")) {
            ivVideoCommentBarrage.setSelected(false);
            isShowComment(0);
        } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "shortDetailShow")) {
            ivVideoCommentBarrage.setSelected(true);
            isShowComment(1);
        }
        if (newBarrageBean.getCollection() == 1) {
            ivVideoCommentCollection.setSelected(true);
        } else {
            ivVideoCommentCollection.setSelected(false);
        }
    }

    /**
     * 获取弹幕
     */
    public void getComment() {
        mBarrageBean = new ArrayList<>();
        mDanmuAdapter = new DanmuShortAdapter(this.getApplicationContext());
        mDanmuContainerView.setAdapter(mDanmuAdapter);
        mDanmuContainerView.setGravity(DanmuContainerView.GRAVITY_FULL);

        requDanmu();  //请求后台弹幕接口
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

    // 弹幕是否显示  0 不显示    1显示

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
                if ((TextUtils.isEmpty(isShowComment) && mOpenThread && mBarrageBean.size() > 0) || (TextUtils.equals(isShowComment, "shortDetailShow")) && mOpenThread && mBarrageBean.size() > 0) {
                    startDanmu(true);
                }

                break;
        }

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
//                Timber.d("========弹幕多少 mShowCount" + mShowCount);
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
            if (mPosition > 5) {
                mPosition -= 5;
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
                    mPosition = 0;
                    mDelayedPotisiton = 0;
                }
            } else {
//                Timber.d("========弹幕多少 2秒");
                if (handler != null) {
                    handler.sendEmptyMessage(3); //开启弹幕线程
                }
            }
        }
    }


    public void setProgress(PathPoint newLoc) {
        mTasksView.setTranslationX(newLoc.mX);
        mTasksView.setTranslationY(newLoc.mY);
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
        mOutPRAnimAlpha = ObjectAnimator.ofFloat(mTasksView, "alpha", 1.0f, 0f);
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
                            if (!isNextVideo)
                                showBannerAd();
                            isNextVideo = false;
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
            mOutPRAnimTranslation = ObjectAnimator.ofFloat(mTasksView, "translationX", "translationY", path);
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
                    if (!isNextVideo)
                        showBannerAd();
                    isNextVideo = false;
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
        mEnterPRAnimAlpha = ObjectAnimator.ofFloat(mTasksView, "alpha", 0f, 1.0f);
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
                    if (mTasksView != null) {
                        if (mTasksView.getAlpha() == 0f || mTasksView.getVisibility() == View.GONE) {
                            mTasksView.setAlpha(1f);
                            mTasksView.setVisibility(View.VISIBLE);
                            mTasksView.setClickable(true);
                        }
                        if (mSceneRootView != null) {
                            mSceneRootView.setVisibility(View.GONE);
                        }
                        mTasksView.setClickable(true);
                        if (!isNextVideo)
                            showBannerAd();
                        isNextVideo = false;
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
            mEnterPRAnimTranslation = ObjectAnimator.ofFloat(mTasksView, "translationX", "translationY", path_return);
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

                    if (mTasksView != null) {
                        if (mTasksView.getAlpha() == 0f || mTasksView.getVisibility() == View.GONE) {
                            mTasksView.setAlpha(1f);
                            mTasksView.setVisibility(View.VISIBLE);
                            mTasksView.setClickable(true);
                        }
                        if (mSceneRootView != null) {
                            mSceneRootView.setVisibility(View.GONE);
                        }
                        mTasksView.setClickable(true);
                        if (!isNextVideo)
                            showBannerAd();
                        isNextVideo = false;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tasks_view:                       //进度条点击事件
                if (!isAd) {
                    isNextVideo = false;
                    closeBannerAD();
                    isShowComment(0);
                    mTasksView.setClickable(false);
                    if (mTasksView.getAlpha() != 0f) {
                        if (mTasksView != null && mTasksView.getVisibility() == View.VISIBLE)
                            showCommentLayout();
                    }
                }
                break;
            case R.id.iv_comment_back:   //返回
                killMyself();
                break;
            case R.id.et_comment:       //分享布局中的输入框
                if (!isAd) {
                    showSendScene();
                    etInputComment.requestFocus();
                    if (!TextUtils.isEmpty(etInputComment.getText().toString())) {
                        mInputString = etInputComment.getText().toString();
                    }
                    if (!TextUtils.isEmpty(etVideoComment.getText().toString())) {
                        mInputString = etInputComment.getText().toString();
                    }
                    imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.showSoftInput(etInputComment, 0);
                }
                break;
            case R.id.iv_comment_count:   //评论显示条数 跳至评论2级页面
                if (!isAd && isLogin()) {
                    Intent intent = new Intent(ShortDetailActivity.this, NewsCommentActivity.class);
                    intent.putExtra("Id", mCurrId);
                    intent.putExtra("Type", mType);
                    intent.putExtra("Title", mVideoTitle);
                    intent.putExtra("Url", mVideoWebUrl);
                    intent.putExtra("commitFrom", "wuli");
                    ArmsUtils.startActivity(intent);
                }
                break;
            case R.id.iv_comment_barrage:   //开启关闭弹幕
                if (!isAd) {
                    if (TextUtils.equals(isShowComment, "shortDetailHide")) { //如果是屏蔽状态  则打开弹幕
                        ivVideoCommentBarrage.setSelected(true);
                        DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SHORT_VIDEO_COMMENT, "shortDetailShow");
                        isShowComment = "shortDetailShow";
                        isShowComment(1);
                    } else if (TextUtils.isEmpty(isShowComment) || TextUtils.equals(isShowComment, "shortDetailShow")) {
                        mShowCount = 0;
                        mPosition = 0;
                        ivVideoCommentBarrage.setSelected(false);
                        DataHelper.setStringSF(getApplicationContext(), Constant.SP_KEY_SHORT_VIDEO_COMMENT, "shortDetailHide");
                        isShowComment = "shortDetailHide";
                        isShowComment(0);
                    }
                    if (isClickTime(2)) { //0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                        if (TextUtils.equals(isShowComment, "shortDetailHide")) {
                            showMessage("弹幕已关闭");
                        } else {
                            showMessage("弹幕已开启");
                        }
                    }
                }
                break;
            case R.id.iv_comment_collection:    //收藏
                if (!isAd && isClickTime(1) && isLogin()) {//0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (isInternet()) {
                        try {
                            String urlEncode = URLEncoder.encode(mVideoWebUrl, "UTF-8");
                            if (mPresenter != null)
                                mPresenter.saveShortCollection(urlEncode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.iv_comment_share:     //分享
                if (!isAd && isClickTime(3)) {//0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    if (isInternet()) {
                        getShareContent();
                    } else {
                        showMessage("网络请求失败，请连网后重试");
                    }
                }
                break;
            case R.id.bt_send:      //评论发送布局 发送按钮
                if (!isAd && isLogin() && isClickTime(0)) {//0:发送按钮      1:收藏按钮     2:弹幕按钮     3分享按钮
                    mComment = etInputComment.getText().toString();
                    if (TextUtils.isEmpty(mComment)) {
                        showMessage("评论不能为空");
                    } else {
                        if (isInternet()) {
                            //编码后的url
                            try {
                                mComment = mComment.replaceAll("\n", "  ");  //检测换行符 替换成空格
                                String commentString = URLEncoder.encode(mComment, "UTF-8");
                                mVideoWebUrl = TextUtils.isEmpty(mVideoWebUrl) ? "" : mVideoWebUrl;
                                String urlEncode = URLEncoder.encode(mVideoWebUrl, "UTF-8");
                                String reqId = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + (int) ((Math.random() * 9 + 1) * 100000);
                                mCommentTime = System.currentTimeMillis() - mCommentTime;
                                int time = (int) mCommentTime / 1000;
                                ///非空处理
                                commentString = TextUtils.isEmpty(commentString) ? "" : commentString;
                                mCurrId = TextUtils.isEmpty(mCurrId) ? "" : mCurrId;
                                mType = TextUtils.isEmpty(mType) ? "" : mType;
                                urlEncode = TextUtils.isEmpty(urlEncode) ? "" : urlEncode;
                                mVideoTitle = TextUtils.isEmpty(mVideoTitle) ? "" : mVideoTitle;
                                reqId = TextUtils.isEmpty(reqId) ? "" : reqId;
                                if (mPresenter != null)
                                    mPresenter.foundComment(commentString, "wuli", time, mCurrId, mType, urlEncode, mVideoTitle, reqId, 0, 0, "");

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
            launchActivity(new Intent(ShortDetailActivity.this, LoginActivity.class));
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
            etVideoComment.setText(mInputString);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initSceneAnimation() {
        ViewGroup commentCount = (ViewGroup) getLayoutInflater().inflate(R.layout.scene_comment_share, null);
        ViewGroup commentSubmit = (ViewGroup) getLayoutInflater().inflate(R.layout.scene_comment_send, null);

        btnSend = commentSubmit.findViewById(R.id.bt_send);
        etInputComment = commentSubmit.findViewById(R.id.et_comment);

        etVideoComment = commentCount.findViewById(R.id.et_comment);
        ivVideoCommentBack = commentCount.findViewById(R.id.iv_comment_back);
        ivVideoCommentShare = commentCount.findViewById(R.id.iv_comment_share);
        ivVideoCommentCollection = commentCount.findViewById(R.id.iv_comment_collection);
        ivVideoCommentCount = commentCount.findViewById(R.id.iv_comment_count);
        tvVideoCommentCount = commentCount.findViewById(R.id.tv_comment_count);
        ivVideoCommentBarrage = commentCount.findViewById(R.id.iv_comment_barrage);

        etVideoComment.setOnClickListener(this);
        etInputComment.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        ivVideoCommentBack.setOnClickListener(this);
        ivVideoCommentShare.setOnClickListener(this);
        ivVideoCommentCollection.setOnClickListener(this);
        ivVideoCommentCount.setOnClickListener(this);
        ivVideoCommentBarrage.setOnClickListener(this);
        mTasksView.setOnClickListener(this);

        mSceneCommentShare = new Scene(mSceneRootView, commentCount);
        mSceneCommentSend = new Scene(mSceneRootView, commentSubmit);

        /**
         * 切换到开始场景状态
         */
        TransitionManager.go(mSceneCommentShare, new ChangeBounds());

        etVideoComment.addTextChangedListener(textWatcher);
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

//    private int sum(List<VideoRecordBean> list) {
//        int sum = 0;
//        for (VideoRecordBean recordBean : list) {
//            sum = sum + recordBean.getVScale();
//            Timber.d("==============RecordScale: " + recordBean.getVScale());
//        }
//        return sum;
//    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//
//    }

    /**
     * 清空EditText内容
     */
    public void clearEditText() {
        if (etVideoComment != null)
            etVideoComment.setText("");
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
            mPresenter.getBarrage(mCurrId, mType, "wuli", 50, mLastId);
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
//        if (flutteringLayout != null) {
//            flutteringLayout.removeAllViews();
//            flutteringLayout.clearAnimation();
//        }
    }

    /**
     * 清理所有广告相关
     */
    public void clearAd() {
        //广告清理
        if (expressADContainer != null && expressADContainer.getChildCount() > 0) {
            expressADContainer.removeAllViews();
        }

        if (interstitialAD != null) {
            interstitialAD.closePopupWindow();
            interstitialAD.destroy();
            interstitialAD = null;
        }

        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }

        if (baiDuBannerView != null) {
            baiDuBannerView.destroy();
            baiDuBannerView = null;
        }

        if (baiDuInterAd != null) {
            baiDuInterAd.destroy();
            baiDuInterAd = null;
        }
        isHasCsjAd = false;
        if (mTTBannerAd != null) {
            mTTBannerAd = null;
        }
        if (mAdSlot != null) {
            mAdSlot = null;
        }
        if (mTTAdNative != null) {
            mTTAdNative = null;
        }
    }
}
