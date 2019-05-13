package com.dzkandian.common.uitls;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.jess.arms.utils.DeviceUtils;

/**
 * 网络状态监听工具类
 * Created by LiuLi on 2018/4/17.
 */

public class NetworkUtils {

    /**
     * 判断是否有可用网络
     * @param context
     * @return 有就返回 true
     */
    public static boolean checkNetwork(@NonNull Context context) {
        int networkType= DeviceUtils.getNetworkType(context);
        //0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
        if (networkType == 0) {
            return false;
        }else {
            return true;
        }
    }

    /**判断是否为WIFI网络
     * @param context
     * @return 是WIFI就返回 true
     */
    public static boolean checkIsWIFI(@NonNull Context context) {
        int networkType= DeviceUtils.getNetworkType(context);
        //0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
        if (networkType == 1) {
            return true;
        }else {
            return false;
        }
    }
}
