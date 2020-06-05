package com.mooc.ppjoke.navigator

import android.content.ComponentName
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.*
import com.mooc.common.utils.AppGlobals
import com.mooc.ppjoke.utils.AppConfigs

/**
 * [AppCompatActivity]的扩展方法，此方法主要是根据自定义的路由页面[AppConfigs.destMap]数据，来重写
 * 官方提供的[Navigation]xml解析的方式
 */
fun AppCompatActivity.navGraphBuilder(@IdRes navHostId: Int) {

    val destMap = AppConfigs.destMap ?: return
    // 根据NavHostFragment的id查找NavController
    val navController = findNavController(navHostId)
    val activityNavigator = navController.navigatorProvider[ActivityNavigator::class]

    // 根据NavHostId获取NavHostFragment
    val navHostFragment = supportFragmentManager.findFragmentById(navHostId)
        ?: throw IllegalStateException("not found fragment by navHostId: $navHostId")
    // 自定义FragmentNavigator,用于解决每次切换都会重新创建页面的问题
    // 此处一定是NavHostFragment#childFragmentManager,
    // 不然即使设置app:defaultNavHost="false" 按返回键时会出现可以回退的问题
    val fragmentNavigator =
        FixFragmentNavigator(this, navHostFragment.childFragmentManager, navHostId)
    // 需要将新创建的navigator添加到provider中
    navController.navigatorProvider.addNavigator(fragmentNavigator)

    val navGraphNavigator = navController.navigatorProvider[NavGraphNavigator::class]

    // 创建一个NavGraph 用于存储路由页面的节点信息
    val navGraph = NavGraph(navGraphNavigator)
    // 遍历所有的路由页面信息，创建Destination
    destMap.values.forEach {
        if (it.isFragment) {
            fragmentNavigator.createDestination().apply {
                id = it.id
                className = it.name
                addDeepLink(it.pageUrl)
                navGraph.addDestination(this)
            }
        } else {
            activityNavigator.createDestination().apply {
                id = it.id
                val application = AppGlobals.getApplication()
                setComponentName(ComponentName(application.packageName, it.name))
                addDeepLink(it.pageUrl)
                navGraph.addDestination(this)
            }
        }
        // 如果页面设置为starter，则默认选中
        if (it.asStarter) {
            navGraph.startDestination = it.id
        }
    }

    navController.graph = navGraph

}