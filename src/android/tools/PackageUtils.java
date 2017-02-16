package com.bjzjns.hxplugin.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * @author zhaolei
 * @describe 应用程序包工具类.
 */
public class PackageUtils {
    /**
     * 得到Application的指定的KEY的MetaData信息
     *
     * @param metaDataKey
     * @return
     */
    public static String getApplicationMetadata(Context context,
                                                String metaDataKey) {
        ApplicationInfo info = null;
        try {
            PackageManager pm = context.getPackageManager();

            info = pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);

            return String.valueOf(info.metaData.get(metaDataKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getPackageName:得到应用包名. <br/>
     *
     * @return
     * @author zhaolei
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * getVersionName:得到版本名称. <br/>
     *
     * @return
     * @author zhaolei
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    /**
     * getVersionCode:得到版本号. <br/>
     *
     * @return
     * @author zhaolei
     */
    public static int getVersionCode(Context context) {

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获得当前进程号
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 判断当前应用是否在前端运行
     *
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager =(ActivityManager) context.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName =context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
