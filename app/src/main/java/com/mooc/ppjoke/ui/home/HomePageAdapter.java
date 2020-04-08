package com.mooc.ppjoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.ppjoke.databinding.LayoutFeedTypeImageBinding;
import com.mooc.ppjoke.databinding.LayoutFeedTypeVideoBinding;
import com.mooc.ppjoke.model.Feed;

import java.util.Objects;

public class HomePageAdapter extends PagedListAdapter<Feed, HomePageAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Context context;

    public HomePageAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding viewDataBinding;
        if (viewType == Feed.IMAGE_TYPE) {
            viewDataBinding = LayoutFeedTypeImageBinding.inflate(layoutInflater, parent, false);
        } else {
            viewDataBinding = LayoutFeedTypeVideoBinding.inflate(layoutInflater, parent, false);
        }
        return new ViewHolder(viewDataBinding.getRoot(), viewDataBinding);
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        return feed != null ? feed.itemType : super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private ViewDataBinding viewDataBinding;

        ViewHolder(@NonNull View itemView, @NonNull ViewDataBinding viewDataBinding) {
            super(itemView);
            this.viewDataBinding = viewDataBinding;
        }

        void bindData(@Nullable Feed feed) {
            if (feed == null) {
                return;
            }

            if (viewDataBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding binding = (LayoutFeedTypeImageBinding) viewDataBinding;
                binding.setFeed(feed);
                binding.feedImage.bindData(feed.width, feed.height, 16, feed.cover);
                binding.interactionBinding.setLifeCycleOwner((LifecycleOwner) itemView.getContext());
            } else {
                LayoutFeedTypeVideoBinding binding = (LayoutFeedTypeVideoBinding) viewDataBinding;
                binding.setFeed(feed);
                binding.cover.bindData(feed.width, feed.height, 16, feed.cover);
                binding.interactionBinding.setLifeCycleOwner((LifecycleOwner) itemView.getContext());
            }
        }
    }
}