package com.mooc.ppjoke.view.player;

import android.view.ViewGroup;

public interface IPlayTarget {
    ViewGroup getOwner();

    /**
     * 激活播放器
     */
    void onActive();

    /**
     * 取消播放
     */
    void inActive();

    boolean isPlaying();

    void setCategory(String category);
}
