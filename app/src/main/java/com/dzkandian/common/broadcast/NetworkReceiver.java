package com.dzkandian.common.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;


/**
 * 网络状态监听广播
 */
public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        //**判断当前的网络连接状态是否可用*/
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            //当前网络状态可用
            int netType = info.getType();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context,"当前网络状态为wifi网络",Toast.LENGTH_SHORT).show();
//                Log.e("NETSTATUE", "当前网络状态为-wifi");
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(context,"当前网络状态为手机移动网络",Toast.LENGTH_SHORT).show();
//                Log.e("NETSTATUE", "当前网络状态为-mobile");
            }
        } else {
            Toast.makeText(context,"当前网络不可用",Toast.LENGTH_SHORT).show();
            //当前网络不可用
//            Log.e("NETSTATUE", "无网络连接");
        }
    }
}