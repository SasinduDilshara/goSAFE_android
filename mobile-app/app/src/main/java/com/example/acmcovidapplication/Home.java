package com.example.acmcovidapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.acmcovidapplication.services.CustomService;

import java.util.List;

public class Home extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    public void goToNext(View view) {
        Intent intent = new Intent(Home.this, how_it_works.class);
        startActivity(intent);

    }


    public void skip(View view) {
        Intent intent = new Intent(Home.this, start_page.class);
        startActivity(intent);
    }

}
