package com.mooc.network.http;

import androidx.annotation.NonNull;

import com.mooc.network.ApiResponse;

import java.lang.reflect.Type;

public interface IConvert<T, R> {

    @NonNull
    ApiResponse<R> convert(@NonNull T response, @NonNull Type type);
}
