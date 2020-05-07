package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class otp_entering extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_entering);
    }

    public void appPermissions(View view) {
        Intent intent = new Intent(otp_entering.this, appPermission.class);
        startActivity(intent);
    }
}
