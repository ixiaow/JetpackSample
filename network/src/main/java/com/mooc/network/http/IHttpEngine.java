package com.mooc.network.http;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.mooc.network.ApiResponse;

public interface IHttpEngine {
    @NonNull
    <T> LiveData<ApiResponse<T>> execute(@NonNull Config config);

    void cancel(@NonNull Object tag);
}
