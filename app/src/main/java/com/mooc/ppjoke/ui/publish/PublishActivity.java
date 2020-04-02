package com.mooc.ppjoke.ui.publish;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mooc.navannotation.ActivityDestination;
import com.mooc.ppjoke.R;

@ActivityDestination(pageUrl = "main/tabs/publish")
public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
    }
}
