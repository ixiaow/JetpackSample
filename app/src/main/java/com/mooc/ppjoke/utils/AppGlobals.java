package com.mooc.ppjoke.utils;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppGlobals {
    private static Application application;

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

}
