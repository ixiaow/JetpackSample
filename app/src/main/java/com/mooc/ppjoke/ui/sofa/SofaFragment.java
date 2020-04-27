package com.mooc.ppjoke.ui.sofa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.mooc.common.utils.Logs;
import com.mooc.common.view.ShareDialog;
import com.mooc.navannotation.FragmentDestination;
import com.mooc.ppjoke.R;

@FragmentDestination(pageUrl = "main/tabs/sofa")
public class SofaFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Logs.d("onCreateView");
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Button share = root.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog(getContext());
                shareDialog.setShareContent("测试分享功能");
                shareDialog.show();
            }
        });
        return root;
    }
}
