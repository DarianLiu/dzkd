package com.dzkandian.app.config;

import org.simple.eventbus.EventBus;

/**
 * ================================================
 * 放置 {@link EventBus} 的 Tag ,便于检索
 * Created by LiuLi on 2018/4/9.
 * ================================================
 */
public interface EventBusTags {

    //悬乎窗显示/隐藏
    String TAG_BOTTOM_MENU = "bottom_menu_state";

    //微信登录
    String WeChat_Login = "WeChatLogin";

    //微信钱包授权
    String WeChat_Warrant = "WeChatWarrant";

    //微信绑定
    String WeChat_Bind = "WeChatBind";

    //登录状态
    String TAG_LOGIN_STATE = "login_state";

    //用户详细信息
    String TAG_UPDATE_USER_INFO = "user_info";

    //更改手机号
    String TAG_UPDATE_PHONE = "update_phone";

    //发起提现： 金币发生改变
    String TAG_COIN_STATE = "coin_state";

    //任务中心，时段奖励领取金币
    String TAG_COIN_REWARD = "coin_reward";

    //切换到看点界面
    String TAG_CHANGE_TAB = "change_tab";

    //栏目点击跳转管理
    String TAG_COLUMN_MANAGE = "column_manage";

    //后台推送新消息
    String TAG_PUSH_MESSAGE = "push_message";

    //活动公告已读
    String TAG_READ_ACTIVE = "active_read";

    //系统通知已读
    String TAG_READ_NOTIFICATION = "notification_read";

    //我的消息已读
    String TAG_READ_MESSAGE = "message_read";

    //悬乎窗显示/隐藏
    String TAG_SHOW_STATE = "show_state";

    //更新对话框
    String TAG_UPDATE_DIALOG = "update_dialog";

    //设置字体大小
    String TAG_TEXT_SIZE = "text_size";

    //删除某个资讯收藏
    String TAG_COLLECTION_NEWS = "collection_news";
    //刷新资讯列表；
    String TAG_COLLECTION_NEWS_REFRESH = "collection_news_refresh";
    //删除某个视频收藏
    String TAG_COLLECTION_VIDEO = "collection_video";
    //刷新视频列表；
    String TAG_COLLECTION_VIDEO_REFRESH = "collection_video_refresh";
    //删除某个小视频收藏
    String TAG_COLLECTION_SHORT = "collection_short";
    //播放某个小视频收藏
    String TAG_COLLECTION_SHORT_PLAY = "collection_short_play";
    //刷新小视频列表；
    String TAG_COLLECTION_SHORT_REFRESH = "collection_short_refresh";
    //点击回复列表的item position；
    String TAG_REPLY_CLICK_POSITION = "reply_click_position";
    //点击回复列表的item 的点赞按钮；
    String TAG_REPLY_CLICK_PRAISE = "reply_click_praise";

    //资讯评论@楼主
    String TAG_COMMENT_REPLY_SOMEONE = "comment_reply_someone";
    //点赞传值至activity
    String TAG_COMMENT_THUMBS_UP = "comment_thumbs_up";
    //二级评论传值到activity
    String TAG_COMMENT_DETAIL = "comment_datail";

    //新闻点赞弹幕评论接口
    String TAG_NEWS_COMMENT_THUBMS_UP = "news_comment_thubms_up";
    //新闻点赞动画效果
    String TAG_NEWS_ANIMATION_THUBMS_UP = "news_animation_thubms_up";


    //视频点赞弹幕评论接口
    String TAG_VIDEO_COMMENT_THUBMS_UP = "video_comment_thubms_up";
    //新闻点赞动画效果
    String TAG_VIDEO_ANIMATION_THUBMS_UP = "video_animation_thubms_up";

    //小视频点赞弹幕评论接口
    String TAG_SHORT_COMMENT_THUBMS_UP = "short_comment_thubms_up";
    //新闻点赞动画效果
    String TAG_SHORT_ANIMATION_THUBMS_UP = "short_animation_thubms_up";

    //点击搜索历史
    String TAG_CLICK_SEARCH_HISTORY = "click_search_history";

    /**
     * -------------时段奖励
     */
    String TAG_TIME_REWARD_TIME_START = "time_reward_time_start";//时段奖励开始计时

    String TAG_TIME_REWARD_TIME_SHOW = "time_reward_time_show";//时段奖励计时显示，计时结束显示奖励动画

    //小视频销毁通知小视频列表
    String TAG_SHORT_FINISH = "short_finish";

}
