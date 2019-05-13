package com.dzkandian.common.uitls.deviceuuid;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.lang.reflect.Method;

public class DeviceBaseInfo {

    /**
     * boot serial no
     */
    public static final String OR_BOOT_SERIALON = "ro.boot.serialno";
    /**
     * serial no
     */
    public static final String OR_SERIALON = "ro.serialno";

    /**
     * get system properties by android.os.SystemProperties
     *
     * @param propertyName {@link String}
     * @return {@link String}
     */
    @NonNull
    public static String getSystemProperties(String propertyName) {
        String value = "";
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method method = clazz.getMethod("get", paramTypes);

            Object object = clazz.newInstance();
            Object[] propertyNames = new Object[1];
            propertyNames[0] = propertyName;
            value = (String) method.invoke(object, propertyNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public static String getAndroidID(@NonNull Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidID)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            androidID = tm.getDeviceId();
        }
        return androidID;
    }
}