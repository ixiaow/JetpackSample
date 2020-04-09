package com.mooc.ppjoke.view.player;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PageListPlayDetector {
    private List<IPlayTarget> targets = new ArrayList<>();
    private RecyclerView recyclerView;
    private IPlayTarget currentTarget;
    private String category;

    public void addTarget(IPlayTarget target) {
        target.setCategory(category);
        targets.add(target);
    }

    public void removeTarget(IPlayTarget target) {
        targets.remove(target);
    }

    public PageListPlayDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    currentTarget = null;
                    targets.clear();
                    recyclerView.removeCallbacks(delayAutoPlay);
                    recyclerView.removeOnScrollListener(scrollListener);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });

        recyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);
        recyclerView.addOnScrollListener(scrollListener);
    }

    private Runnable delayAutoPlay = this::autoPlay;

    private void autoPlay() {
        if (targets.size() <= 0 || recyclerView.getChildCount() <= 0) {
            return;
        }

        if (currentTarget != null && currentTarget.isPlaying() && isTargetInBounds(currentTarget)) {
            return;
        }

        IPlayTarget playTarget = null;
        for (IPlayTarget target : targets) {
            boolean inBounds = isTargetInBounds(target);
            if (inBounds) {
                playTarget = target;
                break;
            }
        }

        if (playTarget != null) {
            if (currentTarget != null) {
                currentTarget.inActive();
            }
            currentTarget = playTarget;
            playTarget.onActive();
        }
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay();
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dx == 0 && dy == 0) {
                postAutoPlay();
            } else {
                if (currentTarget != null && currentTarget.isPlaying() && !isTargetInBounds(currentTarget)) {
                    currentTarget.inActive();
                }
            }
        }
    };

    private boolean isTargetInBounds(IPlayTarget target) {
        ViewGroup owner = target.getOwner();
        ensureRecyclerViewLocation();

        if (!owner.isShown() && !owner.isAttachedToWindow()) {
            return false;
        }

        int[] location = new int[2];
        owner.getLocationOnScreen(location);

        int center = location[1] + owner.getHeight() / 2;
        return center > rvLocation.first && center < rvLocation.second;
    }

    private Pair<Integer, Integer> rvLocation;

    private void ensureRecyclerViewLocation() {
        if (rvLocation == null) {
            int[] location = new int[2];
            recyclerView.getLocationOnScreen(location);
            int top = location[1];
            int bottom = top + recyclerView.getHeight();
            rvLocation = new Pair<>(top, bottom);
        }
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            postAutoPlay();
        }
    };

    private void postAutoPlay() {
        recyclerView.post(delayAutoPlay);
    }

    public void onPause() {
        if (currentTarget != null) {
            currentTarget.inActive();
        }
    }

    public void onResume() {
        if (currentTarget != null) {
            currentTarget.onActive();
        }
    }

    public void onDestroy() {
        PageListManager.release(category);
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
