package com.mooc.ppjoke.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.ActivityLayoutLoginBinding;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLayoutLoginBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_layout_login);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LoginViewModel.class);
        viewModel.setOwner(this);
        viewDataBinding.setViewModel(viewModel);
        viewDataBinding.setActivity(this);
        viewDataBinding.actionClose.setOnClickListener(v -> finish());
        viewModel.getLoginResult().observe(this, result -> {
            if (result) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, viewModel.getLoginListener());
        }
    }
}
