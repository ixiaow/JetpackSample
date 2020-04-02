package com.mooc.ppjoke.navigator;

import android.app.Application;
import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.utils.AppConfig;
import com.mooc.ppjoke.utils.AppGlobals;

import java.util.Map;

public class NavGraphBuilder {

    public static void build(FragmentActivity activity, int containerId, NavController controller) {

        NavigatorProvider provider = controller.getNavigatorProvider();
        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,
                activity.getSupportFragmentManager(), containerId);
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
        controller.setGraph(navGraph);
    }
}
