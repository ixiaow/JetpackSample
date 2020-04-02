package com.mooc.ppjoke.utils;

import android.util.Log;

import java.util.Locale;

public class Logs {

    private static boolean isDebug = true;

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 自动生成tag
     */
    private static String generateTag() {
        StackTraceElement traceElement = new Throwable().getStackTrace()[2];
        String className = traceElement.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        String format = "%s.%s:(L:%d)";
        String methodName = traceElement.getMethodName();
        int lineNum = traceElement.getLineNumber();
        return String.format(Locale.CHINA, format, className, methodName, lineNum);
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(generateTag(), msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(generateTag(), msg);
        }
    }
}
