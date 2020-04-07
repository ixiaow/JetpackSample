package com.mooc.network.http;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.mooc.network.ApiResponse;

public interface IHttpEngine {

    <T> void execute(@NonNull Config config, @NonNull MutableLiveData<ApiResponse<T>> liveData);

    void cancel(@NonNull Object tag);
}
