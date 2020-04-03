package com.mooc.common.utils;

import androidx.annotation.NonNull;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    public static void closeQuietly(@NonNull Closeable... closeables) {

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException ignore) {
                //e.printStackTrace();
            }
        }
    }
}
