package com.dzkandian.app.config;

import android.content.Context;

import com.dzkandian.app.MyApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler crashHandler = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultCaughtExceptionHandler;

    //单例模式
    public static CrashHandler getInstance() {
        return crashHandler;
    }

    public void init(Context context) {
        //获取默认的系统异常捕获器
        mDefaultCaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //把当前的crash捕获器设置成默认的crash捕获器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }

}
