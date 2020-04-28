package com.mooc.common.player;

import android.view.ViewGroup;

public interface IPlayTarget {
    /**
     * 当前视频播放器的持有类
     */
    ViewGroup owner();

    /**
     * 当前播放器进入激活状态
     */
    void onActive();

    /**
     * 当前播放器退出激活状态
     */
    void inActive();

    /**
     * 当前播放器是否在播放
     */
    boolean isPlaying();

    /**
     * 设置播放器所持有的分类
     */
    void setCategory(String category);
}
