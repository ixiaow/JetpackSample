package com.mooc.ppjoke.ui.login;

import com.mooc.common.utils.ToastUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public abstract class LoginListener implements IUiListener {

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {
        ToastUtil.showToast("登录失败: " + uiError.toString());
    }


    @Override
    public void onCancel() {
        ToastUtil.showToast("登录取消");
    }
}
