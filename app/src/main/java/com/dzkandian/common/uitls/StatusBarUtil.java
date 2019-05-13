package com.dzkandian.common.uitls;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtil {

    //把布局顶上状态栏，保留状态栏信息，和京东的banner类似
    public static void setStatusBarTranslucent(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //Window.ID_ANDROID_CONTENT:经测试其加上导航栏等同于DecorView，getChildAt(0)获得的是MainActivity的外层布局RelativeLayout
            ViewGroup contentView = activity.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
            //setFitsSystemWindows：true：最顶布局预留出状态栏高度 false:最顶布局直接顶上状态栏，注意，这里虽然布局而顶上状态栏，
            //但是状态栏上的时间，电量内容依然保存，跟京东的banner是一个道理
            contentView.getChildAt(0).setFitsSystemWindows(false);
        } else {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    //设置状态栏颜色，api大于21有效，19-21的依据styles中的colorPrimaryDark
    public static void setStatusBarColor(Activity activity, int color) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //得到当前界面的装饰视图
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            //设置让应用主题内容占据状态栏和导航栏
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            //        SYSTEM_UI_FLAG_FULLSCREEN表示全屏的意思，也就是会将状态栏隐藏
            //设置系统UI元素的可见性
            decorView.setSystemUiVisibility(option);

            //设置状态栏颜色
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(color));
            //设置导航栏颜色
            activity.getWindow().setNavigationBarColor(Color.BLACK);
        }
    }

    //计算状态栏的高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
