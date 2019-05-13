package com.dzkandian.common.uitls.xposed;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测Xposed
 */

public class CheckXposed {


    public static boolean isXposed(@NonNull Context context) {

        if (checkByMatchPackageName(context) || checkByRaiseException()) {
            return true;
        }
        return false;
    }

    private static boolean checkByMatchPackageName(@NonNull Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                List<ApplicationInfo> applicationInfoList = new ArrayList<>();
                applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                if (applicationInfoList.size() == 0){
                    return false;
                }
                for (ApplicationInfo applicationInfo : applicationInfoList) {
                    if (applicationInfo.packageName.equals("de.robv.android.xposed.installer")) {
                        return true;
                    } else if (applicationInfo.packageName.equals("com.saurik.substrate")) {
                        return true;
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    private static boolean checkByRaiseException() {
        try {

            throw new Exception();

        } catch (Exception e) {

            for (StackTraceElement stackTraceElement : e.getStackTrace()) {

//                Log.d(TAG, stackTraceElement.getClassName() + " + " + stackTraceElement.getMethodName());
                int zygoteInitCallCount = 0;
                if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge")) {
                    return true;
                } else if (stackTraceElement.getClassName().equals("com.android.internal.os.ZygoteInit")) {
                    zygoteInitCallCount++;
                    if (zygoteInitCallCount == 2) {
//                        Log.wtf("HookDetection", "Substrate is active on the device.");
                        return true;
                    }
                } else if (stackTraceElement.getClassName().equals("com.saurik.substrate.MS$2") &&
                        stackTraceElement.getMethodName().equals("invoked")) {
//                    Log.wtf("HookDetection", "A method on the stack trace has been hooked using Substrate.");
                    return true;
                } else if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                        stackTraceElement.getMethodName().equals("main")) {
//                    Log.wtf("HookDetection", "Xposed is active on the device.");
                    return true;
                } else if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                        stackTraceElement.getMethodName().equals("handleHookedMethod")) {
//                    Log.wtf("HookDetection", "A method on the stack trace has been hooked using Xposed.");
                    return true;
                }
            }
        }
        return false;
    }


}
