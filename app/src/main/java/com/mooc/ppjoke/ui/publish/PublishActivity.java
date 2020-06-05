package com.mooc.ppjoke.ui.publish;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mooc.annotation.Destination;
import com.mooc.ppjoke.R;

@Destination.Activity(pageUrl = "main/tabs/publish")
public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
    }
}
