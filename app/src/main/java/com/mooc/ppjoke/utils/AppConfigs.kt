package com.mooc.ppjoke.utils

import androidx.collection.ArrayMap
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.mooc.common.utils.AppGlobals
import com.mooc.ppjoke.model.Destination
import com.mooc.ppjoke.model.MainTabs

object AppConfigs {
    private const val DESTINATION_NAME = "destination.json"
    private const val MAIN_TAB_NAME = "main_tabs_config.json"

    var destMap: Map<String, Destination>? = null
        private set
        get() = field ?: run {
            val content = readAssetsFile(DESTINATION_NAME)
            val map = JSONObject.parseObject(
                content,
                object : TypeReference<Map<String, Destination>>() {})
            field = map ?: ArrayMap()
            field
        }

    var mainTabs: MainTabs? = null
        private set
        get() = field ?: run {
            val content = readAssetsFile(MAIN_TAB_NAME)
            field = JSONObject.parseObject(content, MainTabs::class.java)?.apply {
                tabs?.sortBy { it.index }
            }
            field
        }

    private fun readAssetsFile(fileName: String): String {
        return AppGlobals.getApplication().assets.open(fileName)
            .use { stream -> stream.reader().use { it.readText() } }
    }

}