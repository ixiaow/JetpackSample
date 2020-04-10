package com.mooc.common.utils;

import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(AppGlobals.getApplication(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
