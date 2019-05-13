package com.dzkandian.app.http.utils;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Jni接口获取签名
 * Created by Administrator on 2018/3/8.
 */

public class JniInterface {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * @param context 上下文
     * @param data 参数数据
     */
    @NonNull
    public static native String getSign(Context context, String data);
}
