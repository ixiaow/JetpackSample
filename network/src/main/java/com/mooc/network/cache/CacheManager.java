package com.mooc.network.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.common.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CacheManager {

    private static CacheManager cacheManager;

    public static CacheManager get() {
        if (cacheManager == null) {
            synchronized (CacheManager.class) {
                if (cacheManager == null) {
                    cacheManager = new CacheManager();
                }
            }
        }
        return cacheManager;
    }

    public <T extends Serializable> void save(@NonNull String cacheKey, @Nullable T data) {

        // 如果data 为空则清除掉该条记录就好
        Cache cache = new Cache();
        cache.key = cacheKey;
        cache.data = toObjectArray(data);
        CacheDatabase.get().getCacheDao().insert(cache);
    }

    public void delete(@NonNull String cacheKey) {
        Cache cache = new Cache();
        cache.key = cacheKey;
        CacheDatabase.get().getCacheDao().delete(cache);
    }

    @Nullable
    public <T> T readCache(@NonNull String cacheKey) {
        Cache cache = CacheDatabase.get().getCacheDao().query(cacheKey);
        if (cache == null || cache.data == null) {
            return null;
        }
        return toObject(cache.data);

    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T> T toObject(byte[] data) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(ois, bais);
        }
        return null;
    }

    /**
     * 将object转换为byte数组
     */
    private <T extends Serializable> byte[] toObjectArray(T data) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(oos, baos);
        }
        return new byte[0];
    }
}
