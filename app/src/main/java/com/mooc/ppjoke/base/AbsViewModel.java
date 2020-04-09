package com.mooc.ppjoke.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbsViewModel<Key, Value> extends ViewModel {

    private final LiveData<PagedList<Value>> pageData;
    private MutableLiveData<Boolean> boundaryData = new MutableLiveData<>();
    private DataSource<Key, Value> dataSource;
    protected PagedList.Config config;
    protected LifecycleOwner viewLifecycleOwner;
    private final MutableLiveData<PagedList<Value>> resultData = new MutableLiveData<>();
    protected final AtomicBoolean loadAfter = new AtomicBoolean(false);

    public AbsViewModel() {

        config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(12)
                .build();

        pageData = new LivePagedListBuilder<>(factory, config)
                .setInitialLoadKey(getInitialLoadKey())
                .setBoundaryCallback(callback)
                .build();
    }


    private DataSource.Factory<Key, Value> factory = new DataSource.Factory<Key, Value>() {
        @NonNull
        @Override
        public DataSource<Key, Value> create() {
            if (dataSource == null || dataSource.isInvalid()) {
                dataSource = createDataSource();
            }
            return dataSource;
        }
    };

    public abstract DataSource<Key, Value> createDataSource();

    protected abstract Key getInitialLoadKey();

    private PagedList.BoundaryCallback<Value> callback = new PagedList.BoundaryCallback<Value>() {
        @Override
        public void onZeroItemsLoaded() {
            // 新提交的pageList中没有数据
            boundaryData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull Value itemAtFront) {
            // 新提交的pageList中的第一条数据被加载到列表上
            boundaryData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull Value itemAtEnd) {
            // 新提交的pageList中的最后一条数据被加载到列表上
        }
    };

    public DataSource<Key, Value> getDataSource() {
        return dataSource;
    }

    public MutableLiveData<Boolean> getBoundaryData() {
        return boundaryData;
    }

    public void setLifeOwner(LifecycleOwner viewLifecycleOwner) {
        this.viewLifecycleOwner = viewLifecycleOwner;
        pageData.observe(viewLifecycleOwner, resultData::postValue);
    }

    public LiveData<PagedList<Value>> getResultData() {
        return resultData;
    }

    public abstract void loadAfter(@Nullable Key key);

    @SuppressWarnings("unchecked")
    public void loadAfter(@Nullable PagedList<Value> currentList) {
        Key key = null;
        if (currentList != null) {
            key = (Key) currentList.getLastKey();
        }
        if (loadAfter.get()) {
            postToResult(Collections.emptyList());
            return;
        }
        loadAfter(key);
    }

    protected void postToResult(List<Value> data) {
        MutablePagedKeyDataSource<Key, Value> dataSource = new MutablePagedKeyDataSource<>();
        PagedList<Value> pagedList = dataSource.buildNewPagedList(config);
        dataSource.addList(data);
        resultData.postValue(pagedList);
    }
}
