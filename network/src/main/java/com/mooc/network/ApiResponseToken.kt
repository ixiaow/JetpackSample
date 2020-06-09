package com.mooc.network

import com.mooc.network.http.TypeToken
import java.lang.reflect.ParameterizedType

abstract class ApiResponseToken<T> : TypeToken<T>() {
    init {
        val superClass = javaClass.genericSuperclass
        var oriType = (superClass as? ParameterizedType)?.actualTypeArguments?.get(0)
        if (oriType is Class<*>) {
            type = oriType
        } else {
            // 解决ApiResponse<T>这种情况
            if (oriType is ParameterizedType) {
                oriType = oriType.actualTypeArguments[0]
            }
            type = oriType?.let { putCacheTypeIfAbsent(it) }
        }
    }
}