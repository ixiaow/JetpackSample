package com.mooc.common.player;

import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListPlayDetector {
    private final List<IPlayTarget> playTargets = new ArrayList<>();
    private RecyclerView recyclerView;
    private IPlayTarget currentPlayTarget = null;
    private String category;

    public void addTarget(IPlayTarget target) {
        playTargets.add(target);
        target.setCategory(category);
    }

    public void removeTarget(IPlayTarget target) {
        playTargets.remove(target);
    }

    public ListPlayDetector(@NonNull LifecycleOwner owner, @NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    playTargets.clear();
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.unregisterAdapterDataObserver(dataObserver);
                    }
                    recyclerView.removeOnScrollListener(scrollListener);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("adapter must be not null");
        }
        adapter.registerAdapterDataObserver(dataObserver);
        recyclerView.addOnScrollListener(scrollListener);
    }

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            postAutoPlay();
        }
    };

    private void postAutoPlay() {
        recyclerView.post(delayAutoPlay);
    }

    private final Runnable delayAutoPlay = this::autoPlay;

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
                if (currentPlayTarget != null
                        && currentPlayTarget.isPlaying()
                        && !isTargetInBounds(currentPlayTarget)) {
                    currentPlayTarget.inActive();
                }
            }
        }
    };

    private void autoPlay() {
        if (playTargets.isEmpty() || recyclerView.getChildCount() <= 0) {
            return;
        }

        if (currentPlayTarget != null && currentPlayTarget.isPlaying()
                && isTargetInBounds(currentPlayTarget)) {
            return;
        }

        IPlayTarget playTarget = null;
        for (IPlayTarget target : playTargets) {
            boolean inBounds = isTargetInBounds(target);
            if (inBounds) {
                playTarget = target;
                break;
            }
        }

        if (playTarget != null) {
            if (currentPlayTarget != null) {
                currentPlayTarget.inActive();
            }
            currentPlayTarget = playTarget;
            playTarget.onActive();
        }

    }

    private boolean isTargetInBounds(IPlayTarget playTarget) {
        ViewGroup owner = playTarget.owner();
        if (!owner.isShown() && !owner.isAttachedToWindow()) {
            return false;
        }
        ensureRVLocation();

        int[] location = new int[2];
        owner.getLocationOnScreen(location);
        int center = location[1] + owner.getHeight() / 2;
        return center > rvLocation.first && center < rvLocation.second;
    }

    private Pair<Integer, Integer> rvLocation;

    private void ensureRVLocation() {
        if (rvLocation != null) {
            return;
        }
        int[] location = new int[2];
        recyclerView.getLocationOnScreen(location);
        rvLocation = new Pair<>(location[1], location[1] + recyclerView.getHeight());
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void onResume() {
        if (currentPlayTarget != null) {
            currentPlayTarget.onActive();
        }
    }

    public void onPause() {
        if (currentPlayTarget != null) {
            currentPlayTarget.inActive();
        }
    }

    public void onDestroy() {
        PlayManager.release(category);
    }
}
