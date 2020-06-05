package com.mooc.ppjoke.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.annotation.Destination;
import com.mooc.common.player.ListPlayDetector;
import com.mooc.ppjoke.base.AbsListFragment;
import com.mooc.ppjoke.model.Feed;

@Destination.Fragment(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Integer, Feed, HomeViewModel> {

    private ListPlayDetector pageListPlayDetector;
    private boolean shouldPause;
    private String feedType;

    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        args.putString("feedType", feedType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageListPlayDetector = new ListPlayDetector(getViewLifecycleOwner(), recyclerView);
        pageListPlayDetector.setCategory(feedType);
    }

    @Override
    public PagedListAdapter<Feed, ? extends RecyclerView.ViewHolder> getAdapter() {
        feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new HomePageAdapter(requireActivity()) {
            @Override
            public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if (holder.isVideoItem()) {
                    pageListPlayDetector.addTarget(holder.getPlayListView());
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                if (holder.isVideoItem()) {
                    pageListPlayDetector.removeTarget(holder.getPlayListView());
                }
            }
        };
    }

    @Override
    public void onPause() {
        if (shouldPause) {
            pageListPlayDetector.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldPause = true;
        if (getParentFragment() != null) {
            if (getParentFragment().isVisible() && isVisible()) {
                pageListPlayDetector.onResume();
            }
        } else {
            if (isVisible()) {
                pageListPlayDetector.onResume();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            pageListPlayDetector.onPause();
        } else {
            pageListPlayDetector.onResume();
        }
    }

    @Override
    public void onDestroy() {
        pageListPlayDetector.onDestroy();
        super.onDestroy();
    }
}
