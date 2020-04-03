package com.mooc.ppjoke.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mooc.common.utils.AppGlobals;
import com.mooc.common.utils.IOUtils;
import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.model.MainTabs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private static final String DEST_FILE_NAME = "destination.json";
    private static final String MAIN_TABS_FILE_NAME = "main_tabs_config.json";

    private static Map<String, Destination> destinationMap;
    private static MainTabs mainTabs;


    @Nullable
    public static MainTabs getMainTabs() {
        if (mainTabs == null) {
            String config = parseConfigFile(MAIN_TABS_FILE_NAME);
            mainTabs = JSON.parseObject(config, MainTabs.class);
            if (mainTabs != null) {
                Collections.sort(mainTabs.tabs, (o1, o2) -> Integer.compare(o1.index, o2.index));
            }
        }
        return mainTabs;
    }


    /**
     * 获取导航配置文件
     */
    public static Map<String, Destination> getDestConfig() {
        if (destinationMap == null) {
            destinationMap = new HashMap<>();
            String content = parseConfigFile(DEST_FILE_NAME);
            if (!TextUtils.isEmpty(content)) {
                HashMap<String, Destination> destMap = JSON.parseObject(content,
                        new TypeReference<HashMap<String, Destination>>() {
                        });

                if (destMap != null) {
                    destinationMap = destMap;
                }
            }
        }
        return destinationMap;
    }

    @Nullable
    private static String parseConfigFile(String fileName) {
        InputStream is = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
        try {
            is = AppGlobals.getApplication().getAssets().open(fileName);
            ir = new InputStreamReader(is);
            br = new BufferedReader(ir);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(br, ir, is);
        }
        return null;
    }
}
