package com.example.acmcovidapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.services.CustomService;

public class permission_list extends AppCompatActivity implements Switch.OnCheckedChangeListener {
    Switch aSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_list);
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(this);
        aSwitch.setChecked(DatabaseHelper.getInstance(this).isLocationTrackable());
    }

    public void goToShare(View view) {
        Intent intent = new Intent(permission_list.this, share.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        DatabaseHelper.getInstance(this).insertAllowed(true);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            DatabaseHelper.getInstance(this).insertLocationTrackable(isChecked);
            LocalBroadcastManager.getInstance(this).sendBroadcast(CustomService.makeIntent(isChecked));

    }
}
