package com.mooc.ppjoke.navigator;

import android.app.Application;
import android.content.ComponentName;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;

import com.mooc.common.utils.AppGlobals;
import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.utils.AppConfig;

import java.util.Map;

public class NavGraphBuilder {

    public static void build(@NonNull FragmentActivity activity, @IdRes int navHostId) {

        // 此处很重要，需要通过containerId找到NavHostFragment
        NavHostFragment fragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(navHostId);
        if (fragment == null) {
            throw new IllegalArgumentException("not found fragment by containerId: " + navHostId);
        }

        NavController navController = fragment.getNavController();
        NavigatorProvider provider = navController.getNavigatorProvider();
        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        // 此处的fragmentManager 只能是 childFragmentManager,不然返回按钮会引起返回栈的操作
        // 关于此处可以查看NavHostFragment中的createFragmentNavigator()
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,
                childFragmentManager, navHostId);
        provider.addNavigator(fragmentNavigator);


        NavGraphNavigator graphNavigator = provider.getNavigator(NavGraphNavigator.class);

        NavGraph navGraph = new NavGraph(graphNavigator);
        Application application = AppGlobals.getApplication();
        Map<String, Destination> destConfig = AppConfig.getDestConfig();
        for (Destination dest : destConfig.values()) {
            if (dest.isFragment) {
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setId(dest.id);
                destination.setClassName(dest.name);
                destination.addDeepLink(dest.pageUrl);
                navGraph.addDestination(destination);
            } else {
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(dest.id);
                destination.setComponentName(new ComponentName(application.getPackageName(), dest.name));
                destination.addDeepLink(dest.pageUrl);
                navGraph.addDestination(destination);
            }

            if (dest.asStarter) {
                navGraph.setStartDestination(dest.id);
            }
        }
        navController.setGraph(navGraph);
    }
}
