package com.mooc.network.http

import androidx.lifecycle.MutableLiveData
import com.mooc.network.ApiResponse

interface IHttpEngine {
    /**
     * 执行Http请求操作
     *
     * @param config   http参数配置
     * @param liveData 通过liveData实现结果数据返回
     * @param <T>      泛型
    </T> */
    fun <T> execute(
        config: HttpConfig,
        liveData: MutableLiveData<ApiResponse<T>>
    )

    /**
     * 通过Tag取消请求
     *
     * @param tag tag
     */
    fun cancel(tag: Any)
}