package com.mooc.network;

import com.mooc.network.http.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ApiResponseToken<T> extends TypeToken<T> {

    public ApiResponseToken() {
        Type superClass = getClass().getGenericSuperclass();

        Type oriType = ((ParameterizedType) superClass).getActualTypeArguments()[0];

        if (oriType instanceof Class) {
            type = oriType;
        } else {
            // 解决ApiResponse<T>这种情况
            if (oriType instanceof ParameterizedType) {
                oriType = ((ParameterizedType) oriType).getActualTypeArguments()[0];
            }
            type = putCacheTypeIfAbsent(oriType);
        }
    }
}
