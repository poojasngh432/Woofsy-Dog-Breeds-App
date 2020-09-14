package com.example.woofsyapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.example.woofsyapp.BuildConfig;
import com.example.woofsyapp.R;

public class AboutActivity extends  AppCompatActivity {
    TextView aboutTV, versionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboutTV = findViewById(R.id.about_tv);
        versionTV = findViewById(R.id.version_code_tv);

        aboutTV.setMovementMethod(LinkMovementMethod.getInstance());

        String versionName = BuildConfig.VERSION_NAME;
       versionTV.setText(versionName);

    }
}
