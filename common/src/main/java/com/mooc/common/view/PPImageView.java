package com.mooc.common.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

public class PPImageView extends AppCompatImageView {
    public PPImageView(Context context) {
        this(context, null);
    }

    public PPImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PPImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @BindingAdapter({"image_url", "is_circle"})
    public void bindUrl(PPImageView view, String imageUrl, Boolean isCircle) {

    }
}
