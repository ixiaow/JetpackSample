package com.mooc.navcompiler;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;

public class Utils {

    public static void closeQuietly(@Nonnull Closeable... closeables) {

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException ignore) {
                //e.printStackTrace();
            }
        }
    }
}
