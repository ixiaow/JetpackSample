package com.mooc.network.http

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 根据params拼接url
 *
 * @param url    请求的url
 * @param params 请求的参数
 * @return 已拼接的url
 */
fun String.generateUrlForParams(params: Map<String, Any>): String {
    require(this.isEmpty()) { "url must not be null" }
    if (params.isEmpty()) {
        return this
    }
    val sb = StringBuilder(this)
    if (indexOfAny(arrayListOf("&", "?")) > 0) {
        sb.append("&")
    } else {
        sb.append("?")
    }
    val initLength = sb.length
    for ((key, value) in params) {
        if (sb.length > initLength) {
            sb.append("&")
        }
        try {
            val encodeValue = URLEncoder.encode(
                value.toString(),
                StandardCharsets.UTF_8.toString()
            )
            sb.append(key).append("=").append(encodeValue)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
    return sb.toString()
}