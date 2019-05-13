package com.dzkandian.app.config;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dzkandian.app.MyApplication;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.LoginActivity;
import com.jess.arms.http.GlobalHttpHandler;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.dzkandian.common.JPush.TagAliasOperatorHelper.sequence;

/*可以==time 服务器过来的时间*/

/**
 * ================================================
 * 展示 {@link GlobalHttpHandler} 的用法
 * Created by LiuLi on 2018/4/9.
 * ================================================
 */
public class GlobalHttpHandlerImpl implements GlobalHttpHandler {
    private Context context;

    public GlobalHttpHandlerImpl(Context context) {
        this.context = context;
    }

    /**
     * 固定字段
     */
    private static final List<String> COMMON_KEYS = Arrays.asList("code", "status", "timestamp", "msg");

    @Override
    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, @NonNull Response response) {
        /* 这里可以先客户端一步拿到每一次http请求的结果,可以解析成json,做一些操作,如检测到token过期后
           重新请求token,并重新执行请求 */

        ResponseBody body = response.body();
        MediaType mediaType = null;
        if (body != null) {
            mediaType = body.contentType();
        }

        if (!TextUtils.isEmpty(httpResult)
                && mediaType != null
                && "json".equals(mediaType.subtype())) {
            try {

                JSONObject resultObj = new JSONObject(httpResult);

//                Timber.d("==time 服务器过来的时间：%s,地址：%s",resultObj.getLong("timestamp") + "",response.request().url().toString());

                JSONObject returnObj = new JSONObject();

                if (resultObj.optInt("code") == 401) {
                    DataHelper.removeSF(context, Constant.SP_KEY_TOKEN);
                    DataHelper.removeSF(context, Constant.SP_KEY_EXPIRE);
                    DataHelper.removeSF(context, Constant.SP_KEY_HAVE_TOUCH_HARDWARE); //401时消除触摸触摸硬件
                    DataHelper.removeSF(context, Constant.SP_KEY_INDEX_POP_CLOSE_ACTIVITY_HOUR); //上一次关闭首页弹窗活动的时间
                    EventBus.getDefault().post(false, EventBusTags.TAG_LOGIN_STATE);
                    DataHelper.removeSF(context.getApplicationContext(), Constant.SP_KEY_USER_ID); //删除用户ID

                    MyApplication.get().getDaoSession().getNewsRecordBeanDao().deleteAll();
//                    EventBus.getDefault().post(false, EventBusTags.TAG_PUSH_STATE);
//                    TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
//                    tagAliasBean.alias = "";
//                    tagAliasBean.isAliasAction = true;
//                    tagAliasBean.action = ACTION_CLEAN;
//                    sequence++;
//                    TagAliasOperatorHelper.getInstance().handleAction(context, sequence, tagAliasBean);
                    JPushInterface.deleteAlias(context, sequence++);

                    ArmsUtils.startActivity(new Intent(context, LoginActivity.class));
                    return response;
//                    ArmsUtils.obtainAppComponentFromContext(context).appManager().startActivity(LoginActivity.class);
//                    Toast.makeText(context, resultObj.optString("msg"), Toast.LENGTH_SHORT).show();
                } else if (resultObj.length() == COMMON_KEYS.size() + 1) {//如果只有5个元素
                    if (resultObj.has("data")) {
                        return response;
                    }
                    Iterator<?> iterator = resultObj.keys();
                    while (iterator.hasNext()) {//遍历一级属性
                        String key = iterator.next().toString();
                        if (COMMON_KEYS.contains(key)) {
                            returnObj.put(key, resultObj.get(key));
                        } else if (resultObj.get(key) == JSONObject.NULL) {
                            returnObj.put("data", new JSONObject());
                        } else {
                            returnObj.put("data", resultObj.get(key));
                        }
                    }

                } else {//否则将所有元素塞入data字段中
                    JSONObject data = new JSONObject();

                    Iterator<?> iterator = resultObj.keys();
                    while (iterator.hasNext()) {//遍历一级属性
                        String key = iterator.next().toString();
                        if (COMMON_KEYS.contains(key)) {
                            returnObj.put(key, resultObj.get(key));
                        } else {
                            data.put(key, resultObj.get(key));
                        }
                    }
                    returnObj.put("data", data);
                }
                response.close();
                return new Response.Builder()
                        .body(ResponseBody.create(response.body().contentType(), returnObj.toString()))
                        .request(response.request())
                        .code(response.code())
                        .protocol(response.protocol())
                        .message(response.message())
                        .build();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    // 这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
    @Override
    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
                    /* 如果需要再请求服务器之前做一些操作,则重新返回一个做过操作的的request如增加header,不做操作则直接返回request参数
                       return chain.request().newBuilder().header("token", tokenId)
                              .build(); */
        return request;
    }
}
