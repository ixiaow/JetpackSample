package com.mooc.ppjoke.ui;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;
import java.util.List;

public class MutablePagedKeyDataSource<Key, Value> extends PageKeyedDataSource<Key, Value> {
    private List<Value> list;

    public MutablePagedKeyDataSource() {
        this.list = new ArrayList<>();

    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<Key, Value> callback) {

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Key, Value> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Key, Value> callback) {

    }
}
