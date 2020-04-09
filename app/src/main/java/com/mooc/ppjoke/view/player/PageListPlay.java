package com.mooc.ppjoke.view.player;

import android.app.Application;
import android.view.LayoutInflater;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.common.utils.AppGlobals;
import com.mooc.ppjoke.R;

public class PageListPlay {
    public SimpleExoPlayer exoPlayer;
    public PlayerView playerView;
    public PlayerControlView controlView;
    public String playerUrl;

    public PageListPlay() {
        Application application = AppGlobals.getApplication();
        LayoutInflater inflater = LayoutInflater.from(application);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(application);
        playerView = (PlayerView) inflater.inflate(R.layout.layout_exo_player, null);
        controlView = (PlayerControlView) inflater.inflate(R.layout.layout_exo_player_controll_view, null);

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
        if (playerView != null && playerView != this.playerView) {
            this.playerView.setPlayer(null);
            playerView.setPlayer(exoPlayer);
        } else {
            this.playerView.setPlayer(exoPlayer);
        }
    }
}
