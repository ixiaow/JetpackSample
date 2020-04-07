package com.mooc.common.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.mooc.common.R;

public class EmptyView extends LinearLayout {
    private ImageView icon;
    private TextView title;
    private Button action;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this);
        icon = findViewById(R.id.empty_icon);
        title = findViewById(R.id.empty_text);
        action = findViewById(R.id.empty_action);
    }

    public void setEmptyIcon(@DrawableRes int iconRes) {
        icon.setImageResource(iconRes);
    }

    public void setTitle(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(text);
            title.setVisibility(View.VISIBLE);
        }
    }

    public void setButton(@Nullable String text, @Nullable View.OnClickListener listener) {
        if (TextUtils.isEmpty(text)) {
            action.setVisibility(View.GONE);
        } else {
            action.setText(text);
            if (listener != null) {
                action.setOnClickListener(listener);
            }
            action.setVisibility(VISIBLE);
        }
    }
}
