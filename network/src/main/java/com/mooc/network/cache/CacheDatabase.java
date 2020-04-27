package com.mooc.network.cache;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mooc.common.utils.AppGlobals;

@Database(entities = Cache.class, version = 1, exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase cacheDatabase;

    static {
        Application application = AppGlobals.getApplication();
        cacheDatabase = Room.databaseBuilder(application, CacheDatabase.class, "net_cache.db")
                .allowMainThreadQueries()
                .build();
    }

    public static CacheDatabase get() {
        return cacheDatabase;
    }

    public abstract CacheDao getCacheDao();
}
