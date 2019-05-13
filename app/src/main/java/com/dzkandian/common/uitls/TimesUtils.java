package com.dzkandian.common.uitls;

import java.util.Formatter;
import java.util.Locale;

import timber.log.Timber;

/**
 * 时间戳转换时间工具类
 * Created by Administrator on 2018/5/9 0009.
 */

public class TimesUtils {

    /**获取时间戳的对应——秒钟数
     * @param longTime 时间戳
     * @return int类型 秒钟
     */
    public static int getSecond(long longTime){
        return (int) (longTime / 1000 % 60);
    }

    /**获取时间戳的对应——分钟数
     * @param longTime 时间戳
     * @return int类型 分钟
     */
    public static int getMinute(long longTime){
        return (int) (longTime / 1000 / 60 % 60);
    }

    /**获取时间戳的对应——总时钟数
     * @param longTime 时间戳
     * @return int类型 总时钟
     */
    public static int getHour(long longTime){
        return (int) (longTime / 1000 / 60 / 60);
    }

    /**获取时间戳的对应——总时钟数
     * @param longTime 时间戳
     * @return int类型 总时钟
     */
    public static int getDay(long longTime){
        return (int) (longTime / 1000 / 60 / 60 / 24);
    }

    /**获取进来的 minute second 一共多长时间，单位毫秒；
     * @param minute
     * @param second
     * @return
     */
    public static long getTimeLong(int minute,int second){
        return (long) (minute*60+second)*1000;
    }

    /**获取时间戳的对应——时 分 秒钟数   日志输出；
     * @param longTime
     */
    public static void getTime(long longTime){
        Timber.d("==time  getTime  "+(getHour(longTime)+":"+getMinute(longTime)+":"+getSecond(longTime)));
    }


    /**
     * @param milliseconds long类型 毫秒值
     * @return 返回视频显示的时间 如 02：20
     */
    public static String getVideoTime(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60) {
            return "00:00";
        }
        long mi = milliseconds * 1000;
        long totalSeconds = mi / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}
