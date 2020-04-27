package com.mooc.ppjoke.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.mooc.common.interfaces.Function;
import com.mooc.common.utils.ToastUtil;
import com.mooc.network.ApiResponse;
import com.mooc.network.HttpObserver;
import com.mooc.network.LiveHttp;
import com.mooc.ppjoke.Api;
import com.mooc.ppjoke.model.Comment;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.model.User;
import com.mooc.ppjoke.ui.login.UserManager;

public class InteractionPresenter {

    public static void toggleCommentLike(LifecycleOwner owner, Comment comment) {
        login(owner, () -> toggleCommentLikeInternal(owner, comment));
    }


    public static void toggleFeedLike(LifecycleOwner owner, Feed feed) {
        login(owner, () -> toggleFeedLikeInternal(owner, feed));
    }

    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed) {
        login(owner, () -> toggleFeedDissInternal(owner, feed));
    }

    public static void openShare(Context context, Feed feed) {

    }

    private static void toggleCommentLikeInternal(LifecycleOwner owner, Comment comment) {
        LiveHttp.create()
                .get()
                .path(Api.URL_TOGGLE_COMMENT_LIKE)
                .addParam("commentId", comment.commentId)
                .addParam("userId", UserManager.get().getUserId())
                .observe(owner, new HttpObserver<ApiResponse<JSONObject>>() {
                    @Override
                    public void onChanged(ApiResponse<JSONObject> apiResponse) {
                        if (apiResponse.isSuccessful() && apiResponse.data != null) {
                            boolean hasLiked = apiResponse.data.getBooleanValue("hasLiked");
                            comment.getUgc().setHasLiked(hasLiked);
                        } else {
                            ToastUtil.showToast(apiResponse.message);
                        }
                    }
                });
    }

    private static void toggleFeedLikeInternal(LifecycleOwner owner, Feed feed) {
        LiveHttp.create()
                .get()
                .path(Api.URL_TOGGLE_FEED_LIK)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .observe(owner, new HttpObserver<ApiResponse<JSONObject>>() {
                    @Override
                    public void onChanged(ApiResponse<JSONObject> apiResponse) {
                        if (apiResponse.isSuccessful() && apiResponse.data != null) {
                            boolean hasLiked = apiResponse.data.getBooleanValue("hasLiked");
                            feed.getUgc().setHasLiked(hasLiked);
                        } else {
                            ToastUtil.showToast(apiResponse.message);
                        }
                    }
                });
    }

    private static void toggleFeedDissInternal(LifecycleOwner owner, Feed feed) {
        LiveHttp.create()
                .get()
                .path(Api.URL_TOGGLE_FEED_DISS)
                .addParam("itemId", feed.itemId)
                .addParam("userId", UserManager.get().getUserId())
                .observe(owner, new HttpObserver<ApiResponse<JSONObject>>() {
                    @Override
                    public void onChanged(ApiResponse<JSONObject> apiResponse) {
                        if (apiResponse.isSuccessful() && apiResponse.data != null) {
                            boolean hasLiked = apiResponse.data.getBooleanValue("hasLiked");
                            feed.getUgc().setHasdiss(hasLiked);
                        } else {
                            ToastUtil.showToast(apiResponse.message);
                        }
                    }
                });
    }


    private static void login(@NonNull LifecycleOwner owner, @NonNull Function function) {
        if (!UserManager.get().isLogin()) {
            LiveData<User> loginLive = UserManager.get().login((Context) owner);
            loginLive.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    loginLive.removeObserver(this);
                    function.invoke();
                }
            });
            return;
        }
        function.invoke();
    }
}
