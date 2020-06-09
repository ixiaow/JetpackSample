package com.mooc.network

import android.text.TextUtils
import androidx.collection.ArrayMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.common.utils.Logs
import com.mooc.network.http.FormData
import com.mooc.network.http.HttpConfig
import com.mooc.network.http.IHttpEngine
import com.mooc.network.http.TypeToken
import com.mooc.network.http.okhttp.OkHttpEngine
import java.lang.reflect.Type

class LiveHttp private constructor() {
    private val mConfig: HttpConfig = HttpConfig()

    /**
     * url全路径
     * example: https://www.github.com/ixiaow
     *
     * @param url url全路径
     */
    fun url(url: String): LiveHttp {
        mConfig.url = url
        mConfig.baseUrl = ""
        return this
    }

    /**
     * 设置restful url路径
     * example: baseUrl: https://www.github.com   path: /ixiaow
     *
     * @param path restful url路径
     */
    fun path(path: String): LiveHttp {
        mConfig.url = path
        return this
    }

    /**
     * 设置为get请求方式
     */
    fun get(): LiveHttp {
        mConfig.method = HttpConfig.GET
        return this
    }
    /**
     * 设置post请求方式
     */
    /**
     * 设置post请求方式
     */
    @JvmOverloads
    fun post(formData: FormData = FormData.FORM_DATA): LiveHttp {
        mConfig.method = HttpConfig.POST
        mConfig.setPostType(formData)
        return this
    }

    /**
     * 设置post json请求方式
     */
    fun post(json: String): LiveHttp {
        mConfig.method = HttpConfig.POST
        mConfig.setPostType(FormData.JSON_DATA)
        mConfig.addParam(HttpConfig.JSON_KEY, json)
        return this
    }

    /**
     * 设置缓存策略
     */
    fun cacheStrategy(@HttpConfig.CacheStrategy cacheStrategy: Int): LiveHttp {
        mConfig.cacheStrategy = cacheStrategy
        return this
    }

    /**
     * 设置tag标志，可以根据tag取消请求
     */
    fun tag(tag: Any): LiveHttp {
        mConfig.tag = tag
        return this
    }

    /**
     * 设置解析的数据类型
     */
    fun registerType(type: Type): LiveHttp {
        mConfig.type = type
        return this
    }

    /**
     * 设置解析的数据类型
     */
    fun registerType(typeReference: TypeToken<*>): LiveHttp {
        mConfig.type = typeReference.type
        return this
    }

    /**
     * 设置当前方式是同步的还是异步
     * 默认为异步
     */
    fun isAsync(isAsync: Boolean = true): LiveHttp {
        mConfig.isAsync = isAsync
        return this
    }

    /**
     * 添加请求参数
     *
     * @param key   请求参数key
     * @param value 请求参数值
     */
    fun addParam(key: String, value: Any): LiveHttp {
        mConfig.addParam(key, value)
        return this
    }

    /**
     * 添加请求参数
     *
     * @param params map参数集合
     */
    fun addParams(params: ArrayMap<String, Any>): LiveHttp {
        mConfig.addParams(params)
        return this
    }

    /**
     * 添加请求header
     *
     * @param headers map 头部集合
     */
    fun addHeaders(headers: ArrayMap<String, String>): LiveHttp {
        mConfig.addHeaders(headers)
        return this
    }

    /**
     * 添加请求header
     *
     * @param key   key
     * @param value value
     */
    fun addHeader(key: String?, value: String?): LiveHttp {
        mConfig.addHeader(key, value)
        return this
    }

    /**
     * 开始订阅请求网络数据
     */
    fun <T> observe(
        owner: LifecycleOwner,
        observer: HttpObserver<ApiResponse<T>>
    ) {
        // 获取泛型实际类型
        val type = observer.type
        mConfig.type = type
        Logs.d("type: $type")
        require(!TextUtils.isEmpty(mConfig.url())) { "请求路径不能为空" }
        val liveData =
            MutableLiveData<ApiResponse<T>?>()
        TaskExecutor.postToMain{ liveData.observe(owner, observer) }
        sHttpEngine.execute(mConfig, liveData)
    }

    /**
     * 开始请求网络数据
     */
    fun <T> execute(): LiveData<ApiResponse<T>> {
        require(!TextUtils.isEmpty(mConfig.url())) { "请求路径不能为空" }
        val liveData = MutableLiveData<ApiResponse<T>>()
        sHttpEngine.execute(mConfig, liveData)
        return liveData
    }

    companion object {
        // 提供一个默认引擎
        @Volatile
        private var sHttpEngine: IHttpEngine = OkHttpEngine()
        private var sBaseUrl: String? = null

        /**
         * 初始化http配置信息
         *
         * @param baseUrl    http请求的baseUrl
         * @param httpEngine http请求实现的引擎
         */
        @JvmStatic
        fun init(baseUrl: String, httpEngine: IHttpEngine?) {
            sBaseUrl = baseUrl
            if (httpEngine != null) {
                sHttpEngine = httpEngine
            }
        }

        /**
         * 创建一个http
         */
        @JvmStatic
        fun create(): LiveHttp {
            return LiveHttp()
        }

        fun cancel(tag: Any) {
            sHttpEngine.cancel(tag)
        }
    }

    init {
        mConfig.baseUrl = sBaseUrl
    }
}