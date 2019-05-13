package com.dzkandian.common.player.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.common.player.play.DataInter;
import com.kk.taurus.playerbase.config.PConst;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.utils.NetworkUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 视频播放器异常覆盖层
 * Created by LiuLi on 2018/8/18.
 */

public class ErrorCover extends BaseCover {

    @BindView(R.id.cover_player_error_tv_error)
    TextView mTvError;
    @BindView(R.id.cover_player_error_tv_replay)
    TextView mTvReplay;

    @BindString(R.string.player_error_load_failed)
    String player_error_load_failed;//视频加载异常
    @BindString(R.string.player_error_network)
    String player_error_network;//网络错误
    @BindString(R.string.player_error_network_mobile)
    String player_error_network_mobile;//移动网络

    @BindString(R.string.player_resume_play)
    String player_resume_play;//继续播放
    @BindString(R.string.player_retry)
    String player_retry;//重试

    private Unbinder unbinder;

    private boolean mErrorShow;//错误覆盖层是否显示
    //    private boolean ignoreMobile = false;//忽略移动网络
    private int mCurrPosition;//当前位置

    //相关异常
    private final int STATUS_ERROR = -1;//视频源异常
    private final int STATUS_UNDEFINE = 0;//未定义异常
    private final int STATUS_MOBILE = 1;//移动网络
    private final int STATUS_NETWORK_ERROR = 2;//网络异常
    private int mStatus = STATUS_UNDEFINE;

    public ErrorCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        //Timber.d("=========Player -Error SHOW onCoverAttachedToWindow" + mErrorShow);
        if (mErrorShow) {
            return;
        }
        handleStatusUI(NetworkUtils.getNetworkState(getContext()));
    }

    /**
     * 处理网络状态异常UI
     *
     * @param networkState 网络状态
     */
    private void handleStatusUI(int networkState) {
        if (!getGroupValue().getBoolean(DataInter.Key.KEY_NETWORK_RESOURCE))
            return;
//        Timber.d("ErrorCover=============handleStatusUI  networkState: " + networkState);
        Bundle bundle = BundlePool.obtain();
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();

        if (playerStateGetter != null && playerStateGetter.getState() == IPlayer.STATE_PLAYBACK_COMPLETE) {
//            setErrorState(false);
            return;
        }
        if (networkState < 0) {
            requestStop(bundle);
            mStatus = STATUS_NETWORK_ERROR;
            setErrorInfo(player_error_network);
            setHandleInfo(player_retry);
            setErrorState(true);
        } else {
            //Timber.d("=========Player -Error SHOW  networkState: " + networkState + mErrorShow);
            if (networkState != PConst.NETWORK_STATE_WIFI) {
                if (MyApplication.ignoreMobile) {
                    setErrorState(false);
                    return;
                }
                requestPause(bundle);
                mStatus = STATUS_MOBILE;
                setErrorInfo(player_error_network_mobile);
                setHandleInfo(player_resume_play);
                setErrorState(true);
            } else {
                if (NetworkUtils.isWifiConnected(getContext())) {
                    setErrorState(false);
                }
            }
        }
    }

    /**
     * 设置错误提示文字
     *
     * @param text 提示文案
     */
    private void setErrorInfo(String text) {
        mTvError.setText(text);
    }

    /**
     * 设置错误处理按钮提示文字
     *
     * @param text 处理按钮提示文字
     */
    private void setHandleInfo(String text) {
        mTvReplay.setText(text);
    }

    /**
     * 设置错误覆盖层是否显示
     *
     * @param state 错误覆盖层显示状态
     */
    private void setErrorState(boolean state) {
        mErrorShow = state;
        setCoverVisibility(state ? View.VISIBLE : View.GONE);
        //Timber.d("=========Player -Error setCoverVisibility: " + state + "  " + getView().getVisibility());

        Bundle bundle = BundlePool.obtain();
        //设置错误覆盖层是否显示

        if (!state) {
            mStatus = STATUS_UNDEFINE;
        } else {
            notifyReceiverEvent(DataInter.Event.EVENT_CODE_ERROR_SHOW, null);//更新错误覆盖层接收者事件
        }
        bundle.putBoolean(EventKey.BOOL_DATA, state);
        notifyReceiverPrivateEvent(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, DataInter.PrivateEvent.EVENT_CODE_ERROR_COVER_SHOW, bundle);
        //Timber.d("=========Player -Error SHOW: " + state);
        getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW, state);
    }

    /**
     * 解除接收器（错误覆盖层）绑定
     */
    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        unbinder.unbind();
    }

    /**
     * 创建（错误覆盖层）视图
     */
    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.player_error_cover, null);
    }

    /**
     * 播放事件
     */
    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET://视频数据源设置事件
                mCurrPosition = 0;
                //Timber.d("=========Player -Error SHOW onPlayerEvent");
                if (NetworkUtils.isWifiConnected(getContext().getApplicationContext()) && mErrorShow) {
                    setErrorState(false);
                } else if (NetworkUtils.isNetConnected(getContext().getApplicationContext()) &&
                        NetworkUtils.isMobile(NetworkUtils.getNetworkState(getContext().getApplicationContext()))
                        && MyApplication.ignoreMobile && mErrorShow) {
                    setErrorState(false);
                } else {
                    handleStatusUI(NetworkUtils.getNetworkState(getContext()));
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE://播放器计时器更新事件
                mCurrPosition = bundle.getInt(EventKey.INT_ARG1);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                handleStatusUI(NetworkUtils.getNetworkState(getContext()));
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_START:
                if (mErrorShow) {
                    requestPause(bundle);
                }
                break;
        }
    }

    /**
     * 视频解析错误事件
     */
    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        mStatus = STATUS_ERROR;
        if (!mErrorShow) {
            setErrorInfo(player_error_load_failed);
            setHandleInfo(player_retry);
            setErrorState(true);
            //Timber.d("=========Player -Error onErrorEvent" + eventCode);
        }
    }

    /**
     * 其他接收者（覆盖层）发送的事件
     */
    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    /**
     * 生产者发送的生产者数据(这里主要捕捉网络状态生产者发送的事件)
     *
     * @param key  生产者对应的Key
     * @param data 生产者数据
     */
    @Override
    public void onProducerData(String key, Object data) {
        super.onProducerData(key, data);
        if (DataInter.Key.KEY_NETWORK_STATE.equals(key)) {
            int networkState = (int) data;
//            Timber.d("ErrorCover=============onProducerData: " + networkState);
            if ((networkState == PConst.NETWORK_STATE_WIFI || (NetworkUtils.isMobile(networkState) && MyApplication.ignoreMobile))
                    && mErrorShow) {
                return;
            }
            handleStatusUI(networkState);
        }
    }

    @OnClick({R.id.cover_player_error_tv_error, R.id.cover_player_error_tv_replay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cover_player_error_tv_replay:
                handleStatus();
                break;
        }
    }

    /**
     * 错误处理(重试/继续播放)
     */
    private void handleStatus() {
        Bundle bundle = BundlePool.obtain();
        bundle.putInt(EventKey.INT_DATA, mCurrPosition);
        switch (mStatus) {
            case STATUS_ERROR:
                if (NetworkUtils.isNetConnected(getContext())) {
                    setErrorState(false);
                    requestRetry(bundle);//发送重新播放事件
                }
                break;
            case STATUS_MOBILE:
                if (NetworkUtils.isNetConnected(getContext())) {
                    MyApplication.ignoreMobile = true;
                    setErrorState(false);
                    requestRetry(bundle);//发送继续播放事件
                }
                break;
            case STATUS_NETWORK_ERROR:
                if (NetworkUtils.isNetConnected(getContext())) {
                    setErrorState(false);
                    requestRetry(bundle);//发送重新播放事件
                }
                break;
            default:
                if (NetworkUtils.isNetConnected(getContext())) {
                    setErrorState(false);
                    requestRetry(bundle);//发送重新播放事件
                }
                break;
        }
    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {

        switch (eventCode) {
            case DataInter.PrivateEvent.EVENT_CODE_LOADING_TIMEOUT:
                if (bundle != null) {
                    boolean loadTimeout = bundle.getBoolean(DataInter.Key.KEY_LOAD_TIMEOUT);
                    //Timber.d("=========Player -Error LOADING_TIMEOUT" + loadTimeout);
                    setErrorState(loadTimeout);
                    if (loadTimeout) {
                        requestPause(bundle);
                    }
                }

                break;
        }
        return super.onPrivateEvent(eventCode, bundle);
    }

    /**
     * 获取覆盖层显示层级（最高层级）
     *
     * @return 显示层级
     */
    @Override
    public int getCoverLevel() {
        return levelHigh(1);
    }
}
