package com.mooc.network.http;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mooc.network.ApiResponse;
import com.mooc.network.SimpleX509TrustManager;
import com.mooc.network.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
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
    private static OkHttpClient sHttpClient;

    static {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        sHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        // 设置https证书问题
        TrustManager[] trustManagers = new TrustManager[]{new SimpleX509TrustManager()};
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }


    @Override
    public <T> LiveData<ApiResponse<T>> execute(Options options) {
        MutableLiveData<ApiResponse<T>> liveData = new MutableLiveData<>();
        Request request = generateRequest(options);
        Call call = sHttpClient.newCall(request);

        if (call.isCanceled()) {
            return liveData;
        }

        switch (options.cacheStrategy) {
            case Options.CACHE_ONLY: {
                // 读取缓存
                ApiResponse<T> response = readCache(call.request().url().toString());
                liveData.postValue(response);
            }// 读取网络
            case Options.CACHE_FIRST: {
                // 1. 读取缓存
                ApiResponse<T> response = readCache(call.request().url().toString());
                liveData.postValue(response);
                // 2. 读取网络
                netRequest(options, liveData, call);
                break;
            }
            default:
                netRequest(options, liveData, call);
                break;
        }
        return liveData;
    }


    private <T> void netRequest(Options options, MutableLiveData<ApiResponse<T>> liveData, Call call) {
        if (options.isAsync) {
            doAsync(call, options, liveData);
        } else {
            doSync(call, options, liveData);
        }
    }

    private <T> void doSync(Call call, Options options, MutableLiveData<ApiResponse<T>> liveData) {
        ApiResponse<T> apiResponse;
        try {
            Response response = call.execute();
            apiResponse = parseResponse(options, response);
        } catch (IOException e) {
            e.printStackTrace();
            apiResponse = new ApiResponse<>();
            apiResponse.status = 500;
            apiResponse.message = e.getMessage();
        }
        liveData.postValue(apiResponse);
        if (options.cacheStrategy == Options.NET_ONLY && apiResponse.isSuccessful()) {
            saveCache(call.request().url().toString(), apiResponse.data);
        }
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private <T> ApiResponse<T> parseResponse(Options options, Response response) {
        ConvertFactory convertFactory = ConvertFactory.create();
        return convertFactory.convert(response, options.type);
    }

    private <T> void doAsync(@NonNull Call call, @NonNull final Options options,
                             @NonNull final MutableLiveData<ApiResponse<T>> liveData) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (call.isCanceled()) {
                    return;
                }
                ApiResponse<T> response = new ApiResponse<>();
                response.status = 500;
                response.message = e.getMessage();
                liveData.postValue(response);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (call.isCanceled()) {
                    return;
                }
                ApiResponse<T> apiResponse = parseResponse(options, response);
                liveData.postValue(apiResponse);
                if (options.cacheStrategy == Options.NET_ONLY && apiResponse.isSuccessful()) {
                    saveCache(call.request().url().toString(), apiResponse.data);
                }
            }
        });
    }

    private <T> void saveCache(@NonNull String cacheKey, @Nullable T data) {
        if (data == null) {
            CacheManager.get().delete(cacheKey);
        }

        if (data instanceof Void) {
            return;
        }

        if (!(data instanceof Serializable)) {
            throw new IllegalArgumentException(data + " must implement Serializable or void");
        }
        CacheManager.get().save(cacheKey, (Serializable) data);
    }

    @NonNull
    private <T> ApiResponse<T> readCache(@NonNull String cacheKey) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.status = 304;
        return apiResponse;
    }

    @NonNull
    private Request generateRequest(Options options) {
        switch (options.methodType) {
            case Options.GET:
                return generateGetRequest(options);

            case Options.POST:
                return generatePostRequest(options);

            default:
                throw new IllegalArgumentException("无法识别的请求方式");
        }
    }


    /**
     * 创建GET方式的请求
     */
    @NonNull
    private Request generateGetRequest(Options options) {
        String url = UrlCreator.createUrlFromParams(options.url(), options.getParams());
        Request.Builder builder = new Request.Builder().get().url(url);
        addHeaders(builder, options.getHeaders());
        if (options.tag != null) {
            builder.tag(options.tag);
        }
        return builder.build();
    }


    @NonNull
    private Request generatePostRequest(Options options) {
        String header = options.getHeader(Options.CONTENT_TYPE);
        Request.Builder builder = new Request.Builder();
        addHeaders(builder, options.getHeaders());
        if (FormData.JSON_DATA.getValue().equals(header)) {
            Object json = options.getParam("json");
            if (json == null) {
                throw new IllegalArgumentException("请将json 加入到key 为 json的params中");
            }
            RequestBody body = RequestBody.create(String.valueOf(json), MediaType.parse(header));
            return builder.post(body).build();
        } else if (FormData.FORM_DATA.getValue().equals(header)) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : options.getParams().entrySet()) {
                bodyBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
            return builder.post(bodyBuilder.build()).build();
        } else {
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            addMultiParams(bodyBuilder, options.getParams());
            return builder.post(bodyBuilder.build()).build();
        }
    }

    /**
     * 添加header
     */
    private void addHeaders(Request.Builder builder, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }


    @SuppressWarnings("unchecked")
    private void addMultiParams(MultipartBody.Builder bodyBuilder, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof File) {
                File file = (File) value;
                RequestBody body = MultipartBody.create(file,
                        getFileMediaType(file.getAbsolutePath()));
                bodyBuilder.addFormDataPart(key, ((File) value).getName(), body);
            } else if (value instanceof List) {
                List<File> fileList = (List<File>) value;
                for (int i = 0; i < fileList.size(); i++) {
                    File file = fileList.get(i);
                    bodyBuilder.addFormDataPart(key + i, file.getName(),
                            MultipartBody.create(file, getFileMediaType(file.getAbsolutePath())));
                }
            } else {
                bodyBuilder.addFormDataPart(key, String.valueOf(value));
            }
        }
    }

    private MediaType getFileMediaType(String filePath) {

        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(filePath);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return MediaType.parse(contentTypeFor);
    }

    @Override
    public void cancel(Object tag) {

        if (sHttpClient == null) {
            return;
        }

        //查找当前需要取消的tag是否在未执行的请求中
        for (Call call : sHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

        //查找当前需要请求的tag是否在正在执行的请求中
        for (Call call : sHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}