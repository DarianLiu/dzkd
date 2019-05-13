package com.dzkandian.app.http.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dzkandian.common.uitls.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class DownLoadManager {
    /**
     * 下载文件到本地（只需知道是否成功，无需获取本地保存地址）现在只用来下载用户头像；
     *
     * @param body 文件流
     */
    @Nullable
    public static String writeResponseBodyToDisk(@NonNull ResponseBody body) throws Exception {
        String path = "";
        path = Constant.FIEL_SAVE_PATH + Constant.USER_HEADER_IMAGE_NAME;
        // 其他类型同上 自己判断加入.....
        boolean isDownloadSuccess = writeToDisk(path, body);
        if (isDownloadSuccess) {
            return path;
        } else {
            return "";
        }
    }

    /**
     * 数据流写入本地
     *
     * @return
     */
    private static boolean writeToDisk(@NonNull String path, @NonNull ResponseBody body) throws Exception {
        boolean isDownloadSuccess = false;
        File parent = new File(Constant.FIEL_SAVE_PATH);
        if (!parent.exists()) {
            parent.mkdirs();
        }

        File futureStudioIconFile = new File(path);
        InputStream inputStream;
        OutputStream outputStream;

        byte[] fileReader = new byte[4096];
        long fileSize = body.contentLength();
        long fileSizeDownloaded = 0;
        inputStream = body.byteStream();
        outputStream = new FileOutputStream(futureStudioIconFile);
        while (fileSizeDownloaded < fileSize) {
            int read = inputStream.read(fileReader);
            if (read == -1) {
                break;
            }
            outputStream.write(fileReader, 0, read);
            fileSizeDownloaded += read;
        }

        if (fileSizeDownloaded >= fileSize) {
            isDownloadSuccess = true;
        }
        outputStream.flush();

        if (inputStream != null) {
            inputStream.close();
        }
        outputStream.close();

        return isDownloadSuccess;
    }
}