package com.mooc.ppjoke.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.mooc.common.utils.Logs;
import com.mooc.common.utils.ToastUtil;
import com.mooc.network.ApiResponse;
import com.mooc.network.HttpObserver;
import com.mooc.network.http.Config;
import com.mooc.network.http.LiveHttp;
import com.mooc.ppjoke.Api;
import com.mooc.ppjoke.base.AbsViewModel;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.login.UserManager;

import java.util.Collections;
import java.util.List;

public class HomeViewModel extends AbsViewModel<Integer, Feed> {


    @Override
    public DataSource<Integer, Feed> createDataSource() {
        return new ItemKeyedDataSource<Integer, Feed>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
                loadData(getInitialLoadKey(), params.requestedLoadSize, callback);
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
        loadData(key, count, callback, false);
    }

    private void loadData(Integer key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback, boolean isAsync) {
        if (key > 0) {
            loadAfter.set(true);
        }
        Logs.d("loadData current thread: " + Thread.currentThread().getName());
        LiveHttp.create()
                .url(Api.QUERY_HOT_FEED_LIST)
                .get()
                .addParam("feedType", "all")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .isAsync(isAsync)
                .cacheStrategy(key == 0 ? Config.CACHE_FIRST : Config.NET_ONLY)
                .observe(viewLifecycleOwner, new HttpObserver<ApiResponse<List<Feed>>>() {
                    @Override
                    public void onChanged(ApiResponse<List<Feed>> apiResponse) {
                        Logs.d("thread: " + Thread.currentThread().getName() + ",  apiResponse: " + apiResponse);
                        if (apiResponse.isCached()) {
                            if (apiResponse.data != null) {
                                postToResult(apiResponse.data);
                            }
                        } else {
                            if (apiResponse.isSuccessful()) {
                                callback.onResult(apiResponse.data == null ? Collections.emptyList() : apiResponse.data);
                            } else {
                                callback.onResult(Collections.emptyList());
                                ToastUtil.showToast(apiResponse.message);
                            }
                        }
                        getBoundaryData().postValue(apiResponse.data != null && apiResponse.data.size() > 0);
                        loadAfter.compareAndSet(true, false);
                    }
                });
    }

    @Override
    protected Integer getInitialLoadKey() {
        return 0;
    }

    @Override
    public void loadAfter(@Nullable Integer key) {
        if (loadAfter.get()) {
            postToResult(Collections.emptyList());
            return;
        }
        Logs.d("loadAfter....");
        loadData(key, config.pageSize, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                postToResult(data);
            }
        }, true);
    }
}