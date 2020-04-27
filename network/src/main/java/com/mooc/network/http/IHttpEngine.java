package com.mooc.network.http;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.mooc.network.ApiResponse;

public interface IHttpEngine {
    /**
     * 执行Http请求操作
     *
     * @param config   http参数配置
     * @param liveData 通过liveData实现结果数据返回
     * @param <T>      泛型
     */
    <T> void execute(@NonNull HttpConfig config, @NonNull MutableLiveData<ApiResponse<T>> liveData);

    /**
     * 通过Tag取消请求
     *
     * @param tag tag
     */
    void cancel(@NonNull Object tag);
}
