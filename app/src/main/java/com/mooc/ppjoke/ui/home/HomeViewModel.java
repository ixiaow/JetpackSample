package com.mooc.ppjoke.ui.home;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.mooc.common.utils.Logs;
import com.mooc.network.ApiResponse;
import com.mooc.network.HttpObserver;
import com.mooc.network.http.Config;
import com.mooc.network.http.LiveHttp;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.AbsViewModel;

import java.util.Collections;
import java.util.List;

public class HomeViewModel extends AbsViewModel<Integer, Feed> {

    @Override
    public DataSource<Integer, Feed> createDataSource() {
        return new ItemKeyedDataSource<Integer, Feed>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
                loadData(params.requestedInitialKey, params.requestedLoadSize, callback);
            }

            @Override
            public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
                loadData(params.key, params.requestedLoadSize, callback);
            }

            @Override
            public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
                callback.onResult(Collections.emptyList());
            }

            @NonNull
            @Override
            public Integer getKey(@NonNull Feed item) {
                return item.id;
            }
        };
    }

    private void loadData(Integer key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        Logs.d("loadData current thread: " + Thread.currentThread().getName());
        LiveHttp.create()
                .url("/feeds/queryHotFeedsList")
                .get()
                .addParam("feedType", "all")
                .addParam("userId", 0)
                .addParam("feedId", key)
                .isAsync(false)
                .addParam("pageCount", count)
                .cacheStrategy(key == 0 ? Config.CACHE_FIRST : Config.NET_ONLY)
                .observe(viewLifecycleOwner, new HttpObserver<ApiResponse<List<Feed>>>() {
                    @Override
                    public void onChanged(ApiResponse<List<Feed>> apiResponse) {
                        Logs.d("apiResponse: " + apiResponse);
                        callback.onResult(apiResponse.data == null ? Collections.emptyList() : apiResponse.data);
                        getBoundaryData().postValue(apiResponse.data != null && apiResponse.data.size() > 0);
                    }
                });
    }

    @Override
    protected Integer getInitialLoadKey() {
        return 0;
    }
}