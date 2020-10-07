package com.playplus.app.smswatcher.aaa.utils;

/**
 * Created by andy.huang on 9/8/2016.
 */

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.playplus.app.smswatcher.aaa.BuildConfig;

/**
 * LogUtils工具說明:
 * 1 只輸出等級大於等於LEVEL的日誌
 * 所以在開發和產品發佈後通過修改LEVEL來選擇性輸出日誌.
 * 當LEVEL=NOTHING則遮罩了所有的日誌.
 * 2 v,d,i,w,e均對應兩個方法.
 * 若不設置TAG或者TAG為空則為設置默認TAG
 */
public class LogUtils {
    private static final boolean IS_DEBUG = BuildConfig.DEBUG;
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int NOTHING = 6;
    private static final int LEVEL = VERBOSE;
    private static final String SEPARATOR = ",";

    public static void v(String message) {
        if (LEVEL <= VERBOSE && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.v(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void v(String tag, String message) {
        if (LEVEL <= VERBOSE && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.v(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void d(String message) {
        if (LEVEL <= DEBUG && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.d(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void d(String tag, String message) {
        if (LEVEL <= DEBUG && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.d(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void i(String message) {
        if (LEVEL <= INFO && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.i(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void i(String tag, String message) {
        if (LEVEL <= INFO && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.i(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void w(String message) {
        if (LEVEL <= WARN && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.w(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void w(String tag, String message) {
        if (LEVEL <= WARN && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.w(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void e(String tag, String message) {
        if (LEVEL <= ERROR && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.e(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (LEVEL <= ERROR && IS_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.e(tag, getLogInfo(stackTraceElement) + message, throwable);
        }
    }

    /**
     * 獲取默認的TAG名稱.
     * 比如在MainActivity.java中調用了日誌輸出.
     * 則TAG為MainActivity
     */
    public static String getDefaultTag(StackTraceElement stackTraceElement) {
        String fileName = stackTraceElement.getFileName();
        String stringArray[] = fileName.split("\\.");
        String tag = stringArray[0];
        return tag;
    }

    /**
     * 輸出日誌所包含的資訊
     */
    public static String getLogInfo(StackTraceElement stackTraceElement) {
        StringBuilder logInfoStringBuilder = new StringBuilder();
//        // 獲取執行緒名
//        String threadName = Thread.currentThread().getName();
//        // 獲取執行緒ID
//        long threadID = Thread.currentThread().getId();
        // 獲取檔案名.即xxx.java
        String fileName = stackTraceElement.getFileName();
//        // 獲取類名.即包名+類名
//        String className = stackTraceElement.getClassName();
//        // 獲取方法名稱
//        String methodName = stackTraceElement.getMethodName();
        // 獲取生日輸出行數
        int lineNumber = stackTraceElement.getLineNumber();

        logInfoStringBuilder.append("[ ");
//        logInfoStringBuilder.append("threadID=" + threadID).append(SEPARATOR);
//        logInfoStringBuilder.append("threadName=" + threadName).append(SEPARATOR);
        logInfoStringBuilder.append("fileName=" + fileName).append(SEPARATOR);
//        logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
//        logInfoStringBuilder.append("methodName=" + methodName).append(SEPARATOR);
        logInfoStringBuilder.append("lineNumber=" + lineNumber);
        logInfoStringBuilder.append(" ] ");
        return logInfoStringBuilder.toString();
    }

    public static void stanleyLog(String message) {
        d("stanleyohoo", "================");
        d("stanleyohoo", "is Main thread : " + String.valueOf(isInMainThread()));
        d("stanleyohoo", message);
        d("stanleyohoo", "================");
    }

    private static boolean isInMainThread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        return myLooper == mainLooper;
    }
}

