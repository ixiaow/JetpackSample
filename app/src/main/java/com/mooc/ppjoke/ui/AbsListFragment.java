package com.mooc.ppjoke.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.common.utils.Logs;
import com.mooc.common.view.EmptyView;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutListFragmentBinding;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbsListFragment<Key, Value, VM extends AbsViewModel<Key, Value>>
        extends Fragment implements OnRefreshLoadMoreListener {

    protected LayoutListFragmentBinding fragmentBinding;
    protected SmartRefreshLayout smartRefreshLayout;
    protected RecyclerView recyclerView;
    protected EmptyView emptyView;

    protected PagedListAdapter<Value, ? extends RecyclerView.ViewHolder> adapter;
    private DividerItemDecoration itemDecoration;

    protected VM viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 加载布局
        fragmentBinding = LayoutListFragmentBinding.inflate(inflater, container, false);
        smartRefreshLayout = fragmentBinding.refreshLayout;
        recyclerView = fragmentBinding.recyclerView;
        emptyView = fragmentBinding.emptyView;

        // 设置刷新
        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setOnRefreshLoadMoreListener(this);

        // 获取adapter
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(null);

        //默认给列表中的Item 一个 10dp的ItemDecoration
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.shape_list_divider);
        if (drawable != null) {
            itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
            itemDecoration.setDrawable(drawable);
            recyclerView.addItemDecoration(itemDecoration);
        }
        genericViewModel();
        return fragmentBinding.getRoot();
    }

    @SuppressWarnings("unchecked")
    private void genericViewModel() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        if (arguments.length > 2) {
            Type argument = arguments[2];
            Class modelClass = ((Class) argument).asSubclass(AbsViewModel.class);
            try {
                viewModel = (VM) modelClass.newInstance();
                viewModel.setLifeOwner(getViewLifecycleOwner());
                viewModel.getPageData().observe(getViewLifecycleOwner(), this::submitList);
                viewModel.getBoundaryData().observe(getViewLifecycleOwner(), this::finishRefresh);
            } catch (IllegalAccessException | java.lang.InstantiationException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("实例化viewModel出错！");
            }
        }
    }

    private void finishRefresh(Boolean hasData) {
        Logs.d("hasData: " + hasData);
        PagedList<Value> currentList = adapter.getCurrentList();
        hasData = hasData || currentList != null && currentList.size() > 0;
        RefreshState state = smartRefreshLayout.getState();
        if (state.isFooter && state.isOpening) {
            smartRefreshLayout.finishLoadMore();
        } else if (state.isHeader && state.isOpening) {
            smartRefreshLayout.finishRefresh();
        }
        emptyView.setVisibility(hasData ? View.GONE : View.VISIBLE);
    }

    private void submitList(PagedList<Value> values) {
        boolean hasData = values.size() > 0;
        if (hasData) {
            adapter.submitList(values);
        }
        finishRefresh(hasData);
    }

    public abstract PagedListAdapter<Value, ? extends RecyclerView.ViewHolder> getAdapter();


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        viewModel.getDataSource().invalidate();
    }
}
