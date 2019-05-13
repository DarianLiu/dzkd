package com.dzkandian.common.JPush.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dzkandian.app.config.EventBusTags;
import com.dzkandian.mvp.common.ui.activity.MainActivity;
import com.dzkandian.mvp.common.ui.activity.SplashActivity;
import com.dzkandian.mvp.common.ui.activity.WebViewActivity;
import com.dzkandian.mvp.mine.ui.activity.QuickCashActivity;
import com.dzkandian.mvp.news.ui.activity.NewsDetailActivity;
import com.dzkandian.storage.bean.PushBean;
import com.dzkandian.storage.event.ChangeTabEvent;
import com.dzkandian.storage.event.PushEvent;
import com.google.gson.Gson;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.ArmsUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Timber.d(TAG + "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {      //注册ID；
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Timber.d(TAG + "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {      //自定义消息
                Timber.d(TAG + "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                Timber.d(TAG + "[MyReceiver] 接收到推送下来的自定义参数: " + bundle.getString(JPushInterface.EXTRA_EXTRA));

//                PushMessageEvent event = new PushMessageEvent.ShareBean()
//                        .message(bundle.getString(JPushInterface.EXTRA_MESSAGE))
//                        .param(bundle.getString(JPushInterface.EXTRA_EXTRA))
//                        .build();

                PushBean pushBean = new Gson().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), PushBean.class);
                switch (pushBean.getTag()) {
                    case "newActivities":
                        EventBus.getDefault().post(new PushEvent.Builder().newActive(1).newNotification(-1).newMessage(-1).build(), EventBusTags.TAG_PUSH_MESSAGE);
                        break;
                    case "newFeedbackMsg":
                        EventBus.getDefault().post(new PushEvent.Builder().newActive(-1).newNotification(1).newMessage(-1).build(), EventBusTags.TAG_PUSH_MESSAGE);
                        break;
                    case "newsArticle":
                        EventBus.getDefault().post(new PushEvent.Builder().newActive(-1).newNotification(-1).newMessage(1).build(), EventBusTags.TAG_PUSH_MESSAGE);
                        break;
                }


            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {           //接收通知
                Timber.d(TAG + "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Timber.d(TAG + "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {          //点击了消息
                Timber.d(TAG + "[MyReceiver] 用户点击打开了通知");
                AppManager appManager = ArmsUtils.obtainAppComponentFromContext(context.getApplicationContext()).appManager();

                if (appManager != null && appManager.getActivityList() != null && appManager.getActivityList().size() == 0) {
                    Timber.d(TAG + "  unexist");
                    Intent intentJPush = new Intent(context, SplashActivity.class);
                    intentJPush.putExtra("jPushData", true);
                    intentJPush.putExtras(bundle);
                    intentJPush.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intentJPush);
                } else {
                    Timber.d(TAG + "  exist");
                    //打开自定义的Activity
                    jPushOpenActivity(bundle, context);
                }

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Timber.d(TAG + "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {                 //链接状态；
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Timber.d(TAG + "[MyReceiver]" + intent.getAction() + "============" + connected);
            } else {
                Timber.d(TAG + "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    private void jPushOpenActivity(Bundle bundle, Context context) {
        JSONObject json = null;
        try {
            json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String pushType;                  //  pushType  区分跳转类型;pushNews  pushActivity  pushQuickCash  pushTaskCenter
        String pushNewsUrl;               //  pushNewsUrl  资讯网页url;
        String pushNewsId;                //  pushNewsId  资讯ID;
        String pushNewsTab;               //  pushNewsTab  资讯栏目;
        String pushActivityUrl;           //  pushActivityUrl  活动网页 url;
        String pushActivityTitle;         //  pushActivityTitle  活动网页 title;
        if (json != null) {
            pushType = json.optString("pushType", "");
            pushNewsUrl = json.optString("pushNewsUrl", "");
            pushNewsId = json.optString("pushNewsId", "");
            pushNewsTab = json.optString("pushNewsTab", "");
            pushActivityUrl = json.optString("pushActivityUrl", "");
            pushActivityTitle = json.optString("pushActivityTitle", "");
            Timber.d("==JPush  信息:  "
                    + "\n" + "pushType= " + pushType
                    + "\n" + "pushNewsUrl= " + pushNewsUrl
                    + "\n" + "pushNewsId= " + pushNewsId
                    + "\n" + "pushNewsTab= " + pushNewsTab
                    + "\n" + "pushActivityUrl= " + pushActivityUrl
                    + "\n" + "pushActivityTitle= " + pushActivityTitle
            );

            Intent intent = new Intent();
            intent.putExtra("mPushUpApp", "mPushUpApp");//用于区分是否推送打开唤醒打开

            if (TextUtils.equals(pushType, "pushNews")) {
                intent.putExtra("id", pushNewsId);//资讯、视频ID;
                intent.putExtra("web_url", pushNewsUrl);//资讯、网页url;
                intent.putExtra("tab", pushNewsTab);//资讯、视频频道类型;
                intent.setClass(context, NewsDetailActivity.class);
            } else if (TextUtils.equals(pushType, "pushActivity")) {
                intent.putExtra("URL", pushActivityUrl);//活动网页url;
                intent.putExtra("title", pushActivityTitle);//活动网页url;
                intent.setClass(context, WebViewActivity.class);
            } else if (TextUtils.equals(pushType, "pushQuickCash")) {
                intent.setClass(context, QuickCashActivity.class);//快速提现页
            } else if (TextUtils.equals(pushType, "pushTaskCenter")) {
                intent.setClass(context, MainActivity.class);//主界面的任务中心
                EventBus.getDefault().post(new ChangeTabEvent.Builder().indexTab(2).build(), EventBusTags.TAG_CHANGE_TAB);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append("\nkey:").append(key).append(", EXTRA_NOTIFICATION_ID  value:").append(bundle.getInt(key));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append("\nkey:").append(key).append(", EXTRA_CONNECTION_CHANGE  value:").append(bundle.getBoolean(key));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        Timber.i(TAG + "This icon_errorr_empty_message has no Extra data");
                        continue;
                    }

                    try {
                        JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                        Iterator<String> it = json.keys();

                        while (it.hasNext()) {
                            String myKey = it.next();
                            sb.append("\nkey:").append(key).append(", EXTRA_EXTRA  value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
                        }
                    } catch (JSONException e) {
                        Timber.e(TAG + "Get icon_errorr_empty_message extra JSON error!");
                    }

                    break;
                default:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.get(key));
                    break;
            }
        }
        return sb.toString();
    }

}
