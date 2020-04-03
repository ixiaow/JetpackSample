package com.mooc.network;

public class ApiResponse<T> {
    public int status;
    public String message;
    public T data;


    public boolean isSuccessful() {
        return status >= 200 && status < 300;
    }
}
