package com.dzkandian.app.http;

import android.support.annotation.NonNull;

import com.dzkandian.storage.BaseResponse;
import com.dzkandian.storage.bean.ActiveBean;
import com.dzkandian.storage.bean.CoinBean;
import com.dzkandian.storage.bean.CollectionNewsBean;
import com.dzkandian.storage.bean.CollectionVideoBean;
import com.dzkandian.storage.bean.DeviceInfoBean;
import com.dzkandian.storage.bean.MarqueeBean;
import com.dzkandian.storage.bean.RandomAdBean;
import com.dzkandian.storage.bean.SearchBean;
import com.dzkandian.storage.bean.UserBean;
import com.dzkandian.storage.bean.UserBindBean;
import com.dzkandian.storage.bean.UserInfoBean;
import com.dzkandian.storage.bean.VersionBean;
import com.dzkandian.storage.bean.WeChatShareBean;
import com.dzkandian.storage.bean.mine.AlipayInfoBean;
import com.dzkandian.storage.bean.mine.ApprenticesBean;
import com.dzkandian.storage.bean.mine.BannerBean;
import com.dzkandian.storage.bean.mine.CoinExchangeBean;
import com.dzkandian.storage.bean.mine.InvitePageBean;
import com.dzkandian.storage.bean.mine.InviteProfitBean;
import com.dzkandian.storage.bean.mine.MessageBean;
import com.dzkandian.storage.bean.mine.MyOrderBean;
import com.dzkandian.storage.bean.mine.NotificationBean;
import com.dzkandian.storage.bean.mine.QuestionAllBean;
import com.dzkandian.storage.bean.mine.QuestionBean;
import com.dzkandian.storage.bean.mine.QuestionTypeBean;
import com.dzkandian.storage.bean.mine.TaskRecordBean;
import com.dzkandian.storage.bean.news.CommentRecordBean;
import com.dzkandian.storage.bean.news.NewBarrageBean;
import com.dzkandian.storage.bean.news.NewsBean;
import com.dzkandian.storage.bean.news.NewsOrVideoShareBean;
import com.dzkandian.storage.bean.news.ReplyBean;
import com.dzkandian.storage.bean.task.SignRecordBean;
import com.dzkandian.storage.bean.task.TaskListBean;
import com.dzkandian.storage.bean.video.VideoBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import static com.dzkandian.app.http.Api.AD_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.COMMENT_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.NEWS_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.SEARCH_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

/**
 * 关于看点跟视频的接口
 * Created by LiuLi on 2018/4/11.
 */

public interface BaseService {

    /**
     * 获取资讯分类列表
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + NEWS_DOMAIN_NAME})
    @GET("/a/news.list")
    Observable<BaseResponse<List<String>>> getNewsTitleList(@Header("version") String version,
                                                            @Header("versionCode") String versionCode,
                                                            @Header("sys_name") String sys_name,
                                                            @Header("deviceId") String deviceId,
                                                            @Header("timestamp") String timestamp);

    /**
     * 获取资讯列表
     *
     * @param type     类型
     * @param num      每页数量
     * @param beforeId 最后一项ID
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + NEWS_DOMAIN_NAME, "Authorization:Basic bHVhdXNlcjomKmpmbGthSA=="})
    @GET("/a/news.html")
    Observable<BaseResponse<List<NewsBean>>> getNewsList(@Query("type") String type,
                                                         @Query("num") int num,
                                                         @Query("beforeId") String beforeId,
                                                         @Header("version") String version,
                                                         @Header("versionCode") String versionCode,
                                                         @Header("sys_name") String sys_name,
                                                         @Header("deviceId") String deviceId,
                                                         @Header("timestamp") String timestamp);


    /**
     * 搜索新闻或视频
     *
     * @param keyword  搜索关键词
     * @param num      分页参数：最后一条的时间戳
     * @param beforeId 分页参数：最后一条的ID
     * @return
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + SEARCH_DOMAIN_NAME})
    @GET("/a/search")
    Observable<BaseResponse<List<SearchBean>>> getSearchList(@Query("keyword") String keyword,
                                                             @Query("beforeTime") long num,
                                                             @Query("beforeId") String beforeId);


    /**
     * 获取视频栏目列表
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + NEWS_DOMAIN_NAME})
    @GET("/a/video.list?version=1")
    Observable<BaseResponse<List<String>>> getVideoTitleList(@Header("version") String version,
                                                             @Header("versionCode") String versionCode,
                                                             @Header("sys_name") String sys_name,
                                                             @Header("deviceId") String deviceId,
                                                             @Header("timestamp") String timestamp);

    /**
     * 获取视频列表
     *
     * @param type     类型
     * @param num      每页数量
     * @param beforeId 最后一项ID
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + NEWS_DOMAIN_NAME,
            "Authorization: Basic bHVhdXNlcjomKmpmbGthSA=="})
    @GET("/a/video.html")
    Observable<BaseResponse<List<VideoBean>>> getVideoList(@Query("type") String type,
                                                           @Query("num") int num,
                                                           @Query("beforeId") String beforeId,
                                                           @Header("version") String version,
                                                           @Header("versionCode") String versionCode,
                                                           @Header("sys_name") String sys_name,
                                                           @Header("deviceId") String deviceId,
                                                           @Header("timestamp") String timestamp,
                                                           @Query("placeKeyword") String placeKeyword,
                                                           @Query("supportSdk") String supportSdk
    );

    /**
     * 获取弹幕
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "Authorization: Basic bHVhdXNlcjomKmpmbGthSA==", "appId: dzkandian"})
    @POST("/api/cmt/barrage")
    Observable<BaseResponse<NewBarrageBean>> getBarrage(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 创建评论接口
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "Authorization: Basic bHVhdXNlcjomKmpmbGthSA==", "appId: dzkandian"})
    @POST("/api/cmt/commit")
    Observable<BaseResponse<NewBarrageBean>> foundComment(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取文章、视频评论的列表
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "Authorization: Basic bHVhdXNlcjomKmpmbGthSA==", "appId: dzkandian"})
    @POST("/api/cmt/record")
    Observable<BaseResponse<CommentRecordBean>> commentRecord(@Header("token") String token, @Body RequestBody requestBody);

    /*
     * 点赞评论
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "Authorization: Basic bHVhdXNlcjomKmpmbGthSA==", "appId: dzkandian"})
    @POST("/api/cmt/commentThumbsUp")
    Observable<BaseResponse<Integer>> commentThumbsUp(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取文章、视频     回复的列表
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "Authorization: Basic bHVhdXNlcjomKmpmbGthSA==", "appId: dzkandian"})
    @POST("/api/cmt/reply")
    Observable<BaseResponse<List<ReplyBean>>> replyList(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 创建回复接口
     */
    @NonNull
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "Authorization: Basic bHVhdXNlcjomKmpmbGthSA==", "appId: dzkandian"})
    @POST("/api/cmt/commit")
    Observable<BaseResponse<Object>> foundReply(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 发送验证码
     * 类型：
     * USER_REG(注册)
     * MODIFY_OR_FIND_PASS(修改/找回密码)
     * SMS_LOGIN(验证码登录)
     * MODIFY_PHONE(修改手机号)
     * WITHDRAWALS(提现)
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/sendSmsCode")
    Observable<BaseResponse<UserBean>> sendSmsCode(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 注册
     */
    @NonNull
    @Headers({"appId: dzkandian", "token: "})
    @POST("/api/user/reg")
    Observable<BaseResponse<UserBean>> register(@Body RequestBody requestBody);

    /**
     * 短信登录
     */
    @NonNull
    @Headers({"appId: dzkandian", "token: "})
    @POST("/api/user/smsLogin")
    Observable<BaseResponse<UserBean>> smsLogin(@Body RequestBody requestBody);

    /**
     * 账号密码登录
     */
    @NonNull
    @Headers({"appId: dzkandian", "token: "})
    @POST("/api/user/login")
    Observable<BaseResponse<UserBean>> login(@Body RequestBody requestBody);

    /**
     * 微信登录
     */
    @NonNull
    @Headers({"appId: dzkandian", "token: "})
    @POST("/api/user/wxLogin")
    Observable<BaseResponse<UserBean>> wxLogin(@Body RequestBody requestBody);


    /**
     * 手机找回密码
     */
    @NonNull
    @Headers({"appId: dzkandian", "token: "})
    @POST("/api/user/forgetPassword")
    Observable<BaseResponse<UserBean>> forgetPassword(@Body RequestBody requestBody);

    /**
     * 手机修改密码
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/changePassword")
    Observable<BaseResponse<UserBean>> revicePassword(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 在线更新
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/checkUpdate")
    Observable<BaseResponse<VersionBean>> checkUpdate(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 文件下载
     *
     * @param fileUrl 文件下载地址
     */
    @NonNull
    @Streaming
    @GET
    Observable<ResponseBody> update(@Url String fileUrl);

    /**
     * 上传设备信息
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/uploadDeviceInfo")
    Observable<BaseResponse<DeviceInfoBean>> uploadDeviceInfo(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取APP运行必备信息
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/appConfig")
    Observable<BaseResponse<DeviceInfoBean>> getEssentialParameter(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 签到列表
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/signRecord")
    Observable<BaseResponse<SignRecordBean>> signRecord(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 任务列表
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/list")
    Observable<BaseResponse<TaskListBean>> taskList(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 每日签到
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/sign")
    Observable<BaseResponse<Integer>> sign(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 完成列表中的任务,领取奖励
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/finish")
    Observable<BaseResponse<Integer>> taskFinish(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 微信绑定
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/wxBinding")
    Observable<BaseResponse<String>> wxBinding(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 支付宝绑定
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/alipayBinding")
    Observable<BaseResponse<TaskListBean>> alipayBinding(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 微信钱包授权绑定
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/weixinPayBind")
    Observable<BaseResponse<UserBindBean>> weixinPayBind(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取支付宝加密签名
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/alipayLoginParam")
    Observable<BaseResponse<AlipayInfoBean>> aLiPayLoginParam(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 支付宝登录
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/alipayLogin")
    Observable<BaseResponse<AlipayInfoBean>> aLiPayLogin(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 支付宝钱包绑定
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/alipayBinding")
    Observable<BaseResponse<String>> aLiPayWalletBind(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取个人信息
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/info")
    Observable<BaseResponse<UserInfoBean>> userInfo(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取金币数量
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/gold/mine")
    Observable<BaseResponse<CoinBean>> getCoin(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 上传头像
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/uploadAvatar")
    Observable<BaseResponse<String>> uploadAvatar(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 用户资料修改
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/updateInfo")
    Observable<BaseResponse<UserInfoBean>> updateInfo(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取分享数据
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/invite/share")
    Observable<BaseResponse<WeChatShareBean>> inviteShareData(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 用密码进行绑定手机号
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/bindPhone")
    Observable<BaseResponse<UserBean>> bindPhone(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 当用户已经绑定过手机号码后，可通过此接口更换绑定手机号
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/changePhone")
    Observable<BaseResponse<UserBean>> changePhone(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 重新绑定 旧手机号验证码
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/verifySms")
    Observable<BaseResponse<UserBean>> oldPhoneCheck(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 问题分类列表
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/faq/type")
    Observable<BaseResponse<List<QuestionTypeBean>>> questionTypeList(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 问题列表
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/faq/question")
    Observable<BaseResponse<List<QuestionBean>>> questionList(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 问题列表
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/faq/all")
    Observable<BaseResponse<List<QuestionAllBean>>> questionAll(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 用户修改密码接口
     * 存在密码：修改密码
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/user/changePassword")
    Observable<BaseResponse<UserBean>> existPwdRevisePwd(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 获取收徒页面展示的必需数据
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/invite/pageData")
    Observable<BaseResponse<InvitePageBean>> invitePageData(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取好友邀请收徒分享数据接口
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/invite/share")
    Observable<BaseResponse<WeChatShareBean>> inviteShare(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 徒弟提供的收益列表
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/invite/profitList")
    Observable<BaseResponse<List<InviteProfitBean>>> inviteProfitList(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取当前用户的徒弟列表
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/invite/apprentices")
    Observable<BaseResponse<List<ApprenticesBean>>> apprenticesList(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取金币兑换列表
     * 获取提现数据的参数
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/gold/exchangeList")
    Observable<BaseResponse<CoinExchangeBean>> getCoinExchange(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 提现界面  立即兑换
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/gold/withdrawals")
    Observable<BaseResponse<CoinExchangeBean>> redeemNow(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/record")
    Observable<BaseResponse<List<TaskRecordBean>>> getTaskRecord(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 我的订单
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/gold/withdrawalsList")
    Observable<BaseResponse<List<MyOrderBean>>> getWithdrawalsList(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 活动中心
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/activitycenter/list")
    Observable<BaseResponse<List<ActiveBean>>> activeCenter(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 阅读奖励
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/read")
    Observable<BaseResponse<Integer>> readingReward(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 视频奖励
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/watch")
    Observable<BaseResponse<Integer>> videoReward(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 时段奖励
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/task/duration")
    Observable<BaseResponse<Integer>> timeReward(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 意见反馈
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/feedback/save")
    Observable<BaseResponse<UserBean>> feedBack(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 用户被点赞/评论消息通知接  列表
     *
     * @param token       用户标识
     * @param requestBody ( page:（int)页码，limit	：(int）一页的条数)
     */
    @Headers({"appId: dzkandian"})
    @POST("/api/commentThumbsUpMsg/noticeMsgList")
    Observable<BaseResponse<List<MessageBean>>> replyPraiseList(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 系统通知列表
     *
     * @param token       用户标识
     * @param requestBody ( page:（int)页码，limit	：(int）一页的条数)
     */
    @Headers({"appId: dzkandian"})
    @POST("/api/usermsg/replyList")
    Observable<BaseResponse<List<NotificationBean>>> notificationList(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 激活APP√  交易猫
     *
     * @param requestBody
     */
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/appActivate")
    Observable<BaseResponse<String>> realization(@Body RequestBody requestBody);

    /**
     * 激活APP√  有米
     *
     * @param requestBody
     */
    @Headers({"appId: dzkandian"})
    @POST("/api/utils/youmiCb")
    Observable<BaseResponse<Object>> realizationYouMi(@Body RequestBody requestBody);


    /**
     * 资讯列表分享链接   #更换了接口地址  后台返回新增 标题、文本、图片地址
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/share/articleShareData")
    Observable<BaseResponse<NewsOrVideoShareBean>> newsShare(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 视频分享链接
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/share/convertUrl")
    Observable<BaseResponse<NewsOrVideoShareBean>> videoShare(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 获取轮播图
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/activitycenter/bannerImgs")
    Observable<BaseResponse<List<BannerBean>>> banner(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 邀请好友拆红包列表接口
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/activitycenter/inviteFriendsOpenRedPacketList")
    Observable<BaseResponse<String>> invitationRedPacket(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 发消息给徒弟，提醒徒弟提现或者赚金币
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/activitycenter/messageToApprentice")
    Observable<BaseResponse<String>> messageToApprentice(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 拆红包接口
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/activitycenter/openRedPacket")
    Observable<BaseResponse<String>> openRedPacket(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 用户是否关注公众号接口
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/webchatofficialaccount/isfoucs")
    Observable<BaseResponse<String>> isfoucs(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 关注公众号绑定关系接口
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @POST("/api/webchatofficialaccount/binding")
    Observable<BaseResponse<String>> binding(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 新闻收藏；
     */
    @NonNull
//    @Headers({"appId: dzkandian"})
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "appId: dzkandian"})
    @POST("/api/mycollection/saveNews")
    Observable<BaseResponse<Integer>> newscollection(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 获取资讯收藏列表
     */
    @NonNull
//    @Headers({"appId: dzkandian"})
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "appId: dzkandian"})
    @POST("/api/mycollection/newsList")
    Observable<BaseResponse<List<CollectionNewsBean>>> newscollectionlist(@Header("token") String token, @Body RequestBody requestBody);

    /**
     * 视频或者小视频收藏
     */
    @NonNull
//    @Headers({"appId: dzkandian"})
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "appId: dzkandian"})
    @POST("/api/mycollection/saveVideo")
    Observable<BaseResponse<Integer>> videoCollection(@Header("token") String token, @Body RequestBody requestBody);


    /**
     * 获取视频或者小视频收藏列表
     */
    @NonNull
//    @Headers({"appId: dzkandian"})
    @Headers({DOMAIN_NAME_HEADER + COMMENT_DOMAIN_NAME, "appId: dzkandian"})
    @POST("/api/mycollection/videoList")
    Observable<BaseResponse<List<CollectionVideoBean>>> videoCollectionList(@Header("token") String token, @Body RequestBody requestBody);

    @NonNull
    @Headers({DOMAIN_NAME_HEADER + AD_DOMAIN_NAME, "appId: dzkandian"})
    @POST("/api/placeConfig/randomAd")
    Observable<BaseResponse<RandomAdBean>> getRandomAd(@Header("version") String version,
                                                       @Header("deviceId") String deviceId,
                                                       @Header("supportSdk") String supportSdk,
                                                       @Query("placeKeyword") String placeKeyword);

    /**
     * 跑马灯接口
     */
    @NonNull
    @Headers({"appId: dzkandian"})
    @GET("/api/utils/horseRaceLamp")
    Observable<BaseResponse<MarqueeBean>> getMarquee();

//    /**
//     * 新闻详情页上传网页加载完成时间 给惠彬接口统计；
//     */
//    @NonNull
//    @Headers({DOMAIN_NAME_HEADER + TIME,"Authorization: Basic bHVhdXNlcjomKmpmbGthSA==","appId: dzkandian"})
//    @POST("/api/task/x5web")
//    Observable<BaseResponse<Integer>> x5web(@Header("token") String token, @Body RequestBody requestBody);
}
