package com.mooc.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alibaba.fastjson.TypeReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mooc.common.utils.Logs;
import com.mooc.network.ApiResponse;
import com.mooc.network.HttpObserver;
import com.mooc.network.http.LiveHttp;
import com.mooc.network.http.TypeToken;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.navigator.NavGraphBuilder;
import com.mooc.ppjoke.view.AppBottomBar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppBottomBar navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavGraphBuilder.build(this, R.id.nav_host_fragment, navController);
        navView.setOnNavigationItemSelectedListener(this);
        LiveHttp.create().get().url("/feeds/queryHotFeedsList")
                .observe(this, new HttpObserver<ApiResponse<List<Feed>>>() {
                    @Override
                    public void onChanged(ApiResponse<List<Feed>> httpObserverApiResponse) {

                    }
                });

        ;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return TextUtils.isEmpty(item.getTitle());
    }
}
