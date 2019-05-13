package com.dzkandian.common.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.dzkandian.common.uitls.Constant;

import timber.log.Timber;

/**
 * 广播  接收手机是否正在连接电脑并开启USB调试
 */
public class AdbBroadcastReceiver extends BroadcastReceiver {
    private Context context;
    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        Timber.d("==============连接" + isConnectAdb());
    }

    public int isConnectAdb() {
        if (intent == null)
            return 0;
        String action = intent.getAction();
        if (action.equals(Constant.SP_KEY_ACTION)) {
            boolean connected = intent.getExtras().getBoolean("connected");  //是否正在连接电脑
            if (connected) {
                Timber.d("==============正在连接电脑");
                return 1;
            } else {
//                Toast.makeText(context,"==============没有连接电脑",Toast.LENGTH_SHORT).show();
                Timber.d("==============没有连接电脑");
                return 0;
            }
        }
        return 0;
    }
}
