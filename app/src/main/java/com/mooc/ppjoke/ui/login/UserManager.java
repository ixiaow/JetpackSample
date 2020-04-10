package com.mooc.ppjoke.ui.login;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mooc.network.cache.CacheManager;
import com.mooc.ppjoke.model.User;

public class UserManager {
    private static final String KEY_CACHE_USER = "key_user_cache";
    private User user;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    private static final class Holder {
        private static final UserManager INSTANCE = new UserManager();
    }

    public static UserManager get() {
        return Holder.INSTANCE;
    }

    private UserManager() {
        User user = CacheManager.get().readCache(KEY_CACHE_USER);
        if (user != null && user.expires_time > System.currentTimeMillis()) {
            this.user = user;
        }
    }

    public void save(@NonNull User user) {
        this.user = user;
        CacheManager.get().save(KEY_CACHE_USER, user);
        if (userLiveData.hasObservers()) {
            userLiveData.postValue(user);
        }
    }

    public LiveData<User> login(@NonNull Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return userLiveData;
    }

    public boolean isLogin() {
        return user != null && user.expires_time > System.currentTimeMillis();
    }

    @Nullable
    public User getUser() {
        return isLogin() ? user : null;
    }

    public long getUserId() {
        return isLogin() ? user.userId : 0;
    }

}
