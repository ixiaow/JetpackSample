package com.mooc.network.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class TypeToken<T> {

    static ConcurrentMap<Type, Type> classTypeCache
            = new ConcurrentHashMap<Type, Type>(16, 0.75f, 1);

    private Type type;

    protected TypeToken() {
        Type superClass = getClass().getGenericSuperclass();

        Type oriType = ((ParameterizedType) superClass).getActualTypeArguments()[0];

        if (oriType instanceof Class) {
            type = oriType;
        } else {

            if (oriType instanceof ParameterizedType) {
                oriType = ((ParameterizedType) oriType).getActualTypeArguments()[0];
            }
            //修复在安卓环境中问题
            Type cachedType = classTypeCache.get(oriType);
            if (cachedType == null) {
                classTypeCache.putIfAbsent(oriType, oriType);
                cachedType = classTypeCache.get(oriType);
            }

            type = cachedType;
        }
    }

    public Type getType() {
        return type;
    }
}
