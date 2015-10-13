package io.github.xiaolei.enterpriselibrary.logging;

import android.util.Log;

/**
 * TODO: add comment
 */
public class Logger {
    public static boolean ENABLE = true;

    public static void setEnable(boolean enable) {
        ENABLE = enable;
    }

    public static int d(String tag, String msg) {
        if (!ENABLE) {
            return -1;
        }
        return Log.d(tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (!ENABLE) {
            return -1;
        }
        return Log.d(tag, msg, tr);
    }

    public static int i(String tag, String msg) {
        if (!ENABLE) {
            return -1;
        }
        return Log.i(tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (!ENABLE) {
            return -1;
        }
        return Log.i(tag, msg, tr);
    }

    public static int w(String tag, String msg) {
        if (!ENABLE) {
            return -1;
        }
        return Log.w(tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (!ENABLE) {
            return -1;
        }
        return Log.w(tag, msg, tr);
    }

    public static int w(String tag, Throwable tr) {
        if (!ENABLE) {
            return -1;
        }
        return Log.w(tag, tr);
    }

    public static int e(String tag, String msg) {
        if (!ENABLE) {
            return -1;
        }
        return Log.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (!ENABLE) {
            return -1;
        }
        return Log.e(tag, msg, tr);
    }
}
