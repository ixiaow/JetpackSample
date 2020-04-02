package com.mooc.network;

import androidx.annotation.NonNull;

public interface IRequest<T> {

    /**
     * 异步执行
     */
    void enqueue(JsonCallback<T> callback);

    /**
     * 同步执行方法
     */
    @NonNull
    ApiResponse<T> execute();
}
