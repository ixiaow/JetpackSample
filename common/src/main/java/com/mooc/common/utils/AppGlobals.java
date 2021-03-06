package com.mooc.common.utils;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppGlobals {
    private static Application application;

    @SuppressLint("PrivateApi")
    public static Application getApplication() {
        if (application == null) {
            try {
                Class<?> clazz = Class.forName("android.app.ActivityThread");
                Method currentApplication = clazz.getMethod("currentApplication");
                currentApplication.setAccessible(true);
                application = (Application) currentApplication.invoke(null);
            } catch (ClassNotFoundException | NoSuchMethodException
                    | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return application;
    }

    public void setApplication(@NonNull Application application) {
        AppGlobals.application = application;
    }
}
