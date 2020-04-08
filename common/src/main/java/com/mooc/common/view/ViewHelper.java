package com.mooc.common.view;

import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.mooc.common.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ViewHelper {

    public static final int RADIUS_ALL = 0;
    public static final int RADIUS_LEFT = 1;
    public static final int RADIUS_TOP = 2;
    public static final int RADIUS_RIGHT = 3;
    public static final int RADIUS_BOTTOM = 4;

    @IntDef({RADIUS_ALL, RADIUS_LEFT, RADIUS_TOP, RADIUS_RIGHT, RADIUS_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RadiusSlide {
    }

    public static void setViewOutline(View view, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = view.getContext().obtainStyledAttributes(attrs,
                R.styleable.ViewOutlineStrategy, defStyleAttr, defStyleRes);
        float radius = typedArray.getDimension(R.styleable.ViewOutlineStrategy_clip_radius, 0f);
        int slide = typedArray.getInt(R.styleable.ViewOutlineStrategy_clip_side, RADIUS_ALL);
        typedArray.recycle();
        setViewOutline(view, radius, slide);
    }

    public static void setViewOutline(@NonNull View owner, float radius, @RadiusSlide int radiusSlide) {
        owner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int width = view.getWidth();
                int height = view.getHeight();

                if (width == 0 || height == 0) {
                    return;
                }

                int left = 0;
                int top = 0;
                int right = width;
                int bottom = height;

                if (radiusSlide == RADIUS_ALL) {
                    if (radius <= 0) {
                        outline.setRect(left, top, right, bottom);
                    } else {
                        outline.setRoundRect(left, top, right, bottom, radius);
                    }
                } else if (radiusSlide == RADIUS_LEFT) {
                    right += radius;
                } else if (radiusSlide == RADIUS_TOP) {
                    bottom += radius;
                } else if (radiusSlide == RADIUS_RIGHT) {
                    left -= radius;
                } else {
                    top -= radius;
                }
                outline.setRoundRect(left, top, right, bottom, radius);
            }
        });

        owner.setClipToOutline(radius > 0);
        owner.invalidate();
    }
}
