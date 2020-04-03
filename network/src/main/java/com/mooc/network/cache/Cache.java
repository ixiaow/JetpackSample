package com.mooc.network.cache;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cache")
public class Cache {
    @PrimaryKey
    @NonNull
    public String key;
    public byte[] data;
}
