package com.mooc.ppjoke.utils;

import com.mooc.common.utils.AppGlobals;

public class PxUtils {

    public static int dp2px(float dpValue) {
        float density = AppGlobals.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
