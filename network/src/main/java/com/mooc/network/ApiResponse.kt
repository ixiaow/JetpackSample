package com.mooc.network

const val CACHE_CODE = 304
const val LOCAL_ERROR = 999

data class ApiResponse<T>(var status: Int, var message: String? = null, var data: T? = null)

val ApiResponse<*>.isSuccessful: Boolean
    get() = status in 200 until 300

val ApiResponse<*>.isCached: Boolean
    get() = status == CACHE_CODE