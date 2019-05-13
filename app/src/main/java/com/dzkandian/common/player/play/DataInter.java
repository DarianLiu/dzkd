package com.dzkandian.common.player.play;

import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.InterKey;

public interface DataInter {

    interface Event extends InterEvent {

        int EVENT_CODE_REQUEST_BACK = -100;//返回事件
        int EVENT_CODE_REQUEST_CLOSE = -101;//关闭事件
        int EVENT_CODE_REQUEST_SHARE = -102;//分享事件

        int EVENT_CODE_REQUEST_TOGGLE_SCREEN = -104;//全屏开关事件
        int EVENT_CODE_REQUEST_NEXT = -106;//播放下一个事件

        int EVENT_CODE_ERROR_SHOW = -111;//显示异常事件
        int EVENT_CODE_REQUEST_CONTINUE = -103;//从暂停状态恢复到播放


    }

    interface Key extends InterKey {

        String KEY_IS_LANDSCAPE = "isLandscape";

        String KEY_DATA_SOURCE = "data_source";//视频资源

        String KEY_ERROR_SHOW = "error_show";//错误覆盖层显示

        String KEY_IS_HAS_NEXT = "is_has_next";//是否还有下一个
        String KEY_COMPLETE_SHOW = "complete_show";//播放完成覆盖层显示

        String KEY_CONTROLLER_TOP_ENABLE = "controller_top_enable";//顶部覆盖层(返回/分享)
        String KEY_CONTROLLER_BOTTOM_ENABLE = "controller_bottom_enable";//底部覆盖层(拖动进度条)

        String KEY_CONTROLLER_SCREEN_SWITCH_ENABLE = "screen_switch_enable";//全屏

        String KEY_TIMER_UPDATE_ENABLE = "timer_update_enable";//播放计时器更新

        String KEY_NETWORK_RESOURCE = "network_resource";//数据源（本地/网络数据）

        String KEY_COMPLETE_AUTO_REPLAY = "complete_auto_replay";//播放完成后自动播放

        String KEY_LOAD_TIMEOUT = "load_timeout";//播放完成后自动播放

        String KEY_CONTROLLER_COVER_TOP_SHOW = "controller_top_show";

    }

    /**
     * 接收者（覆盖层）Key
     */
    interface ReceiverKey {
        String KEY_LOADING_COVER = "loading_cover";//加载中覆盖层
        String KEY_CONTROLLER_COVER = "controller_cover";//加载中覆盖层
        String KEY_GESTURE_COVER = "gesture_cover";//手势覆盖层
        String KEY_COMPLETE_COVER = "complete_cover";//播放完成覆盖层
        String KEY_ERROR_COVER = "error_cover";//播放异常覆盖层
        String KEY_CLOSE_COVER = "close_cover";//关闭视频覆盖层


    }

    /**
     * 私有事件Key
     */
    interface PrivateEvent {
        int EVENT_CODE_UPDATE_SEEK = -201;

        int EVENT_CODE_LOADING_COVER_SHOW = -202;

        int EVENT_CODE_ERROR_COVER_SHOW = -204;

        int EVENT_CODE_LOADING_TIMEOUT = -203;

//        String EVENT_CODE_HIDE_ANIMATION = "hide_animation";
//
//        String EVENT_CODE_SHOW_ANIMATION = "show_animation";
//
//        String EVENT_CODE_SHOW_TIME_ANIMATION = "show_time_animation";
    }

}
