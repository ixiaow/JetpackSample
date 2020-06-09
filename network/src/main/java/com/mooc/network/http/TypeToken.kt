package com.mooc.network.http

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

abstract class TypeToken<T> {
    var type: Type? = null
        protected set

    protected fun putCacheTypeIfAbsent(oriType: Type): Type? {
        var cachedType = classTypeCache[oriType]
        if (cachedType == null) {
            classTypeCache.putIfAbsent(oriType, oriType)
            cachedType = classTypeCache[oriType]
        }
        return cachedType
    }

    companion object {
        protected val classTypeCache: ConcurrentMap<Type, Type> =
            ConcurrentHashMap(
                16,
                0.75f,
                1
            )
    }

    init {
        val superClass = javaClass.genericSuperclass
        (superClass as? ParameterizedType)?.actualTypeArguments?.get(0)?.let {
            //修复在安卓环境中问题
            type = it as? Class<*> ?: putCacheTypeIfAbsent(it)
        }
    }
}