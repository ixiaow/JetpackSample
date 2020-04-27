package com.mooc.common.player;

import android.app.Application;
import android.view.LayoutInflater;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.common.R;
import com.mooc.common.utils.AppGlobals;

/**
 * 视频播放器组件
 */
public class PlayView {
    public ExoPlayer exoPlayer;
    public PlayerView playerView;
    public PlayerControlView controlView;

    public PlayView() {
        Application application = AppGlobals.getApplication();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(application);

        LayoutInflater inflater = LayoutInflater.from(application);
        playerView = (PlayerView) inflater.inflate(R.layout.video_player_view, null);
        controlView = (PlayerControlView) inflater.inflate(R.layout.video_player_controller_view, null);

        playerView.setPlayer(exoPlayer);
        controlView.setPlayer(exoPlayer);
    }

    public void release() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }

        if (playerView != null) {
            playerView.setPlayer(null);
            playerView = null;
        }

        if (controlView != null) {
            controlView.setPlayer(null);
            controlView.setVisibilityListener(null);
            controlView = null;
        }
    }

    public void switchPlayerView(PlayerView playerView) {
        if (playerView != null && this.playerView != playerView) {
            this.playerView.setPlayer(null);
            playerView.setPlayer(exoPlayer);
        } else {
            this.playerView.setPlayer(exoPlayer);
        }
    }
}
