package com.mooc.ppjoke.utils;

public class PxUtils {

    public static int dp2px(float dpValue) {
        float density = AppGlobals.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
