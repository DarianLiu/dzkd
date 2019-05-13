package com.dzkandian.common.uitls.root;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * 判断是否root
 * Created by Ben on 2018/1/29.
 */

public class CheckRoot {
    public static boolean isDeviceRooted() {
        if (checkSuperuserApk()
                || checkRootPathSU()
                || checkAccessRootData()) {
            return true;
        }
        return false;
    }

    public static boolean checkSuperuserApk() {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean checkRootPathSU() {
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static synchronized boolean checkAccessRootData() {
        try {
            String fileContent = "test_ok";
            String strRead = readFile("/data/su_test");
            if (fileContent.equals(strRead)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    //读文件
    public static String readFile(@NonNull String fileName) {
        File file = new File(fileName);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            while ((len = fis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            String result = new String(bos.toByteArray());
            return result;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }
}
