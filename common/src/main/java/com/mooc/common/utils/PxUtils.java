package com.mooc.common.utils;

public class PxUtils {

    public static int dp2px(float dpValue) {
        float density = AppGlobals.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static int getScreenWidth() {
        return AppGlobals.getApplication().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return AppGlobals.getApplication().getResources().getDisplayMetrics().heightPixels;
    }
}
