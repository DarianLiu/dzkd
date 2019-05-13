package com.dzkandian.app;

import android.app.ActivityManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.dzkandian.BuildConfig;
import com.dzkandian.R;
import com.dzkandian.common.uitls.Constant;
import com.dzkandian.db.DaoMaster;
import com.dzkandian.db.DaoSession;
import com.jess.arms.base.BaseApplication;
import com.jess.arms.utils.ArmsUtils;
import com.kk.taurus.exoplayer.ExoMediaPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import timber.log.Timber;

import static com.dzkandian.app.http.Api.AD_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.AD_URL;
import static com.dzkandian.app.http.Api.COMMENT_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.COMMENT_URL;
import static com.dzkandian.app.http.Api.NEWS_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.NEWS_URL;
import static com.dzkandian.app.http.Api.SEARCH_DOMAIN_NAME;
import static com.dzkandian.app.http.Api.SEARCH_URL;

/**
 * Application
 * Created by LiuLi on 2018/8/23.
 */

public class MyApplication extends BaseApplication {

    private static MyApplication instance;

    public static boolean ignoreMobile = false;

    private DaoSession daoSession;

    public static MyApplication get() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        fix();
    }

    /**
     * 解决oppoR9 TimeoutExceptions
     */
    public void fix() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get(null));

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


        String processName = getProcessName(this);
//        Timber.d("==========onCreate: " + processName);

        if (!TextUtils.isEmpty(processName) && TextUtils.equals(processName, "com.dzkandian")) {
            instance = this;
//            Timber.d("==========onCreate: initDatabase " + processName);
            initDatabase();

            PlayerConfig.setUseDefaultNetworkEventProducer(true);
//        初始化库
//        PlayerLibrary.init(this);
//        如果添加了'cn.jiajunhui:exoplayer:xxxx'该依赖
            ExoMediaPlayer.init(this);
//        如果添加了'cn.jiajunhui:ijkplayer:xxxx'该依赖
//        IjkPlayer.init(this);


            //ShareSDK初始化
            MobSDK.init(this);
//        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.footer_pullup);//"上拉加载更多";
//        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.footer_release);//"释放立即加载";
//        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.footer_refreshing);//"正在刷新...";
            ClassicsFooter.REFRESH_FOOTER_LOADING = this.getString(R.string.footer_loading);//"正在加载...";
//        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.footer_finish);//"加载完成";
//        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.footer_failed);//"加载失败";
//        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.footer_allloaded);//"没有更多数据了";

            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            if (BuildConfig.LOG_DEBUG) {//Timber初始化
                //Timber 是一个日志框架容器,外部使用统一的Api,内部可以动态的切换成任何日志框架(打印策略)进行日志打印
                //并且支持添加多个日志框架(打印策略),做到外部调用一次 Api,内部却可以做到同时使用多个策略
                //比如添加三个策略,一个打印日志,一个将日志保存本地,一个将日志上传服务器
                Timber.plant(new Timber.DebugTree());
                // 如果你想将框架切换为 Logger 来打印日志,请使用下面的代码,如想切换为其他日志框架请根据下面的方式扩展
//                    Logger.addLogAdapter(new AndroidLogAdapter());
//                    Timber.plant(new Timber.DebugTree() {
//                        @Override
//                        protected void log(int priority, String tag, String message, Throwable t) {
//                            Logger.log(priority, tag, message, t);
//                        }
//                    });
                ButterKnife.setDebug(true);
            }
            //leakCanary内存泄露检查
            ArmsUtils.obtainAppComponentFromContext(this).extras().put(RefWatcher.class.getName(), BuildConfig.USE_CANARY ? LeakCanary.install(this) : RefWatcher.DISABLED);
            //扩展 AppManager 的远程遥控功能
            ArmsUtils.obtainAppComponentFromContext(this).appManager().setHandleListener((appManager, message) -> {
                switch (message.what) {
                    //case 0:
                    //do something ...
                    //   break;
                }
            });
            //Usage:
            //Message msg = new Message();
            //msg.what = 0;
            //AppManager.post(msg); like EventBus

            RetrofitUrlManager.getInstance().putDomain(NEWS_DOMAIN_NAME, NEWS_URL);
            RetrofitUrlManager.getInstance().putDomain(COMMENT_DOMAIN_NAME, COMMENT_URL);
            RetrofitUrlManager.getInstance().putDomain(AD_DOMAIN_NAME, AD_URL);
            RetrofitUrlManager.getInstance().putDomain(SEARCH_DOMAIN_NAME, SEARCH_URL);
//        RetrofitUrlManager.getInstance().putDomain(HHB, HHB_URL);
//        RetrofitUrlManager.getInstance().putDomain(TIME, TIME_URL);

            //极光
//            JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试;
            JPushInterface.init(this);
            //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
//        Set<String> set = new HashSet<>();
//        set.add("yr6688");//名字任意，可多添加几个
//        JPushInterface.setTags(application.getApplicationContext(), 1, set);//设置标签
//        JPushInterface.deleteTags(application.getApplicationContext(),1,set);//删除标签

            /*umeng统计场景*/
//            UMConfigure.setLogEnabled(true);//正式版的时候设置false，关闭调试;
            UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
            MobclickAgent.openActivityDurationTrack(false);
            MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        }
    }

    private void initDatabase() {
//        Stetho.initializeWithDefaults(this);//数据库调试（正式版隐藏）

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, Constant.DB_NAME, null);
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        database.enableWriteAheadLogging();//这将允许来自多个线程的事务
//        database.setLocale(Locale.CHINA);
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();

//        QueryBuilder.LOG_SQL = true;//是否开启数据库日志；正式版false；
//        QueryBuilder.LOG_VALUES = true;
    }

    private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = null;
        if (am != null) {
            runningApps = am.getRunningAppProcesses();
        }
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
