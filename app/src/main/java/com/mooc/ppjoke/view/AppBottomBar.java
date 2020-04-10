package com.mooc.ppjoke.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.mooc.common.utils.PxUtils;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.model.Destination;
import com.mooc.ppjoke.model.MainTabs;
import com.mooc.ppjoke.model.MainTabs.Tab;
import com.mooc.ppjoke.utils.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class AppBottomBar extends BottomNavigationView {
    private static int[] icons = {
            R.drawable.icon_tab_home,
            R.drawable.icon_tab_sofa,
            R.drawable.icon_tab_publish,
            R.drawable.icon_tab_find,
            R.drawable.icon_tab_mine
    };

    private SparseArrayCompat<Tab> tabs = new SparseArrayCompat<>();

    public AppBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }

        MainTabs mainTabs = AppConfig.getMainTabs();
        if (mainTabs == null) {
            return;
        }
        // 设置一直显示label
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        // 根据选择状态设置文字和图片的颜色
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = {Color.parseColor(mainTabs.activeColor), Color.parseColor(mainTabs.inActiveColor)};
        ColorStateList stateList = new ColorStateList(states, colors);
        setItemTextColor(stateList);
        setItemIconTintList(stateList);

        List<Tab> tmpTabs = new ArrayList<>();
        // 遍历tab,添加menu
        for (Tab tab : mainTabs.tabs) {
            if (!tab.enable) {
                continue;
            }
            int id = getItemId(tab);
            if (id != -1) {
                MenuItem menu = getMenu().add(0, id, tab.index, tab.title);
                menu.setIcon(icons[tab.index]);
                tabs.put(id, tab);
                tmpTabs.add(tab);
            }
        }

        // 由于每次调用getMenu().add() 会将之前添加的item移除掉，所以没有办法直接在menu中更改title的大戏和颜色
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
        for (int i = 0; i < tmpTabs.size(); i++) {
            Tab tab = tmpTabs.get(i);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setIconSize(PxUtils.dp2px(tab.size));
            if (TextUtils.isEmpty(tab.title)) {
                int tintColor = TextUtils.isEmpty(tab.tintColor)
                        ? Color.parseColor("#ff678f") : Color.parseColor(tab.tintColor);
                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                itemView.setShifting(false);
            }
        }
        tmpTabs.clear();
    }

    private int getItemId(Tab tab) {
        Destination destination = AppConfig.getDestConfig().get(tab.pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.id;
    }

    public void setSelectedItem(int position) {
        if (!tabs.isEmpty() && position < tabs.size()) {
            int itemId = getItemId(tabs.valueAt(position));
            if (itemId > 0) {
                setSelectedItemId(itemId);
            }
        }
    }

    @Nullable
    public Destination getDestination(int itemId) {
        Tab tab = tabs.get(itemId);
        if (tab == null) {
            return null;
        }
        String pageUrl = tab.pageUrl;
        return AppConfig.getDestConfig().get(pageUrl);
    }
}
