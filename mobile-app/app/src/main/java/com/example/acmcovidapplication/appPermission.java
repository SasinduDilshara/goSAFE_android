package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class appPermission extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permission);
    }

    public void allowAccess(View view) {
        Intent intent = new Intent(appPermission.this, share.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        String IS_ALLOWED = this.getResources().getString(R.string.is_allowed);
        Util.getSharedPreferenceEditor(this.getPackageName(),this).putBoolean(IS_ALLOWED, true);
    }

    public void goToAbout(View view) {
        Intent intent = new Intent(appPermission.this, about.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
