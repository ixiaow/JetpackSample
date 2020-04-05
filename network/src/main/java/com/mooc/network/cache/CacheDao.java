package com.mooc.network.cache;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Cache cache);

    @Delete
    void delete(Cache cache);

    @Query("Select * from cache where `key`=:key")
    Cache query(@NonNull String key);
}
