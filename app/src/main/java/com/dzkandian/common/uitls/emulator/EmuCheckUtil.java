package com.dzkandian.common.uitls.emulator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import timber.log.Timber;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Author: hzlishang
 * Data: 2017/7/7 上午9:07
 * Des:模拟器判断
 * version:
 */

public class EmuCheckUtil {

    public static boolean mayOnEmulator(@NonNull Context context) {

        return isEmulatorFromQemuFeatures(context)
                || notHasLightSensorManager(context)
                || checkIsNotRealPhone()
                || isRunningInEmualtor()
                || isEmulatorFromCpu()
                || hasEth0Interface();


    }

    // 查杀比较严格，放在最后，直接pass x86
    public static boolean isEmulatorFromCpu() {

        String cpuInfo = ShellAdbUtils.execCommand("cat /proc/cpuinfo", false).successMsg;
        return TextUtils.isEmpty(cpuInfo) || ((cpuInfo.contains("intel") || cpuInfo.contains("amd")));
    }

    // 根据Qemu的一些特征信息判断
    public static boolean isEmulatorFromQemuFeatures(Context context) {

        return checkPipes()
                || checkQEmuDriverFile();
    }

    @NonNull
    public static String[] known_pipes = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };

    @NonNull
    public static String[] known_qemu_drivers = {
            "goldfish"
    };

    //检测“/dev/socket/qemud”，“/dev/qemu_pipe”这两个通道

    public static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
                return true;
            }
        }
        return false;
    }

    // 读取文件内容，然后检查已知QEmu的驱动程序的列表
    public static boolean checkQEmuDriverFile() {
        File driver_file = new File("/proc/tty/drivers");
        if (driver_file.exists() && driver_file.canRead()) {
            byte[] data = new byte[1024];  //(int)driver_file.length()
            try {
                InputStream inStream = new FileInputStream(driver_file);
                inStream.read(data);
                inStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String driver_data = new String(data);
            for (String known_qemu_driver : known_qemu_drivers) {
                if (driver_data.indexOf(known_qemu_driver) != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return true 为模拟器
     */
    public static Boolean notHasLightSensorManager(@NonNull Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
        if (null == sensor8) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据部分特征参数设备信息来判断是否为模拟器
     *
     * @return true 为模拟器
     */
    public static boolean isFeatures() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    @NonNull
    public static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    /**
     * 判断cpu是否为电脑来判断 模拟器
     *
     * @return true 为模拟器
     */
    public static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    /**
     * ro.kernel.qemu属性值是否为1，通常在手机上没有
     *
     * @return
     */

    public static boolean isRunningInEmualtor() {
        boolean qemuKernel = false;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("getprop ro.kernel.qemu");
            os = new DataOutputStream(process.getOutputStream());//获取输出流
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            os.writeBytes("exit\n");//执行推出
            os.flush();
            process.waitFor();
            qemuKernel = (Integer.valueOf(in.readLine()) == 1);//判断    ro.kernel.qemu属性值是否为1，通常在手机上没有


        } catch (Exception e) {
            qemuKernel = false;//出现异常可能是在手机上运行
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {

            }
        }
        return qemuKernel;
    }

    /**
     * 判断是否存在网卡
     *
     * @return
     */

    private static boolean hasEth0Interface() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().equals("eth0"))
                    return true;
            }
        } catch (SocketException ex) {
        }
        return false;
    }
}
