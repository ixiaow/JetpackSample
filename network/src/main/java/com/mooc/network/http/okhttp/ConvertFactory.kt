package com.mooc.network.http.okhttp

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.mooc.network.ApiResponse
import com.mooc.network.LOCAL_ERROR
import com.mooc.network.http.IConvert
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

class ConvertFactory<R> : IConvert<Response, R> {
    override fun convert(
        response: Response,
        type: Type
    ): ApiResponse<R> {
        val apiResponse = ApiResponse<R>()
        apiResponse.status = response.code
        if (!response.isSuccessful) {
            apiResponse.message = response.message
            return apiResponse
        }
        val body = response.body
        if (body != null && type !== Void.TYPE) {
            try {
                val content = body.string()
                if (!TextUtils.isEmpty(content)) {
                    val jsonObject = JSON.parseObject(content)
                    val data = jsonObject.getJSONObject("data")
                    if (data != null) {
                        val `object` = data["data"]
                        apiResponse.data = JSON.parseObject(`object`.toString(), type)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                apiResponse.status = LOCAL_ERROR
                apiResponse.message = e.message
            }
        }
        return apiResponse
    }

    companion object {
        private var convertFactory: ConvertFactory<*>? = null

        @JvmStatic
        fun create(): ConvertFactory<*> {
            if (convertFactory == null) {
                convertFactory = ConvertFactory<Any>()
            }
            return convertFactory as ConvertFactory<*>
        }
    }
}