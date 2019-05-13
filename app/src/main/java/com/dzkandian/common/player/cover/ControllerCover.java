package com.dzkandian.common.player.cover;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.player.play.DataInter;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.player.OnTimerUpdateListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.touch.OnTouchGestureListener;
import com.kk.taurus.playerbase.utils.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 控制器覆盖层
 * Created by LiuLi on 2018/8/18.
 */

public class ControllerCover extends BaseCover implements OnTimerUpdateListener, OnTouchGestureListener {

    private final int MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101;
    private final int MSG_CODE_DELAY_HIDDEN_TOP = 102;

    @BindView(R.id.cover_player_controller_top_container)
    RelativeLayout mTopContainer;
    @BindView(R.id.cover_player_controller_iv_back)
    ImageView mIvBack;
    @BindView(R.id.cover_player_controller_iv_share)
    ImageView mIvShare;

    @BindView(R.id.cover_player_controller_bottom_container)
    View mBottomContainer;
    @BindView(R.id.cover_player_controller_iv_play_state)
    ImageView mStateIcon;
    @BindView(R.id.cover_player_controller_tv_curr_time)
    TextView mCurrTime;
    @BindView(R.id.cover_player_controller_tv_total_time)
    TextView mTotalTime;
    @BindView(R.id.cover_player_controller_iv_switch_screen)
    ImageView mSwitchScreen;
    @BindView(R.id.cover_player_controller_seek_bar)
    SeekBar mSeekBar;

    @BindView(R.id.cover_player_controller_progress_bar)
    ProgressBar mBottomProgressBar;

    private int mBufferPercentage;

    private int mSeekProgress = -1;

    private boolean mTimerUpdateProgressEnable = true;

    private boolean isShow;

    private boolean isErrorCoverShow;//异常覆盖层是否显示

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CODE_DELAY_HIDDEN_CONTROLLER:
//                    Timber.d("=========Player -" + getTag().toString() + " msg_delay_hidden...");
                    setControllerState(false);
                    break;
                case MSG_CODE_DELAY_HIDDEN_TOP:
                    setTopContainerState(false);
                    break;
            }
        }
    };

    private boolean mGestureEnable = true;

    private String mTimeFormat;

    private boolean mControllerTopEnable;
    private boolean mControllerBottomEnable;
    private Unbinder unbinder;
    private ObjectAnimator mBottomAnimator;
    private ObjectAnimator mIvPlayStateAnimator;
    private ObjectAnimator mTopAnimator;

    public ControllerCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());

        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);

    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
//        Timber.d("===========ControllerCover onCoverAttachedToWindow " + isErrorCoverShow);

        boolean topEnable = getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
        mControllerTopEnable = topEnable;
        if (!topEnable) {
            mTopContainer.clearAnimation();
            cancelTopAnimation();
            mTopContainer.setVisibility(View.GONE);
        }

        boolean bottomEnable = getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_BOTTOM_ENABLE, true);
        mControllerBottomEnable = bottomEnable;
        if (!bottomEnable) {
            mBottomContainer.clearAnimation();
            cancelBottomAnimation();
            mBottomContainer.setVisibility(View.GONE);
        }

        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null && playerStateGetter.getState() == IPlayer.STATE_PAUSED) {
//            Timber.d("===========ControllerCover onCoverAttachedToWindow " + playerStateGetter.getState());
            removeDelayHiddenMessage();
            mStateIcon.clearAnimation();
            cancelPlayStateAnimation();
            mStateIcon.setAlpha(1f);
            mStateIcon.setVisibility(View.VISIBLE);

        }

        boolean screenSwitchEnable = getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_SCREEN_SWITCH_ENABLE, true);
        setScreenSwitchEnable(screenSwitchEnable);

        if (isErrorCoverShow && mControllerTopEnable) {
            mTopContainer.setAlpha(1f);
            mTopContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        mTopContainer.setVisibility(View.GONE);
        mStateIcon.setVisibility(View.GONE);
        mBottomContainer.setVisibility(View.GONE);

        removeDelayHiddenMessage();
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();

        cancelTopAnimation();
        cancelBottomAnimation();
        cancelPlayStateAnimation();

        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
        removeDelayHiddenMessage();
        mHandler.removeCallbacks(mSeekEventRunnable);

        unbinder.unbind();

    }

    @OnClick({R.id.cover_player_controller_iv_back,
            R.id.cover_player_controller_iv_share,
            R.id.cover_player_controller_iv_play_state,
            R.id.cover_player_controller_iv_switch_screen})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.cover_player_controller_iv_back:
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_BACK, null);
                break;
            case R.id.cover_player_controller_iv_share:
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_SHARE, null);
                break;
            case R.id.cover_player_controller_iv_play_state:
                boolean selected = mStateIcon.isSelected();
                if (selected) {
                    notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_CONTINUE, null);
                    setControllerState(false);
                    requestResume(null);
                } else {
                    removeDelayHiddenMessage();
                    if (mControllerTopEnable) {
                        cancelTopAnimation();
                        mTopContainer.setVisibility(View.VISIBLE);
                    }
                    if (mControllerBottomEnable) {
                        mBottomContainer.clearAnimation();
                        cancelBottomAnimation();
                        mBottomContainer.setVisibility(View.VISIBLE);
                    } else {
                        mBottomContainer.clearAnimation();
                        cancelBottomAnimation();
                        mBottomContainer.setVisibility(View.GONE);
                    }
                    cancelPlayStateAnimation();
                    mStateIcon.clearAnimation();
                    mStateIcon.setVisibility(View.VISIBLE);

                    requestPause(null);
                }
                mStateIcon.setSelected(!selected);
                break;
            case R.id.cover_player_controller_iv_switch_screen:
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN, null);
                break;
        }
    }

    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
                @Override
                public String[] filterKeys() {
                    return new String[]{
                            DataInter.Key.KEY_COMPLETE_SHOW,
                            DataInter.Key.KEY_TIMER_UPDATE_ENABLE,
                            DataInter.Key.KEY_DATA_SOURCE,
                            DataInter.Key.KEY_IS_LANDSCAPE,
                            DataInter.Key.KEY_CONTROLLER_TOP_ENABLE,
                            DataInter.Key.KEY_CONTROLLER_BOTTOM_ENABLE,
                            DataInter.Key.KEY_CONTROLLER_COVER_TOP_SHOW};
                }

                @Override
                public void onValueUpdate(String key, Object value) {
                    if (key.equals(DataInter.Key.KEY_COMPLETE_SHOW)) {
                        boolean show = (boolean) value;
//                        Timber.d("=========Player -Controller COMPLETE_SHOW: " + show);
                        if (show) {
                            removeDelayHiddenMessage();
                            removeDelayHiddenTop();

                            if (mControllerTopEnable) {
                                cancelTopAnimation();
                                mTopContainer.setAlpha(1);
                                mTopContainer.setVisibility(View.VISIBLE);
                            }
//                            Timber.d("=========Player -Controller COMPLETE_SHOW TopContainer " + mTopContainer.getVisibility());

                            cancelBottomAnimation();
                            mBottomContainer.setVisibility(View.GONE);

                            cancelPlayStateAnimation();
                            mStateIcon.setVisibility(View.GONE);
                        }
//                        else {
//                            Timber.d("=========Player -Controller COMPLETE_SHOW");
//                            removeDelayHiddenMessage();
//                            setTopContainerState(false);
//
//                            cancelBottomAnimation();
//                            mBottomContainer.setVisibility(View.GONE);
//
//                            cancelPlayStateAnimation();
//                            mStateIcon.setVisibility(View.GONE);
//                        }

                        setGestureEnable(!show);
                    } else if (key.equals(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE)) {
                        mControllerTopEnable = (boolean) value;
                        if (!mControllerTopEnable) {
                            setTopContainerState(false);
                        }
                    } else if (key.equals(DataInter.Key.KEY_CONTROLLER_BOTTOM_ENABLE)) {
                        mControllerBottomEnable = (boolean) value;
                        if (!mControllerBottomEnable) {
                            mBottomContainer.clearAnimation();
                            cancelBottomAnimation();
                            mBottomContainer.setVisibility(View.GONE);
                        }
                    } else if (key.equals(DataInter.Key.KEY_IS_LANDSCAPE)) {
                        setSwitchScreenIcon((Boolean) value);
                    } else if (key.equals(DataInter.Key.KEY_TIMER_UPDATE_ENABLE)) {
                        mTimerUpdateProgressEnable = (boolean) value;
                    } else if (key.equals(DataInter.Key.KEY_DATA_SOURCE)) {
                        DataSource dataSource = (DataSource) value;
                        if (!TextUtils.isEmpty(dataSource.getTitle())) {
                            if (TextUtils.equals("1", dataSource.getTitle())) {
                                mIvShare.setVisibility(View.VISIBLE);
                            } else if (TextUtils.equals("0", dataSource.getTitle())) {
                                mIvShare.setVisibility(View.GONE);
                            }
                        } else { //如果为空则是没有登陆
                            mIvShare.setVisibility(View.GONE);
                        }
//                        setTitle(dataSource);
                    } else if (key.equals(DataInter.Key.KEY_CONTROLLER_COVER_TOP_SHOW)) {
                        if (mTopContainer.getVisibility() != View.VISIBLE)
                            toggleTopState(true);
                    }
                }
            };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        updateUI(progress, seekBar.getMax());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    sendSeekEvent(seekBar.getProgress());
                }
            };

    private void sendSeekEvent(int progress) {
        mTimerUpdateProgressEnable = false;
        mSeekProgress = progress;
        mHandler.removeCallbacks(mSeekEventRunnable);
        mHandler.postDelayed(mSeekEventRunnable, 300);
    }

    private Runnable mSeekEventRunnable = () -> {
        if (mSeekProgress < 0)
            return;
        Bundle bundle = BundlePool.obtain();
        bundle.putInt(EventKey.INT_DATA, mSeekProgress);
        requestSeek(bundle);
    };

    private void setSwitchScreenIcon(boolean isFullScreen) {
        mSwitchScreen.setImageResource(isFullScreen ? R.drawable.player_control_full_screen_exit
                : R.drawable.player_control_full_screen);
    }

    private void setScreenSwitchEnable(boolean screenSwitchEnable) {
        mSwitchScreen.setVisibility(screenSwitchEnable ? View.VISIBLE : View.GONE);
    }

    private void setGestureEnable(boolean gestureEnable) {
        this.mGestureEnable = gestureEnable;
    }

    /**
     * 取消顶部视图消失动画（返回/分享）
     */
    private void cancelTopAnimation() {
        if (mTopAnimator != null) {
            mTopAnimator.cancel();
            mTopAnimator.removeAllListeners();
            mTopAnimator.removeAllUpdateListeners();
        }
    }

    /**
     * 取消底部进度条消失动画
     */
    private void cancelBottomAnimation() {
        if (mBottomAnimator != null) {
            mBottomAnimator.cancel();
            mBottomAnimator.removeAllListeners();
            mBottomAnimator.removeAllUpdateListeners();
        }

    }

    /**
     * 取消播放按钮消失动画
     */
    private void cancelPlayStateAnimation() {
        if (mIvPlayStateAnimator != null) {
            mIvPlayStateAnimator.cancel();
            mIvPlayStateAnimator.removeAllListeners();
            mIvPlayStateAnimator.removeAllUpdateListeners();
        }
    }

    /**
     * 设置顶部视图显示状态
     *
     * @param state 显示状态
     */
    private void setTopContainerState(final boolean state) {
        if (mControllerTopEnable) {
            mTopContainer.clearAnimation();
            cancelTopAnimation();
            mTopAnimator = ObjectAnimator.ofFloat(mTopContainer,
                    "alpha", state ? 0 : 1, state ? 1 : 0).setDuration(300);
            mTopAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (state) {
                        mTopContainer.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!state) {
                        mTopContainer.setVisibility(View.GONE);
                    }
                }
            });
            mTopAnimator.start();
        } else {
            mTopContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 设置播放按钮显示状态
     *
     * @param state 显示状态
     */
    private void setPlaySateIconState(boolean state) {
        mStateIcon.clearAnimation();
        cancelPlayStateAnimation();
        mIvPlayStateAnimator = ObjectAnimator.ofFloat(mStateIcon,
                "alpha", state ? 0 : 1, state ? 1 : 0).setDuration(300);
        mIvPlayStateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (state) {
                    mStateIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!state) {
                    mStateIcon.setVisibility(View.GONE);
                }
            }
        });
        mIvPlayStateAnimator.start();
    }

    /**
     * 设置底部进度条显示状态
     *
     * @param state 显示状态
     */
    private void setBottomContainerState(final boolean state) {
        //Timber.d("=========Player -ControllerState" + state + " " + mControllerBottomEnable);
        if (mControllerBottomEnable) {
            mBottomContainer.clearAnimation();
            cancelBottomAnimation();
            mBottomAnimator = ObjectAnimator.ofFloat(mBottomContainer,
                    "alpha", state ? 0 : 1, state ? 1 : 0).setDuration(300);
            mBottomAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (state) {
                        mBottomContainer.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!state) {
                        mBottomContainer.setVisibility(View.GONE);
                    }
                }
            });
            mBottomAnimator.start();
        } else {
            mBottomContainer.setVisibility(View.GONE);
        }
    }


    private void setControllerState(boolean state) {
//        //Timber.d("=========Player -ControllerState" + state);
        removeDelayHiddenTop();
        if (state) {
            sendDelayHiddenMessage();
        } else {
            removeDelayHiddenMessage();
        }
        setTopContainerState(state);
        setPlaySateIconState(state);
        setBottomContainerState(state);
    }

    private void toggleTopState(boolean state) {
//        Timber.d("=========Player -Controller -toggleTopState" + state);
        removeDelayHiddenMessage();
        mStateIcon.setVisibility(View.GONE);
        mBottomContainer.setVisibility(View.GONE);
        if (state) {
            sendDelayHiddenTop();
        } else {
            removeDelayHiddenTop();
        }
        setTopContainerState(state);
    }

    private void sendDelayHiddenTop() {
        removeDelayHiddenTop();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_TOP, 3000);
    }

    private void removeDelayHiddenTop() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_TOP);
    }

    private boolean isControllerShow() {
        return mStateIcon.getVisibility() == View.VISIBLE;
    }

    private void toggleController() {
        if (isControllerShow()) {
            setControllerState(false);
//            Timber.d("=========Player -Controller toggle State ： false");
        } else {
//            Timber.d("=========Player -Controller toggle State ： true");
            setControllerState(true);
        }
    }

    private void sendDelayHiddenMessage() {
        removeDelayHiddenMessage();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 3000);
    }

    private void removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER);
    }

    private void setCurrTime(int curr) {
        mCurrTime.setText(TimeUtil.getTime(mTimeFormat, curr));
    }

    private void setTotalTime(int duration) {
        mTotalTime.setText(TimeUtil.getTime(mTimeFormat, duration));
    }

    private void setSeekProgress(int curr, int duration) {
        mSeekBar.setMax(duration);
        mSeekBar.setProgress(curr);
        float secondProgress = mBufferPercentage * 1.0f / 100 * duration;
        setSecondProgress((int) secondProgress);
    }

    private void setSecondProgress(int secondProgress) {
        mSeekBar.setSecondaryProgress(secondProgress);
    }

    @Override
    public void onTimerUpdate(int curr, int duration, int bufferPercentage) {
        if (!mTimerUpdateProgressEnable)
            return;
        if (mTimeFormat == null) {
            mTimeFormat = TimeUtil.getFormat(duration);
        }
        mBufferPercentage = bufferPercentage;
//        Timber.d("=========Player -ControllerCover  onTimerUpdate..." + bufferPercentage + " curr: " + curr);
        updateUI(curr, duration);
    }

    private void updateUI(int curr, int duration) {
        updateBottomProgress(curr, duration);

        setSeekProgress(curr, duration);
        setCurrTime(curr);
        setTotalTime(duration);
    }

    private void updateBottomProgress(int curr, int duration) {
        mBottomProgressBar.setMax(duration);
        mBottomProgressBar.setProgress(curr);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                mBufferPercentage = 0;
                mTimeFormat = null;
                updateUI(0, 0);
                DataSource data = (DataSource) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                getGroupValue().putObject(DataInter.Key.KEY_DATA_SOURCE, data);
//                setTitle(data);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE:
                int status = bundle.getInt(EventKey.INT_DATA);
                if (status == IPlayer.STATE_PAUSED) {
                    if (!isShow && !isControllerShow()) {
                        mStateIcon.setVisibility(View.VISIBLE);
                        mStateIcon.setAlpha(1f);
                        if (mControllerTopEnable) {
                            mTopContainer.setVisibility(View.VISIBLE);
                            mTopContainer.setAlpha(1f);
                        }

                        if (mControllerBottomEnable && mBottomContainer.getVisibility() == View.GONE) {
                            mBottomContainer.setVisibility(View.VISIBLE);
                            mBottomContainer.setAlpha(1f);
                        }
                    }
                    mStateIcon.setSelected(true);
                } else if (status == IPlayer.STATE_STARTED) {
                    mStateIcon.setSelected(false);
                    isShow = false;
                } else if (status == IPlayer.STATE_PLAYBACK_COMPLETE) {
                    isShow = true;
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
                int state = getPlayerStateGetter().getState();
                if (state == IPlayer.STATE_PAUSED && !isControllerShow()) {
                    removeDelayHiddenMessage();
                    cancelTopAnimation();
                    if (mControllerTopEnable)
                        mTopContainer.setVisibility(View.VISIBLE);

                    cancelBottomAnimation();
                    mBottomContainer.setVisibility(View.VISIBLE);

                    cancelPlayStateAnimation();
                    mStateIcon.setVisibility(View.VISIBLE);
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                mTimerUpdateProgressEnable = true;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                updateBottomProgress(100, 100);
//                setCoverVisibility(View.VISIBLE);
//                    removeDelayHiddenMessage();
//                    cancelTopAnimation();
//                    mTopContainer.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.PrivateEvent.EVENT_CODE_UPDATE_SEEK:
                if (bundle != null) {
                    int curr = bundle.getInt(EventKey.INT_ARG1);
                    int duration = bundle.getInt(EventKey.INT_ARG2);
                    updateUI(curr, duration);
                }
                break;
            case DataInter.PrivateEvent.EVENT_CODE_LOADING_COVER_SHOW:
                if (bundle != null) {
                    boolean loadingCoverShow = bundle.getBoolean(EventKey.BOOL_DATA);
//                    Timber.d("=========Player -Controller loadingCoverShow：" + loadingCoverShow);
                    if (loadingCoverShow) {
                        if (mControllerTopEnable) {
                            removeDelayHiddenTop();
                            cancelTopAnimation();
                            mTopContainer.setAlpha(1f);
                            mTopContainer.setVisibility(View.VISIBLE);
                        }

                        removeDelayHiddenMessage();
                        cancelBottomAnimation();
                        mBottomContainer.setVisibility(View.GONE);

                        cancelPlayStateAnimation();
                        mStateIcon.setVisibility(View.GONE);
                    } else {
                        if (isErrorCoverShow) {
                            break;
                        }
                        removeDelayHiddenMessage();
                        removeDelayHiddenTop();

                        cancelTopAnimation();
                        setTopContainerState(false);

                        cancelBottomAnimation();
                        mBottomContainer.setVisibility(View.GONE);

                        cancelPlayStateAnimation();
                        mStateIcon.setVisibility(View.GONE);
                    }
                }
                break;
            case DataInter.PrivateEvent.EVENT_CODE_ERROR_COVER_SHOW:
                if (bundle != null) {
                    boolean errorCoverShow = bundle.getBoolean(EventKey.BOOL_DATA);
//                    Timber.d("=========Player -Controller errorCoverShow: " + errorCoverShow);
                    if (errorCoverShow) {
                        isErrorCoverShow = true;
                        removeDelayHiddenMessage();
                        cancelBottomAnimation();
                        mBottomContainer.setVisibility(View.GONE);
                        cancelPlayStateAnimation();
                        mStateIcon.setVisibility(View.GONE);

                        if (mControllerTopEnable) {
                            removeDelayHiddenTop();
                            cancelTopAnimation();
                            mTopContainer.setAlpha(1f);
                            mTopContainer.setVisibility(View.VISIBLE);
                        }
                    } else {
                        isErrorCoverShow = false;
                    }
                }
                break;
        }
        return super.onPrivateEvent(eventCode, bundle);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.player_controller_cover, null);
    }

    @Override
    public int getCoverLevel() {
        return levelHigh(0);
    }

    @Override
    public void onSingleTapUp(MotionEvent event) {
        if (!mGestureEnable) {
            return;
        }
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null) {
//            Timber.d("=========Player -Controller onSingleTapUp: " + playerStateGetter.getState());
            int state = playerStateGetter.getState();
            if (isErrorCoverShow || state == IPlayer.STATE_IDLE || state == IPlayer.STATE_ERROR || state == IPlayer.STATE_STOPPED
                    || state == IPlayer.STATE_INITIALIZED || (state == IPlayer.STATE_PAUSED && isControllerShow())) {
                return;
            }
            toggleController();
        }

    }

    @Override
    public void onDoubleTap(MotionEvent event) {
    }

    @Override
    public void onDown(MotionEvent event) {
    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mGestureEnable)
            return;
    }

    @Override
    public void onEndGesture() {
    }
}
