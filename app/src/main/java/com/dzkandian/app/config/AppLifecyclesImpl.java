package com.dzkandian.app.config;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.dzkandian.BuildConfig;
import com.dzkandian.R;
import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.utils.ArmsUtils;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import timber.log.Timber;

import static com.dzkandian.app.http.Api.COMMENT_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.COMMENT_URL;
import static com.dzkandian.app.http.Api.NEWS_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.NEWS_URL;

/**
 * Application的生命周期
 * ================================================
 * 展示 {@link AppLifecycles} 的用法
 * Application中初始化在这里操作
 * Created by LiuLi on 2018/4/9.
 * ================================================
 */
public class AppLifecyclesImpl implements AppLifecycles {

    @Override
    public void attachBaseContext(Context base) {
        MultiDex.install(base);  //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
    }

    @Override
    public void onCreate(@NonNull Application application) {
////        Timber.d("==========onCreate: AppLifecyclesImpl");
//        //ShareSDK初始化
//        MobSDK.init(application);
////        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.footer_pullup);//"上拉加载更多";
////        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.footer_release);//"释放立即加载";
////        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.footer_refreshing);//"正在刷新...";
//        ClassicsFooter.REFRESH_FOOTER_LOADING = application.getString(R.string.footer_loading);//"正在加载...";
////        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.footer_finish);//"加载完成";
////        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.footer_failed);//"加载失败";
////        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.footer_allloaded);//"没有更多数据了";
//
//        if (LeakCanary.isInAnalyzerProcess(application)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        if (BuildConfig.LOG_DEBUG) {//Timber初始化
//            //Timber 是一个日志框架容器,外部使用统一的Api,内部可以动态的切换成任何日志框架(打印策略)进行日志打印
//            //并且支持添加多个日志框架(打印策略),做到外部调用一次 Api,内部却可以做到同时使用多个策略
//            //比如添加三个策略,一个打印日志,一个将日志保存本地,一个将日志上传服务器
//            Timber.plant(new Timber.DebugTree());
//            // 如果你想将框架切换为 Logger 来打印日志,请使用下面的代码,如想切换为其他日志框架请根据下面的方式扩展
////                    Logger.addLogAdapter(new AndroidLogAdapter());
////                    Timber.plant(new Timber.DebugTree() {
////                        @Override
////                        protected void log(int priority, String tag, String message, Throwable t) {
////                            Logger.log(priority, tag, message, t);
////                        }
////                    });
//            ButterKnife.setDebug(true);
//        }
//        //leakCanary内存泄露检查
//        ArmsUtils.obtainAppComponentFromContext(application).extras().put(RefWatcher.class.getName(), BuildConfig.USE_CANARY ? LeakCanary.install(application) : RefWatcher.DISABLED);
//        //扩展 AppManager 的远程遥控功能
//        ArmsUtils.obtainAppComponentFromContext(application).appManager().setHandleListener((appManager, message) -> {
//            switch (message.what) {
//                //case 0:
//                //do something ...
//                //   break;
//            }
//        });
//        //Usage:
//        //Message msg = new Message();
//        //msg.what = 0;
//        //AppManager.post(msg); like EventBus
//
//        RetrofitUrlManager.getInstance().putDomain(NEWS_DOMAIN_NAME, NEWS_URL);
//        RetrofitUrlManager.getInstance().putDomain(COMMENT_DOMAIN_NAME, COMMENT_URL);
////        RetrofitUrlManager.getInstance().putDomain(HHB, HHB_URL);
////        RetrofitUrlManager.getInstance().putDomain(TIME, TIME_URL);
//
//        //极光
////        JPushInterface.setDebugMode(false);//正式版的时候设置false，关闭调试;
//        JPushInterface.init(application);
//        //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
////        Set<String> set = new HashSet<>();
////        set.add("yr6688");//名字任意，可多添加几个
////        JPushInterface.setTags(application.getApplicationContext(), 1, set);//设置标签
////        JPushInterface.deleteTags(application.getApplicationContext(),1,set);//删除标签
//
//        /*umeng统计场景*/
//        UMConfigure.setLogEnabled(true);//正式版的时候设置false，关闭调试;
//        UMConfigure.init(application, UMConfigure.DEVICE_TYPE_PHONE, null);
//        MobclickAgent.openActivityDurationTrack(false);
//        MobclickAgent.setScenarioType(application, MobclickAgent.EScenarioType.E_UM_NORMAL);

    }

    @Override
    public void onTerminate(Application application) {
    }
}
