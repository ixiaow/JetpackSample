package com.mooc.network.http

import android.text.TextUtils
import androidx.annotation.IntDef
import androidx.collection.ArrayMap
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.reflect.Type

class HttpConfig {
    /**
     * http请求方式
     */
    @JvmField
    @Method
    var method = GET

    /**
     * 缓存策略
     */
    @JvmField
    @CacheStrategy
    var cacheStrategy = NET_CACHE

    /**
     * 设置post提交方式
     */
    @JvmField
    var formData: FormData? = null

    /*
     * 网络数据主动取消的tag
     */
    @JvmField
    var tag: Any? = null

    /**
     * http请求的基础url
     */
    var baseUrl: String? = null

    /**
     * http请求的业务url
     */
    var url: String? = null

    /**
     * 同步请求还是异步请求
     */
    @JvmField
    var isAsync = true

    /**
     * 数据请求的转换类型
     */
    @JvmField
    var type: Type = Void::class.java

    /**
     * http请求头部信息
     */
    val headers: ArrayMap<String, String> = ArrayMap()

    /**
     * http请求的参数
     */
    val params: ArrayMap<String, Any> = ArrayMap()

    /**
     * 添加header信息
     *
     * @param name   header名称
     * @param header header值
     */
    fun addHeader(name: String, header: String) {
        headers[name] = header
    }

    /**
     * 添加header信息
     *
     * @param headers header的map集合
     */
    fun addHeaders(headers: Map<String, String>?) {
        this.headers.putAll(headers!!)
    }

    /**
     * 添加param信息
     *
     * @param name  param 名称
     * @param param param值
     */
    fun addParam(name: String, param: Any) {
        params[name] = param
    }

    /**
     * 添加params
     *
     * @param params params集合
     */
    fun addParams(params: Map<String, Any>?) {
        this.params.putAll(params!!)
    }

    fun url(): String {
        return if (!TextUtils.isEmpty(baseUrl)) {
            baseUrl + url
        } else url
    }

    fun setPostType(formData: FormData?) {
        this.formData = formData
    }

    @IntDef(GET, POST)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Method

    @IntDef(
        CACHE_ONLY,
        CACHE_FIRST,
        NET_ONLY,
        NET_CACHE
    )
    @Retention(RetentionPolicy.SOURCE)
    annotation class CacheStrategy
    companion object {
        const val JSON_KEY = "json"

        /**
         * http get请求方式
         */
        const val GET = 0

        /**
         * http post请求方式
         */
        const val POST = 1

        /**
         * 缓存策略  仅仅使用缓存
         */
        const val CACHE_ONLY = 0

        /**
         * 缓存策略  先使用缓存，然后请求网络更新数据
         */
        const val CACHE_FIRST = 1

        /**
         * 缓存策略 仅仅使用网络数据
         */
        const val NET_ONLY = 2

        /**
         * 缓存策略 仅仅使用网络数据，后将数据保缓存
         */
        const val NET_CACHE = 3
    }

}