package com.mooc.common.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.mooc.common.GlideApp;
import com.mooc.common.GlideRequest;
import com.mooc.common.utils.PxUtils;

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
    public void setImageUrl(PPImageView view, String imageUrl, Boolean isCircle) {
        GlideRequest<Drawable> request = GlideApp.with(view).load(imageUrl);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            request = request.override(layoutParams.width, layoutParams.height);
        }
        if (isCircle) {
            request = request.transform(new CircleCrop());
        }
        request.into(view);
    }

    public void bindData(int widthPx, int heightPx, int marginLeft, @Nullable String imageUrl) {
        bindData(widthPx, heightPx, marginLeft, PxUtils.getScreenWidth(), PxUtils.getScreenWidth(), imageUrl);
    }

    public void bindData(int widthPx, int heightPx, final int marginLeft, final int maxWidth,
                         final int maxHeight, @Nullable String imageUrl) {

        if (TextUtils.isEmpty(imageUrl)) {
            setVisibility(GONE);
            return;
        }

        if (widthPx <= 0 || heightPx <= 0) {
            GlideApp.with(this).load(imageUrl).into(new ImageViewTarget<Drawable>(this) {
                @Override
                protected void setResource(@Nullable Drawable resource) {
                    if (resource != null) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();
                        setSize(width, height, marginLeft, maxWidth, maxHeight);
                        setImageDrawable(resource);
                    } else {
                        setVisibility(GONE);
                    }
                }
            });
            return;
        }
        setSize(widthPx, heightPx, marginLeft, maxWidth, maxHeight);
        setImageUrl(this, imageUrl, false);
    }

    private void setSize(int width, int height, int marginLeft, int maxWidth, int maxHeight) {
        int finalWidth = 0;
        int finalHeight = 0;

        // 宽度大于高度时，宽度为最大宽度，高度自适应
        if (width > height) {
            finalWidth = maxWidth;
            finalHeight = (int) (height * (1.0 * width / finalWidth));
        } else {
            // 高度大于宽度时，高度为最大高度，宽度自适应
            finalHeight = maxHeight;
            finalWidth = (int) (width * (1.0f * height / finalHeight));
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) layoutParams).leftMargin = width > height ? 0 : PxUtils.dp2px(marginLeft);
        } else if (layoutParams instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) layoutParams).leftMargin = width > height ? 0 : PxUtils.dp2px(marginLeft);
        }

        setLayoutParams(layoutParams);
    }

    @BindingAdapter({"blur_url", "radius"})
    public static void setBlurImageUrl(PPImageView imageView, String blurImageUrl, int radius) {
        GlideApp.with(imageView).load(blurImageUrl)
                .into(new ImageViewTarget<Drawable>(imageView) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        if (resource != null) {
                            imageView.setBackground(resource);
                        }
                    }
                });
    }
}
