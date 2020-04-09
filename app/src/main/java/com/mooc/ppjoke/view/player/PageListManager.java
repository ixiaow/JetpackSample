package com.mooc.ppjoke.view.player;

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

public final class PageListManager {
    private static final Map<String, PageListPlay> PAGE_LIST_PLAY_MAP = new HashMap<>();
    private static final ProgressiveMediaSource.Factory MEDIA_SOURCE_FACTORY;

    static {
        Application application = AppGlobals.getApplication();
        String userAgent = Util.getUserAgent(application, application.getPackageName());
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        Cache cache = new SimpleCache(application.getCacheDir(), new LeastRecentlyUsedCacheEvictor(
                200 * 1024 * 1024), new ExoDatabaseProvider(application));
        CacheDataSinkFactory dataSinkFactory = new CacheDataSinkFactory(cache, Long.MAX_VALUE);

        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(cache, dataSourceFactory,
                new FileDataSourceFactory(), dataSinkFactory, CacheDataSource.FLAG_BLOCK_ON_CACHE, null);
        MEDIA_SOURCE_FACTORY = new ProgressiveMediaSource.Factory(cacheDataSourceFactory);

    }

    public static MediaSource buildMediaSource(String url) {
        return MEDIA_SOURCE_FACTORY.createMediaSource(Uri.parse(url));
    }


    public static PageListPlay get(String pageName) {
        PageListPlay pageListPlay = PAGE_LIST_PLAY_MAP.get(pageName);
        if (pageListPlay == null) {
            pageListPlay = new PageListPlay();
            PAGE_LIST_PLAY_MAP.put(pageName, pageListPlay);
        }
        return pageListPlay;
    }

    public static void release(String pageName) {
        PageListPlay pageListPlay = PAGE_LIST_PLAY_MAP.remove(pageName);
        if (pageListPlay != null) {
            pageListPlay.release();
        }
    }
}
