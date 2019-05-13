package com.dzkandian.common.uitls;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dzkandian.common.uitls.deviceuuid.DeviceBaseInfo;
import com.dzkandian.common.uitls.deviceuuid.InstallUuidFactory;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.DeviceUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import timber.log.Timber;


/**
 * 获取各种手机信息  如imei等
 * Created by Administrator on 2018/4/16.
 */

public class TelephoneUtils {
    @NonNull
    public static String uploadInfomation(@NonNull Context context, boolean isPermissions) {

        String info = "&code=" + DeviceUtils.getVersionCode(context)//版本号
                + "&version=" + DeviceUtils.getVersionName(context)//版本名
                + "&current_channel=" + getAppMetaData(context, "UMENG_CHANNEL")//渠道信息
                + printSystemInfo() + printTelephoneInfo(context, isPermissions)
                + "&applist=" + sCanningPackage(context.getPackageManager());//包名

        return info;
    }

    /**
     * 获取手机所有应用包名
     *
     * @param packageManager 应用包管理
     * @return
     */

    @NonNull
    public static List<String> sCanningPackage(PackageManager packageManager) {
        List<String> allAppInfos = new ArrayList<>();
        if (packageManager == null){
            return allAppInfos;
        }
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        if (installedPackages == null)
            return allAppInfos;

        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            //过滤掉系统app
            if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                continue;
            }
            allAppInfos.add(packageInfo.packageName);
        }
        return allAppInfos;
    }

    /**
     * 获取application中指定的meta-data(获取渠道信息)
     *
     * @return 如果没有获取成功(没有对应值或者异常)，则返回值为空
     */
    @Nullable
    public static String getAppMetaData(@Nullable Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /**
     * 获取Build  系统信息
     *
     * @return
     */

    public static String printSystemInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("&ID=").append(Build.ID);
        sb.append("&BRAND=").append(Build.BRAND);
        sb.append("&MODEL=").append(Build.MODEL);
        sb.append("&RELEASE=").append(Build.VERSION.RELEASE);
        sb.append("&SDK =").append(Build.VERSION.SDK);
        sb.append("&BOARD=").append(Build.BOARD);
        sb.append("&PRODUCT=").append(Build.PRODUCT);
        sb.append("&DEVICE=").append(Build.DEVICE);
        sb.append("&FINGERPRINT=").append(Build.FINGERPRINT);
        sb.append("&HOST=").append(Build.HOST);
        sb.append("&TAGS=").append(Build.TAGS);
        sb.append("&TYPE=").append(Build.TYPE);
        sb.append("&TIME=").append(Build.TIME);
        sb.append("&INCREMENTAL=").append(Build.VERSION.INCREMENTAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sb.append("&DISPLAY=").append(Build.DISPLAY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            sb.append("&SDK_INT=").append(Build.VERSION.SDK_INT);
            sb.append("&MANUFACTURER=").append(Build.MANUFACTURER);
            sb.append("&BOOTLOADER=").append(Build.BOOTLOADER);
            sb.append("&CPU_ABI=").append(Build.CPU_ABI);
            sb.append("&CPU_ABI2=").append(Build.CPU_ABI2);
            sb.append("&HARDWARE=").append(Build.HARDWARE);
            sb.append("&UNKNOWN=").append(Build.UNKNOWN);
            sb.append("&CODENAME=").append(Build.VERSION.CODENAME);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sb.append("&SERIAL=").append(Build.SERIAL);
        }

        return sb.toString();
    }


    /**
     * Print telephone info.
     */
    public static String printTelephoneInfo(@NonNull Context context, boolean isPres) {
        final StringBuilder sb = new StringBuilder();
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Timber.d("========isPres"+isPres);
        if (isPres) {
            try {
                sb.append("&Phone number=").append(tm.getLine1Number());
                sb.append("&DeviceID(IMEI)=").append(tm.getDeviceId());
                DataHelper.setStringSF(context, Constant.PHONE_IMEI, tm.getDeviceId());
                sb.append("&DeviceSoftwareVersion=").append(tm.getDeviceSoftwareVersion());
                sb.append("&SimSerialNumber=").append(tm.getSimSerialNumber());
                sb.append("&IMSI=").append(tm.getSubscriberId());
                sb.append("&VoiceMailNumber=").append(tm.getVoiceMailNumber());
                sb.append("&");
                Timber.d("========读取手机状态" + sb.toString());
            } catch (Exception e) {
                Timber.d("========读取手机状态权限未获取");
            }
        }
        sb.append("NetworkCountryIso=").append(tm.getNetworkCountryIso());
        sb.append("&getSimState=").append(tm.getSimState());
        sb.append("&NetworkOperator=").append(tm.getNetworkOperator());
        sb.append("&NetworkOperatorName=").append(tm.getNetworkOperatorName());
        sb.append("&NetworkType=").append(tm.getNetworkType());
        sb.append("&PhoneType=").append(tm.getPhoneType());
        sb.append("&SimCountryIso=").append(tm.getSimCountryIso());
        sb.append("&SimOperator=").append(tm.getSimOperator());
        sb.append("&SimOperatorName=").append(tm.getSimOperatorName());
        sb.append("&uuid=").append(getUUID(context));
        sb.append("&mac=").append(getDeviceIDByMac());
        sb.append("&sn=").append(getDeviceIDBySN(context));
        sb.append("&address=").append(getDevicesHardwareAddress());
        sb.append("&ip=").append(getMacAddressByIP());
        Timber.d("=======设备信息：" + sb.toString());
        return sb.toString();
    }

    /**
     * get uuid size 32
     *
     * @param context {@link Context}
     * @return {@link String}
     */
    private static String uuid32;

    private static String getUUID(@NonNull Context context) {
        return null == uuid32 ? uuid32 = new InstallUuidFactory(context).getInstallUuid().toString().replace("-", "") : uuid32;
    }

    @Nullable
    private static String wifiMac;

    @Nullable
    public static String getDeviceIDByMac() {
        if (TextUtils.isEmpty(wifiMac)) {
            wifiMac = getMacAddressByIP();
        }
        if (TextUtils.isEmpty(wifiMac)) {
            wifiMac = getDevicesHardwareAddress();
        }
        return wifiMac;
    }

    /**
     * need open WIFI add at uses-permission android:name="android.permission.READ_PHONE_STATE"
     *
     * @param context {@link Context}
     * @return {@link String}
     */

    private static String sn;

    private static String getDeviceIDBySN(Context context) {
        if (!TextUtils.isEmpty(sn)) {
            return sn;
        }
        String or_serialon = DeviceBaseInfo.getSystemProperties(DeviceBaseInfo.OR_SERIALON);
        if (TextUtils.isEmpty(or_serialon)) {
            sn = DeviceBaseInfo.getSystemProperties(DeviceBaseInfo.OR_BOOT_SERIALON);
        }
        sn = or_serialon;
        return sn;
    }

    /**
     * 获取设备HardwareAddress地址,扫描各个网络接口
     *
     * @return this will return null
     */
    @Nullable
    private static String getDevicesHardwareAddress() {
        String hardWareAddress = null;
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                NetworkInterface iF;
                while (interfaces.hasMoreElements()) {
                    iF = interfaces.nextElement();
                    hardWareAddress = bytes2String(iF.getHardwareAddress());
                    if (hardWareAddress != null)
                        break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hardWareAddress;
    }

    @Nullable
    private static String bytes2String(@Nullable byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    /**
     * get mac address by IP
     *
     * @return this will return null
     */
    @Nullable
    private static String getMacAddressByIP() {
        String strMacAddr = null;
        try {
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
//            Log.w("MacAddressByIP", "error", e);
        }
        return strMacAddr;
    }

    /**
     * 移动设备本地IP,得到一个ip地址的列表
     *
     * @return {@link InetAddress} this will return null
     */
    @Nullable
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":"))
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }


}
