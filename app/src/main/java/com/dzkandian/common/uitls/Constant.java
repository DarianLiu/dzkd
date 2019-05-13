package com.dzkandian.common.uitls;

import android.os.Environment;

import java.io.File;

import io.rx_cache2.internal.RxCache;

/**
 * App相关常量配置
 * Created by LiuLi on 2018/4/12.
 */

public class Constant {
    /**
     * 是否开启emulator,root,xposed检测
     */
    public static final boolean OPEN_TESTING = true;

    /**
     * App进程状态管理
     */
    public static final int APP_STATUS_FORCE_KILLED = -1;//进程被杀死状态
    public static final int APP_STATUS_RESTART = 1;//重启状态
    public static final int APP_STATUS_NORMAL = 2;//正常状态

    /**
     * ====================== 本地缓存相关常量 Start ============================
     * {@link RxCache 本地缓存 }
     */
    /* 缓存公共字段*/
    private static final String CACHE_COMMON = "dzkd-cache";

    /*- Provider的 KEY：考虑到代码混淆(方法名的改变会导致缓存文件命名的改变)和缓存数据迁移
        更新时如果缓存的数据结构发生变化，都需要处理上一个版本缓存的数据 -*/
    public static final String CACHE_PK_OLD = CACHE_COMMON + "-v1";//上一个版本的ProviderKEY
    public static final String CACHE_PK_NEW = CACHE_COMMON + "-v2";//最新版本的ProviderKEY

    /*- DynamicKey：对应缓存数据的Key值 -*/
    public static final String CACHE_DK_NEWS_TITLE = "-news-title";
    /*- DynamicKey：对应缓存数据的Key值 -*/
    public static final String CACHE_DK_VIDEO_TITLE = "-video-title";

    /**
     * 手机相关常量
     */
    public static final String PHONE_IMEI = "imei";

    public static final int SMS_MAX_TIME = 60;//发送验证码计时时间


    /**
     * 数据库名称
     */
    public static final String DB_NAME = "dzkd_db";


    /**
     * 腾讯广点通相关配置
     */
    public static final String GDT_APP_ID = "1106851121";                 //腾讯广点通APP ID
    public static final String GDT_AD_ID_SPLASH = "4040539405048870";     //启动页开屏广告ID
    public static final String GDT_AD_ID_BANNER = "6070238405551161";     //小视频播放页banner广告
    public static final String GDT_AD_ID_VIDEO_PLAY = "9070030405099890"; //视频播放页广告ID
    public static final String GDT_AD_ID_TASK_BANNER = "8010252460183318";//任务中心banner广告
    public static final String GDT_AD_ID_MINE_BOTTOM = "1000731447319743";//我的页面广告ID

    /**
     * 百度SSP媒体服务相关配置
     */
    /*news*/
    public static final String BAIDU_AD_ID_NEWS = "6052222";              //新闻列表页信息流广告
    /*shortvideo*/
    public static final String BAIDU_AD_ID_VIDEO_WATEFALL = "6052218";    //小视频列表页信息流广告
    public static final String BAIDU_AD_ID_BANNER = "6052212";            //小视频播放页横幅广告（20：3）
    public static final String BAIDU_AD_ID_SHORT_BIG = "6052216";         //小视频播放页大图广告
    public static final String BAIDU_AD_ID_SHORT_SMALL = "6052217";       //小视频播放页小图广告
    /*video*/
    public static final String BAIDU_AD_ID_VIDEO = "6052219";             //视频列表页信息流广告
    public static final String BAIDU_AD_ID_VIDEO_DETAIL = "6052221";      //视频详情页列表信息流广告
    public static final String BAIDU_AD_ID_VIDEO_DETAIL_TIMER = "6052220";//视频详情页播放完成计时广告
    /*task*/
    public static final String BAIDU_AD_ID_TASK_BANNER = "6052213";       //任务中心banner广告
    /*mine*/
    public static final String BAIDU_AD_ID_MINE_BOTTOM = "6052215";       //个人中心底部大图

    /**
     * 穿山甲联盟广告相关配置
     */
    public static final String CSJ_APP_ID = "5005316";//穿山甲联盟APP ID
    public static final String CSJ_AD_ID_SHORT_BANNER = "905316598";//小视频播放页横幅广告
    public static final String CSJ_AD_ID_VIDEO_DETAIL = "905316048";//视频播放页信息流广告

    /*DSP参数*/
    public static final String AD_SUPPORT_SDK_ALL= "BaiDu,CSJ,GDT";//目前的三种广告sdk
    public static final String AD_SUPPORT_SDK_BAIDU = "BaiDu";//百度的广告
    public static final String AD_SUPPORT_SDK_CSJ = "CSJ";//穿山甲的广告
    public static final String AD_SUPPORT_SDK_GDT = "GDT";//广点通的广告
    public static final String AD_SUPPORT_SDK_OWN = "AD_OWN";//自营的广告
    public static final String AD_MINE_BOTTOM_BIG_PIC = "mine_bottom_big_pic";//我的页底部大图广告
    public static final String AD_SMALL_VIDEO_DETAIL_BOTTOM_BANNER = "small_video_detail_bottom_banner";//小视频详情页Banner广告
    public static final String AD_NATURAL_VIDEO_DETAIL_BOTTOM_BANNER = "natural_video_detail_list_item";//横版视频详情页信息流左图右文广告
    public static final String AD_NEWS_LIST = "news_list";//资讯列表页信息流左图右文广告
    public static final String AD_SMALL_VIDEO_LIST_BIG_PIC = "small_video_list_big_pic";//小视频列表页信息流大图广告
    public static final String AD_JUMP_DOWNLOAD_APK = "JUMP_DOWNLOAD_APK";//广告的点击后的操作  下载SDK
    public static final String AD_JUMP_EXTERNAL_URL= "JUMP_EXTERNAL_URL";//广告的点击后的操作   跳转外部URL
    public static final String AD_JUMP_INTERNAL_URL= "JUMP_INTERNAL_URL";//广告的点击后的操作   跳转内部的URL


    /**
     * 开启APP自身服务器
     */
    public static final int SP_KEY_WEB_SOCKET = 18059;

    /**
     * 微信登录
     */
    public static final String APP_ID = "wx56bca4b9873b6694";//微信APP_ID
    public static final String WX_LOGIN = "wx_login_";//微信登录
    public static final String WX_WARRANT = "wx_warrant_";//微信授权
    public static final String WX_BIND = "wx_bind_";//微信绑定

    /**
     * SharedPreferences保存的相关Key值
     */
    public static final String SP_KEY_APP_STATUS = "app_status";//App运行状态

    public static final String SP_KEY_TOKEN = "token";

    public static final String SP_KEY_EXPIRE = "expire";//token有效期

    public static final String SP_KEY_MOBILE_INFO = "mobile";//手机信息（渠道、xposed等）

    public static final String SP_KEY_MOBILE_EMULATOR = "emulator";//是否模拟器

    public static final String SP_KEY_MOBILE_XPOSED = "xposed";//是否xposed

    public static final String SP_KEY_MOBILE_ROOT = "root";//是否root

    public static final String SP_KEY_NEWS_PROGRESS_SCALE = "progressScale";//资讯阅读奖励进度

    public static final String SP_KEY_VIDEO_PROGRESS_SCALE = "progressVideoScale";//视频阅读奖励进度

    public static final String SP_KEY_TIME_STAMP = "time_stamp";//倒计时需要的时间戳

    public static final String SP_KEY_TIME_LAST = "time_list";//时段奖励的请求领取时间

    public static final String SP_KEY_NEWS_COLUMN = "news_column";//新闻栏目集合

    public static final String SP_KEY_VIDEO_COLUMN = "video_column";//视频栏目集合

    public static final String SP_KEY_HAVE_TOUCH_HARDWARE = "haveTouchHardware";//是否有触摸硬件（触摸面积不为0）

    public static final String SP_KEY_TOUCH_AREA = "touchArea";//保存触摸面积次数到本地

    public static final String SP_KEY_SET_SOUND = "setSound";//是否开启金币音效；

    public static final String SP_KEY_SET_JPUSH = "setJpush";//是否开启极光推送；

    public static final String SP_KEY_SET_TEXTSIZE = "setTextSize";//字体大小设置；small;medium,big

    public static final String SP_KEY_INDEX_POP_CLOSE_ACTIVITY_HOUR = "indexPopCloseActivityHour";//上一次首页弹窗活动出现的时间，小时数;

    public static final String SP_KEY_HAS_APPRENTICE_CLOSE_DAY = "hasApprenticeCloseDay";//上一次"是否有收徒"出现的时间，天数;

    public static final String SP_KEY_FINISH_NOVICE_TASK_CLOSE_DAY = "finishNoviceTaskCloseDay";//上一次"是否完成新手任务"出现的时间，天数;

    public static final String SP_KEY_TODAY_SIGN_CLOSE_DAY = "todaySignCloseDay";//上一次"今日是否签到"出现的时间，天数;

    public static final String SP_KEY_ACTION = "android.hardware.usb.action.USB_STATE";//广播

    public static final String SP_KEY_UPDATE_APP = "updateApp";//有没有新的版本；


    /**
     * 新版2.2.1 SP KEY
     */
    public static final String SP_KEY_USER_ID = "user_id";//有没有用户ID ；


    /**
     * SharedPreferences保存的相关value值
     */
    public static final int VALUE_EMULATOR = 1;//模拟器

    public static final int VALUE_XPOSED = 2;//xposed

    public static final int VALUE_ROOT = 3;//root

    public static final String DEVICE_INFO_ID = "859";//配置信息数据库ID查询


    /**
     * 任务列表任务事件
     */
    public static final String TASK_EVENT_BIND_WECHAT = "bindWeChat";//绑定微信

    public static final String TASK_EVENT_BIND_ALIPAY = "bindAlipay";//绑定支付宝

    public static final String TASK_EVENT_BIND_PHONE = "bindPhone";//绑定手机

    public static final String TASK_EVENT_FULL_INFO = "fullInfo";//完善资料

    public static final String TASK_EVENT_READ_NEWS = "readNews";//阅读资讯

    public static final String TASK_EVENT_WATCH_VIDEO = "watchVideo";//看视频

    public static final String TASK_EVENT_WITHDRAWALS = "withdrawals";//提现

    public static final String TASK_EVENT_INVITATION = "invitation";//邀请

    public static final String TASK_EVENT_INNER_JUMP = "innerJump";//APP内跳转链接

    public static final String TASK_EVENT_OUTER_JUMP = "outnerJump";//第三方浏览器跳转链接

    public static final String TASK_EVENT_SHARE_TO_GROUP = "shareToGroup";//分享到微信群

    public static final String TASK_EVENT_SHARE_TO_COF = "shareToCOF";//分享到朋友圈

    public static final String TASK_EVENT_PLAY_JUMP = "xianwan";//闲玩游戏

    /**
     * Intent传值Key
     */
    public static final String INTENT_KEY_TYPE = "type";
    public static final String INTENT_KEY_PHONE = "phone";

    public static final String INTENT_KEY_PASSWORD = "password";


    /**
     * 异常布局常量
     */
    public static final int ERROR_NETWORK = -1;//网络异常
    public static final int ERROR_EMPTY_ORDER = -2;//暂无订单
    public static final int ERROR_EMPTY_PROFIT = -3;//暂无收益明细
    public static final int ERROR_EMPTY_PROFIT_APPRENTICE = -4;//暂无徒弟提供收益
    public static final int ERROR_EMPTY_APPRENTICE = -5;//暂无徒弟
    public static final int ERROR_EMPTY_NOTICE = -6;//暂无公告
    public static final int ERROR_EMPTY_MESSAGE = -7;//暂无消息
    public static final int ERROR_EMPTY_COMMENT = -8;//暂无评论
    public static final int ERROR_NETWORK_COMMENT = -9;//暂无评论网络异常
    public static final int ERROR_EMPTY_COLLECTION_NEWS = -10;//暂无资讯收藏
    public static final int ERROR_EMPTY_COLLECTION_VIDEO = -11;//暂无视频收藏


    /**
     * ================================================
     * RequestCode
     * ================================================
     */
    public static final int REQUEST_CODE_CAMERA = 100;

    public static final int REQUEST_CODE_ALBUM = 101;

    public static final int REQUEST_CODE_CROP = 102;


    public static final String filePath = Environment.getExternalStorageDirectory() + "/cash/" + "head.jpg";

    public static final String BASE_PATH = "";

    public static final String FIEL_SAVE_PATH = Environment.getExternalStorageDirectory() + File.separator + "/dzkandian/";

    public static final String USER_HEADER_IMAGE_NAME = "header.png";

    public static final String SP_KEY_FIRST = "isFirst";

    public static final String SP_YOU_MI_KEY_FIRST = "isYouMiFirst";

    public static final String SP_KEY_NEW_COMMENT = "newsShow";

    public static final String SP_KEY_VIDEO_COMMENT = "videoShow";

    public static final String SP_KEY_SHORT_VIDEO_COMMENT = "shortShow";

}
