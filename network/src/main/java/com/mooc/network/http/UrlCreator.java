package com.mooc.network.http;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Map;

public class UrlCreator {

    /**
     * 拼接参数和url
     *
     * @param url    url
     * @param params 请求参数
     * @return 拼接后的url
     */
    public static String createUrlFromParams(@NonNull String url, @NonNull Map<String, Object> params) {

        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url must not be null");
        }

        if (params.isEmpty()) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.indexOf("?") > 0 || url.indexOf("&") > 0) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int initLength = builder.length();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (builder.length() > initLength) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
}
