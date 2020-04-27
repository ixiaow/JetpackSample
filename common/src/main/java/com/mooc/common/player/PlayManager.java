package com.mooc.common.player;

import android.app.Application;
import android.net.Uri;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.mooc.common.utils.AppGlobals;

import java.util.HashMap;
import java.util.Map;

public class PlayManager {
    private final static Map<String, PlayView> PLAY_VIEW_MAP = new HashMap<>();
    private static final ProgressiveMediaSource.Factory mediaSourceFactory;

    static {
        Application application = AppGlobals.getApplication();

        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024);
        Cache simpleCache = new SimpleCache(application.getCacheDir(), evictor, new ExoDatabaseProvider(application));
        String userAgent = Util.getUserAgent(application, application.getApplicationInfo().name);
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        CacheDataSinkFactory dataSinkFactory = new CacheDataSinkFactory(simpleCache, Long.MAX_VALUE);
        CacheDataSourceFactory dataSourceFactory = new CacheDataSourceFactory(simpleCache,
                httpDataSourceFactory, new FileDataSourceFactory(), dataSinkFactory,
                CacheDataSource.FLAG_BLOCK_ON_CACHE, null);
        mediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);
    }

    public static MediaSource buildMediaSource(String url) {
        return mediaSourceFactory.createMediaSource(Uri.parse(url));
    }

    public static PlayView get(String category) {
        PlayView playView = PLAY_VIEW_MAP.get(category);
        if (playView == null) {
            playView = new PlayView();
            PLAY_VIEW_MAP.put(category, playView);
        }
        return playView;
    }

    public static void release(String category) {
        PlayView playView = PLAY_VIEW_MAP.remove(category);
        if (playView != null) {
            playView.release();
        }
    }
}
