package com.mooc.network.http.okhttp;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mooc.network.ApiResponse;
import com.mooc.network.http.IConvert;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class ConvertFactory<R> implements IConvert<Response, R> {
    private static ConvertFactory convertFactory;


    public static ConvertFactory create() {
        if (convertFactory == null) {
            convertFactory = new ConvertFactory<>();
        }
        return convertFactory;
    }


    @NonNull
    @Override
    public ApiResponse<R> convert(@NonNull Response response, @NonNull Type type) {
        ApiResponse<R> apiResponse = new ApiResponse<>();
        apiResponse.status = response.code();
        if (!response.isSuccessful()) {
            apiResponse.message = response.message();
            return apiResponse;
        }

        ResponseBody body = response.body();
        if (body != null && type != Void.TYPE) {
            try {
                String content = body.string();
                if (!TextUtils.isEmpty(content)) {
                    JSONObject jsonObject = JSON.parseObject(content);
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data != null) {
                        Object object = data.get("data");
                        apiResponse.data = JSON.parseObject(String.valueOf(object), type);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                apiResponse.status = ApiResponse.LOCAL_ERROR;
                apiResponse.message = e.getMessage();
            }
        }
        return apiResponse;
    }
}
