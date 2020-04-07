package com.mooc.network.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class TypeToken<T> {

    protected final static ConcurrentMap<Type, Type> classTypeCache
            = new ConcurrentHashMap<Type, Type>(16, 0.75f, 1);

    protected Type type;

    public TypeToken() {
        Type superClass = getClass().getGenericSuperclass();

        Type oriType = ((ParameterizedType) superClass).getActualTypeArguments()[0];

        if (oriType instanceof Class) {
            type = oriType;
        } else {
            //修复在安卓环境中问题
            type = putCacheTypeIfAbsent(oriType);
        }
    }

    protected Type putCacheTypeIfAbsent(Type oriType) {
        Type cachedType = classTypeCache.get(oriType);
        if (cachedType == null) {
            classTypeCache.putIfAbsent(oriType, oriType);
            cachedType = classTypeCache.get(oriType);
        }
        return cachedType;
    }

    public Type getType() {
        return type;
    }
}
