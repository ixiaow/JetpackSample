package com.mooc.network.http;

import androidx.annotation.NonNull;

import com.mooc.network.ApiResponse;

import java.lang.reflect.Type;

/**
 * 数据转换类
 * @param <T> 响应数据类型
 * @param <R> 转换后的数据类型
 */
public interface IConvert<T, R> {

    @NonNull
    ApiResponse<R> convert(@NonNull T response, @NonNull Type type);
}
