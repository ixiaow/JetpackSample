package com.mooc.network.http;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;

import okhttp3.Response;

public interface IConvert<T> {
    @NonNull
    T convert(@NonNull Response response, @NonNull Type type);
}
