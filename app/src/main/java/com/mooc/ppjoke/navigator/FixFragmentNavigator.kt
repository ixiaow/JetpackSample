package com.mooc.ppjoke.navigator

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

private const val TAG = "FixFragmentNavigator"

/**
 * 自定义FragmentNavigator,用于解决原有[FragmentNavigator]每次切换页面即执行[navigate]时，都会重新创建页面的问题
 */
@Navigator.Name("fixfragment")
class FixFragmentNavigator(
    val context: Context,
    val manager: FragmentManager,
    val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    private var mBackStack: ArrayDeque<Int>? = null


    @Suppress("UNCHECKED_CAST", "DEPRECATED")
    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (manager.isStateSaved) {
            Log.i(
                TAG, "Ignoring navigate() call: FragmentManager has already"
                        + " saved its state"
            )
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }

        val ft = manager.beginTransaction()

        // 先隐藏当前显示的fragment
        val primaryNavigationFragment = manager.primaryNavigationFragment
        if (primaryNavigationFragment != null) {
            ft.hide(primaryNavigationFragment)
        }
        val tag = destination.id.toString()
        var frag: Fragment? = manager.findFragmentByTag(tag)
        if (frag == null) {
            frag = instantiateFragment(context, manager, className, args)
            frag.arguments = args
            ft.add(containerId, frag, tag)
        } else {
            ft.show(frag)
        }

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        ft.setPrimaryNavigationFragment(frag)

        if (mBackStack == null) {
            try {
                val mBackStackField = FragmentNavigator::class.java.getDeclaredField("mBackStack")
                mBackStackField.isAccessible = true
                mBackStack = mBackStackField.get(this) as? ArrayDeque<Int>
            } catch (e: Exception) {
            }
        }

        @IdRes val destId = destination.id
        val initialNavigation = mBackStack?.isEmpty() == true
        // TODO Build first class singleTop behavior for fragments
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack?.peekLast() == destId)

        val isAdded: Boolean
        val backSize = mBackStack?.size ?: 0
        isAdded = when {
            initialNavigation -> {
                true
            }
            isSingleTopReplacement -> {
                // Single Top means we only want one instance on the back stack
                if (backSize > 1) {

                    // If the Fragment to be replaced is on the FragmentManager's
                    // back stack, a simple replace() isn't enough so we
                    // remove it from the back stack and put our replacement
                    // on the back stack in its place
                    manager.popBackStack(
                        generateBackStackName(backSize, mBackStack?.peekLast() ?: 0),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    ft.addToBackStack(generateBackStackName(backSize, destId))
                }
                false
            }
            else -> {
                ft.addToBackStack(generateBackStackName(backSize + 1, destId))
                true
            }
        }
        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key!!, value!!)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        // The commit succeeded, update our view of the world
        // The commit succeeded, update our view of the world
        return if (isAdded) {
            mBackStack?.add(destId)
            destination
        } else {
            null
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int) = "$backStackIndex-$destId"
}