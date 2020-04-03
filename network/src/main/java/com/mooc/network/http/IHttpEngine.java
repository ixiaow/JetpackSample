package com.mooc.network.http;

import androidx.lifecycle.LiveData;

import com.mooc.network.ApiResponse;

/**
 * Author: xw
 * Email:i.xiaowujiang@gmail.com
 * Date: 2018-05-09 2018/5/9
 * Description: IHttpEngine
 */
public interface IHttpEngine {

    <T> LiveData<ApiResponse<T>> execute(Options options);

    /**
     * 根据tag 取消任务
     *
     * @param tag 绑定的tag
     */
    void cancel(Object tag);
}