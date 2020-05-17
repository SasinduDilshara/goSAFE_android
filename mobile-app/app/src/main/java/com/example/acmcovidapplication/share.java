package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class share extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
    }
    public void shareApp(View view) {

    }

    public void goToAbout(View view) {
        Intent intent = new Intent(share.this, about.class);
        startActivity(intent);
    }
}
