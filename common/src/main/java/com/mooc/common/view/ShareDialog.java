package com.mooc.common.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.common.R;
import com.mooc.common.utils.PxUtils;

import java.util.ArrayList;
import java.util.List;

public class ShareDialog extends AlertDialog {
    private ShareAdapter adapter;
    private List<ResolveInfo> shareItems = new ArrayList<>();
    private String shareContent;
    private View.OnClickListener mListener;


    public ShareDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置没有title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Context context = getContext();

        CornerFrameLayout frameLayout = new CornerFrameLayout(context);
        frameLayout.setViewOutline(PxUtils.dp2px(20), ViewHelper.RADIUS_TOP);
        frameLayout.setBackgroundColor(Color.WHITE);

        RecyclerView gridView = new RecyclerView(context);
        gridView.setLayoutManager(new GridLayoutManager(context, 4));
        adapter = new ShareAdapter(context, shareItems);
        adapter.setShareContent(shareContent);
        adapter.setListener(v -> {
            if (mListener != null) {
                mListener.onClick(v);
            }
            dismiss();
        });
        gridView.setAdapter(adapter);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = PxUtils.dp2px(20);
        params.leftMargin = params.topMargin = params.rightMargin = params.bottomMargin = margin;
        params.gravity = Gravity.CENTER;
        frameLayout.addView(gridView, params);

        setContentView(frameLayout);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }

        generateShareParams();
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
        if (adapter != null) {
            adapter.setShareContent(shareContent);
        }
    }

    public void setListener(View.OnClickListener listener) {
        this.mListener = listener;
    }

    private void generateShareParams() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        Context context = getContext();
        shareItems.clear();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (TextUtils.equals(packageName, "com.tencent.mm")
                    || TextUtils.equals(packageName, "com.tencent.mobileqq")) {
                shareItems.add(resolveInfo);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private static class ShareAdapter extends RecyclerView.Adapter<DefaultViewHolder> {
        private final LayoutInflater layoutInflater;
        private final PackageManager packageManager;
        private Context context;
        private List<ResolveInfo> shareItems;
        private String shareContent;
        private View.OnClickListener listener;

        public ShareAdapter(Context context, List<ResolveInfo> shareItems) {
            this.context = context;
            this.shareItems = shareItems;
            layoutInflater = LayoutInflater.from(context);
            packageManager = context.getPackageManager();
        }

        @NonNull
        @Override
        public DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.layout_share_list_item, parent, false);
            return new DefaultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DefaultViewHolder holder, int position) {
            ResolveInfo resolveInfo = shareItems.get(position);

            PPImageView ppImageView = holder.itemView.findViewById(R.id.share_icon);
            Drawable drawable = resolveInfo.loadIcon(packageManager);
            ppImageView.setImageDrawable(drawable);

            TextView shareText = holder.itemView.findViewById(R.id.share_text);
            CharSequence loadLabel = resolveInfo.loadLabel(packageManager);
            shareText.setText(loadLabel);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String pkg = resolveInfo.activityInfo.packageName;
                String cls = resolveInfo.activityInfo.name;
                intent.setComponent(new ComponentName(pkg, cls));
                intent.putExtra(Intent.EXTRA_TEXT, shareContent);
                context.startActivity(intent);
                if (listener != null) {
                    listener.onClick(v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return shareItems == null ? 0 : shareItems.size();
        }

        public void setShareContent(String shareContent) {
            this.shareContent = shareContent;
        }

        public void setListener(View.OnClickListener listener) {
            this.listener = listener;
        }
    }
}
