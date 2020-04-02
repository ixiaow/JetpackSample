package com.mooc.network;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseRequest<T> implements IRequest<T> {

    private Builder<T> mBuilder;

    public BaseRequest(Builder<T> builder) {
        this.mBuilder = builder;
    }

    @Override
    public void enqueue(JsonCallback<T> callback) {
        Call call = newCall();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }

    @NotNull
    @Override
    public ApiResponse<T> execute() {

        Call call = newCall();
        ApiResponse<T> apiResponse;
        try {
            Response response = call.execute();
            apiResponse = parseResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            apiResponse = new ApiResponse<>();
            apiResponse.message = e.getMessage();
            apiResponse.status = 500;
        }
        return apiResponse;
    }

    @NonNull
    private ApiResponse<T> parseResponse(Response response) {
        return null;
    }

    private Call newCall() {
        Request.Builder builder = new Request.Builder();
        addHeader(builder);
        Request request = generateParams(builder, mBuilder.params);
        return ApiService.sOkHttpClient.newCall(request);
    }

    public abstract Request generateParams(Request.Builder builder, Map<String, Object> params);

    private void addHeader(Request.Builder builder) {
        Map<String, String> headers = mBuilder.headers;
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.addHeader(header.getKey(), header.getValue());
        }
    }

    public static class Builder<T> {

        private static final int GET = 0;
        private static final int POST = 1;

        @IntDef({GET, POST})
        @Retention(RetentionPolicy.SOURCE)
        @interface Method {
        }

        private Class<T> type;
        private String url;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, Object> params = new HashMap<>();

        @Method
        private int method = GET;

        public Builder<T> registerType(Class<T> type) {
            this.type = type;
            return this;
        }

        public Builder<T> url(String url) {
            this.url = url;
            return this;
        }

        public Builder<T> addHeader(String name, String value) {
            if (!headers.containsKey(name)) {
                headers.put(name, value);
            }
            return this;
        }

        public Builder<T> get() {
            this.method = GET;
            return this;
        }

        public Builder<T> post() {
            this.method = POST;
            return this;
        }

        public BaseRequest<T> build() {
            if (method == GET) {
                return new GetRequest<>(this);
            }
            return new PostRequest<>(this);
        }
    }
}
