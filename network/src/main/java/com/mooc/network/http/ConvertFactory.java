package com.mooc.network.http;

import androidx.annotation.NonNull;

import com.mooc.common.utils.Logs;
import com.mooc.network.ApiResponse;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import okhttp3.Response;

public class ConvertFactory<T> implements IConvert<ApiResponse<T>> {
    private static ConvertFactory convertFactory;


    public static ConvertFactory create() {
        if (convertFactory == null) {
            convertFactory = new ConvertFactory();
        }
        return convertFactory;
    }

    @NonNull
    @Override
    public ApiResponse<T> convert(@NonNull Response response, @NotNull Type type) {
        Logs.d("resposne: " + response.code() + ", type: " + type);

        return new ApiResponse<>();
    }
}
