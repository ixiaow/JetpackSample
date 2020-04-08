package com.mooc.ppjoke.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.navannotation.FragmentDestination;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.AbsListFragment;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Integer, Feed, HomeViewModel> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getCacheLiveData().observe(getViewLifecycleOwner(), this::submitList);
    }

    @Override
    public PagedListAdapter<Feed, ? extends RecyclerView.ViewHolder> getAdapter() {
        return new HomePageAdapter(requireActivity());
    }
}
