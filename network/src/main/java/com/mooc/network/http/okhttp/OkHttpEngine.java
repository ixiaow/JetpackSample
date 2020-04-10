package com.mooc.network.http.okhttp;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.mooc.common.utils.Logs;
import com.mooc.network.ApiResponse;
import com.mooc.network.SimpleX509TrustManager;
import com.mooc.network.TaskExecutor;
import com.mooc.network.cache.CacheManager;
import com.mooc.network.http.Config;
import com.mooc.network.http.FormData;
import com.mooc.network.http.IConvert;
import com.mooc.network.http.IHttpEngine;
import com.mooc.network.http.UrlCreator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpEngine implements IHttpEngine {

    private static final OkHttpClient OK_HTTP_CLIENT;

    static {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OK_HTTP_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        TrustManager[] trustManagers = new TrustManager[]{new SimpleX509TrustManager()};
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public <T> void execute(@NonNull Config config, @NonNull MutableLiveData<ApiResponse<T>> liveData) {
        Request request = generateRequest(config);
        Call call = OK_HTTP_CLIENT.newCall(request);
        if (!config.isAsync) {
            execute(call, config, liveData);
        } else {
            enqueue(call, config, liveData);
        }
    }

    /**
     * 同步执行的方法
     */
    @SuppressWarnings("unchecked")
    private <T> void execute(Call call, Config config, MutableLiveData<ApiResponse<T>> liveData) {
        ApiResponse<T> apiResponse;
        Logs.d("execute before cache: " + Thread.currentThread().getName());
        // 只访问本地数据
        if (config.cacheStrategy == Config.CACHE_ONLY) {
            apiResponse = readCache(call.request().url().toString());
            liveData.postValue(apiResponse);
            return;
        }

        // 先访问本地数据，然后再发起网络请求
        if (config.cacheStrategy == Config.CACHE_FIRST) {
            apiResponse = readCache(call.request().url().toString());
            liveData.postValue(apiResponse);
        }

        Logs.d("execute current thread: " + Thread.currentThread().getName());
        try {
            Response response = call.execute();
            IConvert<Response, T> convert = ConvertFactory.create();
            apiResponse = convert.convert(response, config.type);
        } catch (IOException e) {
            e.printStackTrace();
            apiResponse = new ApiResponse<>();
            apiResponse.status = 500;
            apiResponse.message = e.getMessage();
        }

        if (call.isCanceled()) {
            return;
        }

        liveData.postValue(apiResponse);
        if (config.cacheStrategy != Config.NET_ONLY) {
            saveCache(call.request().url().toString(), apiResponse);
        }
    }

    private <T> void enqueue(Call call, Config config, MutableLiveData<ApiResponse<T>> liveData) {
        // 异步先发起网络请求
        if (config.cacheStrategy == Config.CACHE_ONLY) {
            TaskExecutor.get().executeOnDiskIO(() -> {
                ApiResponse<T> apiResponse = readCache(call.request().url().toString());
                liveData.postValue(apiResponse);
            });
            return;
        }

        // 先访问本地数据，然后再发起网络请求
        if (config.cacheStrategy == Config.CACHE_FIRST) {
            TaskExecutor.get().executeOnDiskIO(() -> {
                ApiResponse<T> apiResponse = readCache(call.request().url().toString());
                liveData.postValue(apiResponse);
            });
        }

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (call.isCanceled()) {
                    return;
                }
                ApiResponse<T> apiResponse = new ApiResponse<>();
                apiResponse.status = 500;
                apiResponse.message = e.getMessage();
                liveData.postValue(apiResponse);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (call.isCanceled()) {
                    return;
                }
                IConvert<Response, T> convert = ConvertFactory.create();
                ApiResponse<T> apiResponse = convert.convert(response, config.type);
                liveData.postValue(apiResponse);
                if (config.cacheStrategy != Config.NET_ONLY) {
                    saveCache(call.request().url().toString(), apiResponse);
                }
            }
        });
    }

    private <T> void saveCache(String cacheKey, ApiResponse<T> response) {
        if (response.data == null) {
            CacheManager.get().delete(cacheKey);
            return;
        }

        if (response.data instanceof Void) {
            return;
        }

        if (!(response.data instanceof Serializable)) {
            throw new IllegalArgumentException(response.data + " must implement Serializable or void");
        }
        CacheManager.get().save(cacheKey, (Serializable) response.data);
    }

    private <T> ApiResponse<T> readCache(String cacheKey) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.status = 304;
        apiResponse.data = CacheManager.get().readCache(cacheKey);
        Logs.d("cache: " + cacheKey + "=======");
        return apiResponse;
    }


    /**
     * 根据配置信息生成request
     *
     * @param config 配置信息
     * @return request
     */
    @NonNull
    private Request generateRequest(@NonNull Config config) {
        switch (config.method) {
            case Config.GET:
                return generateGetRequest(config);
            case Config.POST:
                return generatePostRequest(config);

            default:
                throw new IllegalStateException("this request method invalidate: " + config.method);
        }
    }

    /**
     * 生成post请求
     *
     * @param config http请求配置信息
     * @return 请求request
     */
    @NonNull
    private Request generatePostRequest(@NonNull Config config) {
        Request.Builder builder = new Request.Builder().url(config.url());
        addHeader(builder, config);
        // 根据提交方式添加header信息
        Pair<String, String> header = config.formData.getHeader();
        builder.addHeader(header.first, header.second);

        // 创建body
        RequestBody body = generatePostRequestBody(config);
        return builder.post(body).build();
    }

    /**
     * 获取post提交体
     *
     * @param config 请求配置信息
     * @return RequestBody
     */
    @NonNull
    private RequestBody generatePostRequestBody(@NonNull Config config) {
        FormData formData = config.formData;
        switch (formData) {
            case FORM_DATA:
                return getFormDataRequestBody(config);
            case JSON_DATA:
                return getJsonDataRequestBody(config);
            case MULTI_PART_DATA:
                return getMultiDataRequestBody(config);
            default:
                throw new IllegalArgumentException("post formData is invalidate: " + formData);

        }
    }

    /**
     * 获取复杂的post提交体
     *
     * @param config 请求配置信息
     * @return MultipartBody
     */
    @NonNull
    @SuppressWarnings("unchecked")
    private RequestBody getMultiDataRequestBody(@NonNull Config config) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (Map.Entry<String, Object> entry : config.getParams().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof File) {
                File file = (File) value;
                RequestBody requestBody = MultipartBody.create(file, getFileMediaType(file));
                builder.addFormDataPart(key, file.getName(), requestBody);
            } else if (value instanceof List) {
                List<File> files = (List<File>) value;
                for (int i = 0; i < files.size(); i++) {
                    File file = files.get(i);
                    RequestBody requestBody = MultipartBody.create(file, getFileMediaType(file));
                    builder.addFormDataPart(key + i, file.getName(), requestBody);
                }
            } else {
                builder.addFormDataPart(key, String.valueOf(value));
            }
        }
        return builder.build();
    }

    /**
     * 获取文件的type类型
     *
     * @param file 文件
     * @return MediaType
     */
    @Nullable
    private MediaType getFileMediaType(@NonNull File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(file.getAbsolutePath());
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return MediaType.parse(contentTypeFor);
    }

    /**
     * 生成json形式的post数据
     *
     * @param config 请求配置
     * @return RequestBody
     */
    @NonNull
    private RequestBody getJsonDataRequestBody(@NonNull Config config) {

        if (config.getParams().isEmpty()) {
            throw new IllegalArgumentException("json data is null");
        }
        Object json = config.getParams().get(Config.JSON_KEY);
        return RequestBody.create(String.valueOf(json), MediaType.parse(config.formData.getValue()));
    }


    /**
     * 生成form data形式的post数据
     *
     * @param config 请求配置
     * @return FromBody
     */
    @NonNull
    private RequestBody getFormDataRequestBody(@NonNull Config config) {
        FormBody.Builder builder = new FormBody.Builder(StandardCharsets.UTF_8);
        Map<String, Object> params = config.getParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.addEncoded(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return builder.build();
    }

    /**
     * 生成get方式的请求
     *
     * @param config 请求配置
     * @return 返回get方式的request
     */
    @NonNull
    private Request generateGetRequest(@NonNull Config config) {
        Request.Builder builder = new Request.Builder().get();
        addHeader(builder, config);
        String url = UrlCreator.generateUrlForParams(config.url(), config.getParams());
        return builder.url(url).build();
    }


    /**
     * 添加header信息
     *
     * @param builder okHttp builder
     * @param config  请求参数信息
     */
    private void addHeader(@NonNull Request.Builder builder, @NonNull Config config) {
        for (Map.Entry<String, String> entry : config.getHeaders().entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }


    @Override
    public void cancel(@NonNull Object tag) {
        if (OK_HTTP_CLIENT == null) {
            return;
        }

        //查找当前需要取消的tag是否在未执行的请求中
        for (Call call : OK_HTTP_CLIENT.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

        //查找当前需要请求的tag是否在正在执行的请求中
        for (Call call : OK_HTTP_CLIENT.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}
