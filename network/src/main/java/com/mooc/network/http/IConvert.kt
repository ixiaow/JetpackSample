package com.mooc.network.http

import com.mooc.network.ApiResponse
import java.lang.reflect.Type

/**
 * 数据转换类
 * @param <T> 响应数据类型
 * @param <R> 转换后的数据类型
</R></T> */
interface IConvert<T, R> {
    fun convert(response: T, type: Type): ApiResponse<R>
}