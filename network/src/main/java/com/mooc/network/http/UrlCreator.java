package com.mooc.network.http;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UrlCreator {

    /**
     * 根据params拼接url
     *
     * @param url    请求的url
     * @param params 请求的参数
     * @return 已拼接的url
     */
    public static String generateUrlForParams(@NonNull String url, @NonNull Map<String, Object> params) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url must not be null");
        }

        if (params.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);

        if (url.indexOf("&") > 0 || url.indexOf("?") > 0) {
            sb.append("&");
        } else {
            sb.append("?");
        }

        int initLength = sb.length();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (sb.length() > initLength) {
                sb.append("&");
            }
            try {
                String value = URLEncoder.encode(String.valueOf(entry.getValue()),
                        StandardCharsets.UTF_8.toString());
                sb.append(entry.getKey()).append("=").append(value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
