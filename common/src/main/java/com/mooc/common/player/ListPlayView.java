package com.mooc.common.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.common.R;
import com.mooc.common.utils.PxUtils;
import com.mooc.common.view.PPImageView;

public class ListPlayView extends FrameLayout implements
        IPlayTarget, Player.EventListener, PlayerControlView.VisibilityListener {

    private PPImageView blurImage;
    private PPImageView coverImage;
    private PPImageView playIcon;
    private ProgressBar bufferView;
    private int widthPx;
    private int heightPx;
    private String coverUrl;
    private String videoUrl;
    private String category;
    private boolean isPlaying;

    public ListPlayView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_list_play_view, this);

        blurImage = findViewById(R.id.blur_image);
        coverImage = findViewById(R.id.cover_image);
        playIcon = findViewById(R.id.player_icon);
        bufferView = findViewById(R.id.buffer_view);

        playIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    onActive();
                } else {
                    inActive();
                }
            }
        });
    }

    public void bind(int widthPx, int heightPx, String coverUrl, String videoUrl) {
        this.widthPx = widthPx;
        this.heightPx = heightPx;
        this.coverUrl = coverUrl;
        this.videoUrl = videoUrl;
        // 当宽度大于高度，则不显示背景
        if (widthPx >= heightPx) {
            blurImage.setVisibility(View.INVISIBLE);
        } else {
            blurImage.setVisibility(View.VISIBLE);
            blurImage.setBlurImageUrl(coverUrl, PxUtils.dp2px(10));
        }
        executeParams();
    }

    private void executeParams() {
        // 最大宽度为屏幕宽度
        final int maxWidth = PxUtils.getScreenWidth();
        // 最大高度也为屏幕的宽度
        final int maxHeight = maxWidth;

        final int layoutWidth = maxWidth;
        int layoutHeight = 0;

        int coverWidth = 0;
        int coverHeight = 0;

        if (widthPx >= heightPx) {
            // 当宽度大于高度，宽度为屏幕宽度，高度为自适应
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / (1.0f * widthPx / maxWidth));
        } else {
            // 当高度大于宽度，高度为屏幕宽度，宽度为自适应
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (1.0f * heightPx / maxHeight));
        }

        // 设置当前视频view 布局
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;
        setLayoutParams(layoutParams);

        // 设置背景视图布局参数
        ViewGroup.LayoutParams blurParams = blurImage.getLayoutParams();
        blurParams.width = layoutWidth;
        blurParams.height = layoutHeight;
        blurImage.setLayoutParams(blurParams);

        // 设置封面视图布局参数
        FrameLayout.LayoutParams coverParams = (LayoutParams) coverImage.getLayoutParams();
        coverParams.width = coverWidth;
        coverParams.height = coverHeight;
        coverParams.gravity = Gravity.CENTER;
        coverImage.setLayoutParams(coverParams);

        // 设置play按钮的位置
        FrameLayout.LayoutParams playIconParams = (LayoutParams) playIcon.getLayoutParams();
        playIconParams.gravity = Gravity.CENTER;
        playIcon.setLayoutParams(playIconParams);

        // 设置尺寸大小
        coverImage.setImageUrl(coverUrl);
    }

    @Override
    public ViewGroup owner() {
        return this;
    }

    @Override
    public void onActive() {

        PlayView playView = PlayManager.get(category);
        PlayerView playerView = playView.playerView;
        PlayerControlView controlView = playView.controlView;
        ExoPlayer exoPlayer = playView.exoPlayer;

        if (playerView == null) {
            return;
        }
        // 切换player
        playView.switchPlayerView(playerView);
        // 将playerView添加到当前view上
        attachPlayerView(playerView);
        // 当controllerView 添加到当前view上
        attachControllerView(controlView);

        if (!TextUtils.equals(videoUrl, playView.playUrl)) {
            playView.playUrl = videoUrl;
            MediaSource mediaSource = PlayManager.buildMediaSource(videoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        } else {
            onPlayerStateChanged(true, Player.STATE_READY);
        }
        controlView.show();
        controlView.setVisibilityListener(this);
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);
    }

    private void attachControllerView(PlayerControlView controlView) {
        ViewParent controlViewParent = controlView.getParent();
        if (controlViewParent != this) {
            if (controlViewParent instanceof ViewGroup) {
                ((ViewGroup) controlViewParent).removeView(controlView);
            }
            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            addView(controlView, params);
        }
    }

    private void attachPlayerView(PlayerView playerView) {
        ViewParent playerViewParent = playerView.getParent();
        if (playerViewParent != this) {
            if (playerViewParent instanceof ViewGroup) {
                ((ViewGroup) playerViewParent).removeView(playerView);
            }
            addView(playerView, 1, coverImage.getLayoutParams());
        }
    }

    @Override
    public void inActive() {
        PlayView playView = PlayManager.get(category);
        if (playView.playerView == null
                || playView.exoPlayer == null
                || playView.controlView == null) {
            return;
        }

        playView.exoPlayer.setPlayWhenReady(false);
        playView.exoPlayer.removeListener(this);
        playView.controlView.setVisibilityListener(null);

        playIcon.setImageResource(R.drawable.icon_video_play);
        playIcon.setVisibility(VISIBLE);
        coverImage.setVisibility(VISIBLE);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        PlayView playView = PlayManager.get(category);
        ExoPlayer exoPlayer = playView.exoPlayer;
        if (playWhenReady && exoPlayer.getBufferedPosition() != 0
                && playbackState == Player.STATE_READY) {
            coverImage.setVisibility(GONE);
            bufferView.setVisibility(GONE);
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferView.setVisibility(VISIBLE);
        }
        isPlaying = playWhenReady && exoPlayer.getBufferedPosition() != 0 && playbackState == Player.STATE_READY;
        playIcon.setImageResource(isPlaying ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    @Override
    public void onVisibilityChange(int visibility) {
        playIcon.setVisibility(visibility);
        playIcon.setImageResource(isPlaying() ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isPlaying = false;
        coverImage.setVisibility(VISIBLE);
        bufferView.setVisibility(GONE);
        playIcon.setVisibility(VISIBLE);
        playIcon.setImageResource(R.drawable.icon_video_play);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PlayView playView = PlayManager.get(category);
        playView.controlView.show();
        return true;
    }
}
