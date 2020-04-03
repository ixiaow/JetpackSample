package com.mooc.network.http;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Options {
    public static final int GET = 0x0001;
    public static final int POST = 0x0002;

    public static final String CONTENT_TYPE = "Content-Type";

    //仅仅只访问本地缓存，即便本地缓存不存在，也不会发起网络请求
    public static final int CACHE_ONLY = 1;
    //先访问缓存，同时发起网络的请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    //仅仅只访问服务器，不存任何存储
    public static final int NET_ONLY = 3;
    //先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;


    @IntDef({GET, POST})
    @Retention(RetentionPolicy.SOURCE)
    @interface MethodOption {
    }

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    @Retention(RetentionPolicy.SOURCE)
    @interface CacheStrategy {
    }

    //Tag
    public Object tag;
    //请求的url
    public String url;
    public String baseUrl;
    public Type type = Void.class;
    public boolean isAsync = true;
    // 请求的方法
    @MethodOption
    public int methodType = GET;

    @CacheStrategy
    public int cacheStrategy = NET_CACHE;
    // 请求参数
    private Map<String, Object> params;
    // 请求头
    private Map<String, String> headers;

    public Options() {
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public void addParam(String name, Object value) {
        if (!params.containsKey(name)) {
            params.put(name, value);
        }
    }

    public void addParams(Map<String, Object> params) {
        params.putAll(params);
    }

    public void addHeaders(Map<String, String> headers) {
        headers.putAll(headers);
    }

    public void addHeader(String key, String value) {
        if (!headers.containsKey(key)) {
            headers.put(key, value);
        }
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Object getParam(String name) {
        return params.get(name);
    }


    public String url() {
        return baseUrl + url;
    }
}
