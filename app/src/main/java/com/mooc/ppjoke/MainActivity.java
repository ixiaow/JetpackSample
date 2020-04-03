package com.mooc.ppjoke;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mooc.network.ApiResponse;
import com.mooc.network.http.LiveHttp;
import com.mooc.ppjoke.model.User;
import com.mooc.ppjoke.navigator.NavGraphBuilder;
import com.mooc.ppjoke.view.AppBottomBar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppBottomBar navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavGraphBuilder.build(this, R.id.nav_host_fragment, navController);
        navView.setOnNavigationItemSelectedListener(this);
        LiveHttp.create().url("/feeds/queryHotFeedsList")
                .get()
                .async(true)
                .registerType(User.class)
                .cache()
                .observe(this, new Observer<ApiResponse<User>>() {
                    @Override
                    public void onChanged(ApiResponse<User> userApiResponse) {
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return TextUtils.isEmpty(item.getTitle());
    }
}
