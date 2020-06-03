package com.mooc.network.http;

import android.util.Pair;

/**
 * post表单提交的几种方式
 */
public enum FormData {
    FORM_DATA("application/x-www-form-urlencoded;charset=utf-8"),
    JSON_DATA("application/json;charset=utf-8"),
    MULTI_PART_DATA("multipart/form-data;charset=utf-8");

    private String value;

    FormData(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 获取Content-Type header
     * @return Pair类型的数据 first为"Content-Type", second 为 具体的提交类型header
     */
    public Pair<String, String> getHeader() {
        return new Pair<>("Content-Type", getValue());
    }
}