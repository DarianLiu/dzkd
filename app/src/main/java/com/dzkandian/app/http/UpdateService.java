package com.dzkandian.app.http;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;


import com.dzkandian.BuildConfig;
import com.dzkandian.R;

import java.io.File;

/**
 * 下载
 * Created by LiuLi on 17/10/10.
 */
public class UpdateService extends Service {
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        String apkUrl = intent.getStringExtra("apk");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, Intent intent) {
                unregisterReceiver(receiver);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = (FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider",
                            new File(Environment.getExternalStorageDirectory() + "/download/vetor.apk")));
                } else {
                    uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/vetor.apk"));
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                startActivity(intent);
                stopSelf();
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownload(apkUrl);
        return Service.START_STICKY;
    }

    private void startDownload(String apkUrl) {
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(apkUrl));
        request.setTitle(getString(R.string.app_name));
        request.setDescription("新版本下载中");
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "vetor.apk");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        assert dm != null;
        dm.enqueue(request);
//        ToastUtil.shortShow("后台下载中，请稍候...");
    }
}
