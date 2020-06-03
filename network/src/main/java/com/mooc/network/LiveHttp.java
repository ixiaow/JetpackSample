package com.mooc.network;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mooc.common.utils.Logs;
import com.mooc.network.http.FormData;
import com.mooc.network.http.HttpConfig;
import com.mooc.network.http.IHttpEngine;
import com.mooc.network.http.TypeToken;
import com.mooc.network.http.okhttp.OkHttpEngine;

import java.lang.reflect.Type;
import java.util.Map;

public class LiveHttp {
    // 提供一个默认引擎
    private static volatile IHttpEngine sHttpEngine = new OkHttpEngine();
    private static String sBaseUrl;
    private HttpConfig mConfig;

    private LiveHttp() {
        mConfig = new HttpConfig();
        mConfig.baseUrl = sBaseUrl;
    }

    /**
     * 初始化http配置信息
     *
     * @param baseUrl    http请求的baseUrl
     * @param httpEngine http请求实现的引擎
     */
    public static void init(@NonNull String baseUrl, @Nullable IHttpEngine httpEngine) {
        sBaseUrl = baseUrl;
        if (httpEngine != null) {
            sHttpEngine = httpEngine;
        }
    }

    /**
     * 创建一个http
     */
    public static LiveHttp create() {
        return new LiveHttp();
    }

    public static void cancel(Object tag) {
        sHttpEngine.cancel(tag);
    }

    /**
     * url全路径
     * example: https://www.github.com/ixiaow
     *
     * @param url url全路径
     */
    public LiveHttp url(String url) {
        mConfig.url = url;
        mConfig.baseUrl = "";
        return this;
    }

    /**
     * 设置restful url路径
     * example: baseUrl: https://www.github.com   path: /ixiaow
     *
     * @param path restful url路径
     */
    public LiveHttp path(String path) {
        mConfig.url = path;
        return this;
    }

    /**
     * 设置为get请求方式
     */
    public LiveHttp get() {
        mConfig.method = HttpConfig.GET;
        return this;
    }

    /**
     * 设置post请求方式
     */
    public LiveHttp post() {
        return post(FormData.FORM_DATA);
    }

    /**
     * 设置post请求方式
     */
    public LiveHttp post(FormData formData) {
        mConfig.method = HttpConfig.POST;
        mConfig.setPostType(formData);
        return this;
    }

    /**
     * 设置post json请求方式
     */
    public LiveHttp post(String json) {
        mConfig.method = HttpConfig.POST;
        mConfig.setPostType(FormData.JSON_DATA);
        mConfig.addParam(HttpConfig.JSON_KEY, json);
        return this;
    }

    /**
     * 设置缓存策略
     */
    public LiveHttp cacheStrategy(@HttpConfig.CacheStrategy int cacheStrategy) {
        mConfig.cacheStrategy = cacheStrategy;
        return this;
    }

    /**
     * 设置tag标志，可以根据tag取消请求
     */
    public LiveHttp tag(Object tag) {
        mConfig.tag = tag;
        return this;
    }

    /**
     * 设置解析的数据类型
     */
    public LiveHttp registerType(Type type) {
        mConfig.type = type;
        return this;
    }

    /**
     * 设置解析的数据类型
     */
    public LiveHttp registerType(TypeToken<?> typeReference) {
        mConfig.type = typeReference.getType();
        return this;
    }

    /**
     * 设置当前方式是同步的还是异步
     * 默认为异步
     */
    public LiveHttp isAsync(boolean isAsync) {
        mConfig.isAsync = isAsync;
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param key   请求参数key
     * @param value 请求参数值
     */
    public LiveHttp addParam(String key, Object value) {
        this.mConfig.addParam(key, value);
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param params map参数集合
     */
    public LiveHttp addParams(ArrayMap<String, Object> params) {
        this.mConfig.addParams(params);
        return this;
    }

    /**
     * 添加请求header
     *
     * @param headers map 头部集合
     */
    public LiveHttp addHeaders(ArrayMap<String, String> headers) {
        this.mConfig.addHeaders(headers);
        return this;
    }

    /**
     * 添加请求header
     *
     * @param key   key
     * @param value value
     */
    public LiveHttp addHeader(String key, String value) {
        this.mConfig.addHeader(key, value);
        return this;
    }

    /**
     * 开始订阅请求网络数据
     */
    public <T> void observe(@NonNull LifecycleOwner owner, @NonNull HttpObserver<ApiResponse<T>> observer) {
        // 获取泛型实际类型
        Type type = observer.getType();
        mConfig.type = type;
        Logs.d("type: " + type);

        if (TextUtils.isEmpty(mConfig.url())) {
            throw new IllegalArgumentException("请求路径不能为空");
        }

        MutableLiveData<ApiResponse<T>> liveData = new MutableLiveData<>();
        TaskExecutor.get().postToMain(() -> liveData.observe(owner, observer));
        sHttpEngine.execute(mConfig, liveData);
    }

    /**
     * 开始请求网络数据
     */
    public <T> LiveData<ApiResponse<T>> execute() {
        if (TextUtils.isEmpty(mConfig.url())) {
            throw new IllegalArgumentException("请求路径不能为空");
        }
        MutableLiveData<ApiResponse<T>> liveData = new MutableLiveData<>();
        sHttpEngine.execute(mConfig, liveData);
        return liveData;
    }

}
