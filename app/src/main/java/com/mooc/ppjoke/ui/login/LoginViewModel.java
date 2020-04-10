package com.mooc.ppjoke.ui.login;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mooc.common.utils.AppGlobals;
import com.mooc.common.utils.ToastUtil;
import com.mooc.network.ApiResponse;
import com.mooc.network.HttpObserver;
import com.mooc.network.http.LiveHttp;
import com.mooc.ppjoke.Api;
import com.mooc.ppjoke.base.BaseViewModel;
import com.mooc.ppjoke.model.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginViewModel extends BaseViewModel {
    private Tencent tencent;
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();

    public void login(AppCompatActivity activity) {
        if (tencent == null) {
            Application application = AppGlobals.getApplication();
            tencent = Tencent.createInstance("101794421", application);
        }

        tencent.login(activity, "all", loginListener);
    }

    private final LoginListener loginListener = new LoginListener() {
        @Override
        public void onComplete(Object o) {
            super.onComplete(o);
            JSONObject response = (JSONObject) o;
            try {
                String openid = response.getString("openid");
                String access_token = response.getString("access_token");
                String expires_in = response.getString("expires_in");
                long expires_time = response.getLong("expires_time");

                tencent.setOpenId(openid);
                tencent.setAccessToken(access_token, expires_in);
                QQToken qqToken = tencent.getQQToken();
                getUserInfo(qqToken, expires_time, openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getUserInfo(QQToken qqToken, long expires_time, String openid) {
        UserInfo userInfo = new UserInfo(AppGlobals.getApplication(), qqToken);
        userInfo.getUserInfo(new LoginListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject response = (JSONObject) o;

                try {
                    String nickname = response.getString("nickname");
                    String figureurl_2 = response.getString("figureurl_2");

                    save(nickname, figureurl_2, openid, expires_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void save(String nickname, String avatar, String openid, long expires_time) {
        LiveHttp.create()
                .get()
                .url(Api.INSERT_USER)
                .addParam("name", nickname)
                .addParam("avatar", avatar)
                .addParam("qqOpenId", openid)
                .addParam("expires_time", expires_time)
                .observe(viewLifecycleOwner, new HttpObserver<ApiResponse<User>>() {
                    @Override
                    public void onChanged(ApiResponse<User> userApiResponse) {
                        if (userApiResponse.isSuccessful() && userApiResponse.data != null) {
                            UserManager.get().save(userApiResponse.data);
                            loginResult.postValue(true);
                        } else {
                            ToastUtil.showToast("登录失败 " + userApiResponse.message);
                        }
                    }
                });
    }

    public IUiListener getLoginListener() {
        return loginListener;
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }
}
