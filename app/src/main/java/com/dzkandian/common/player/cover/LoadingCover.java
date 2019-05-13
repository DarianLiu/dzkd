package com.dzkandian.common.player.cover;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.dzkandian.R;
import com.dzkandian.app.MyApplication;
import com.dzkandian.common.player.play.DataInter;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 加载中覆盖层
 * Created by Taurus on 2018/8/18.
 */

public class LoadingCover extends BaseCover {

    private final int MSG_CODE_DELAY_LOADING_TIMEOUT = 101;

    @BindView(R.id.cover_player_loading_image)
    ImageView mLoadingImage;

    private ObjectAnimator mRotateAnimator;
    private Unbinder unbinder;

    private Bundle mBundle;
    private boolean isLoadingShow;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CODE_DELAY_LOADING_TIMEOUT:
                    //Timber.d("=========Player -LOADING_TIMEOUT  msg_delay_hidden...");
                    if (isLoadingShow){
                        setControllerState(false, true);
                        //Timber.d("==========Player -LOADING_TIMEOUT: hideLoading");
                    }
                    break;
            }
        }
    };

    public LoadingCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());

        mBundle = new Bundle();
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();

        cancelRotateAnimation();

        removeDelayHiddenMessage();
        mHandler.removeCallbacksAndMessages(null);

        unbinder.unbind();
    }

    private void sendDelayHiddenMessage() {
        removeDelayHiddenMessage();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_LOADING_TIMEOUT, 10000);
    }

    private void removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_LOADING_TIMEOUT);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null && isInPlaybackState(playerStateGetter)) {
            if (NetworkUtils.isMobile(NetworkUtils.getNetworkState(getContext().getApplicationContext()))
                    && !MyApplication.ignoreMobile && isLoadingShow) {
                setLoadingState(false, false);
            } else {
                if (playerStateGetter.isBuffering()) {
                    if (!isLoadingShow) {
                        setControllerState(true, false);
                    }
                } else {
                    if (isLoadingShow)
                        setControllerState(false, false);
                }

            }

        }
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();

        removeDelayHiddenMessage();
    }

    private boolean isInPlaybackState(PlayerStateGetter playerStateGetter) {
        int state = playerStateGetter.getState();
        return state != IPlayer.STATE_END
                && state != IPlayer.STATE_ERROR
                && state != IPlayer.STATE_IDLE
                && state != IPlayer.STATE_INITIALIZED
                && state != IPlayer.STATE_STOPPED;
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO:
                if (!isLoadingShow) {
                    setControllerState(true, false);
                    //Timber.d("==========Player -OnPlayerEventListener ON_SEEK_TO: showLoading");
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                if ((NetworkUtils.isMobile(NetworkUtils.getNetworkState(getContext())) && !MyApplication.ignoreMobile) ||
                        !NetworkUtils.isNetConnected(getContext())) {
                    if (isLoadingShow) {
                        setControllerState(false, false);
                        //Timber.d("==========Player -OnPlayerEventListener DATA_SOURCE_SET: hideLoading");
                    }
                } else {
                    if (!isLoadingShow) {
                        setControllerState(true, false);
                        //Timber.d("==========Player -OnPlayerEventListener DATA_SOURCE_SET: showLoading");
                    }
                }
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
            case OnPlayerEventListener.PLAYER_EVENT_ON_STOP:
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                if (isLoadingShow) {
                    setControllerState(false, false);
                    //Timber.d("==========Player -OnPlayerEventListener ON_SEEK_COMPLETE: hideLoading");
                }
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        if (isLoadingShow){
            setControllerState(false, false);
            //Timber.d("==========Player -onErrorEvent: hideLoading");
        }
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    private void setControllerState(boolean state, boolean loadTimeout) {
        if (state) {
            sendDelayHiddenMessage();
        } else {
            removeDelayHiddenMessage();
        }
        setLoadingState(state, loadTimeout);
    }


    /**
     * 设置loading cover显示状态
     *
     * @param show 显示状态
     */
    private void setLoadingState(boolean show, boolean loadTimeout) {
        isLoadingShow = show;
        cancelRotateAnimation();
        if (show) {
            if (mRotateAnimator == null) {
                mRotateAnimator = ObjectAnimator.ofFloat(mLoadingImage, "rotation", 0f, 360f);
                mRotateAnimator.setInterpolator(new LinearInterpolator());
                mRotateAnimator.setDuration(500);
                mRotateAnimator.setRepeatCount(-1);
            }
            mRotateAnimator.start();
            if (mBundle == null) {
                mBundle = new Bundle();
            }
            mBundle.putBoolean(EventKey.BOOL_DATA, true);
        } else {
            mBundle.putBoolean(EventKey.BOOL_DATA, false);
            if (loadTimeout) {
                mBundle.putBoolean(DataInter.Key.KEY_LOAD_TIMEOUT, true);
                notifyReceiverPrivateEvent(DataInter.ReceiverKey.KEY_ERROR_COVER, DataInter.PrivateEvent.EVENT_CODE_LOADING_TIMEOUT, mBundle);
                requestStop(mBundle);
            }
        }
        notifyReceiverPrivateEvent(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, DataInter.PrivateEvent.EVENT_CODE_LOADING_COVER_SHOW, mBundle);
        setCoverVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 取消旋转动画
     */
    private void cancelRotateAnimation() {
        if (mLoadingImage != null) {
            mLoadingImage.clearAnimation();
        }

        if (mRotateAnimator != null) {
            mRotateAnimator.cancel();
            mRotateAnimator.removeAllListeners();
            mRotateAnimator.removeAllUpdateListeners();
        }
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.player_loading_cover, null);
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(1);
    }
}
