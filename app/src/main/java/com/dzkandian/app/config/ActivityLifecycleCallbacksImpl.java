package com.dzkandian.app.config;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dzkandian.common.uitls.Constant;
import com.dzkandian.mvp.common.ui.activity.MainActivity;
import com.dzkandian.mvp.common.ui.activity.SplashActivity;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DataHelper;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

/**
 * Activity的生命周期
 * ================================================
 * 展示 {@link Application.ActivityLifecycleCallbacks} 的用法
 * Created by LiuLi on 2018/4/9.
 * ================================================
 */
public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.w(activity + " - onActivityCreated");
        int app_status = DataHelper.getIntergerSF(activity.getApplicationContext(), Constant.SP_KEY_APP_STATUS);
        Timber.w(activity + " - app_status : " + app_status);
        switch (app_status) {
            case Constant.APP_STATUS_FORCE_KILLED:
                if (activity instanceof MainActivity) {
                    Timber.w(activity + " - 1111111 app_status : " + app_status);
                    return;
                } else {
                    Timber.w(activity + " - 2222222 app_status : " + app_status);
                    Intent intent = new Intent(ArmsUtils.obtainAppComponentFromContext(activity)
                            .appManager().getTopActivity(), SplashActivity.class);
                    intent.putExtra("app_status", Constant.APP_STATUS_RESTART);
                    ArmsUtils.startActivity(intent);
                    ArmsUtils.obtainAppComponentFromContext(activity.getApplicationContext())
                            .appManager().getTopActivity().finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.w(activity + " - onActivityStarted");
//        if (!activity.getIntent().getBooleanExtra("isInitToolbar", false)) {
//            //由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
//            //而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated 中移动到 onActivityStarted 中执行
//            activity.getIntent().putExtra("isInitToolbar", true);
//            //这里全局给Activity设置toolbar和title,你想象力有多丰富,这里就有多强大,以前放到BaseActivity的操作都可以放到这里
//            if (activity.findViewById(R.id.toolbar) != null) {
//                if (activity instanceof AppCompatActivity) {
//                    ((AppCompatActivity) activity).setSupportActionBar((Toolbar) activity.findViewById(R.id.toolbar));
//                    ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(false);
//                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        activity.setActionBar((android.widget.Toolbar) activity.findViewById(R.id.toolbar));
//                        activity.getActionBar().setDisplayShowTitleEnabled(false);
//                    }
//                }
//            }
//            if (activity.findViewById(R.id.toolbar_title) != null) {
//                ((TextView) activity.findViewById(R.id.toolbar_title)).setText(activity.getTitle());
//            }
//            if (activity.findViewById(R.id.toolbar_back) != null) {
//                activity.findViewById(R.id.toolbar_back).setOnClickListener(v -> {
//                    activity.onBackPressed();
//                });
//            }
//        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.w(activity + " - onActivityResumed");
        JPushInterface.onResume(activity);
        int app_status = DataHelper.getIntergerSF(activity.getApplicationContext(), Constant.SP_KEY_APP_STATUS);
        switch (app_status) {
            case Constant.APP_STATUS_FORCE_KILLED:
//                if (activity instanceof MainActivity) {
//                    Timber.w(activity + " - 1111111 Resumed app_status : " + app_status);
//                    return;
//                } else {
//                    Timber.w(activity + " - 2222222 Resumed app_status : " + app_status);
//                    Intent intent = new Intent(ArmsUtils.obtainAppComponentFromContext(activity.getApplicationContext())
//                            .appManager().getTopActivity(), SplashActivity.class);
//                    intent.putExtra("app_status", Constant.APP_STATUS_RESTART);
//                    ArmsUtils.startActivity(intent);
//                    ArmsUtils.obtainAppComponentFromContext(activity.getApplicationContext())
//                            .appManager().getTopActivity().finish();
//                }
                break;
            default:
                break;
        }

        /*友盟统计  onResume*/
        MobclickAgent.onPageStart(activity.getClass().getName());
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.w(activity + " - onActivityPaused");
        JPushInterface.onPause(activity);

        /*友盟统计  onPause*/
        MobclickAgent.onPageEnd(activity.getClass().getName());
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.w(activity + " - onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.w(activity + " - onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Timber.w(activity + " - onActivityDestroyed");
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        activity.getIntent().removeExtra("isInitToolbar");
    }
}
