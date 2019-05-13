package com.dzkandian.common.player.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dzkandian.R;
import com.dzkandian.common.player.play.DataInter;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
//import Timber.log.Timber;

/**
 * 播放完成覆盖层（重新播放/下一个）
 * Created by LiuLi on 2018/8/18.
 */
public class CompleteCover extends BaseCover {

    @BindView(R.id.cover_player_complete_tv_replay)
    TextView mTvReplay;//重新播放
    @BindView(R.id.cover_player_complete_tv_next)
    TextView mTvNext;//下一个

    private Unbinder unbinder;

    private boolean isCompleteShow;

    public CompleteCover(Context context) {
        super(context);
    }

    @Override
    protected View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.player_complete_cover, null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());

        //注册是否有下一个视频事件监听
        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        //取消注册是否有下一个视频事件监听
        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
        unbinder.unbind();
    }

    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
                @Override
                public String[] filterKeys() {
                    return new String[]{
                            DataInter.Key.KEY_IS_HAS_NEXT,
                            DataInter.Key.KEY_COMPLETE_AUTO_REPLAY};
                }

                @Override
                public void onValueUpdate(String key, Object value) {
                    if (key.equals(DataInter.Key.KEY_IS_HAS_NEXT)) {
                        setNextState((Boolean) value);
                    }
                }
            };

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        if (getGroupValue().getBoolean(DataInter.Key.KEY_COMPLETE_SHOW) && !isCompleteShow) {
            //Timber.d("=========Player -Complete onCoverAttachedToWindow:  show");
            setPlayCompleteState(true);
        }
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        setCoverVisibility(View.GONE);
    }

    /**
     * 设置“下一个”按钮是否显示
     *
     * @param state 是否显示
     */
    private void setNextState(boolean state) {
        mTvNext.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置播放完成覆盖层是否显示
     *
     * @param state 是否完成
     */
    private void setPlayCompleteState(boolean state) {
        isCompleteShow = state;
        setCoverVisibility(state ? View.VISIBLE : View.GONE);
        getGroupValue().putBoolean(DataInter.Key.KEY_COMPLETE_SHOW, state);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                if (isCompleteShow) {
                    setPlayCompleteState(false);
                    //Timber.d("=========Player -Complete onPlayerEvent:  hide");
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                if (!isCompleteShow) {
                    setPlayCompleteState(true);
                    //Timber.d("=========Player -Complete onPlayerEvent:  show");
                }
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    /**
     * 获取覆盖层层级（优先级）
     *
     * @return 覆盖层层级
     */
    @Override
    public int getCoverLevel() {
        return levelMedium(20);
    }

    @OnClick({R.id.cover_player_complete_tv_replay, R.id.cover_player_complete_tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cover_player_complete_tv_replay://重新播放视频
                requestReplay(null);
                break;
            case R.id.cover_player_complete_tv_next://播放下一条视频
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_NEXT, null);
                break;
        }
        setPlayCompleteState(false);
    }

}
