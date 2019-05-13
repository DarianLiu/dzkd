package com.dzkandian.app.http.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.dzkandian.common.uitls.Constant;
import com.jess.arms.utils.DataHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

/**
 * 请求参数构造工具类
 * Created by LiuLi on 2018/4/18.
 */

public class RequestParamUtils {

    public RequestParamUtils() {
    }

    /**
     * 携带文件的相关参数构造
     *
     * @param postData 提交的数据
     */
    private static RequestBody buildFileRequestBody(Context context, @NonNull Map<String, String> postData, @NonNull Map<String, File> files) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String key : files.keySet()) {
            File file = files.get(key);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            builder.addFormDataPart(key, file.getName(), fileBody);
        }

        SignUtils.fillCSign(context, postData);

        for (String key : postData.keySet()) {
            builder.addFormDataPart(key, postData.get(key));
        }

        return builder.build();
    }

    /**
     * 未携带文件的相关参数构造
     *
     * @param postData 提交的数据
     */
    public static RequestBody buildRequestBody(Context context, @NonNull Map<String, String> postData) {

        String imei = DataHelper.getStringSF(context, Constant.PHONE_IMEI);
        if (TextUtils.isEmpty(imei))
            imei = "";

        postData.put("deviceId", imei);
        SignUtils.fillCSign(context, postData);

        Timber.d("======（未携带文件）加密后提交的数据：" + postData);

        Set<Map.Entry<String, String>> entries = postData.entrySet();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : entries) {
            builder.add(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    /**
     * 构建上传设备信息参数
     */
    public static RequestBody buildUploadDeviceInfo(Context context, String info, int touchHardware) {

        String decode = null;
        try {
            decode = URLDecoder.decode(info, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> postData = new HashMap<>();
        postData.put("model", Build.MODEL);
        postData.put("touchHardware", String.valueOf(touchHardware));
        postData.put("info", Base64.encodeToString(decode.getBytes(), Base64.DEFAULT));
        return buildRequestBody(context, postData);
    }

    /**
     * 构建获取验证码参数
     */
    public static RequestBody buildSendSmsCode(Context context, String phone, String type) {
        Map<String, String> postData = new HashMap<>();
        postData.put("phone", phone);
        postData.put("type", type);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建注册参数
     */
    public static RequestBody buildRegister(Context context, String phone, String smsCode, String password) {
        Map<String, String> postData = new HashMap<>();
        postData.put("phone", phone);
        postData.put("smsCode", smsCode);
        postData.put("password", password);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建手机验证码登录参数
     */
    public static RequestBody buildSmsLogin(Context context, String phone, String smsCode) {
        Map<String, String> postData = new HashMap<>();
        postData.put("phone", phone);
        postData.put("smsCode", smsCode);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建账号密码登录参数
     */
    public static RequestBody buildLogin(Context context, String phone, String password) {
        Map<String, String> postData = new HashMap<>();
        postData.put("phone", phone);
        postData.put("password", password);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建账号微信登录参数
     */
    public static RequestBody buildWXLogin(Context context, String code) {
        Map<String, String> postData = new HashMap<>();
        postData.put("code", code);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建微信钱包授权参数（新添加的）
     */
    public static RequestBody buildWXPay(Context context, String code, String weixinPayAppid) {
        Map<String, String> postData = new HashMap<>();
        postData.put("code", code);
        postData.put("weixinPayAppid", weixinPayAppid);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建忘记密码参数
     */
    public static RequestBody buildForgetPwd(Context context, String phone, String smsCode, String password) {
        Map<String, String> postData = new HashMap<>();
        postData.put("phone", phone);
        postData.put("smsCode", smsCode);
        postData.put("password", password);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建修改密码参数(没有密码状态)
     */
    public static RequestBody buildRevisePwd(Context context, String phone, String smsCode, String password) {
        Map<String, String> postData = new HashMap<>();
        postData.put("type", "SMS");
        postData.put("phone", phone);
        postData.put("smsCode", smsCode);
        postData.put("oldPassword", "");
        postData.put("password", password);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建完成任务领取奖励参数
     */
    public static RequestBody buildTaskFinish(Context context, long id, int typeId) {
        Map<String, String> postData = new HashMap<>();
        postData.put("id", String.valueOf(id));
        postData.put("typeId", String.valueOf(typeId));
        return buildRequestBody(context, postData);
    }


    /**
     * 构建版本更新参数
     */
    public static RequestBody buildCheckUpdate(Context context, String channel) {
        Map<String, String> postData = new HashMap<>();
        postData.put("appId", "dzkandian");
        postData.put("channel", channel);
        return buildRequestBody(context, postData);
    }


    /**
     * 构建上传头像参数
     */
    public static RequestBody buildUploadAvatar(Context context, File file) {
        Map<String, String> postData = new HashMap<>();
        postData.put("type", "AVATAR");
        Map<String, File> avatarData = new HashMap<>();
        avatarData.put("file", file);
        return buildFileRequestBody(context, postData, avatarData);
    }

    /**
     * 构建用户资料修改参数
     */
    public static RequestBody buildUpdateInfo(Context context, String key, String value, String key2, String value2) {
        Map<String, String> postData = new HashMap<>();
        postData.put("key", key);
        postData.put("value", value);
        if (!TextUtils.isEmpty(key2)) {
            postData.put("key2", key2);
            postData.put("value2", value2);
        }
        return buildRequestBody(context, postData);
    }

    /**
     * 构建绑定手机号参数
     */
    public static RequestBody bindPhone(Context context, String phone, String code, String password) {
        Map<String, String> postData = new HashMap<>();
        postData.put("phone", phone);
        postData.put("smsCode", code);
        postData.put("password", password);
        return buildRequestBody(context, postData);
    }


    /**
     * 构建以密码方式重新绑定手机号参数
     */
    public static RequestBody buildPasswordUpdatePhone(Context context, String phone, String code, String password) {
        Map<String, String> postData = new HashMap<>();
        postData.put("phone", phone);
        postData.put("smsCode", code);
        postData.put("password", password);
        postData.put("type", "PWD");
        postData.put("oldPhoneSmsCode", "");
        return buildRequestBody(context, postData);
    }


    /**
     * 构建以原号码短信验证方式重新绑定手机号参数
     */
    public static RequestBody buildSmsCodeUpdatePhone(Context context, String phone, String newCode, String oldCode) {
        Map<String, String> postData = new HashMap<>();
        postData.put("type", "SMS");
        postData.put("phone", phone);
        postData.put("password", "");
        postData.put("smsCode", newCode);
        postData.put("oldPhoneSmsCode", oldCode);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建验证短信参数
     * 验证码类型：
     * USER_REG(注册)
     * MODIFY_OR_FIND_PASS(修改/找回密码)
     * SMS_LOGIN(验证码登录)
     * MODIFY_PHONE(修改手机号)
     * WITHDRAWALS(提现)
     */
    public static RequestBody buildVerifySms(Context context, String code, String type) {
        Map<String, String> postData = new HashMap<>();
//        postData.put("phone", phone);
        postData.put("smsCode", code);
        postData.put("smsType", type);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建问题分类列表参数
     */
    public static RequestBody buildQuestionType(Context context, String page, String limit) {
        Map<String, String> postData = new HashMap<>();
        postData.put("page", page);
        postData.put("limit", limit);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建问题列表参数
     */
    public static RequestBody buildQuestionList(Context context, String type, String page, String limit, String version) {
        Map<String, String> postData = new HashMap<>();
        postData.put("type", type);
        postData.put("page", page);
        postData.put("limit", limit);
        postData.put("version", version);
        return buildRequestBody(context, postData);
    }

    /**
     * 修改密码： 有密码的参数
     */
    public static RequestBody buildExistPwdRevisePwd(Context context, String oldPwd, String newPwd) {
        Map<String, String> postData = new HashMap<>();
        postData.put("oldPassword", oldPwd);
        postData.put("password", newPwd);
        postData.put("type", "PWD");
        return buildRequestBody(context, postData);
    }


    /**
     * 修改密码： 有密码的参数
     */
    public static RequestBody buildNotPwdRevisePwd(Context context, String code, String newPwd) {
        Map<String, String> postData = new HashMap<>();
        postData.put("smsCode", code);
        postData.put("password", newPwd);
        postData.put("type", "SMS");
        return buildRequestBody(context, postData);
    }

    /**
     * 阅读奖励
     */
    public static RequestBody readingReward(Context context, String id, String aid, String aType,
                                            int record, String model, int debug, String debugInfo,
                                            int progress, String pgk, String aList) {
        Map<String, String> postData = new HashMap<>();
        postData.put("id", id);
        postData.put("aid", aid);
        postData.put("aType", aType);
        postData.put("record", String.valueOf(record));
        postData.put("model", model);
        postData.put("debug", String.valueOf(debug));
        postData.put("debugInfo", debugInfo);
        postData.put("progress", String.valueOf(progress));
        postData.put("pgk", pgk);
        postData.put("aList", aList);
        return buildRequestBody(context, postData);
    }

    /**
     * 视频奖励
     */
    public static RequestBody videoReward(Context context, String id, String vid, String vType,String aList) {
        Map<String, String> postData = new HashMap<>();
        postData.put("id", id);
        postData.put("vid", vid);
        postData.put("vType", vType);
        postData.put("vList", aList);
        return buildRequestBody(context, postData);
    }

    /**
     * 时段奖励
     */
    public static RequestBody timeReward(Context context, String id) {
        Map<String, String> postData = new HashMap<>();
        postData.put("id", id);
        return buildRequestBody(context, postData);
    }

    /**
     * 构建我的订单参数
     */
    public static RequestBody buildMyOrderType(Context context, String page, String limit, String status) {
        Map<String, String> postData = new HashMap<>();
        postData.put("status", status);
        postData.put("page", page);
        postData.put("limit", limit);
        return buildRequestBody(context, postData);
    }

    /**
     * 发起提现： 参数
     */
    public static RequestBody buildRedeemNow(Context context, String rmb, String alipay) {
        Map<String, String> postData = new HashMap<>();
        postData.put("rmb", rmb);
        postData.put("channel", alipay);
        return buildRequestBody(context, postData);
    }

    /**
     * 意见反馈
     */
    public static RequestBody feedBack(Context context, String opinion, String phone, String app_edition) {
        Map<String, String> postData = new HashMap<>();
        postData.put("opinion", opinion);
        postData.put("phone", phone);
        postData.put("app_edition", app_edition);
        return buildRequestBody(context, postData);
    }


    /**
     * 构建是否有交易猫Id
     */
    public static RequestBody buildisRealizationId(Context context, String rand) {
        Map<String, String> postData = new HashMap<>();
        postData.put("rand", rand);
        return buildRequestBody(context, postData);
    }

    /**
     * 资讯列表分享链接
     */
    public static RequestBody newsShare(Context context, String url, String ua) {
        Map<String, String> postData = new HashMap<>();
        postData.put("url", url);
        postData.put("ua", ua);
        return buildRequestBody(context, postData);
    }

    /**
     * 根据页面名称获取轮播图
     */
    public static RequestBody banner(Context context, String page) {
        Map<String, String> postData = new HashMap<>();
        postData.put("pageName", page);
        return buildRequestBody(context, postData);
    }

    /**
     * 根据用户ID邀请好友拆红包列表接口
     */
    public static RequestBody invitationRedPacket(Context context, String userId) {
        Map<String, String> postData = new HashMap<>();
        postData.put("userId", userId);
        return buildRequestBody(context, postData);
    }

    /**
     * 发消息给徒弟，提醒徒弟提现或者赚金币
     */
    public static RequestBody messageToApprentice(Context context, String userId, String apprenticeId, String msgType) {
        Map<String, String> postData = new HashMap<>();
        postData.put("userId", userId);
        postData.put("apprenticeId", apprenticeId);
        postData.put("msgType", msgType);
        return buildRequestBody(context, postData);
    }

    /**
     * 拆红包
     */
    public static RequestBody openRedPacket(Context context, String userId, String redPacketId) {
        Map<String, String> postData = new HashMap<>();
        postData.put("userId", userId);
        postData.put("redPacketId", redPacketId);
        return buildRequestBody(context, postData);
    }

    /**
     * 关注公众号列表接口
     */
    public static RequestBody isFoucs(Context context, String userId) {
        Map<String, String> postData = new HashMap<>();
        postData.put("userId", userId);
        return buildRequestBody(context, postData);
    }

    /**
     * 关注公众号绑定关系接口
     */
    public static RequestBody binding(Context context, String userId, String code) {
        Map<String, String> postData = new HashMap<>();
        postData.put("userId", userId);
        postData.put("code", code);
        return buildRequestBody(context, postData);
    }

    /**
     * 获取弹幕
     */
    public static RequestBody getBarrage(Context context, String aId,String commitFrom, String aType, int size, String lastId) {
        Map<String, String> postData = new HashMap<>();
        commitFrom = TextUtils.isEmpty(commitFrom) ? "" : commitFrom;
        postData.put("aId", aId);
        postData.put("aType", aType);
        postData.put("commitFrom", commitFrom);
        postData.put("size", String.valueOf(size));
        postData.put("lastId", lastId);
        return buildRequestBody(context, postData);
    }

    /**
     * 点赞评论接口
     */
    public static RequestBody commentThumbsUp(Context context, String commId, String type) {
        Map<String, String> postData = new HashMap<>();
        postData.put("commId", commId);
        postData.put("type", type);
        return buildRequestBody(context, postData);
    }

    /**
     * 创建评论接口
     */
    public static RequestBody foundComment(Context context, String content, String commitFrom, int readTime, String aId, String aType, String aUrl, String aTitle, String reqId,long parentId,long replyId,String replyName) {
        Map<String, String> postData = new HashMap<>();
        postData.put("content", content);
        postData.put("commitFrom", commitFrom);
        postData.put("readTime", String.valueOf(readTime));
        postData.put("aId", aId);
        postData.put("aType", aType);
        postData.put("aTitle", aTitle);
        postData.put("reqId", reqId);
        postData.put("aUrl", aUrl);
        postData.put("parentId",String.valueOf(parentId));
        postData.put("replyId",String.valueOf(replyId));
        postData.put("replyName",replyName);
        return buildRequestBody(context, postData);
    }

    /*
     *点赞接口
     */
    public static RequestBody commentThumbsUp(Context context, Long commitId,String type) {
        Map<String, String> postData = new HashMap<>();
        postData.put("commId",String.valueOf(commitId));
        postData.put("type",type);
        return buildRequestBody(context, postData);
    }

    /**
     * 获获取文章、视频评论的列表
     */
    public static RequestBody commentRecord(Context context, String aId, String aType,String commitFrom, int size, String lastId) {
        Map<String, String> postData = new HashMap<>();
        postData.put("aId", aId);
        postData.put("aType", aType);
        postData.put("commitFrom", commitFrom);
        postData.put("size", String.valueOf(size));
        postData.put("lastId",lastId);
        return buildRequestBody(context, postData);
    }

    /**
     * 获取文章、视频   回复的列表
     */
    public static RequestBody getReplyList(Context context, String commentId, String refresh,String commitFrom, String size, String lastId) {
        Map<String, String> postData = new HashMap<>();
        postData.put("commentId", commentId);
        postData.put("refresh", refresh);
        postData.put("commitFrom", commitFrom);
        postData.put("size", size);
        postData.put("lastId",lastId);
        return buildRequestBody(context, postData);
    }

    /**
     * 创建 回复接口
     */
    public static RequestBody foundReply(Context context,
                                         String content, String commitFrom, String readTime, String aId, String aType, String aUrl, String aTitle, String reqId,
                                         String parentId, String replyId, String replyName) {
        Map<String, String> postData = new HashMap<>();
        postData.put("content", content);
        postData.put("commitFrom", commitFrom);
        postData.put("readTime", readTime);
        postData.put("aId", aId);
        postData.put("aType", aType);
        postData.put("aTitle", aTitle);
        postData.put("reqId", reqId);
        postData.put("aUrl", aUrl);
        postData.put("parentId", parentId);
        postData.put("replyId", replyId);
        postData.put("replyName", replyName);
        return buildRequestBody(context, postData);
    }

    /**
     *回复界面的  点赞接口
     */
    public static RequestBody foundPraise(Context context, String commitId,String type) {
        Map<String, String> postData = new HashMap<>();
        postData.put("commId",commitId);
        postData.put("type",type);
        return buildRequestBody(context, postData);
    }

    /**
     * 文章收藏
     */
    public static RequestBody collectionNews(Context context, String url) {
        Map<String, String> postData = new HashMap<>();
        postData.put("url", url);
        return buildRequestBody(context, postData);
    }

    /**
     * 获取资讯收藏列表
     */
    public static RequestBody collectionList(Context context, String page, String limit) {
        Map<String, String> postData = new HashMap<>();
        postData.put("page", page);
        postData.put("limit", limit);
        return buildRequestBody(context, postData);
    }

    /**
     * 视频或者小视频收藏
     */
    public static RequestBody collectionVideo(Context context, String url) {
        Map<String, String> postData = new HashMap<>();
        postData.put("url", url);
        return buildRequestBody(context, postData);
    }

    /**
     * 获取视频或者小视频收藏列表
     */
    public static RequestBody videoCollectionList(Context context, String page, String limit, String type) {
        Map<String, String> postData = new HashMap<>();
        postData.put("page", page);
        postData.put("limit", limit);
        postData.put("type", type);
        return buildRequestBody(context, postData);
    }

//    /**
//     * 新闻详情页上传网页加载完成时间 给惠彬接口统计；
//     */
//    public static RequestBody x5Web(Context context, String id, String time) {
//        Map<String, String> postData = new HashMap<>();
//        postData.put("id", id);
//        postData.put("time", time);
//        return buildRequestBody(context, postData);
//    }

}
