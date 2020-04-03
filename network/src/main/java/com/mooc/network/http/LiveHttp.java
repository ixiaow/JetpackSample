package com.mooc.network.http;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.mooc.network.ApiResponse;

import java.lang.reflect.Type;
import java.util.Map;

public class LiveHttp {
    //默认引擎
    private static IHttpEngine sDefaultHttp = new OkHttpEngine();
    // 引擎参数的配置
    private Options engineOptions;

    private static String sBaseUrl;

    /**
     * 初始化引擎
     */
    public static void init(String baseUrl) {
        sBaseUrl = baseUrl;
    }

    /**
     * HttpUtils 构造方法
     */
    private LiveHttp() {
        engineOptions = new Options();
        engineOptions.baseUrl = sBaseUrl;
    }

    public static LiveHttp create() {
        return new LiveHttp();
    }

    /**
     * 设置访问的url
     *
     * @param url url
     * @return HttpsUtils
     */
    public LiveHttp url(String url) {
        this.engineOptions.url = url;
        return this;
    }


    /**
     * 设置tag 主要用来取消请求
     *
     * @param object tag
     * @return HttpUtils
     */
    public LiveHttp tag(Object object) {
        engineOptions.tag = object;
        return this;
    }

    public LiveHttp cacheStrategy(@Options.CacheStrategy int strategy) {
        engineOptions.cacheStrategy = strategy;
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param key   请求参数key
     * @param value 请求参数值
     * @return HttpUtils
     */
    public LiveHttp addParam(String key, Object value) {
        this.engineOptions.addParam(key, value);
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param params map参数集合
     * @return HttpUtils
     */
    public LiveHttp addParams(Map<String, Object> params) {
        this.engineOptions.addParams(params);
        return this;
    }

    /**
     * 添加请求header
     *
     * @param headers map 头部集合
     * @return HttpUtils
     */
    public LiveHttp addHeaders(Map<String, String> headers) {
        this.engineOptions.addHeaders(headers);
        return this;
    }

    /**
     * 添加请求header
     *
     * @param key   key
     * @param value value
     * @return HttpUtils
     */
    public LiveHttp addHeader(String key, String value) {
        this.engineOptions.addHeader(key, value);
        return this;
    }


    /**
     * 当前请求为get 方法
     *
     * @return HttpUtils
     */
    public LiveHttp get() {
        this.engineOptions.methodType = Options.GET;
        return this;
    }

    /**
     * 设置当前请求为post 方法
     *
     * @param formData 设置post的内容请求方式
     * @return HttpUtils
     */
    public LiveHttp post(FormData formData) {
        //默认设置
        this.engineOptions.methodType = Options.POST;
        this.engineOptions.addHeader(Options.CONTENT_TYPE, formData.getValue());
        return this;
    }


    /**
     * 设置当前请求为post 方法
     *
     * @return
     */
    public LiveHttp post() {
        //默认设置
        this.engineOptions.methodType = Options.POST;
        this.engineOptions.addHeader(Options.CONTENT_TYPE, FormData.FORM_DATA.getValue());
        return this;
    }

    /**
     * 设置当前请求为post 方法
     *
     * @return
     */
    public LiveHttp registerType(Type type) {
        engineOptions.type = type;
        return this;
    }

    public <T> void observe(LifecycleOwner owner, Observer<ApiResponse<T>> observer) {
        LiveData<ApiResponse<T>> liveData = execute();
        liveData.observe(owner, observer);
    }

    public <T> LiveData<ApiResponse<T>> execute() {
        if (TextUtils.isEmpty(engineOptions.url())) {
            throw new IllegalArgumentException("请求路径不能为空");
        }
        return sDefaultHttp.execute(engineOptions);
    }


    public LiveHttp async(boolean isAsync) {
        engineOptions.isAsync = isAsync;
        return this;
    }

    public static void cancel(Object object) {
        sDefaultHttp.cancel(object);
    }
}