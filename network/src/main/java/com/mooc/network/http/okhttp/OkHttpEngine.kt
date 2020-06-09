package com.mooc.network.http.okhttp

import androidx.lifecycle.MutableLiveData
import com.mooc.common.utils.Logs
import com.mooc.network.*
import com.mooc.network.cache.CacheManager
import com.mooc.network.http.*
import com.mooc.network.http.okhttp.ConvertFactory.Companion.create
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

class OkHttpEngine : IHttpEngine {
    companion object {
        private var OK_HTTP_CLIENT: OkHttpClient

        init {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            OK_HTTP_CLIENT = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()
            val trustManagers = arrayOf<TrustManager>(SimpleX509TrustManager())
            try {
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustManagers, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
                HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
        }
    }

    override fun <T> execute(
        config: HttpConfig,
        liveData: MutableLiveData<ApiResponse<T>>
    ) {
        // 创建http请求
        val request = generateRequest(config)
        val call = OK_HTTP_CLIENT.newCall(request)
        if (!config.isAsync) {
            execute(call, config, liveData)
        } else {
            enqueue(call, config, liveData)
        }
    }

    /**
     * 同步执行的方法
     */
    private fun <T> execute(
        call: Call,
        config: HttpConfig,
        liveData: MutableLiveData<ApiResponse<T>>
    ) {
        var apiResponse: ApiResponse<T>
        Logs.d("execute before cache: " + Thread.currentThread().name)
        // 只访问本地数据
        if (config.cacheStrategy == HttpConfig.CACHE_ONLY) {
            apiResponse = readCache(call.request().url.toString())
            if (!call.isCanceled()) {
                liveData.postValue(apiResponse)
            }
            return
        }

        // 先访问本地数据，然后再发起网络请求
        if (config.cacheStrategy == HttpConfig.CACHE_FIRST) {
            apiResponse = readCache(call.request().url.toString())
            if (!call.isCanceled()) {
                liveData.postValue(apiResponse)
            }
        }
        // 如果当前请求已经取消，则不在进行网络请求
        if (call.isCanceled()) {
            return
        }
        Logs.d("execute current thread: " + Thread.currentThread().name)
        try {
            val response = call.execute()
            // 创建解析类将json--> bean
            val convert: IConvert<Response?, T> = ConvertFactory.create()
            apiResponse = convert.convert(response, config.type)
        } catch (e: IOException) {
            apiResponse = ApiResponse(LOCAL_ERROR, e.message)
        }
        if (!call.isCanceled()) {
            liveData.postValue(apiResponse)
        }

        // 缓存策略不能为仅使用网络数据并且只有访问服务器成功才会更新数据缓存
        if (config.cacheStrategy != HttpConfig.NET_ONLY
            && apiResponse.status != LOCAL_ERROR
        ) {
            saveCache(call.request().url.toString(), apiResponse)
        }
    }

    private fun <T> enqueue(
        call: Call, config: HttpConfig,
        liveData: MutableLiveData<ApiResponse<T>>
    ) {
        // 异步先发起网络请求
        if (config.cacheStrategy == HttpConfig.CACHE_ONLY) {
            TaskExecutor.executeOnDiskIO {
                val apiResponse: ApiResponse<T> = readCache(call.request().url.toString())
                if (!call.isCanceled()) {
                    liveData.postValue(apiResponse)
                }
            }
            return
        }

        // 先访问本地数据，然后再发起网络请求
        if (config.cacheStrategy == HttpConfig.CACHE_FIRST) {
            TaskExecutor.executeOnDiskIO {
                val apiResponse: ApiResponse<T> = readCache(call.request().url.toString())
                if (!call.isCanceled()) {
                    liveData.postValue(apiResponse)
                }
            }
        }

        // 如果当前请求已经取消，则不在进行网络请求
        if (call.isCanceled()) {
            return
        }
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (call.isCanceled()) {
                    return
                }
                val apiResponse = ApiResponse<T>(LOCAL_ERROR, e.message)
                liveData.postValue(apiResponse)
            }

            @Throws(IOException::class)
            override fun onResponse(
                call: Call,
                response: Response
            ) {
                if (call.isCanceled()) {
                    return
                }
                val convert: IConvert<Response?, T>? = create()
                val apiResponse = convert!!.convert(response, config.type)
                liveData.postValue(apiResponse)
                if (config.cacheStrategy != HttpConfig.NET_ONLY) {
                    saveCache(call.request().url.toString(), apiResponse)
                }
            }
        })
    }

    /**
     * 缓存数据
     *
     * @param cacheKey 缓存数据的key
     * @param response 响应数据
     * @param <T>      泛型
    </T> */
    private fun <T> saveCache(cacheKey: String, response: ApiResponse<T>) {
        // 如果当前数据为空则删除缓存数据
        if (response.data == null) {
            CacheManager.get().delete(cacheKey)
            return
        }

        // 如果响应的数据类型是Void 则不需要进行任何处理
        if (response.data is Void) {
            return
        }
        // 缓存数据对象必须要实现Serializable
        require(response.data is Serializable) { response.data.toString() + " must implement Serializable or void" }
        // 保存数据
        CacheManager.get()
            .save(cacheKey, response.data as Serializable?)
    }

    /**
     * 读取缓存数据
     *
     * @param cacheKey 缓存数据key
     * @param <T>      泛型
     * @return ApiResponse
    </T> */
    private fun <T> readCache(cacheKey: String): ApiResponse<T> {
        val apiResponse = ApiResponse<T>(CACHE_CODE, data = CacheManager.get().readCache(cacheKey))
        Logs.d("cache: $cacheKey=======")
        return apiResponse
    }

    /**
     * 根据配置信息生成request
     *
     * @param config 配置信息
     * @return request
     */
    private fun generateRequest(config: HttpConfig): Request {
        return when (config.method) {
            HttpConfig.GET -> generateGetRequest(config)
            HttpConfig.POST -> generatePostRequest(config)
            else -> throw IllegalStateException("this request method invalidate: " + config.method)
        }
    }

    /**
     * 生成post请求
     *
     * @param config http请求配置信息
     * @return 请求request
     */
    private fun generatePostRequest(config: HttpConfig): Request {
        val builder = Request.Builder().url(config.url()).tag(config.tag).addHeader(config)
        // 根据提交方式添加header信息
        val header = config.formData.header
        builder.addHeader(header.first, header.second)

        // 创建body
        val body = generatePostRequestBody(config)
        return builder.post(body).build()
    }

    /**
     * 获取post提交体
     *
     * @param config 请求配置信息
     * @return RequestBody
     */
    private fun generatePostRequestBody(config: HttpConfig): RequestBody {
        return when (val formData = config.formData) {
            FormData.FORM_DATA -> getFormDataRequestBody(config)
            FormData.JSON_DATA -> getJsonDataRequestBody(config)
            FormData.MULTI_PART_DATA -> getMultiDataRequestBody(config)
            else -> throw IllegalArgumentException("post formData is invalidate: $formData")
        }
    }

    /**
     * 获取复杂的post提交体
     *
     * @param config 请求配置信息
     * @return MultipartBody
     */
    private fun getMultiDataRequestBody(config: HttpConfig): RequestBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        for ((key, value) in config.params) {
            if (value is File) {
                val file = value
                val requestBody: RequestBody = RequestBody.create(file, getFileMediaType(file))
                builder.addFormDataPart(key, file.name, requestBody)
            } else if (value is List<*>) {
                val files =
                    value as List<File>
                for (i in files.indices) {
                    val file = files[i]
                    val requestBody: RequestBody = RequestBody.create(file, getFileMediaType(file))
                    builder.addFormDataPart(key + i, file.name, requestBody)
                }
            } else {
                builder.addFormDataPart(key, value.toString())
            }
        }
        return builder.build()
    }

    /**
     * 获取文件的type类型
     *
     * @param file 文件
     * @return MediaType
     */
    private fun getFileMediaType(file: File): MediaType? {
        val fileNameMap = URLConnection.getFileNameMap()
        var contentTypeFor = fileNameMap.getContentTypeFor(file.absolutePath)
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream"
        }
        return parse.parse(contentTypeFor)
    }

    /**
     * 生成json形式的post数据
     *
     * @param config 请求配置
     * @return RequestBody
     */
    private fun getJsonDataRequestBody(config: HttpConfig): RequestBody {
        require(!config.params.isEmpty) { "json data is null" }
        val json = config.params[HttpConfig.JSON_KEY]!!
        return RequestBody.create(json.toString(), parse.parse(config.formData!!.value))
    }

    /**
     * 生成form data形式的post数据
     *
     * @param config 请求配置
     * @return FromBody
     */
    private fun getFormDataRequestBody(config: HttpConfig): RequestBody {
        val builder =
            FormBody.Builder(StandardCharsets.UTF_8)
        val params: Map<String, Any> = config.params
        for ((key, value) in params) {
            builder.addEncoded(key, value.toString())
        }
        return builder.build()
    }

    /**
     * 生成get方式的请求
     *
     * @param config 请求配置
     * @return 返回get方式的request
     */
    private fun generateGetRequest(config: HttpConfig): Request {
        val builder = Request.Builder().get().tag(config.tag).addHeader(config)
        val url: String = config.url().generateUrlForParams(config.params)
        return builder.url(url).build()
    }

    /**
     * 添加header信息
     *
     * @param builder okHttp builder
     * @param config  请求参数信息
     */
    private fun Request.Builder.addHeader(config: HttpConfig): Request.Builder {
        for ((key, value) in config.headers) {
            addHeader(key, value)
        }
        return this
    }

    override fun cancel(tag: Any) {
        //查找当前需要取消的tag是否在未执行的请求中
        for (call in OK_HTTP_CLIENT.dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }

        //查找当前需要请求的tag是否在正在执行的请求中
        for (call in OK_HTTP_CLIENT.dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }
}