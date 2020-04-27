package com.mooc.network;

public class ApiResponse<T> {
    public static final int CACHE_CODE = 304;
    public static final int LOCAL_ERROR = 999;

    public int status;
    public String message;
    public T data;


    public boolean isSuccessful() {
        return status >= 200 && status < 300;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean isCached() {
        return status == CACHE_CODE;
    }
}
