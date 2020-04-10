package com.mooc.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.navigator.NavGraphBuilder;
import com.mooc.ppjoke.ui.login.UserManager;
import com.mooc.ppjoke.view.AppBottomBar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private AppBottomBar navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavGraphBuilder.build(this, R.id.nav_host_fragment, navController);
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Destination destination = navView.getDestination(itemId);
        if (destination != null && destination.isNeedLogin && !UserManager.get().isLogin()) {
            UserManager.get().login(this).observe(this,
                    user -> navController.navigate(item.getItemId()));
        } else {
            navController.navigate(item.getItemId());
        }
        return !TextUtils.isEmpty(item.getTitle());
    }
}
