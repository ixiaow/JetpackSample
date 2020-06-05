package com.mooc.ppjoke.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.view.forEach
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mooc.ppjoke.R
import com.mooc.ppjoke.exts.dp
import com.mooc.ppjoke.exts.parserColor
import com.mooc.ppjoke.exts.sp
import com.mooc.ppjoke.model.MainTabs
import com.mooc.ppjoke.utils.AppConfigs

/**
 * 自定义底部导航栏，通过读取assets目录下的配置文件来显示底部item
 */
@SuppressLint("RestrictedApi")
class AppBottomBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    private val icons = intArrayOf(
        R.drawable.icon_tab_home,
        R.drawable.icon_tab_sofa,
        R.drawable.icon_tab_publish,
        R.drawable.icon_tab_find,
        R.drawable.icon_tab_mine
    )

    init {
        AppConfigs.mainTabs?.takeUnless { isInEditMode }?.run {
            // 设置显示tab的label
            labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            // 创建二维数组用于表示选中和未选中颜色的变化
            val state = Array(2) { IntArray(1) }
            state[0] = intArrayOf(android.R.attr.state_selected)
            state[1] = intArrayOf()
            // 转换颜色
            val colors = intArrayOf(activeColor.parserColor(), inActiveColor.parserColor())
            val colorState = ColorStateList(state, colors)
            // 设置icon图标着色
            itemIconTintList = colorState
            // 设置item文字颜色
            itemTextColor = colorState
            // 遍历tab,根据tab添加menuItem
            val tabList = mutableListOf<MainTabs.Tab>()
            tabs?.filter { it.enable }?.forEachIndexed { index, tab ->
                val itemId = tab.getItemId()
                if (itemId != -1) {
                    menu.add(0, itemId, index, tab.title).also { it.setIcon(icons[index]) }
                    tabList.add(tab)
                }
            }

            val menuView = getChildAt(0) as BottomNavigationMenuView
            tabList.forEachIndexed { index, tab ->
                val itemView = menuView.getChildAt(index) as BottomNavigationItemView
                itemView.setIconSize(tab.size.sp)
                if (tab.title.isNullOrEmpty()) {
                    itemView.setShifting(false)
                    val color = if (tab.tintColor.isNullOrEmpty()) "#ff678f" else tab.tintColor
                    itemView.setIconTintList(ColorStateList.valueOf(color.parserColor()))
                }
            }

            tabList.clear()
        }
    }

    private fun MainTabs.Tab.getItemId(): Int = AppConfigs.destMap?.get(pageUrl)?.id ?: -1

}