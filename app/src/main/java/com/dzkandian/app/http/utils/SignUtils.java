package com.dzkandian.app.http.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jess.arms.utils.DeviceUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 参数签名工具类
 * Created by LiuLi on 2018/4/18.
 */

public class SignUtils {

    /**
     * 签名数据以备提交，计算签名并填充到map
     *
     * @param info
     */
    public static void fillCSign(Context context, @NonNull Map<String, String> info) {

        info.put("timestamp", String.valueOf(System.currentTimeMillis()));
        info.put("sys_name", "Android");
        info.put("version", DeviceUtils.getVersionName(context));
        info.put("versionCode", String.valueOf(DeviceUtils.getVersionCode(context)));
//        info.put("channel", TelephoneUtils.getAppMetaData(context, "UMENG_CHANNEL"));

        /**
         * 使用 Map按key进行排序   返回结果
         */
        Map<String, String> resultMap = sortMapByKey(info);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            String entryKey = entry.getKey();
            String entryValue = entry.getValue();

            //除去sign字段和空字段
            if ("sign".equals(entryKey) || entryValue == null || "".equals(entryValue)) {
                continue;
            }
            sb.append(entryKey);
            sb.append("=");
            sb.append(entryValue);
            sb.append("&");
        }

        sb.deleteCharAt(sb.length() - 1);//删除最后一个&

//        System.out.println("HTTPUtils Native层参数：" + sb.toString());
//        Timber.d("========签名前结果：" + sb.toString());

        String secret = JniInterface.getSign(context, sb.toString());

//        System.out.println("HTTPUtils Native签名结果：" + secret);

//        Timber.d("========签名后结果：" + secret);

        info.put("sign", secret);

    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    @Nullable
    public static Map<String, String> sortMapByKey(@Nullable Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());

        sortMap.putAll(map);

        //   System.out.println("排序后：" + sortMap);

        return sortMap;
    }

    static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(@NonNull String str1, @NonNull String str2) {

            return str1.compareTo(str2);
        }
    }
}
