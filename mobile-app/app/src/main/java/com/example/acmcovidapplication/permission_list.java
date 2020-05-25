package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.acmcovidapplication.db.DatabaseHelper;

public class permission_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_list);
    }

    public void goToShare(View view) {
        Intent intent = new Intent(permission_list.this, share.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


        DatabaseHelper.getInstance(this).insertAllowed(true);


    }
}
