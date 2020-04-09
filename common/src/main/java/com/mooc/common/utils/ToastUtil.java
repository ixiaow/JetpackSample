package com.mooc.common.utils;

import android.widget.Toast;

public class ToastUtil {

    public static void showToast(String message) {
        Toast.makeText(AppGlobals.getApplication(), message, Toast.LENGTH_SHORT).show();
    }
}
