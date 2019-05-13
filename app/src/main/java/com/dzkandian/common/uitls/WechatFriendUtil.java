package com.dzkandian.common.uitls;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liuli on 2018/5/9.
 */

public class WechatFriendUtil {


    /**
     * 保存本地图片
     */
    public static String saveImageToGallery(@NonNull Context context, @NonNull Bitmap bmp) throws IOException {
        File appDir = null;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 首先保存图片
            appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        }
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "share.jpg";
        String fileNameTemp;
        File file = new File(appDir, fileName);
        if (file.exists()) {
            file.delete();
        } else {
            file.createNewFile();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
        }

        // 其次把文件插入到系统图库
        try {
            fileNameTemp = MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException | NullPointerException e) {
            e.printStackTrace();
            fileNameTemp = "";
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(Environment.getExternalStorageDirectory().toString() + "/Boohee")));
        return fileNameTemp;//返回的就是 content://开头的具体地址
    }
}
