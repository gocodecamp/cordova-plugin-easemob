package com.bjzjns.hxplugin.tools;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by blade on 2/19/16.
 */
public class LogUtils {

    public static boolean isDebug = true;

    private static final String TAG = "StyleMe";

    // default tagList StyleMe
    public static void i(String msg) {
        if (isDebug && !TextUtils.isEmpty(msg))
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug && !TextUtils.isEmpty(msg))
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug && !TextUtils.isEmpty(msg))
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug && !TextUtils.isEmpty(msg))
            Log.v(TAG, msg);
    }

    public static void i(Class<?> _class, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && _class != null)
            Log.i(_class.getName(), msg);
    }

    public static void d(Class<?> _class, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && _class != null)
            Log.d(_class.getName(), msg);
    }

    public static void e(Class<?> _class, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && _class != null)
            Log.e(_class.getName(), msg);
    }

    public static void v(Class<?> _class, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && _class != null)
            Log.v(_class.getName(), msg);
    }

    // custom tagList
    public static void i(String tag, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(tag))
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(tag))
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(tag))
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(tag))
            Log.v(tag, msg);
    }
}
